package com.adanac.framework.dac.client;

import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

import com.adanac.framework.dac.pagination.PaginationResult;

/**
 * 对非分库分表的项目提供分页查询
 * 
 * @author adanac
 */
public interface IPaginationDacClient {
	/**
	 * 根据sqlId查询多个对象，返回Map<String, Object>类型对象List集合和数据行数
	 * <p>
	 * 
	 * @param sqlId
	 *            配置到sql map文件的id
	 * @param param
	 *            SQL参数
	 * @param offset
	 *            分页起始位置
	 * @param limit
	 *            数据结束位置
	 * @return PaginationResult，包含分页查询结果及数据行数
	 */
	PaginationResult<Map<String, Object>> queryForList(String sqlId, Object param, int offset, int limit);

	/**
	 * 根据sqlId查询多个对象，返回Map<String, Object>类型对象List集合和数据行数
	 * <p>
	 * 
	 * @param sqlId
	 *            配置到sql map文件的id
	 * @param param
	 *            SQL参数
	 * @param offset
	 *            分页起始位置
	 * @param limit
	 *            数据结束位置
	 * @return PaginationResult，包含分页查询结果及数据行数
	 */
	PaginationResult<Map<String, Object>> queryForList(String sqlId, Map<String, Object> paramMap, int offset,
			int limit);

	/**
	 * 根据sqlId查询多个对象，返回requiredType类型对象List集合和数据行数
	 * <p>
	 * 注意：适用于元素较少的集合，如果集合元素较多，会出现反射造成的性能问题
	 * 
	 * @param sqlId
	 *            配置到sql map文件的id
	 * @param param
	 *            SQL参数
	 * @param offset
	 *            分页起始位置
	 * @param limit
	 *            数据结束位置
	 * @return PaginationResult，包含分页查询结果及数据行数
	 */
	<T> PaginationResult<T> queryForList(String sqlId, Map<String, Object> paramMap, Class<T> requiredType, int offset,
			int limit);

	/**
	 * 根据sqlId查询多个对象，返回requiredType类型对象List集合和数据行数
	 * <p>
	 * 注意：适用于元素较少的集合，如果集合元素较多，会出现反射造成的性能问题
	 * 
	 * @param sqlId
	 *            配置到sql map文件的id
	 * @param param
	 *            SQL参数
	 * @param offset
	 *            分页起始位置
	 * @param limit
	 *            数据结束位置
	 * @return PaginationResult，包含分页查询结果及数据行数
	 */
	<T> PaginationResult<T> queryForList(String sqlId, Object param, Class<T> requiredType, int offset, int limit);

	/**
	 * 
	 * 根据sqlId查询多个对象，返回RowMapper映射的对象List集合和数据行数
	 * <p>
	 * 〈功能详细描述〉
	 * 
	 * @param sqlId
	 *            配置到sql map文件的id
	 * @param param
	 *            SQL参数
	 * @param rowMapper
	 *            结果集行映射的RowMapper
	 * @param offset
	 *            分页数据起始位置
	 * @param limit
	 *            数据结束位置
	 * @return PaginationResult，包含分页查询结果及数据行数
	 */
	<T> PaginationResult<T> queryForList(String sqlId, Object param, RowMapper<T> rowMapper, int offset, int limit);

	/**
	 * 
	 * 根据sqlId查询多个对象，返回RowMapper映射的对象List集合和数据行数
	 * <p>
	 * 〈功能详细描述〉
	 * 
	 * @param sqlId
	 *            配置到sql map文件的id
	 * @param param
	 *            SQL参数
	 * @param rowMapper
	 *            结果集行映射的RowMapper
	 * @param offset
	 *            分页数据起始位置
	 * @param limit
	 *            数据结束位置
	 * @return PaginationResult，包含分页查询结果及数据行数
	 */
	<T> PaginationResult<T> queryForList(String sqlId, Map<String, Object> paramMap, RowMapper<T> rowMapper, int offset,
			int limit);

}
