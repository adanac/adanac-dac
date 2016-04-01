package com.adanac.framework.rws.wrapper;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class ConnectionWrapper implements Connection {

	private Connection currentConnection;

	public ConnectionWrapper(Connection currentConnection) {
		super();
		this.currentConnection = currentConnection;
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return currentConnection.unwrap(iface);
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return currentConnection.isWrapperFor(iface);
	}

	public Statement createStatement() throws SQLException {
		return currentConnection.createStatement();
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return new PreparedStatementWrapper(currentConnection.prepareStatement(sql));
	}

	public CallableStatement prepareCall(String sql) throws SQLException {
		return currentConnection.prepareCall(sql);
	}

	public String nativeSQL(String sql) throws SQLException {
		return currentConnection.nativeSQL(sql);
	}

	public void setAutoCommit(boolean autoCommit) throws SQLException {
		currentConnection.setAutoCommit(autoCommit);
	}

	public boolean getAutoCommit() throws SQLException {
		return currentConnection.getAutoCommit();
	}

	public void commit() throws SQLException {
		currentConnection.commit();
	}

	public void rollback() throws SQLException {
		currentConnection.rollback();
	}

	public void close() throws SQLException {
		currentConnection.close();
	}

	public boolean isClosed() throws SQLException {
		return currentConnection.isClosed();
	}

	public DatabaseMetaData getMetaData() throws SQLException {
		return currentConnection.getMetaData();
	}

	public void setReadOnly(boolean readOnly) throws SQLException {
		currentConnection.setReadOnly(readOnly);
	}

	public boolean isReadOnly() throws SQLException {
		return currentConnection.isReadOnly();
	}

	public void setCatalog(String catalog) throws SQLException {
		currentConnection.setCatalog(catalog);
	}

	public String getCatalog() throws SQLException {
		return currentConnection.getCatalog();
	}

	public void setTransactionIsolation(int level) throws SQLException {
		currentConnection.setTransactionIsolation(level);
	}

	public int getTransactionIsolation() throws SQLException {
		return currentConnection.getTransactionIsolation();
	}

	public SQLWarning getWarnings() throws SQLException {
		return currentConnection.getWarnings();
	}

	public void clearWarnings() throws SQLException {
		currentConnection.clearWarnings();
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		return currentConnection.createStatement(resultSetType, resultSetConcurrency);
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		return new PreparedStatementWrapper(
				currentConnection.prepareStatement(sql, resultSetType, resultSetConcurrency));
	}

	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return currentConnection.prepareCall(sql);
	}

	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return currentConnection.getTypeMap();
	}

	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		currentConnection.setTypeMap(map);
	}

	public void setHoldability(int holdability) throws SQLException {
		currentConnection.setHoldability(holdability);
	}

	public int getHoldability() throws SQLException {
		return currentConnection.getHoldability();
	}

	public Savepoint setSavepoint() throws SQLException {
		return currentConnection.setSavepoint();
	}

	public Savepoint setSavepoint(String name) throws SQLException {
		return currentConnection.setSavepoint(name);
	}

	public void rollback(Savepoint savepoint) throws SQLException {
		currentConnection.rollback(savepoint);
	}

	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		currentConnection.releaseSavepoint(savepoint);
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return currentConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		return new PreparedStatementWrapper(
				currentConnection.prepareStatement(sql, resultSetType, resultSetConcurrency));
	}

	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		return currentConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		return new PreparedStatementWrapper(currentConnection.prepareStatement(sql, autoGeneratedKeys));
	}

	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		return new PreparedStatementWrapper(currentConnection.prepareStatement(sql, columnIndexes));
	}

	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		return new PreparedStatementWrapper(currentConnection.prepareStatement(sql, columnNames));
	}

	public Clob createClob() throws SQLException {
		return currentConnection.createClob();
	}

	public Blob createBlob() throws SQLException {
		return currentConnection.createBlob();
	}

	public NClob createNClob() throws SQLException {
		return currentConnection.createNClob();
	}

	public SQLXML createSQLXML() throws SQLException {
		return currentConnection.createSQLXML();
	}

	public boolean isValid(int timeout) throws SQLException {
		return currentConnection.isValid(timeout);
	}

	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		currentConnection.setClientInfo(name, value);
	}

	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		currentConnection.setClientInfo(properties);
	}

	public String getClientInfo(String name) throws SQLException {
		return currentConnection.getClientInfo(name);
	}

	public Properties getClientInfo() throws SQLException {
		return currentConnection.getClientInfo();
	}

	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		return currentConnection.createArrayOf(typeName, elements);
	}

	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		return currentConnection.createStruct(typeName, attributes);
	}

	@Override
	public void setSchema(String schema) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getSchema() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void abort(Executor executor) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

}