package com.adanac.framework.dac.cache.support;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * 描述：缓存属性（参数）
 */
public class CacheProperty {

	private Properties properties;

	public CacheProperty(Properties properties) {
		this.properties = properties;
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	public String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	/**
	 * 功能描述：获取超时时间
	 */
	public int getTimeOut() {
		String timeout = properties.getProperty("timeOut");
		try {
			return Integer.valueOf(timeout);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * 功能描述：获取清理缓存的sqlIds
	 */
	public String[] getFlushOn() {
		String flushOn = properties.getProperty("flushOn");
		Set<String> result = new HashSet<String>();
		if (flushOn == null || "".equals(flushOn)) {
			return new String[] {};
		}
		String[] texts = flushOn.split(",");
		for (String text : texts) {
			if (text == null || "".equals(text.trim())) {
				continue;
			}
			result.add(text.trim());
		}
		return result.toArray(new String[result.size()]);
	}

	/**
	 * 功能描述：获取缓存名称（选择ehcache时的配置参数）
	 */
	public String getCacheName() {
		return properties.getProperty("cacheName");
	}

	/**
	 * 功能描述：获取redis命名空间，用作计算的缓存key值的前缀（选择redis时的配置参数）
	 */
	public String getRedisNamespace() {
		String namespace = properties.getProperty("redisNamespace");
		if (namespace == null) {
			namespace = "";
		}
		return namespace;
	}

	@Override
	public String toString() {
		return "CachedProperty" + this.properties.toString();
	}
}
