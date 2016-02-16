package com.adanac.framework.dac.parsing.annotation;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import com.adanac.framework.dac.client.support.Configuration;
import com.adanac.framework.dac.client.support.MappedStatement;
import com.adanac.framework.dac.client.support.SqlCommandType;
import com.adanac.framework.dac.parsing.builder.BaseBuilder;
import com.adanac.framework.dac.parsing.exception.ParsingException;
import com.adanac.framework.dac.parsing.support.annotation.TableRoute;

/**
 * 描述：单表操作的sql生成类
 * 
 * @author 13092011/jorgie
 */
public class AnnotationSqlMapBuilder extends BaseBuilder {

	private Class<?> entityClass;
	private String currentNamespace;

	public AnnotationSqlMapBuilder(Configuration configuration, Class<?> entityClass) {
		super(configuration);
		this.entityClass = entityClass;
		if (entityClass == null) {
			throw new ParsingException("entityClass can't null.");
		}
	}

	public void parse() {

		try {
			// 生成当前namespace
			currentNamespace = entityClass.getName();
			addStatements();
		} catch (Exception e) {
			throw new ParsingException("Error parsing sqlMap XML. " + currentNamespace + " Cause: " + e, e);
		}

	}

	/**
	 * 功能描述：生成有关该实体类的增删改查操作的MappedStatement对象
	 * 
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	private void addStatements() {
		ParserAssist assist = new ParserAssist(entityClass);
		String idProperty = assist.getId();
		String idGenerator = assist.querySequence();
		Boolean isGenerator = assist.isGenerator();

		// 获得各单表操作SQL语句
		String insert = assist.getInsert();
		String delete = assist.getDelete();

		String update = assist.getUpdate();
		String updateDynamic = assist.getDynamicUpdate();

		String select = assist.getSelect();
		String selectAll = assist.getSelectAll();

		MappedStatement statement = new MappedStatement();
		statement.setIsGenerator(isGenerator);
		statement.setConfiguration(configuration);
		statement.setId(applyCurrentNamespace("insert"));
		statement.setIdProperty(idProperty);
		statement.setKeyGenerator(idGenerator);
		statement.setSqlCommandType(SqlCommandType.INSERT);
		statement.setSqlSource(insert);
		configuration.addMappedStatement(statement);

		statement = new MappedStatement();
		statement.setIsGenerator(isGenerator);
		statement.setConfiguration(configuration);
		statement.setId(applyCurrentNamespace("delete"));
		statement.setIdProperty(idProperty);
		statement.setKeyGenerator(idGenerator);
		statement.setSqlCommandType(SqlCommandType.DELETE);
		statement.setSqlSource(delete);
		configuration.addMappedStatement(statement);

		statement = new MappedStatement();
		statement.setIsGenerator(isGenerator);
		statement.setConfiguration(configuration);
		statement.setId(applyCurrentNamespace("update"));
		statement.setIdProperty(idProperty);
		statement.setKeyGenerator(idGenerator);
		statement.setSqlCommandType(SqlCommandType.UPDATE);
		statement.setSqlSource(update);
		configuration.addMappedStatement(statement);

		statement = new MappedStatement();
		statement.setIsGenerator(isGenerator);
		statement.setConfiguration(configuration);
		statement.setId(applyCurrentNamespace("updateDynamic"));
		statement.setIdProperty(idProperty);
		statement.setKeyGenerator(idGenerator);
		statement.setSqlCommandType(SqlCommandType.UPDATE);
		statement.setSqlSource(updateDynamic);
		configuration.addMappedStatement(statement);

		statement = new MappedStatement();
		statement.setIsGenerator(isGenerator);
		statement.setConfiguration(configuration);
		statement.setId(applyCurrentNamespace("select"));
		statement.setIdProperty(idProperty);
		statement.setKeyGenerator(idGenerator);
		statement.setSqlCommandType(SqlCommandType.SELECT);
		statement.setSqlSource(select);
		configuration.addMappedStatement(statement);

		statement = new MappedStatement();
		statement.setIsGenerator(isGenerator);
		statement.setConfiguration(configuration);
		statement.setId(applyCurrentNamespace("selectAll"));
		statement.setIdProperty(idProperty);
		statement.setKeyGenerator(idGenerator);
		statement.setSqlCommandType(SqlCommandType.SELECT);
		statement.setSqlSource(selectAll);
		configuration.addMappedStatement(statement);
	}

	private String applyCurrentNamespace(String id) {
		if (id.startsWith(currentNamespace + ".")) {
			return id;
		} else {
			return currentNamespace + "." + id;
		}
	}

	/**
	 * 功能描述：解析类
	 * 
	 * @author 作者 13092011
	 */
	private static class ParserAssist {

		private String tableName;

		private String id;

		private String idName;

		private boolean isGenerator = true;

		private String sequenceName;

		// private String sequenceColumnName;

