package com.adanac.framework.dac.client.support.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 简单SQL跟踪器
 * 实现SqlAuditor接口的跟踪方法
 * @author adanac
 * @version 1.0
 */
public class SimpleSqlAuditor implements SqlAuditor {

	private transient final Logger logger = LoggerFactory.getLogger(SimpleSqlAuditor.class);

	/**
	 * 实现sql跟踪器的audit方法，具体是打出告警日志
	 * @param sql
	 * @param 参数
	 * @param sql执行毫秒数
	 */
	public void audit(String sql, Object param, long interval) {
		logger.warn("SQL Statement [{}] with parameter object [{}] ran out of the normal time range, "
				+ "it consumed [{}] milliseconds.", new Object[] { sql, param, interval });
	}

}
