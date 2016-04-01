package com.adanac.framework.rws.schema.config;

import java.util.Map;

/**
 *  自定义的读写分离spring schema中的datasource组配置
 * @author adanac
 * @version 1.0
 */
public class DsGroupConfig {
	private String id;

	/**
	 * 写库对应的数据源配置
	 */
	private DsConfig wrDsConfig;

	/**
	 * 读库对应的数据源配置列表
	 */
	private Map<String, DsConfig> roDsConfigs;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DsConfig getWrDsConfig() {
		return wrDsConfig;
	}

	public void setWrDsConfig(DsConfig wrDsConfig) {
		this.wrDsConfig = wrDsConfig;
	}

	public Map<String, DsConfig> getRoDsConfigs() {
		return roDsConfigs;
	}

	public void setRoDsConfigs(Map<String, DsConfig> roDsConfigs) {
		this.roDsConfigs = roDsConfigs;
	}
}