		private String catalog = "db2"; // default db2 database.

		// private int allcationSize = 1;

		private List<String> columnList = new ArrayList<String>();

		private List<String> columnNameList = new ArrayList<String>();

		private List<String> fieldList = new ArrayList<String>();

		private String insert;

		private String update;

		private String delete;

		private String select;

		private String dynamicUpdate;

		private String selectAll;

		private String querySequence;

		private Map<String, Column> propertyMap = new HashMap<String, Column>();

		private Map<String, Column> fieldMap = new HashMap<String, Column>();

		private ParserAssist(Class<? extends Object> clazz) {
			setFieldList(clazz);
			setTable(clazz);
			setId(clazz);
			setColumnList(clazz);

			setInsertSql();
			setUpdateSql();
			setDeleteSql();
			setSelectSql();
			setSelectAllSql();
		}

		private String getId() {
			return id;
		}

		private String getInsert() {
			return insert;
		}

		private String getUpdate() {
			return update;
		}

		private String getDynamicUpdate() {
			setDynamicUpdateSql();
			return dynamicUpdate;
		}

		private String getDelete() {
			return delete;
		}

		private String getSelect() {
			return select;
		}

		public boolean isGenerator() {
			return isGenerator;
		}

		private String querySequence() {
			buildQuerySequence();
			return querySequence;
		}

		/**
		 * 
		 * 功能描述：生成查询序列<br>
		 * 只支持DB2，因为Mysql无法保证集群下的原子性序列操作
		 */
		private void buildQuerySequence() {
			if (catalog != null && StringUtils.isNotBlank(sequenceName) && StringUtils.isBlank(querySequence)) {
				StringBuffer buffer = new StringBuffer();
				if ("db2".equalsIgnoreCase(catalog)) {
					buffer.append("SELECT NEXTVAL FOR ").append(sequenceName)
							.append(" FROM SYSIBM.SYSSEQUENCES WHERE SEQNAME='").append(sequenceName).append("'");
					querySequence = buffer.toString();
				}
				// Mysql无法保证集群下的原子性序列操作
				// if ("mysql".equalsIgnoreCase(catalog)) {
				// buffer.append("update ").append(sequenceName).append(" set
				// ").append(sequenceColumnName)
				// .append(" =
				// last_insert_id(").append(sequenceColumnName).append("+").append(allcationSize)
				// .append(")");
				// querySequence = buffer.toString();
				// isMysql = true;
				// }
			}
		}

		private String getSelectAll() {
			return selectAll;
		}

		/**
		 * 功能描述：组装动态更新sql语句
		 */
		private void setDynamicUpdateSql() {
			StringBuffer sb = new StringBuffer("UPDATE ");
			sb.append(tableName).append(" SET ");
			int size = columnNameList.size();
			for (int i = 0; i < size; i++) {
				if (!propertyMap.get(columnNameList.get(i)).updatable()) {
					continue;
				}
				// 由于动态更新，需要写成动态语句
				sb.append("<#if " + columnList.get(i) + "?exists> ");
				sb.append(columnNameList.get(i)).append(" = :").append(columnList.get(i));
				sb.append(", ");
				sb.append("</#if>");
			}
			sb.append(" WHERE ");
			sb.append(idName).append(" = :").append(id);
			dynamicUpdate = sb.toString();// 组装成的sql中"WHERE"前必然存在无法删除的逗号
		}

		/**
		 * 
		 * 功能描述：组装插入sql语句
		 */
		private void setInsertSql() {
			StringBuffer sb = new StringBuffer("INSERT INTO ");
			sb.append(tableName).append("(");

			if (!isGenerator) {
				if (idName != null && id != null) {
					sb.append(idName);
					sb.append(", ");
				}
			}
			int size = columnNameList.size();
			for (int i = 0; i < size; i++) {
				if (propertyMap.get(columnNameList.get(i)).insertable()) {
					sb.append(columnNameList.get(i));
					sb.append(", ");
				}
			}
			// 删除多余的逗号
			sb.deleteCharAt(sb.length() - 2);
			sb.append(") VALUES (");
			if (!isGenerator) {
				if (idName != null && id != null) {
					sb.append(":").append(id);
					sb.append(", ");
				}
			}
			size = columnList.size();
			for (int i = 0; i < size; i++) {
				if (fieldMap.get(columnList.get(i)).insertable()) {
					sb.append(":").append(columnList.get(i));
					sb.append(", ");
				}
			}
			sb.deleteCharAt(sb.length() - 2);
			sb.append(")");
			insert = sb.toString();
		}

