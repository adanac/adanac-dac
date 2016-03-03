package com.adanac.framework.dac.util;

import java.util.Stack;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据源上下文
 * 基于线程局部变量及栈实现
 * @author adanac
 * @version 1.0
 */
@Deprecated
public class DataSourceContext {
	private static Logger logger = LoggerFactory.getLogger(DataSourceContext.class);

	private static ThreadLocal<Stack<DataSource>> transcationContext = new ThreadLocal<Stack<DataSource>>();

	public static DataSource getDataSource() {
		DataSource dataSource = null;

		Stack<DataSource> stack = getStack();
		if (!stack.empty()) {
			dataSource = stack.peek();
		}

		logger.debug("get currentThread datasource : " + dataSource);
		return dataSource;
	}

	public static void pushCurrentDataSource(DataSource dataSource) {
		Stack<DataSource> stack = getStack();
		stack.push(dataSource);
		logger.debug("bind currentThread datasource : " + dataSource);
	}

	public static void popCurrentDataSource() {
		Stack<DataSource> stack = getStack();
		if (!stack.empty()) {
			logger.debug("release currentThread datasource : " + stack.pop());
		}
	}

	private static Stack<DataSource> getStack() {
		if (transcationContext.get() == null) {
			transcationContext.set(new Stack<DataSource>());
		}
		return transcationContext.get();
	}
}
