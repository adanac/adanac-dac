package com.adanac.framework.dac.client.support;

import java.util.Map;

import com.adanac.framework.dac.parsing.FreeMakerParser;

/**
 * sql描述
 * @author adanac
 * @version 1.0
 */
public class MappedStatement {
	private String resource;
	private Configuration configuration;
	private String id;
	private String idProperty;
	private Boolean isGenerator;

	private String keyGenerator;
	private int fetchSize;
	private int maxRows;
	private int timeout;
	private boolean isRead;
	private String sqlSource;
	private String dsName;
	private SqlCommandType sqlCommandType;

	public String getIdProperty() {
		return idProperty;
	}

	public void setIdProperty(String idProperty) {
		this.idProperty = idProperty;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getFetchSize() {
		return fetchSize;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public String getSqlSource() {
		return sqlSource;
	}

	public void setSqlSource(String sqlSource) {
		this.sqlSource = sqlSource;
	}

	public String getDsName() {
		return dsName;
	}

	public void setDsName(String dsName) {
		this.dsName = dsName;
	}

	public SqlCommandType getSqlCommandType() {
		return sqlCommandType;
	}

	public void setSqlCommandType(SqlCommandType sqlCommandType) {
		this.sqlCommandType = sqlCommandType;
	}

	public String getBoundSql(Map<String, Object> parameterMap) {
		return FreeMakerParser.process(this.sqlSource, parameterMap);
	}

	public Integer getMaxRows() {
		return maxRows;
	}

	public void setMaxRows(Integer maxRows) {
		this.maxRows = maxRows;
	}

	public String getKeyGenerator() {
		return keyGenerator;
	}

	public void setKeyGenerator(String keyGenerator) {
		this.keyGenerator = keyGenerator;
	}

	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	public void setMaxRows(int maxRows) {
		this.maxRows = maxRows;
	}

	public Boolean getIsGenerator() {
		return isGenerator;
	}

	public void setIsGenerator(Boolean isGenerator) {
		this.isGenerator = isGenerator;
	}
}
