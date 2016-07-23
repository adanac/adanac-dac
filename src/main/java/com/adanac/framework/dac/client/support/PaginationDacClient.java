package com.adanac.framework.dac.client.support;

import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

import com.adanac.framework.dac.client.IPaginationDacClient;
import com.adanac.framework.dac.client.support.executor.DBType;
import com.adanac.framework.dac.client.support.executor.PaginationSqlExecutor;
import com.adanac.framework.dac.pagination.DB2Dialect;
import com.adanac.framework.dac.pagination.Dialect;
import com.adanac.framework.dac.pagination.MySQLDialect;
import com.adanac.framework.dac.pagination.OracleDialect;
import com.adanac.framework.dac.pagination.PaginationResult;

/**
 * 功能描述： 带分页的客户端
 * @author adanac
 * @version 1.0
 */
public class PaginationDacClient extends DefaultDacClient implements IPaginationDacClient {
	private Dialect dialect;

	private PaginationSqlExecutor paginationSqlExecutor;

	public Dialect getDialect() {
		return dialect;
	}

	public void setDialect(Dialect dialect) {
		this.dialect = dialect;
	}

	public void setDialect(String dialect) {
		if (dialect == null) {
			throw new IllegalArgumentException();
		}
		dialect = dialect.trim();
		if (dialect.length() == 0) {
			throw new IllegalArgumentException();
		}
		Class<?> clazz = null;
		try {
			clazz = Class.forName(dialect);
			this.dialect = (Dialect) clazz.newInstance();
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("ClassNotFoundException " + dialect);
		} catch (Exception e) {
			throw new IllegalArgumentException("create dialect error" + dialect);
		}

	}

	public PaginationResult<Map<String, Object>> queryForList(String sqlId, Object param, int offset, int limit) {
		return paginationSqlExecutor.queryForList(sqlId, param, offset, limit);
	}

	public PaginationResult<Map<String, Object>> queryForList(String sqlId, Map<String, Object> paramMap, int offset,
			int limit) {
		return paginationSqlExecutor.queryForList(sqlId, paramMap, offset, limit);
	}

	public <T> PaginationResult<T> queryForList(String sqlId, Map<String, Object> paramMap, Class<T> requiredType,
			int offset, int limit) {
		return paginationSqlExecutor.queryForList(sqlId, paramMap, requiredType, offset, limit);
	}

	public <T> PaginationResult<T> queryForList(String sqlId, Object param, Class<T> requiredType, int offset,
			int limit) {
		return paginationSqlExecutor.queryForList(sqlId, param, requiredType, offset, limit);
	}

	public <T> PaginationResult<T> queryForList(String sqlId, Object param, RowMapper<T> rowMapper, int offset,
			int limit) {
		return paginationSqlExecutor.queryForList(sqlId, param, rowMapper, offset, limit);
	}

	public <T> PaginationResult<T> queryForList(String sqlId, Map<String, Object> paramMap, RowMapper<T> rowMapper,
			int offset, int limit) {
		return paginationSqlExecutor.queryForList(sqlId, paramMap, rowMapper, offset, limit);
	}

	/**
	 * 描述：在bean初始化时执行
	 */
	public void afterPropertiesSet() throws Exception {

		Assert.notNull(dataSource, "Property 'dataSource' is required");

		if (isProfileLongTimeRunningSql()) {
			Assert.isTrue(longTimeRunningSqlIntervalThreshold > 0,
					"'longTimeRunningSqlIntervalThreshold' should have a positive value "
							+ "if 'profileLongTimeRunningSql' is set to true");
		}

		// 生成SqlMap
		buildSqlMap();

		// DBType dbType = DatabaseTypeProvider.getDatabaseType(dataSource);
		// logger.debug("this 'dataSource' database type is " + dbType);
		PaginationSqlExecutor mappedSqlExecutor = new PaginationSqlExecutor();
		mappedSqlExecutor.setConfiguration(configuration);
		mappedSqlExecutor.setDataSource(dataSource);
		// mappedSqlExecutor.setDbType(dbType);
		mappedSqlExecutor.setSqlAuditor(sqlAuditor);
		mappedSqlExecutor.setProfileLongTimeRunningSql(isProfileLongTimeRunningSql());
		mappedSqlExecutor.setLongTimeRunningSqlIntervalThreshold(longTimeRunningSqlIntervalThreshold);
		DBType dbType = mappedSqlExecutor.getDbType();
		if (this.dialect == null && dbType != null) {
			switch (dbType) {
			case MYSQL:
				mappedSqlExecutor.setDialect(new MySQLDialect());
				break;
			case DB2:
				mappedSqlExecutor.setDialect(new DB2Dialect());
				break;
			case ORACLE:
				mappedSqlExecutor.setDialect(new OracleDialect());
				break;
			}
		} else {
			mappedSqlExecutor.setDialect(this.dialect);
		}
		this.mappedSqlExecutor = mappedSqlExecutor;
		this.paginationSqlExecutor = mappedSqlExecutor;

	}
}
