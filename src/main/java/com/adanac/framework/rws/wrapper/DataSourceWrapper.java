package com.adanac.framework.rws.wrapper;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.adanac.framework.rws.schema.config.DsConfig;

public class DataSourceWrapper implements DataSource {

	private DataSource currentDataSource;

	private DsConfig dsConfig;

	public DataSourceWrapper(DsConfig dsConfig) {
		super();
		this.dsConfig = dsConfig;
		this.currentDataSource = dsConfig.getRefDataSource();
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return currentDataSource.getLogWriter();
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		currentDataSource.setLogWriter(out);
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		currentDataSource.setLoginTimeout(seconds);
	}

	public DsConfig getDsConfig() {
		return dsConfig;
	}

	public void setDsConfig(DsConfig dsConfig) {
		this.dsConfig = dsConfig;
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return currentDataSource.getLoginTimeout();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return currentDataSource.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return currentDataSource.isWrapperFor(iface);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return new ConnectionWrapper(currentDataSource.getConnection());
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return new ConnectionWrapper(currentDataSource.getConnection(username, password));
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

}