package com.adanac.framework.dac.cache.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * CacheController注解 <br>
 * 如果单表API（即find方法）需要用缓存，则需要在实体类中配此注解
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface CacheController {

	/** CacheProperty注解组成的数组 */
	CacheProperty[]value();

}
