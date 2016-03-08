package com.adanac.framework.dac.pagination;

/**
 * MySQL方言实现，通过SQL
 * @author adanac
 * @version 1.0
 */
public class MySQLDialect implements Dialect {
	private static final String COUNT_ALIAS = "_count_block_";

	public String getLimitSqlString(String sql, int offset, int limit) {
		StringBuilder sb = new StringBuilder(sql.length() + 20);
		sb.append(sql);
		if (offset > 0) {
			sb.append(" limit ").append(offset).append(',').append(limit);
		} else {
			sb.append(" limit ").append(limit);
		}
		return sb.toString();
	}

	public String getCountSqlString(String sql) {
		StringBuilder sb = new StringBuilder(sql.length() + 20);
		sb.append("select count(1) from (");
		sb.append(sql);
		sb.append(") ");
		sb.append(COUNT_ALIAS);
		return sb.toString();
	}
}
