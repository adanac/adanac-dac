package com.adanac.framework.dac.parsing.xml;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import com.adanac.framework.dac.cache.support.CacheConfigurationUtil;
import com.adanac.framework.dac.cache.support.CacheProperty;
import com.adanac.framework.dac.client.support.Configuration;
import com.adanac.framework.dac.parsing.builder.BaseBuilder;
import com.adanac.framework.dac.parsing.exception.ParsingException;

/*8
 * 缓存配置文件解析类
 */
public class XmlCacheBuilder extends BaseBuilder {

	private XPathParser parser;
	private String resource;

	public XmlCacheBuilder(InputStream inputStream, Configuration configuration, String resource) {
		this(new XPathParser(inputStream, false, configuration.getVariables(), new XmlSqlMapEntityResolver()),
				configuration, resource);
	}

	private XmlCacheBuilder(XPathParser parser, Configuration configuration, String resource) {
		super(configuration);
		this.parser = parser;
		this.resource = resource;
	}

	/**
	 * 功能描述：解析cacheController标签
	 */
	public void parse() {
		configurationElement(parser.evalNode("/cacheController"));
	}

	/**
	 * 功能描述：解析子标签：cachedStatement、cachedNamespace标签<br>
	 * 输入参数：xml节点<按照参数定义顺序> 
	 * @param context
	 * 返回值:   <说明> 
	 * @return 返回值
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	private void configurationElement(XNode context) {
		try {
			addStatements(context.evalNodes("cachedStatement|cachedNamespace"));
		} catch (Exception e) {
			throw new ParsingException("Error parsing cacheController XML. " + resource + " Cause: " + e, e);
		}
	}

	/**
	 * 功能描述：具体解析每个cachedStatement、cachedNamespace标签<br>
	 * 支持多个cachedStatement或cachedNamespace配置<br>
	 * 输入参数：list集合，元素时xml节点<按照参数定义顺序> 
	 * @param list
	 * 返回值:  <说明> 
	 * @return 返回值
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	private void addStatements(List<XNode> list) {
		for (XNode elementNode : list) {
			String nodeName = elementNode.getName();
			if ("cachedStatement".equals(nodeName)) {
				// 如果节点名称为cachedStatement，表示以statementId（sqlId）配置
				String statementId = elementNode.getStringAttribute("statementId");
				if (statementId == null || statementId.equals("")) {
					throw new ParsingException(
							this.resource + " element <cachedStatement>" + "'s statementId cannot be empty");
				}
				Properties properties = parserProperties(elementNode);
				// 是否配置多个statementId
				if (statementId.contains(",")) {
					String[] statementIds = statementId.split(",");
					for (String aStatementId : statementIds) {
						CacheConfigurationUtil.addStatementCacheProperties(configuration, aStatementId.trim(),
								new CacheProperty(properties));
					}
				} else {
					CacheConfigurationUtil.addStatementCacheProperties(configuration, statementId.trim(),
							new CacheProperty(properties));
				}

			} else if ("cachedNamespace".equals(nodeName)) {
				// 如果节点名称为cachedNamespace，表示以namespace配置
				String namespace = elementNode.getStringAttribute("namespace");
				if (namespace == null || namespace.equals("")) {
					throw new ParsingException(
							this.resource + " element <cachedNamespace>" + "'s namespace cannot be empty");
				}
				Properties properties = parserProperties(elementNode);
				// 是否配置多个namespace
				if (namespace.contains(",")) {
					String[] namespaces = namespace.split(",");
					for (String aNamespace : namespaces) {
						CacheConfigurationUtil.addNamespaceCacheProperties(configuration, aNamespace.trim(),
								new CacheProperty(properties));
					}
				} else {
					CacheConfigurationUtil.addNamespaceCacheProperties(configuration, namespace.trim(),
							new CacheProperty(properties));
				}
			}

		}
	}

	/**
	 * 功能描述：解析属性<br>
	 * 输入参数：xml节点<按照参数定义顺序> 
	 * @param elementNode
	 * 返回值:  属性对象 
	 * @return Properties
	 * @throw 异常描述
	 * @see 需要参见的其它内容
	 */
	private Properties parserProperties(XNode elementNode) {
		Properties properties = new Properties();
		List<XNode> children = elementNode.getChildren();
		for (XNode xNode : children) {
			if ("property".equals(xNode.getName())) {
				String name = xNode.getStringAttribute("name");
				String value = xNode.getStringAttribute("value");
				properties.setProperty(name, value);
			}
		}
		return properties;
	}

}