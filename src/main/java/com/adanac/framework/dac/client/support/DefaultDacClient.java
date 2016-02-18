package com.adanac.framework.dac.client.support;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.util.Assert;

import com.adanac.framework.dac.client.DacClient;
import com.adanac.framework.dac.client.support.audit.SqlAuditor;
import com.adanac.framework.dac.client.support.executor.MappedSqlExecutor;
import com.adanac.framework.dac.exception.DalException;
import com.adanac.framework.dac.parsing.annotation.AnnotationCacheBuilder;
import com.adanac.framework.dac.parsing.annotation.AnnotationSqlMapBuilder;
import com.adanac.framework.dac.parsing.exception.ParsingException;
import com.adanac.framework.dac.parsing.io.ResolverUtil;
import com.adanac.framework.dac.parsing.support.annotation.TableRoute;
import com.adanac.framework.dac.parsing.xml.XmlSqlMapBuilder;

/**
 * 默认客户端
 * @author adanac
 */
public class DefaultDacClient implements DacClient, InitializingBean {
	protected transient Logger logger = LoggerFactory.getLogger(getClass());

	protected Resource[] sqlMapConfigLocation;

	protected String entityPackage;

	protected Configuration configuration = new Configuration();;

	protected DataSource dataSource;

	protected SqlAuditor sqlAuditor;

	protected MappedSqlExecutor mappedSqlExecutor;

	protected boolean profileLongTimeRunningSql;

	protected long longTimeRunningSqlIntervalThreshold;

	public Resource[] getSqlMapConfigLocation() {
		return sqlMapConfigLocation;
	}

	public void setSqlMapConfigLocation(Resource[] sqlMapConfigLocation) {
		this.sqlMapConfigLocation = sqlMapConfigLocation;
	}

	public String getEntityPackage() {
		return entityPackage;
	}

