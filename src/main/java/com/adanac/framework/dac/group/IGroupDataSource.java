package com.adanac.framework.dac.group;

import javax.sql.DataSource;

/*8
 * 分组数据源
 */
public interface IGroupDataSource extends DataSource {
	/**
	 * 
	 * 功能描述: 根据SQL特性获取原生类型数据源
	 * @param sqlBean SQL模板映射对象
	 * @return
	 */
	DataSource getDataSource(String sqlId);
}
