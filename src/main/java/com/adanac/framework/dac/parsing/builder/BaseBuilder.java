package com.adanac.framework.dac.parsing.builder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.adanac.framework.dac.client.support.Configuration;

/**
 * 描述：基本生成类
 * 
 * @author
 */
public abstract class BaseBuilder {
	protected final Configuration configuration;

	public BaseBuilder(Configuration configuration) {
		this.configuration = configuration;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * 功能描述：是否为空，为空则赋默认值<br>
	 * 返回值: Boolean <说明>
	 */
	protected Boolean booleanValueOf(String value, Boolean defaultValue) {
		return value == null ? defaultValue : Boolean.valueOf(value);
	}

	/**
	 * 功能描述：是否为空，为空则赋默认值<br>
	 * 返回值: Integer <说明>
	 */
	protected Integer integerValueOf(String value, Integer defaultValue) {
		return value == null ? defaultValue : Integer.valueOf(value);
	}

	/**
	 * 功能描述：是否为空，为空则赋默认值<br>
	 * 返回值: Set<String> <说明>
	 */
	protected Set<String> stringSetValueOf(String value, String defaultValue) {
		String value1 = value == null ? defaultValue : value;
		return new HashSet<String>(Arrays.asList(value1.split(",")));
	}

}