	public void setEntityPackage(String entityPackage) {
		this.entityPackage = entityPackage;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public boolean isProfileLongTimeRunningSql() {
		return profileLongTimeRunningSql;
	}

	public void setProfileLongTimeRunningSql(boolean profileLongTimeRunningSql) {
		this.profileLongTimeRunningSql = profileLongTimeRunningSql;
	}

	public long getLongTimeRunningSqlIntervalThreshold() {
		return longTimeRunningSqlIntervalThreshold;
	}

	public void setLongTimeRunningSqlIntervalThreshold(long longTimeRunningSqlIntervalThreshold) {
		this.longTimeRunningSqlIntervalThreshold = longTimeRunningSqlIntervalThreshold;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public SqlAuditor getSqlAuditor() {
		return sqlAuditor;
	}

	public void setSqlAuditor(SqlAuditor sqlAuditor) {
		this.sqlAuditor = sqlAuditor;
	}

	public Number persist(Object entity) {
		return persist(entity, Number.class);
	}

	public <T> T persist(Object entity, Class<T> requiredType) {
		assertMapped(entity);
		return mappedSqlExecutor.persist(entity, requiredType);
	}

	public int merge(Object entity) {
		assertMapped(entity);
		return mappedSqlExecutor.merge(entity);
	}

	public int dynamicMerge(Object entity) {
		assertMapped(entity);
		return mappedSqlExecutor.dynamicMerge(entity);
	}

	public int remove(Object entity) {
		assertMapped(entity);
		return mappedSqlExecutor.remove(entity);
	}

	public <T> T find(Class<T> entityClass, Object entity) {
		assertMapped(entityClass);
		return mappedSqlExecutor.find(entityClass, entity);
	}

	public <T> T queryForObject(String sqlId, Map<String, Object> paramMap, Class<T> requiredType) {
		return mappedSqlExecutor.queryForObject(sqlId, paramMap, requiredType);
	}

	public <T> T queryForObject(String sqlId, Object param, Class<T> requiredType) {
		return mappedSqlExecutor.queryForObject(sqlId, param, requiredType);
	}

	public <T> T queryForObject(String sqlId, Map<String, Object> paramMap, RowMapper<T> rowMapper) {
		return mappedSqlExecutor.queryForObject(sqlId, paramMap, rowMapper);
	}

	public <T> T queryForObject(String sqlId, Object param, RowMapper<T> rowMapper) {
		return mappedSqlExecutor.queryForObject(sqlId, param, rowMapper);
	}

	public Map<String, Object> queryForMap(String sqlId, Map<String, Object> paramMap) {
		return mappedSqlExecutor.queryForMap(sqlId, paramMap);
	}

	public Map<String, Object> queryForMap(String sqlId, Object param) {
		return mappedSqlExecutor.queryForMap(sqlId, param);
	}

	public <T> List<T> queryForList(String sqlId, Map<String, Object> paramMap, Class<T> requiredType) {
		return mappedSqlExecutor.queryForList(sqlId, paramMap, requiredType);
	}

	public <T> List<T> queryForList(String sqlId, Object param, Class<T> requiredType) {
		return mappedSqlExecutor.queryForList(sqlId, param, requiredType);
	}

	public List<Map<String, Object>> queryForList(String sqlId, Map<String, Object> paramMap) {
		return mappedSqlExecutor.queryForList(sqlId, paramMap);
	}

	public List<Map<String, Object>> queryForList(String sqlId, Object param) {
		return mappedSqlExecutor.queryForList(sqlId, param);
	}

	public <T> List<T> queryForList(String sqlId, Map<String, Object> paramMap, RowMapper<T> rowMapper) {
		return mappedSqlExecutor.queryForList(sqlId, paramMap, rowMapper);
	}

	public <T> List<T> queryForList(String sqlId, Object param, RowMapper<T> rowMapper) {
		return mappedSqlExecutor.queryForList(sqlId, param, rowMapper);
	}

	public int execute(String sqlId, Map<String, Object> paramMap) {
		return mappedSqlExecutor.execute(sqlId, paramMap);
	}

	public Number execute4PrimaryKey(String sqlId, Map<String, Object> paramMap) {
		return mappedSqlExecutor.execute4PrimaryKey(sqlId, paramMap);
	}

	public int execute(String sqlId, Object param) {
		return mappedSqlExecutor.execute(sqlId, param);
	}

	public int[] batchUpdate(String sqlId, Map<String, Object>[] batchValues) {
		return mappedSqlExecutor.batchUpdate(sqlId, batchValues);
	}

	public Map<String, Object> call(String sqlId, Map<String, Object> paramMap, List<SqlParameter> sqlParameters) {
		return mappedSqlExecutor.call(sqlId, paramMap, sqlParameters);
	}

	protected MappedSqlExecutor getMappedSqlExector() {
		return mappedSqlExecutor;
	}

	protected void assertMapped(Object entity) {
		if (entity == null) {
			throw new DalException("the entity can't null");
		}
		Class<? extends Object> entityClass = entity.getClass();
		assertMapped(entityClass);
	}

	/**
	 * 功能描述：维护映射。<br>
	 * 根据实体类型查询configuration对象中是否有该实体类的mappedStatement对象。<br>
	 * 若有，则跳过；若没有，则扫描实体类，判断是否有TableRoute或Entity注解。<br>
	 * 若有，则日志告警配置的entityPackage下不包含此实体类；若没有，则报错。<br>
	 * 输入参数：实体bean<按照参数定义顺序>
	 * 
	 * @param entityClass
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	protected void assertMapped(Class<?> entityClass) {
		if (entityClass == null) {
			throw new DalException("the entity can't null");
		}
		String sqlId = entityClass.getName() + ".insert";
		MappedStatement mappedStatement = configuration.getMappedStatement(sqlId);
		if (mappedStatement == null) {
			if (entityClass.isAnnotationPresent(TableRoute.class)) {
				logger.debug("Please configure the entityPackage for {} in order to it can scan the entity classes.",
						entityClass.getName());
				new AnnotationSqlMapBuilder(configuration, entityClass).parse();
			} else if (entityClass.isAnnotationPresent(Entity.class)) {
				logger.debug("Please configure the entityPackage for {} in order to it can scan the entity classes.",
						entityClass.getName());
				new AnnotationSqlMapBuilder(configuration, entityClass).parse();
			} else {

				throw new DalException("The persist method is not support this pojo:" + entityClass.getName());
			}
		}
	}

	/**
	 * 描述：在bean初始化时执行
	 */
	public void afterPropertiesSet() throws Exception {

		Assert.notNull(dataSource, "Property 'dataSource' is required");

		if (isProfileLongTimeRunningSql()) {
			Assert.isTrue(longTimeRunningSqlIntervalThreshold > 0,
					"'longTimeRunningSqlIntervalThreshold' should have a positive value "
							+ "if 'profileLongTimeRunningSql' is set to true");
		}

		// 生成SqlMap
		buildSqlMap();

		// DBType dbType = DatabaseTypeProvider.getDatabaseType(dataSource);
		// logger.debug("this 'dataSource' database type is " + dbType);
		mappedSqlExecutor = new MappedSqlExecutor();
		mappedSqlExecutor.setConfiguration(configuration);
		mappedSqlExecutor.setDataSource(dataSource);
		// mappedSqlExecutor.setDbType(dbType);
		mappedSqlExecutor.setSqlAuditor(sqlAuditor);
		mappedSqlExecutor.setProfileLongTimeRunningSql(isProfileLongTimeRunningSql());
		mappedSqlExecutor.setLongTimeRunningSqlIntervalThreshold(longTimeRunningSqlIntervalThreshold);

	}

	/**
	 * 功能描述：生成SqlMap。<br>
	 * 扫描entityPackage路径下的所有实体类，添加单表操作的sql语句。<br>
	 * 并解析SqlMap配置文件，添加sql语句。<br>
	 */
	protected void buildSqlMap() {
		try {
			Set<Class<? extends Class<?>>> classSet = new HashSet<Class<? extends Class<?>>>();
			if (entityPackage != null && !"".equals(entityPackage)) {
				// 如果entityPackage不为空，开始扫描路径下的所有实体类
				ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<Class<?>>();
				resolverUtil.setClassLoader(getClass().getClassLoader());

				// 存在Entity注解
				resolverUtil.find(new ResolverUtil.AnnotatedWith(Entity.class), entityPackage);
				classSet.addAll(resolverUtil.getClasses());

				// 或者存在TableRoute注解
				resolverUtil.find(new ResolverUtil.AnnotatedWith(TableRoute.class), entityPackage);
				classSet.addAll(resolverUtil.getClasses());

				for (Class<?> entityClass : classSet) {
					// 解析 Entity注解
					new AnnotationSqlMapBuilder(configuration, entityClass).parse();
					// 解析CacheController 注解
					new AnnotationCacheBuilder(configuration, entityClass).parse();
				}
			}
			if (sqlMapConfigLocation != null) {
				for (Resource resource : sqlMapConfigLocation) {
					// 解析sqlMap配置文件
					new XmlSqlMapBuilder(resource.getInputStream(), configuration, resource.getFilename()).parse();
				}
			}
		} catch (ParsingException e) {
			logger.error(this.getClass() + "Error occurred.  Cause: ", e);
			throw e;
		} catch (IOException e) {
			throw new ParsingException("Error occurred.  Cause: ", e);
		}
	}
}
