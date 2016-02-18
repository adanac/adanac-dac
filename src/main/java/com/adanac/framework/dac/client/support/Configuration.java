package com.adanac.framework.dac.client.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.Assert;

/**
 * 描述：配置类<br>
 * 用来存放全局配置
 * @author adanac
 */
public class Configuration {
	protected boolean cacheEnabled = true;

	/** 默认jdbcTimeOut值 */
	protected Integer defaultStatementTimeout = 100;

	protected Properties variables = new Properties();

	protected Map<String, Object> attributes = new HashMap<String, Object>();

	protected final ConcurrentHashMap<String, MappedStatement> mappedStatements = new ConcurrentHashMap<String, MappedStatement>();

	protected final Set<String> loadedResources = new HashSet<String>();

	public void addLoadedResource(String resource) {
		loadedResources.add(resource);
	}

	public boolean isResourceLoaded(String resource) {
		return loadedResources.contains(resource);
	}

	public boolean isCacheEnabled() {
		return cacheEnabled;
	}

	public void setCacheEnabled(boolean cacheEnabled) {
		this.cacheEnabled = cacheEnabled;
	}

	public Integer getDefaultStatementTimeout() {
		return defaultStatementTimeout;
	}

	public void setDefaultStatementTimeout(Integer defaultStatementTimeout) {
		this.defaultStatementTimeout = defaultStatementTimeout;
	}

	public Properties getVariables() {
		return variables;
	}

	public void setVariables(Properties variables) {
		this.variables = variables;
	}

	public void addMappedStatement(MappedStatement ms) {
		mappedStatements.putIfAbsent(ms.getId(), ms);
	}

	public Collection<String> getMappedStatementNames() {
		return mappedStatements.keySet();
	}

	public MappedStatement getMappedStatement(String id) {
		return getMappedStatement(id, false);
	}

	/**
	 * 功能描述：根据sqlId，查询sql状态
	 * 
	 * @param 参数说明
	 *            返回值: 类型 <说明>
	 * @return 返回值
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	public MappedStatement getMappedStatement(String id, boolean validation) {
		MappedStatement mappedStatement = mappedStatements.get(id);
		if (validation) {
			Assert.notNull(mappedStatement, "Can't find sql statement for key " + id + " in sqlmap.");
		}
		return mappedStatement;
	}

	public Collection<MappedStatement> getMappedStatements() {
		// buildAllStatements();
		return mappedStatements.values();
	}

	public boolean hasStatement(String statementName) {
		return hasStatement(statementName, true);
	}

	public boolean hasStatement(String statementName, boolean validateIncompleteStatements) {
		return mappedStatements.containsKey(statementName);
	}

	public Object addAttribute(String attributeName, Object attributeValue) {
		return attributes.put(attributeName, attributeValue);
	}

	public Object getAttribute(String attributeName) {
		return attributes.get(attributeName);
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	/**
	 * Extracts namespace from fully qualified statement id.
	 * 
	 * @param statementId
	 * @return namespace or null when id does not contain period.
	 */
	public String extractNamespace(String statementId) {
		int lastPeriod = statementId.lastIndexOf('.');
		return lastPeriod > 0 ? statementId.substring(0, lastPeriod) : null;
	}
}
