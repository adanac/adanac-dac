package com.adanac.framework.rws.common;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据库实例连接可用性监测（心跳用）
 * @author adanac
 * @version 1.0
 */
public class ConnectionChecker {
	private static final Logger logger = LoggerFactory.getLogger(ConnectionChecker.class);

	public static SQLException isValidConnection(Connection c, String type) throws IllegalAccessException {
		String sql = "";
		if ("mysql".equalsIgnoreCase(type)) {
			sql = "SELECT 1";
		} else if ("db2".equalsIgnoreCase(type)) {
			sql = "SELECT 1 FROM SYSIBM.SYSDUMMY1";
		} else if ("oracle".equalsIgnoreCase(type)) {
			sql = "select 1 from dual";
		} else {
			throw new IllegalAccessException("DataBase type:" + type + "is not supported!");
		}

		return isConnectionValid(c, sql);
	}

	public static SQLException isConnectionValid(Connection c, String sql) {

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = c.createStatement();
			rs = stmt.executeQuery(sql);
		} catch (Exception e) {
			if (e instanceof SQLException) {
				logger.warn("Unexpected error while execute ({})", sql);
				return (SQLException) e;
			} else {
				logger.warn("Unexpected error while execute ({})", sql);
				return new SQLException("execute (" + sql + ") failed: " + e.toString());
			}
		} finally {
			// cleanup the Statment
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
			}
		}

		return null;
	}
}
