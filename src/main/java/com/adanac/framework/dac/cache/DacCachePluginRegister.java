package com.adanac.framework.dac.cache;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import com.adanac.framework.dac.cache.support.CacheConfigurationUtil;
import com.adanac.framework.dac.cache.support.CacheProperty;
import com.adanac.framework.dac.client.support.Configuration;
import com.adanac.framework.dac.client.support.DefaultDacClient;
import com.adanac.framework.dac.exception.CacheException;
import com.adanac.framework.dac.parsing.exception.ParsingException;
import com.adanac.framework.dac.parsing.xml.XmlCacheBuilder;
import com.adanac.framework.dac.util.DacUtils;

public class DacCachePluginRegister implements BeanPostProcessor, InitializingBean {

	protected transient Logger logger = LoggerFactory.getLogger(getClass());

	/** dal缓存配置路径 */
	protected Resource[] dalCacheConfig;

	/** 绑定缓存的dal客户端名称 */
	protected String dalClientBeanName;

	/** 缓存提供方 */
	private CacheProvider cacheProvider;

	private Configuration configuration;

	public Resource[] getDalCacheConfig() {
		return dalCacheConfig;
	}

	public void setDalCacheConfig(Resource[] dalCacheConfig) {
		this.dalCacheConfig = dalCacheConfig;
	}

	public String getDalClientBeanName() {
		return dalClientBeanName;
	}

	public void setDalClientBeanName(String dalClientBeanName) {
		this.dalClientBeanName = dalClientBeanName;
	}

	public CacheProvider getCacheProvider() {
		return cacheProvider;
	}

	public void setCacheProvider(CacheProvider cacheProvider) {
		this.cacheProvider = cacheProvider;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

		if (!beanName.equals(this.dalClientBeanName) || !(bean instanceof DefaultDacClient)) {
			return bean;
		}

		// Object dalClientBean = applicationContext.getBean(dalClientBeanName);
		Assert.notNull(bean, "Can't look up the DelClient by beanName " + dalClientBeanName + ".");

		// Assert.isTrue(bean instanceof DefaultDalClient, "The bean " +
		// dalClientBeanName
		// + " type is " + bean.getClass().getName() + ", it not type of "
		// + DefaultDalClient.class.getName());

		DefaultDacClient dalClient = (DefaultDacClient) bean;
		this.configuration = dalClient.getConfiguration();

		buildCacheCofiguration();

		ProxyFactory pf = new ProxyFactory();
		// pf.setInterfaces(new Class[] { DalClient.class });//不设置该属性，则Cglib动态代理
		pf.setTarget(bean);
		pf.addAdvice(new DalCachePlugin());
		return (DefaultDacClient) pf.getProxy();

	}

	/**
	 * 功能描述：验证属性是否都已配置
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(dalClientBeanName, "Property 'dalClientBeanName' is required");
		Assert.notNull(dalCacheConfig, "Property 'cacheConfigLocation' is required");
		Assert.notNull(cacheProvider, "Property 'cacheProvider' not be null.");
	}

	/**
	 * 功能描述：解析dal缓存配置文件
	 * @param 参数说明
	 * @return 返回值
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	private void buildCacheCofiguration() {
		for (Resource resource : dalCacheConfig) {
			try {
				// 解析dal缓存配置文件，并放到configuration对象中
				new XmlCacheBuilder(resource.getInputStream(), configuration, resource.getFilename()).parse();
			} catch (ParsingException e) {
				logger.error("Error occurred.  Cause: " + e, e);
				throw e;
			} catch (IOException e) {
				throw new ParsingException("Can't parser cacheConfig file " + resource.getFilename(), e);
			}
		}
	}

	/**
	 * 功能描述：根据sqlId到configuration对象中查找对应的cacheProperty缓存属性
	 * 输入参数：sqlId<按照参数定义顺序> 
	 * @param sqlId
	 * 返回值:  缓存属性 <说明> 
	 * @return cacheProperty
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	private CacheProperty lookupCacheProperties(String sqlId) {
		// 先根据sqlId查找
		CacheProperty cacheProperty = CacheConfigurationUtil.getStatementCacheProperties(configuration, sqlId);
		if (cacheProperty == null) {
			// 如果没查找到，再根据namespace查找
			cacheProperty = CacheConfigurationUtil.getNamespaceCacheProperties(configuration,
					configuration.extractNamespace(sqlId));
		}
		return cacheProperty;
	}

	/**
	 * 功能描述：dal缓存插件类<br>
	 * 继承MethodInterceptor
	 * @author 作者 13092011
	 */
	private class DalCachePlugin implements MethodInterceptor {

