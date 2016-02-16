package com.adanac.framework.dac.cache.annotation;

/**
 * 描述：CacheProperty注解 <br>
 * 用作CacheController注解的属性
 */
public @interface CacheProperty {

	/** 缓存属性key值 */
	String key();

	/** 缓存属性value值 */
	String value();

}