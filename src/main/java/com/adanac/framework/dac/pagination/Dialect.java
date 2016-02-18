package com.adanac.framework.dac.pagination;

/**
 * 方言
 * @author adanac
 * @version 1.0
 */
public interface Dialect {

	public String getLimitSqlString(String sql, int offset, int limit);

	public String getCountSqlString(String sql);
}
