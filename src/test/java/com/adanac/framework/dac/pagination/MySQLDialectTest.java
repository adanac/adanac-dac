package com.adanac.framework.dac.pagination;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MySQLDialectTest {
	private MySQLDialect mySQLDialect = null;
	String sql = "", sqlString = "";

	int offset, limit;

	@Before
	public void setUp() throws Exception {
		mySQLDialect = new MySQLDialect();
		sql = "select * from user";
		offset = 1;
		limit = 10;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetLimitSqlString() {
		sqlString = mySQLDialect.getLimitSqlString(sql, offset, limit);
		System.out.println(sqlString);
	}

	@Test
	public void testGetCountSqlString() {
		sqlString = mySQLDialect.getCountSqlString(sql);
		System.out.println(sqlString);
	}

}
