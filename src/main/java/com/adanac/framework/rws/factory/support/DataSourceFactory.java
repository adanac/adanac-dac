package com.adanac.framework.rws.factory.support;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adanac.framework.rws.common.CollectionUtils;
import com.adanac.framework.rws.common.ConnectionChecker;
import com.adanac.framework.rws.common.NamedThreadFactory;
import com.adanac.framework.rws.common.URFConstant;
import com.adanac.framework.rws.exception.RWSException;
import com.adanac.framework.rws.factory.IDataSourceFactory;
import com.adanac.framework.rws.schema.config.DsConfig;
import com.adanac.framework.rws.schema.config.DsGroupConfig;
import com.adanac.framework.rws.selector.IDataSourceSelector;
import com.adanac.framework.rws.selector.surpport.DefaultDataSourceSelector;

/**
 * 读写分离 数据源工厂
 * @author adanac
 * @version 1.0
 */
public class DataSourceFactory implements IDataSourceFactory {

	private static final Logger logger = LoggerFactory.getLogger(DataSourceFactory.class);

	/**
	 * 数据源组
	 */
	private DsGroupConfig dataSourceGroup;

	/**
	 * 写库数据源
	 */
	private DsConfig wrDataSource;

	/**
	 * 读库数据源
	 */
	private final List<DsConfig> roDataSources = Collections.synchronizedList(new ArrayList<DsConfig>());

	/**
	 * 暂时不可用的数据源集合
	 */
	private final List<DsConfig> failedRoDataSources = Collections.synchronizedList(new ArrayList<DsConfig>());

	/**
	 * 默认的数据源选择器（根据权重随机选择）
	 */
	private IDataSourceSelector dataSourceSelector = new DefaultDataSourceSelector();

	// 定时任务执行器
	private final ScheduledExecutorService beatExecutor = Executors.newScheduledThreadPool(1,
			new NamedThreadFactory("UrfDataSourceHeartBeatTimer", true));

	// 心跳定时器，定时检查数据源是否正常
	private ScheduledFuture<?> beatFuture;

	private int period;

	public DataSourceFactory(DsGroupConfig dataSourceGroup) {
		this(dataSourceGroup, 0);
	}

	public DataSourceFactory(DsGroupConfig dataSourceGroup, int period) {
		this.dataSourceGroup = dataSourceGroup;
		this.period = period;
		try {
			init();
		} catch (IllegalAccessException e) {
			throw new RWSException(e);
		}
	}

	public void init() throws IllegalAccessException {
		if (dataSourceGroup == null) {
			throw new IllegalAccessException("DataSourceFactory propperty dataSourceGroup is null !");
		}
		this.wrDataSource = dataSourceGroup.getWrDsConfig();
		for (DsConfig dsc : dataSourceGroup.getRoDsConfigs().values()) {
			roDataSources.add(dsc);
		}
		if (wrDataSource == null) {
			throw new IllegalAccessException("DataSourceFactory propperty wrDataSource is null !");
		}
		if (roDataSources.size() <= 0) {
			throw new IllegalAccessException("DataSourceFactory propperty roDataSources is empty !");
		}
		// 启动心跳线程
		int beatPeriod = period == 0 ? URFConstant.HEART_BEAT_PERIOD : period * 1000;
		this.beatFuture = beatExecutor.scheduleWithFixedDelay(new Runnable() {
			public void run() {
				// 检测并连接注册中心
				try {
					doHeartBeat();
				} catch (Throwable t) { // 防御性容错
					logger.error("Unexpected error occur at failed heartbeat, cause: " + t.getMessage(), t);
				}
			}
		}, beatPeriod, beatPeriod, TimeUnit.MILLISECONDS);
	}

	private void doHeartBeat() throws IllegalAccessException {
		logger.debug("[{}] HeartBeat is working...!", this);
		List<Integer> faildDsIndexes = new ArrayList<Integer>();
		List<Integer> okDsIndexes = new ArrayList<Integer>();
		for (int i = 0; i < this.roDataSources.size(); i++) {
			if (!checkDataSource(this.roDataSources.get(i))) {
				faildDsIndexes.add(i);
				logger.debug("RoDataSource {} connected failed, remove to failedRoDataSources!",
						roDataSources.get(i).getName());
			}
		}
		for (int i = 0; i < this.failedRoDataSources.size(); i++) {
			if (checkDataSource(this.failedRoDataSources.get(i))) {
				okDsIndexes.add(i);
				logger.debug("failed RoDataSource {} reConnected succeed, remove to roDataSources!",
						failedRoDataSources.get(i).getName());
			} else {
				logger.debug("RoDataSource {} retry connect failed!", failedRoDataSources.get(i).getName());
			}
		}
		// 这里需要从大到小删除,否则删除会越界
		for (int index : CollectionUtils.sortDesc(faildDsIndexes)) {
			this.failedRoDataSources.add(this.roDataSources.remove(index));
		}
		// 这里需要从大到小删除,否则删除会越界
		for (int index : CollectionUtils.sortDesc(okDsIndexes)) {
			this.roDataSources.add(this.failedRoDataSources.remove(index));
		}
	}

	private boolean checkDataSource(DsConfig dsConfig) throws IllegalAccessException {

		boolean resultOk = true;
		Connection c = null;
		try {
			c = dsConfig.getRefDataSource().getConnection();
		} catch (SQLException e) {
			logger.error("DsConfig" + dsConfig.getName() + "getConnection failed!", e);
			resultOk = false;
			closeConnection(c);
		}
		if (resultOk) {
			if (ConnectionChecker.isValidConnection(c, dsConfig.getType()) != null) {
				resultOk = false;
			}
			closeConnection(c);
		}

		return resultOk;
	}

	@Override
	public DsConfig getRoDataSource(String id) throws IllegalAccessException {
		if (id == null || "".equals(id)) {
			return dataSourceSelector.select(roDataSources);
		} else {
			DsConfig dsConfig = dataSourceGroup.getRoDsConfigs().get(id);
			if (dsConfig == null) {
				throw new IllegalAccessException("DataSource:" + id + " is not exist!");
			}
			return dataSourceGroup.getRoDsConfigs().get(id);
		}

	}

	@Override
	public DsConfig getWrDataSource() {
		return wrDataSource;
	}

	@Override
	public void destory() {
		try {
			beatFuture.cancel(true);
		} catch (Throwable t) {
			logger.warn(t.getMessage(), t);
		}
	}

	private void closeConnection(Connection c) {
		if (c != null) {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}