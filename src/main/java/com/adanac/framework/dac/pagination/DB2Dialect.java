package com.adanac.framework.dac.pagination;

/**
 * 功能描述： SQL分页封装
 * @author adanac
 * @version 1.0
 */
public class DB2Dialect implements Dialect {
	public String getLimitSqlString(String sql, int offset, int limit) {
		// 分页查询语句，因为传入的sql不定，无法确定具体字段，所以这里用*
		StringBuilder pagingSelect = new StringBuilder(sql.length() + 200)
				.append("select * from ( select inner2_.*, "
						+ "rownumber() over(order by order of inner2_) as rownumber_ from ( ")
				.append(sql).append(" fetch first ").append(limit)
				.append(" rows only ) as inner2_ ) as inner1_ where rownumber_ > ").append(offset)
				.append(" order by rownumber_");
		return pagingSelect.toString();
	}

	public String getCountSqlString(String sql) {
		return new StringBuffer(sql.length() + 20).append("select count(1) from( ").append(sql).append(" )").toString();
	}
}
