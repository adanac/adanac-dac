package com.adanac.framework.rws.selector;

import java.util.List;

import com.adanac.framework.rws.schema.config.DsConfig;

public interface IDataSourceSelector {
	/**
	 * 从数据源列表中选择出一个合适的数据源
	 * @param DsConfigs
	 * @return DataSource 数据源对象
	 */
	public DsConfig select(List<DsConfig> dsConfigs);
}
