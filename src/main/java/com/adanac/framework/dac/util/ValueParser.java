package com.adanac.framework.dac.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

/**
 * 
 * 对象解析
 */
public class ValueParser {
	private static Logger logger = LoggerFactory.getLogger(ValueParser.class);

	/**
	 * 功能描述：解析方法<br>
	 * 根据实体类中的Column注解转为map 输入参数：Object类型<按照参数定义顺序>
	 * 
	 * @param entity
	 *            返回值: map类型 <说明>
	 * @return Map<String, Object>
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	public static Map<String, Object> parser(Object entity) {
		Map<String, Object> values = new HashMap<String, Object>();
		Method[] methods = entity.getClass().getMethods();
		for (Method method : methods) {
			if (method.isAnnotationPresent(Column.class)) {
				Column column = method.getAnnotation(Column.class);
				PropertyDescriptor descriptor = BeanUtils.findPropertyForMethod(method);
				String key = descriptor.getName();
				Object value = null;
				try {
					value = method.invoke(entity, new Object[] {});
					if (value instanceof java.util.Date) {
						value = dateFormat(column, (Date) value);
					}
				} catch (Exception e) {
					logger.debug("reflect error.[" + method + "]", e);
				}
				values.put(key, value);
			}
		}

		return values;
	}

	/**
	 * 功能描述：日期类型转换 输入参数：字段注解，日期类型<按照参数定义顺序>
	 * 
	 * @param column，date
	 *            返回值: Object类型 <说明>
	 * @return Object
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	private static Object dateFormat(Column column, Date date) {
		if (date != null && !"".equals(column.columnDefinition())) {
			SimpleDateFormat format = new SimpleDateFormat(column.columnDefinition());
			return format.format(date);
		}
		return date;
	}
}