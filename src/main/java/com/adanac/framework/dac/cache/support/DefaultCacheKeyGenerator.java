package com.adanac.framework.dac.cache.support;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.adanac.framework.dac.cache.CacheKeyGenerator;
import com.adanac.framework.dac.cache.CacheKeyParam;

/*8
 * 默认缓存key的算法
 */
public class DefaultCacheKeyGenerator implements CacheKeyGenerator {

	/**
	 * 功能描述：计算缓存key
	 * 输入参数：方法名称，方法参数数组<按照参数定义顺序> 
	 * @param methodName,params
	 * 返回值:  缓存key值 <说明> 
	 * @return String
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	@Override
	public String generateCacheKey(String methodName, Object[] params) {
		int hashCode = HashCodeBuilder.reflectionHashCode(new CacheKeyParam(methodName, params));
		return DigestUtils.md5Hex(String.valueOf(hashCode));
	}

}