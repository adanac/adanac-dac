package com.adanac.framework.dac.parsing.annotation;

import java.util.Properties;

import com.adanac.framework.dac.cache.annotation.CacheController;
import com.adanac.framework.dac.cache.support.CacheConfigurationUtil;
import com.adanac.framework.dac.cache.support.CacheProperty;
import com.adanac.framework.dac.client.support.Configuration;
import com.adanac.framework.dac.parsing.builder.BaseBuilder;
import com.adanac.framework.dac.parsing.exception.ParsingException;

/**
 * 描述：缓存注解解析类
 * 
 * @author 13092011/jorgie
 */
public class AnnotationCacheBuilder extends BaseBuilder {

	private Class<?> entityClass;
	private String currentNamespace;

	public AnnotationCacheBuilder(Configuration configuration, Class<?> entityClass) {
		super(configuration);
		this.entityClass = entityClass;
		if (entityClass == null) {
			throw new ParsingException("entityClass can't null.");
		}
	}

	/**
	 * 
	 * 功能描述：解析缓存注解CacheController
	 */
	public void parse() {

		try {
			// 生成namespace
			currentNamespace = entityClass.getName();

			if (!entityClass.isAnnotationPresent(CacheController.class)) {
				// 如果没有CacheController注解则跳出方法
				return;
			}
			CacheController cacheController = entityClass.getAnnotation(CacheController.class);
			String cacheName = "";
			String timeOut = "";
			String namespace = "";
			// 获取CacheProperty注解
			com.adanac.framework.dac.cache.annotation.CacheProperty[] cacheProperties = cacheController.value();
			// 遍历CacheProperty注解
			for (com.adanac.framework.dac.cache.annotation.CacheProperty cacheProperty : cacheProperties) {
				if ("cacheName".equals(cacheProperty.key())) {
					cacheName = cacheProperty.value();
				} else if ("timeOut".equals(cacheProperty.key())) {
					timeOut = cacheProperty.value();
				} else if ("redisNamespace".equals(cacheProperty.key())) {
					namespace = cacheProperty.value();
				}
			}
			// 将获取的缓存属性连同sqlId一起注入到configuration中
			Properties properties = new Properties();
			properties.put("cacheName", cacheName);
			properties.put("timeOut", timeOut);
			properties.put("redisNamespace", namespace);
			CacheConfigurationUtil.addStatementCacheProperties(configuration, applyCurrentNamespace("select"),
					new CacheProperty(properties));
		} catch (Exception e) {
			throw new ParsingException(
					"Error happens when parsing '@CacheController' . " + currentNamespace + " Cause: " + e, e);
		}

	}

	private String applyCurrentNamespace(String id) {
		if (id.startsWith(currentNamespace + ".")) {
			return id;
		} else {
			return currentNamespace + "." + id;
		}
	}

}
