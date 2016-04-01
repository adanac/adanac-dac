package com.adanac.framework.rws.schema.config;

import javax.sql.DataSource;

/**
 * 自定义的读写分离spring schema中的datasource配置
 * @author adanac
 * @version 1.0
 */
public class DsConfig {
	private String name;

	private DataSource refDataSource;

	private int weight;

	private String type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public DataSource getRefDataSource() {
		return refDataSource;
	}

	public void setRefDataSource(DataSource refDataSource) {
		this.refDataSource = refDataSource;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
