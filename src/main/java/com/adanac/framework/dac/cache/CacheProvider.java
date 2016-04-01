package com.adanac.framework.dac.cache;

import com.adanac.framework.dac.cache.support.CacheProperty;

public interface CacheProvider {
	/** 从缓存中获取数据 */
	Object getCachedQueryResult(String methodName, Object[] params, CacheProperty cacheProperty);

	/** 往缓存中存储数据 */
	void setCachedQueryResult(String methodName, Object[] params, Object cacheData, CacheProperty cacheProperty);

	/** 从缓存中删除数据 */
	void removeCachedResult(String methodName, Object[] params, CacheProperty cacheProperty);
}
