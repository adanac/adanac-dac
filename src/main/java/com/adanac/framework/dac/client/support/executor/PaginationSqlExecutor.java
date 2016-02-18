package com.adanac.framework.dac.client.support.executor;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import com.adanac.framework.dac.client.support.MappedStatement;
import com.adanac.framework.dac.client.support.rowmapper.RowMapperFactory;
import com.adanac.framework.dac.pagination.Dialect;
import com.adanac.framework.dac.pagination.PaginationResult;
import com.adanac.framework.dac.util.DacUtils;

public class PaginationSqlExecutor extends MappedSqlExecutor {

	private static Logger logger = LoggerFactory.getLogger(PaginationSqlExecutor.class);

	private Dialect dialect;

	public Dialect getDialect() {
		return dialect;
	}

	public void setDialect(Dialect dialect) {
		this.dialect = dialect;
	}

	/**
	 * 功能描述：根据sql查询list，其中list中元素类型自由指定<br>
	 * 输入参数：sqlId，实体对象，实体类型
	 * @param sqlId,param，requiredType
	 * @return List<T> PaginationResult类型，元素为实体类型 
	 */
	public <T> PaginationResult<T> queryForList(String sqlId, Object param, Class<T> requiredType, int offset,
			int limit) {
		return queryForList(sqlId, DacUtils.convertToMap(param), requiredType, offset, limit);
	}

	/**
	 * 功能描述：根据sql查询list，其中list中元素类型自由指定<br>
	 * 输入参数：sqlId，map类型的参数，实体类型
	 * @return PaginationResult<T> list类型，元素为实体类型
	 */
	public <T> PaginationResult<T> queryForList(String sqlId, Map<String, Object> paramMap, Class<T> requiredType,
			int offset, int limit) {
		return this.queryForList(sqlId, paramMap, new RowMapperFactory<T>(requiredType).getRowMapper(), offset, limit);
	}

	/**
	 * 功能描述：根据sql查询list，其中list中元素类型自由指定
	 * 输入参数：sqlId，实体对象，实体映射
	 * @return PaginationResult<T>
	 */
	public <T> PaginationResult<T> queryForList(String sqlId, Object param, RowMapper<T> rowMapper, int offset,
			int limit) {
		return queryForList(sqlId, DacUtils.convertToMap(param), rowMapper, offset, limit);
	}

	/**
	 * 功能描述：根据sql查询list，其中list中元素类型自由指定<br>
	 * 输入参数：sqlId，map类型的参数，实体映射
	 * @return List<T> list类型，元素为实体类型
	 */
	public <T> PaginationResult<T> queryForList(String sqlId, Map<String, Object> paramMap, RowMapper<T> rowMapper,
			int offset, int limit) {

		MappedStatement stmt = configuration.getMappedStatement(sqlId, true);
		String originalSql = stmt.getBoundSql(paramMap);
		String dataSql = this.dialect.getLimitSqlString(originalSql, offset, limit);
		String countSql = this.dialect.getCountSqlString(originalSql);

		String tracerService = getTrackerServiceName();
		// TraceContext traceContext = getTraceContext(tracerService);
		long startTimestamp = System.currentTimeMillis();
		this.applyStatementSettings(stmt);
		List<T> list = null;
		int count = 0;
		try {
			logMessage("queryForList(3 paramter)", dataSql, paramMap);
			list = execution.query(dataSql, DacUtils.mapIfNull(paramMap), rowMapper);
			logMessage("queryForList(3 paramter)", dataSql, paramMap);
		} catch (Exception e) {
			// Tracer.clientAcceptWithError(traceContext, e.getMessage());
			throwException(e);
			return null;// never return
		} finally {
			if (isProfileLongTimeRunningSql()) {
				long interval = System.currentTimeMillis() - startTimestamp;
				if (interval > getLongTimeRunningSqlIntervalThreshold()) {
					logger.warn(SQL_AUDIT_LOGMESSAGE, new Object[] { sqlId, paramMap, interval });
					executeSqlAuditorIfNecessary(dataSql, paramMap, interval);
				}
			}
		}
		try {
			logMessage("queryForCount(3 paramter)", countSql, paramMap);
			count = execution.queryForObject(countSql, paramMap, Integer.class);
			logMessage("queryForCount(3 paramter)", countSql, paramMap);
		} catch (Exception e) {
			// Tracer.clientAcceptWithError(traceContext, e.getMessage());
			throwException(e);
			return null;// never return
		} finally {
			if (isProfileLongTimeRunningSql()) {
				long interval = System.currentTimeMillis() - startTimestamp;
				if (interval > getLongTimeRunningSqlIntervalThreshold()) {
					logger.warn(SQL_AUDIT_LOGMESSAGE, new Object[] { sqlId, paramMap, interval });
					executeSqlAuditorIfNecessary(countSql, paramMap, interval);
				}
			}
		}
		// Tracer.clientAccept(traceContext);
		return new PaginationResult<T>(list, count);

	}

