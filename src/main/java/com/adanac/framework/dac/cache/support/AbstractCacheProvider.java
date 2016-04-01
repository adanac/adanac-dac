package com.adanac.framework.dac.cache.support;

import com.adanac.framework.dac.cache.CacheKeyGenerator;
import com.adanac.framework.dac.cache.CacheProvider;
import com.adanac.framework.dac.exception.CacheException;

/**
 * 抽象缓存提供
 * @author adanac
 * @version 1.0
 */
public abstract class AbstractCacheProvider implements CacheProvider {

	protected CacheKeyGenerator cacheKeyGenerator;

	public AbstractCacheProvider() {
		this.cacheKeyGenerator = new DefaultCacheKeyGenerator();
	}

	protected CacheKeyGenerator getCacheKeyGenerator() {
		return cacheKeyGenerator;
	}

	protected void setCacheKeyGenerator(CacheKeyGenerator cacheKeyGenerator) {
		this.cacheKeyGenerator = cacheKeyGenerator;
	}

	/**
	 * 功能描述：从缓存中获取结果<br>
	 * 先根据传参计算缓存key，再查询<br>
	 * 输入参数：方法名称，方法参数数组，缓存属性对象<按照参数定义顺序> 
	 * @param methodName, params,  cacheProperty
	 * 返回值:  结果数据 <说明> 
	 * @return Object
	 * @throw 
	 * @see 需要参见的其它内容
	 */
	@Override
	public Object getCachedQueryResult(String methodName, Object[] params, CacheProperty cacheProperty) {
		try {
			String cacheKey = cacheKeyGenerator.generateCacheKey(methodName, params);
			return getCacheData(cacheKey, cacheProperty);
		} catch (Exception e) {
			throw new CacheException("Cache exception occoured when getData . Exception :" + e.getMessage(), e);
		}
	}

	/**
	 * 功能描述：往缓存中存储结果<br>
	 * 先根据传参计算缓存key，再存储<br>
	 * 输入参数：方法名称，方法参数数组，存储数据，缓存属性对象<按照参数定义顺序> 
	 * @param methodName, params, cacheData， cacheProperty
	 * 返回值:  <说明> 
	 * @return
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	@Override
	public void setCachedQueryResult(String methodName, Object[] params, Object cacheData,
			CacheProperty cacheProperty) {
		try {
			String cacheKey = cacheKeyGenerator.generateCacheKey(methodName, params);
			setCacheData(cacheKey, cacheData, cacheProperty);
		} catch (Exception e) {
			throw new CacheException("Cache exception occoured when setData . Exception :" + e.getMessage(), e);
		}
	}

	/**
	 * 功能描述：从缓存中删除结果<br>
	 * 先根据传参计算缓存key，再删除<br>
	 * 输入参数：方法名称，方法参数数组，缓存属性对象<按照参数定义顺序> 
	 * @param methodName, params,  cacheProperty
	 * 返回值:  <说明> 
	 * @return 
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	@Override
	public void removeCachedResult(String methodName, Object[] params, CacheProperty cacheProperty) {
		try {
			String cacheKey = cacheKeyGenerator.generateCacheKey(methodName, params);
			removeCacheData(cacheKey, cacheProperty);
		} catch (Exception e) {
			throw new CacheException("Cache exception occoured when removeData  . Exception :" + e.getMessage(), e);
		}
	}

	/**
	 * 功能描述：抽象的读取缓存方法<br>
	 * 输入参数：缓存key值 ， 缓存属性对象<br> 
	 * @param key, cacheProperty
	 * 返回值:  Object类型 <br> 
	 * @return Object
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	public abstract Object getCacheData(String key, CacheProperty cacheProperty);

	/**
	 * 功能描述：抽象的存储缓存方法<br>
	 * 输入参数：缓存key值 ，存储数据， 缓存属性对象<br> 
	 * @param key,data, cacheProperty
	 * 返回值:  
	 * @return 
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	public abstract void setCacheData(String key, Object data, CacheProperty cacheProperty);

	/**
	 * 功能描述：抽象的清理缓存方法<br>
	 * 输入参数：缓存key值 ， 缓存属性对象<br> 
	 * @param key, cacheProperty
	 * 返回值:   <br> 
	 * @return 
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	public abstract void removeCacheData(String key, CacheProperty cacheProperty);

}
