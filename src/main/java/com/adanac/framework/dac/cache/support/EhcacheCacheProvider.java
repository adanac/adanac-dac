package com.adanac.framework.dac.cache.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * ehcache缓存提供方
 * @author adanac
 * @version 1.0
 */
public class EhcacheCacheProvider extends AbstractCacheProvider implements InitializingBean {

	protected transient Logger logger = LoggerFactory.getLogger(getClass());

	/**缓存配置文件路径   */
	private Resource cacheConfigLocation;

	/** 缓存管理器：根据缓存名称获取缓存*/
	private CacheManager manager;

	/**
	 * 功能描述：从ehcache缓存中获取结果<br>
	 * 输入参数：缓存key值 ， 缓存属性对象<br> 
	 * @param key, cacheProperty
	 * 返回值:  Object类型 <br> 
	 * @return Object
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	@Override
	public Object getCacheData(String key, CacheProperty cacheProperty) {
		Cache cache = manager.getCache(cacheProperty.getCacheName());
		if (cache != null) {
			Element e = manager.getCache(cacheProperty.getCacheName()).get(key);
			if (e == null) {
				logger.debug(" @ Here is Ehcache.getData , but null . And key is [{}] ", new Object[] { key });
				return null;
			} else {
				logger.debug(" @ Here is Ehcache.getData . And key is [{}] ", new Object[] { key });
				return e.getObjectValue();
			}
		} else {
			logger.warn(" @ Result of  the method 'getCache({})' is null when Ehcache.getData . ",
					new Object[] { cacheProperty.getCacheName() });
			return null;
		}
	}

	/**
	 * 功能描述：往ehcache缓存中存储结果<br>
	  * 输入参数：缓存key值 ，存储数据， 缓存属性对象<br> 
	 * @param key,data, cacheProperty
	 * 返回值:  
	 * @return 
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	@Override
	public void setCacheData(String key, Object data, CacheProperty cacheProperty) {
		Cache cache = manager.getCache(cacheProperty.getCacheName());
		if (cache != null) {
			Element element = new Element(key, data);
			if (cacheProperty.getTimeOut() != 0) {
				element.setTimeToLive(cacheProperty.getTimeOut());
			}
			cache.put(element);
			logger.debug(" @ Here is Ehcache.setData . And key is [{}] ", new Object[] { key });
		} else {
			logger.warn(" @ Result of  the method 'getCache({})' is null when Ehcache.setData . ",
					new Object[] { cacheProperty.getCacheName() });
		}
	}

	/**
	 * 功能描述：从ehcache缓存中删除结果<br>
	 * 输入参数：缓存key值 ， 缓存属性对象<br> 
	 * @param key, cacheProperty
	 * 返回值:   <br> 
	 * @return 
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	@Override
	public void removeCacheData(String key, CacheProperty cacheProperty) {
		Cache cache = manager.getCache(cacheProperty.getCacheName());
		if (cache != null) {
			cache.remove(key);
			logger.debug(" @ Here is Ehcache.removeData . And key is [{}] ", new Object[] { key });
		} else {
			logger.warn(" @ Result of  the method 'getCache({})' is null when Ehcache.removeData . ",
					new Object[] { cacheProperty.getCacheName() });
		}
	}

	/**
	 * 功能描述：根据缓存配置文件获取ehcache缓存管理器<br>
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		manager = new CacheManager(cacheConfigLocation.getFile().getAbsolutePath());
	}

	public Resource getCacheConfigLocation() {
		return cacheConfigLocation;
	}

	public void setCacheConfigLocation(Resource cacheConfigLocation) {
		this.cacheConfigLocation = cacheConfigLocation;
	}

}