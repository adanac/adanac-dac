package com.adanac.framework.dac.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adanac.framework.dac.cache.CacheKeyParam;

/**
 * 
 * 
 * 功能描述：DAL工具类
 * 
 * @author
 * @version 1.0.0
 */
public class DacUtils {
	private static Logger logger = LoggerFactory.getLogger(DacUtils.class);

	private static final int NUM0 = 0;
	private static final int NUM1 = 1;
	private static final int NUM2 = 2;
	private static final int NUM3 = 3;
	private static final int NUM4 = 4;
	private static final int NUM8 = 8;
	private static final int NUM16 = 16;
	private static final int NUM24 = 24;
	private static final int NUM_0XFF = 0xFF;

	/**
	 * 功能描述：把参数转成对象数组 输入参数：Object类型
	 * 
	 * @param parameter
	 *            返回值: Object数组 <说明>
	 * @return Object[]
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	public static Object[] convertToObjectArray(Object parameter) {
		Object[] retObject = null;
		if (parameter instanceof Object[]) {
			retObject = (Object[]) parameter;
		} else {
			retObject = new Object[] { parameter };
		}
		return retObject;
	}

	/**
	 * 
	 * 功能描述：把对象转成Map集合 输入参数：Object类型<按照参数定义顺序>
	 * 
	 * @param arg
	 *            返回值: Map类型 <说明>
	 * @return Map<String, Object>
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> convertToMap(Object arg) {
		try {
			if (arg instanceof Map) {
				return (Map<String, Object>) arg;
			}
			Map<String, Object> returnMap = BeanUtils.describe(arg);
			returnMap.remove("class");
			return returnMap;
		} catch (IllegalAccessException e) {
			logger.warn(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			logger.warn(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			logger.warn(e.getMessage(), e);
		}
		return new HashMap<String, Object>();

	}

	/**
	 * 功能描述：根据执行的DalClient方法名和方法参数数组计算出缓存key 输入参数：String类型，Object数组类型<按照参数定义顺序>
	 * 
	 * @param methodName
	 *            ,params 返回值: String类型 <说明>
	 * @return String
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	public static String getCacheKey(String methodName, Object[] params) {
		if (params.length < 2) {// 因为用传参数组就需要前两个元素，所以传参数组大小不能小于2
			logger.warn("The length of array param must not less than 2 . ");
			return null;
		}
		Object[] paramArg = new Object[2];
		if (methodName.equals("find")) {
			paramArg[0] = ((Class<?>) params[0]).getName() + ".select";
			paramArg[1] = params[1];
		} else if (methodName.startsWith("queryFor")) {
			paramArg[0] = params[0];
			paramArg[1] = convertToMap(params[1]);
		} else {
			logger.warn("The methodName must being 'find' or start with 'queryFor' .");
			return null;
		}
		int hashCode = HashCodeBuilder.reflectionHashCode(new CacheKeyParam(methodName, paramArg));
		return DigestUtils.md5Hex(String.valueOf(hashCode));
	}

	public static Map<String, Object> mapIfNull(Map<String, Object> map) {
		if (map == null) {
			return new HashMap<String, Object>();
		}
		return map;
	}

	public static void setProperty(Object targetObject, String propertyName, Object propertyValue) {
		try {
			Field field = findField(targetObject.getClass(), propertyName);
			if (field == null) {
				throw new IllegalArgumentException(propertyName + " can not be found.");
			}
			field.setAccessible(true);
			if (propertyValue instanceof BigDecimal) {
				if (field.getType().isAssignableFrom(Integer.class) || field.getType().isAssignableFrom(int.class)) {
					field.set(targetObject, ((BigDecimal) propertyValue).intValue());
				}
				if (field.getType().isAssignableFrom(Long.class) || field.getType().isAssignableFrom(long.class)) {
					field.set(targetObject, ((BigDecimal) propertyValue).longValue());
				}
				if (field.getType().isAssignableFrom(Double.class) || field.getType().isAssignableFrom(double.class)) {
					field.set(targetObject, ((BigDecimal) propertyValue).doubleValue());
				}
				if (field.getType().isAssignableFrom(Float.class) || field.getType().isAssignableFrom(float.class)) {
					field.set(targetObject, ((BigDecimal) propertyValue).floatValue());
				}
				if (field.getType().isAssignableFrom(Byte.class) || field.getType().isAssignableFrom(byte.class)) {
					field.set(targetObject, ((BigDecimal) propertyValue).byteValue());
				}
			} else {
				field.set(targetObject, propertyValue);
			}
		} catch (SecurityException e) {
			logger.warn(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			logger.warn(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			logger.warn(e.getMessage(), e);
		}
	}

	private static Field findField(Class<?> clazz, String name) {
		if (clazz == null) {
			throw new IllegalArgumentException("Class must not be null");
		}
		if (name == null) {
			throw new IllegalArgumentException("field must not be null");
		}
		Class<?> searchType = clazz;
		while (!Object.class.equals(searchType) && searchType != null) {
			Field[] fields = searchType.getDeclaredFields();
			for (Field field : fields) {
				if (name.equals(field.getName())) {
					return field;
				}
			}
			searchType = searchType.getSuperclass();
		}
		return null;
	}

	public static byte[] computeMd5(String k) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.reset();
			md5.update(k.getBytes("UTF-8"));
			return md5.digest();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("MD5 not supported", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unknown string :" + k, e);
		}
	}

	/**
	 * 描述：hash算法
	 * 
	 * @author 作者 13092011
	 */
	public static enum HashAlgorithm {
		/**
		 * MD5-based hash algorithm used by ketama.
		 */
		KETAMA_HASH;
		public long hash(byte[] digest, int nTime) {
			long rv = ((long) (digest[NUM3 + nTime * NUM4] & NUM_0XFF) << NUM24)
					| ((long) (digest[NUM2 + nTime * NUM4] & NUM_0XFF) << NUM16)
					| ((long) (digest[NUM1 + nTime * NUM4] & NUM_0XFF) << NUM8)
					| (digest[NUM0 + nTime * NUM4] & NUM_0XFF);

			return rv & 0xffffffffL; /* Truncate to 32-bits */
		}
	}

}