		/**
		 * 
		 * 功能描述：组装更新sql语句
		 */
		private void setUpdateSql() {
			StringBuffer sb = new StringBuffer("UPDATE ");
			sb.append(tableName).append(" SET ");
			int size = columnNameList.size();
			for (int i = 0; i < size; i++) {
				if (propertyMap.get(columnNameList.get(i)).updatable()) {
					sb.append(columnNameList.get(i)).append(" = :").append(columnList.get(i));
					sb.append(", ");
				}
			}
			sb.deleteCharAt(sb.length() - 2);
			sb.append(" WHERE ");
			sb.append(idName).append(" = :").append(id);
			update = sb.toString();
		}

		/**
		 * 
		 * 功能描述：组装删除sql语句
		 */
		private void setDeleteSql() {
			StringBuffer sb = new StringBuffer("DELETE FROM ");
			sb.append(tableName).append(" WHERE ");
			sb.append(idName).append(" = :").append(id);
			delete = sb.toString();
		}

		/**
		 * 
		 * 功能描述：组装条件查询sql语句
		 */
		private void setSelectSql() {
			StringBuffer sb = new StringBuffer("SELECT ");
			List<String> tempList = new ArrayList<String>(columnNameList);
			tempList.add(idName);
			int size = tempList.size();
			for (int i = 0; i < size; i++) {
				sb.append(tempList.get(i));
				if (i < size - 1) {
					sb.append(", ");
				}
			}
			sb.append(" FROM ").append(tableName).append(" WHERE ");
			sb.append(idName).append(" = :").append(id);
			select = sb.toString();
		}

		/**
		 * 
		 * 功能描述：组装查询sql语句
		 */
		private void setSelectAllSql() {
			StringBuffer sb = new StringBuffer("SELECT ");
			List<String> tempList = new ArrayList<String>(columnNameList);
			tempList.add(idName);
			int size = tempList.size();
			for (int i = 0; i < size; i++) {
				sb.append(tempList.get(i));
				if (i < size - 1) {
					sb.append(", ");
				}
			}
			sb.append(" FROM ").append(tableName);
			selectAll = sb.toString();
		}

		/**
		 * 
		 * 功能描述：生成表名
		 */
		private void setTable(Class<? extends Object> clazz) {
			TableRoute table = clazz.getAnnotation(TableRoute.class);
			Entity entity = clazz.getAnnotation(Entity.class);
			if (entity != null && entity.name() != null) {
				tableName = entity.name();
			}
			if (table != null && table.tableName() != null) {
				tableName = table.tableName();
			}

		}

		private void setFieldList(Class<? extends Object> clazz) {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				fieldList.add(field.getName());
			}
		}

		/**
		 * 
		 * 功能描述：生成主键
		 */
		private void setId(Class<? extends Object> clazz) {
			Method[] methods = clazz.getMethods();
			for (Method method : methods) {
				if (method.isAnnotationPresent(Id.class)) {
					idName = method.getAnnotation(Column.class).name();
					id = BeanUtils.findPropertyForMethod(method).getName();
					GeneratedValue generatedValue = method.getAnnotation(GeneratedValue.class);
					SequenceGenerator sequenceGenerator = method.getAnnotation(SequenceGenerator.class);
					if (generatedValue != null && generatedValue.strategy() != null
							&& generatedValue.strategy().compareTo(GenerationType.AUTO) != 0) {
						isGenerator = false;
					} else {
						isGenerator = true;
					}
					if (sequenceGenerator != null && generatedValue != null
							&& sequenceGenerator.name().equals(generatedValue.generator())) {
						sequenceName = sequenceGenerator.sequenceName();
						// sequenceColumnName = sequenceGenerator.name();
						if (StringUtils.isNotBlank(sequenceGenerator.catalog())) {
							catalog = sequenceGenerator.catalog();
						}
						// allcationSize = sequenceGenerator.allocationSize();
					}
				}
			}
		}

		/**
		 * 
		 * 功能描述：生成字段列表
		 */
		private void setColumnList(Class<? extends Object> clazz) {
			Method[] methods = clazz.getMethods();
			Field[] fields = clazz.getDeclaredFields();
			Field[] superFields = clazz.getSuperclass().getDeclaredFields();
			for (Method method : methods) {
				if (method.isAnnotationPresent(Column.class) && !method.isAnnotationPresent(Id.class)) {
					PropertyDescriptor descriptor = BeanUtils.findPropertyForMethod(method);
					if (isTransient(fields, descriptor.getName()) || isTransient(superFields, descriptor.getName())) {
						continue;
					}
					Column columnAnnoation = method.getAnnotation(Column.class);
					columnNameList.add(columnAnnoation.name());
					columnList.add(BeanUtils.findPropertyForMethod(method).getName());
					propertyMap.put(columnAnnoation.name(), columnAnnoation);
					fieldMap.put(BeanUtils.findPropertyForMethod(method).getName(), columnAnnoation);
				}
			}
		}

		private boolean isTransient(Field[] fields, String fileName) {
			for (Field field : fields) {
				if (field.getName().equals(fileName) && Modifier.isTransient(field.getModifiers())) {
					return true;
				}
			}
			return false;
		}
	}

}
