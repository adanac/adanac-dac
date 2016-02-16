package com.adanac.framework.dac.parsing.support.annotation;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 功能描述：TableRoute注解<br>
 * 
 * @author 12072528
 */
@Target({ TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface TableRoute {

	/** 表名 */
	String tableName();

}