		/**
		 * 描述：拦截所有dalClient的数据操作方法 。<br>
		 * 先过滤掉非查询操作 , 剩下的查询操作再一一根据sqlId判断是否走缓存 
		 */
		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			Method method = invocation.getMethod();
			Object[] params = invocation.getArguments();
			String sqlId = null;
			if (method.getName().startsWith("queryFor")) {
				sqlId = (String) params[0];
			} else if (method.getName().equals("find")) {
				sqlId = ((Class<?>) params[0]).getName() + ".select";
			} else {
				return invocation.proceed();
			}
			Object result = null;
			try {
				CacheProperty cacheProperty = lookupCacheProperties(sqlId);
				if (cacheProperty != null) {
					// 如果cacheProperty存在，则开始走缓存
					Object[] adjustParams = parseParamArray(method.getName(), params);
					// 先从缓存中获取数据
					result = cacheProvider.getCachedQueryResult(method.getName(), adjustParams, cacheProperty);
					if (result == null) {
						// 获取不到，则调用方法（DB中查询）
						result = invocation.proceed();
						// 再将结果放入缓存
						cacheProvider.setCachedQueryResult(method.getName(), adjustParams, result, cacheProperty);
					} else if (logger.isErrorEnabled()) {
						logger.debug(
								"execute the method {} with sqlId:[{}] param:[{}] fetch query result data from cached.",
								new Object[] { method.toGenericString(), sqlId, params[1] });
					}
				} else {
					// 如果cacheProperty不存在，则表示没有走缓存，执行方法
					logger.debug("The method {} with sqlId:[{}] without bring cache controller.",
							new Object[] { method.toGenericString(), sqlId });
					result = invocation.proceed();
				}
				return result;
			} catch (CacheException e) {
				// 只拦截缓存异常
				logger.warn(
						"execute the method {} with sqlId:[{}] param:[{}] try to fetch query result from cached "
								+ "occured exception [{}].",
						new Object[] { method.toGenericString(), sqlId, params[1], e });
				return invocation.proceed();
			}
		}
	}

	/**
	 * 功能描述：将方法参数数组转为最终计算的参数数组<br>
	 *  1、只取前两个参数；
	 *  2、第一参数为sqlId，第二参数为map对象。
	 * 输入参数：方法名，方法参数数组<按照参数定义顺序> 
	 * @param methodName,params
	 * 返回值:  对象数组 <说明> 
	 * @return Object[]
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	private Object[] parseParamArray(String methodName, Object[] params) {
		Object[] adjustParams = new Object[2];
		if (methodName.startsWith("queryFor")) {
			adjustParams[0] = (String) params[0];
			adjustParams[1] = convertToMap(params[1]);
		} else {
			adjustParams[0] = ((Class<?>) params[0]).getName() + ".select";
			adjustParams[1] = params[1];
		}
		return adjustParams;
	}

	/**
	 * 描述：将实体对象转为map
	 * 输入参数：实体对象<按照参数定义顺序> 
	 * @param param
	 * 返回值:  Object类型 <说明> 
	 * @return Object
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	private Object convertToMap(Object param) {
		Object object = param;
		if (!(param instanceof Map)) {
			object = DacUtils.convertToMap(param);
		}
		return object;
	}
}