	/**
	 * 功能描述：根据sql查询list，其中list中元素为map类型<br>
	 * 输入参数：sqlId，实体对象
	 * @return List<Map<String, Object>>
	 */
	public PaginationResult<Map<String, Object>> queryForList(String sqlId, Object param, int offset, int limit) {
		return queryForList(sqlId, DacUtils.convertToMap(param), offset, limit);
	}

	/**
	 * 功能描述：根据sql查询list，其中list中元素为map类型<br>
	 * 输入参数：sqlId，map类型的参数
	 * @return List<Map<String, Object>>
	 */
	public PaginationResult<Map<String, Object>> queryForList(String sqlId, Map<String, Object> paramMap, int offset,
			int limit) {
		// processTableRoute(paramMap);
		MappedStatement stmt = configuration.getMappedStatement(sqlId, true);
		String originalSql = stmt.getBoundSql(paramMap);
		String dataSql = this.dialect.getLimitSqlString(originalSql, offset, limit);
		String countSql = this.dialect.getCountSqlString(originalSql);

		String tracerService = getTrackerServiceName();
		// TraceContext traceContext = getTraceContext(tracerService);
		long startTimestamp = System.currentTimeMillis();
		this.applyStatementSettings(stmt);
		List<Map<String, Object>> list = null;
		int count = 0;
		try {
			logMessage("queryForList(2 paramter)", dataSql, paramMap);
			list = execution.queryForList(dataSql, DacUtils.mapIfNull(paramMap));
			logMessage("queryForList(2 paramter)", dataSql, paramMap);
		} catch (Exception e) {
			// Tracer.clientAcceptWithError(traceContext, e.getMessage());
			throwException(e);
			return null;// never return
		} finally {
			if (isProfileLongTimeRunningSql()) {
				long interval = System.currentTimeMillis() - startTimestamp;
				if (interval > getLongTimeRunningSqlIntervalThreshold()) {
					logger.warn(SQL_AUDIT_LOGMESSAGE, new Object[] { sqlId, paramMap, interval });
					executeSqlAuditorIfNecessary(dataSql, paramMap, interval);
				}
			}
		}
		startTimestamp = System.currentTimeMillis();
		try {
			logMessage("queryForCount(2 paramter)", countSql, paramMap);
			count = execution.queryForObject(countSql, paramMap, Integer.class);
			logMessage("queryForCount(2 paramter)", countSql, paramMap);
		} catch (Exception e) {
			// Tracer.clientAcceptWithError(traceContext, e.getMessage());
			throwException(e);
			return null;// never return
		} finally {
			if (isProfileLongTimeRunningSql()) {
				long interval = System.currentTimeMillis() - startTimestamp;
				if (interval > getLongTimeRunningSqlIntervalThreshold()) {
					logger.warn(SQL_AUDIT_LOGMESSAGE, new Object[] { sqlId, paramMap, interval });
					executeSqlAuditorIfNecessary(countSql, paramMap, interval);
				}
			}
		}
		// Tracer.clientAccept(traceContext);
		return new PaginationResult<Map<String, Object>>(list, count);
	}
}
