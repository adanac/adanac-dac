package com.adanac.framework.dac.cache.support;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.adanac.framework.cache.redis.client.ShardedBinaryClient;
import com.adanac.framework.cache.redis.client.impl.MyShardedBinaryClient;

/**
 * redis缓存提供方
 * @author adanac
 * @version 1.0
 */
public class JedisCacheProvider extends AbstractCacheProvider implements InitializingBean {

	protected transient Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 缓存文件路径
	 */
	private String cacheConfigLocation;

	/**
	 * 采用二进制redis客户端
	 */
	// private ISNBinaryRedisClient redisClient;
	private ShardedBinaryClient redisClient;

	/**
	 * 功能描述：从redis缓存中查询结果<br>
	 * 输入参数：缓存key值 ， 缓存属性对象<br> 
	 * @param key, cacheProperty
	 * 返回值:  Object类型 <br> 
	 * @return Object
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	@Override
	public Object getCacheData(String key, CacheProperty cacheProperty) {
		// 添加命名空间作为前缀
		String cacheKey = cacheProperty.getRedisNamespace() + key;
		Object object = redisClient.get(cacheKey);
		if (object == null) {
			logger.debug(" @ Here is RedisClient.getData , but null. And key is [{}] ", new Object[] { cacheKey });
		} else {
			logger.debug(" @ Here is RedisClient.getData . And key is [{}] ", new Object[] { cacheKey });
		}
		return object;
	}

	/**
	 * 功能描述：往redis缓存中存储结果<br>
	  * 输入参数：缓存key值 ，存储数据， 缓存属性对象<br> 
	 * @param key,data, cacheProperty
	 * 返回值:  
	 * @return 
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	@Override
	public void setCacheData(String key, Object data, CacheProperty cacheProperty) {
		// 添加命名空间以作为前缀
		String cacheKey = cacheProperty.getRedisNamespace() + key;
		if (cacheProperty.getTimeOut() != 0) {
			// 如果配置timeOut属性，调用setex方法
			redisClient.setex(cacheKey, cacheProperty.getTimeOut(), (Serializable) data);
		} else {
			redisClient.set(cacheKey, (Serializable) data);
		}
		logger.debug(" @ Here is RedisClient.setData . And key is [{}] ", new Object[] { cacheKey });
	}

	/**
	 * 功能描述：从redis缓存中删除结果<br>
	 * 输入参数：缓存key值 ， 缓存属性对象<br> 
	 * @param key, cacheProperty
	 * 返回值:   <br> 
	 * @return 
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	@Override
	public void removeCacheData(String key, CacheProperty cacheProperty) {
		// 添加命名空间作为前缀
		String cacheKey = cacheProperty.getRedisNamespace() + key;
		redisClient.del(cacheKey);
		logger.debug(" @ Here is RedisClient.removeData . And key is [{}] ", new Object[] { cacheKey });
	}

	/**
	 * 功能描述：实例化二进制redis客户端<br>
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		redisClient = new MyShardedBinaryClient(
				cacheConfigLocation.contains(":") ? cacheConfigLocation.split(":")[1] : cacheConfigLocation);
	}

	public String getCacheConfigLocation() {
		return cacheConfigLocation;
	}

	public void setCacheConfigLocation(String cacheConfigLocation) {
		this.cacheConfigLocation = cacheConfigLocation;
	}

}
