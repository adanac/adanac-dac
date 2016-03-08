package com.adanac.framework.dac.pagination;

/**
 * 
 * @author adanac
 * @version 1.0
 */
public class OracleDialect implements Dialect {
	private static final String DATA_ALIAS = "data_block_";

	public String getLimitSqlString(String sql, int offset, int limit) {

		StringBuilder sb = new StringBuilder(sql.length() + 100);
		sb.append("select * from (");
		sb.append("select ").append(DATA_ALIAS).append(".*, rownum row_num_ from ( ");
		sb.append(sql);
		sb.append(") ").append(DATA_ALIAS);
		sb.append(" where rownum <= ").append(limit + offset);
		sb.append(") where row_num_ > ").append(offset);

		return sb.toString();

	}

	public String getCountSqlString(String sql) {
		StringBuilder sb = new StringBuilder(sql.length() + 20);
		sb.append("select count(1) from (");
		sb.append(sql);
		sb.append(")");
		return sb.toString();
	}
}
