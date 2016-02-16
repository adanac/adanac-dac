package com.adanac.framework.dac.cache.support;

import java.util.HashMap;
import java.util.Map;

import com.adanac.framework.dac.client.support.Configuration;

/**
 * 描述：缓存配置处理
 * 
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class CacheConfigurationUtil {

	public static final String CACHED_STATEMENT_HOLD_ON_CONFIGURATION_ATTRIBUTE_NAME = "_dalClient_CachedStatement_";

	public static final String CACHED_NAMESPACE_HOLD_ON_CONFIGURATION_ATTRIBUTE_NAME = "_dalClient_cachedNamespace";

	/**
	 * 功能描述：将Statement及其缓存属性存入configuration对象<br>
	 * 输入参数：配置对象，sqlId，缓存属性<按照参数定义顺序>
	 * 
	 * @param configuration,statementId,cacheProperty
	 * @return 返回值
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	public static void addStatementCacheProperties(Configuration configuration, String statementId,
			CacheProperty cacheProperty) {
		@SuppressWarnings("unchecked")
		Map<String, CacheProperty> statementCacheProps = (Map<String, CacheProperty>) configuration
				.getAttribute(CACHED_STATEMENT_HOLD_ON_CONFIGURATION_ATTRIBUTE_NAME);
		if (statementCacheProps == null) {
			statementCacheProps = new HashMap<String, CacheProperty>();
			configuration.addAttribute(CACHED_STATEMENT_HOLD_ON_CONFIGURATION_ATTRIBUTE_NAME, statementCacheProps);
		}
		statementCacheProps.put(statementId, cacheProperty);

	}

	/**
	 * 功能描述：根据Statement从configuration对象读取出对应的缓存属性 输入参数：配置对象，sqlId<按照参数定义顺序>
	 * 
	 * @param configuration,statementId
	 * @return CacheProperty
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	public static CacheProperty getStatementCacheProperties(Configuration configuration, String statementId) {
		@SuppressWarnings("unchecked")
		Map<String, CacheProperty> statementCacheProps = (Map<String, CacheProperty>) configuration
				.getAttribute(CACHED_STATEMENT_HOLD_ON_CONFIGURATION_ATTRIBUTE_NAME);
		if (statementCacheProps == null) {
			statementCacheProps = new HashMap<String, CacheProperty>();
			configuration.addAttribute(CACHED_STATEMENT_HOLD_ON_CONFIGURATION_ATTRIBUTE_NAME, statementCacheProps);
		}
		return statementCacheProps.get(statementId);

	}

	/**
	 * 功能描述：将namespace及其缓存属性存入configuration对象 输入参数：配置对象，namesapce，缓存属性<按照参数定义顺序>
	 * 
	 * @param configuration,namespace,cacheProperty
	 * @return 返回值
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	public static void addNamespaceCacheProperties(Configuration configuration, String namespace,
			CacheProperty cacheProperty) {
		@SuppressWarnings("unchecked")
		Map<String, CacheProperty> namespaceProps = (Map<String, CacheProperty>) configuration
				.getAttribute(CACHED_NAMESPACE_HOLD_ON_CONFIGURATION_ATTRIBUTE_NAME);
		if (namespaceProps == null) {
			namespaceProps = new HashMap<String, CacheProperty>();
			configuration.addAttribute(CACHED_NAMESPACE_HOLD_ON_CONFIGURATION_ATTRIBUTE_NAME, namespaceProps);
		}
		namespaceProps.put(namespace, cacheProperty);

	}

	/**
	 * 功能描述：根据namespace从configuration对象读取出对应的缓存属性 输入参数：配置对象，namesapce<按照参数定义顺序>
	 * 
	 * @param configuration,namesapce
	 * @return CacheProperty
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	public static CacheProperty getNamespaceCacheProperties(Configuration configuration, String namespace) {
		@SuppressWarnings("unchecked")
		Map<String, CacheProperty> namespaceProps = (Map<String, CacheProperty>) configuration
				.getAttribute(CACHED_NAMESPACE_HOLD_ON_CONFIGURATION_ATTRIBUTE_NAME);
		if (namespaceProps == null) {
			namespaceProps = new HashMap<String, CacheProperty>();
			configuration.addAttribute(CACHED_NAMESPACE_HOLD_ON_CONFIGURATION_ATTRIBUTE_NAME, namespaceProps);
		}
		return namespaceProps.get(namespace);

	}

}