package com.adanac.framework.dac.cache;

public interface CacheKeyGenerator {
	/**
	 * 功能描述：计算缓存key
	 * 输入参数：方法名称，方法参数数组<按照参数定义顺序> 
	 * @param methodName,params
	 * 返回值:  缓存key值 <说明> 
	 * @return String
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	String generateCacheKey(String methodName, Object[] params);
}
