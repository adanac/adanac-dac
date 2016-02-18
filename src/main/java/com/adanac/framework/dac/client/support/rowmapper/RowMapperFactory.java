package com.adanac.framework.dac.client.support.rowmapper;

import java.util.Date;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;

/**
 * 映射类型工厂
 * @author adanac
 * @version 1.0
 */
public class RowMapperFactory<T> {
	private Class<T> requiredType;

	public RowMapperFactory(Class<T> requiredType) {
		this.requiredType = requiredType;
	}

	public RowMapper<T> getRowMapper() {
		if (requiredType.equals(String.class) || Number.class.isAssignableFrom(requiredType)
				|| requiredType.equals(Date.class)) {
			return new SingleColumnRowMapper<T>(requiredType);
		} else {
			return new BeanPropertyRowMapper<T>(requiredType);
		}
	}
}