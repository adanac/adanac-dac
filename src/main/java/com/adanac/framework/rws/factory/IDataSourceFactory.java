package com.adanac.framework.rws.factory;

import com.adanac.framework.rws.schema.config.DsConfig;

public interface IDataSourceFactory {
	/**
	 * 获取读库数据源
	 * @return DataSource
	 */
	public DsConfig getWrDataSource();

	/**
	 * 获取写库数据源
	 * @param id datasource的id，如果为null或者""则由选择器选择
	 * @return DataSource
	 * @throws IllegalAccessException 
	 */
	public DsConfig getRoDataSource(String id) throws IllegalAccessException;

	/**
	 * 销毁心跳线程
	 */
	public void destory();
}
