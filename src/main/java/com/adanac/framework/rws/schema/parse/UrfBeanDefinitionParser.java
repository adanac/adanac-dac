package com.adanac.framework.rws.schema.parse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.adanac.framework.rws.schema.config.DsConfig;

/**
 * URF配置文件paser
 * @author adanac
 * @version 1.0
 */
public class UrfBeanDefinitionParser implements BeanDefinitionParser {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(UrfBeanDefinitionParser.class);

	private final Class<?> beanClass;

	private final boolean required;

	private static String WR_DS = "wr_ds";

	private static String RO_DS = "ro_ds";

	public UrfBeanDefinitionParser(Class<?> beanClass, boolean required) {
		this.beanClass = beanClass;
		this.required = required;
	}

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		return parse(element, parserContext, beanClass, required);
	}

	private BeanDefinition parse(Element element, ParserContext parserContext, Class<?> beanClass, boolean required) {
		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(beanClass);
		beanDefinition.setLazyInit(false);
		String id = element.getAttribute("id");
		if (id != null && id.length() > 0) {
			if (parserContext.getRegistry().containsBeanDefinition(id)) {
				throw new IllegalStateException("Duplicate spring bean id " + id);
			}
			parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
			beanDefinition.getPropertyValues().addPropertyValue("id", id);
		}
		BeanDefinition wr_dsBeanDefinition = getWrDsBeanDefinition(element, parserContext);
		beanDefinition.getPropertyValues().addPropertyValue("wrDsConfig", wr_dsBeanDefinition);

		ManagedMap<String, BeanDefinition> roDsBeanDefinitionMap = getRoDsBeanDefinitionMap(element, parserContext);
		beanDefinition.getPropertyValues().addPropertyValue("roDsConfigs", roDsBeanDefinitionMap);
		return beanDefinition;
	}

	private BeanDefinition getWrDsBeanDefinition(Element element, ParserContext parserContext) {

		NodeList wr_dsNodes = element.getElementsByTagNameNS("*", WR_DS);

		if (wr_dsNodes == null || wr_dsNodes.getLength() == 0 || wr_dsNodes.getLength() > 1) {
			throw new IllegalStateException("每个ds_group要求必须有且仅有一个wr_ds节点 !");
		}

		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(DsConfig.class);
		beanDefinition.setLazyInit(false);

		Node wr_dsNode = wr_dsNodes.item(0);
		if (wr_dsNode.getNodeType() == Node.ELEMENT_NODE) {
			Element wr_dsElement = (Element) wr_dsNode;
			String name = wr_dsElement.getAttribute("name");
			String ref = wr_dsElement.getAttribute("ref");
			beanDefinition.getPropertyValues().addPropertyValue("name", name);
			beanDefinition.getPropertyValues().addPropertyValue("refDataSource", new RuntimeBeanReference(ref));
		}

		return beanDefinition;
	}

	private ManagedMap<String, BeanDefinition> getRoDsBeanDefinitionMap(Element element, ParserContext parserContext) {

		NodeList ro_dsNodes = element.getElementsByTagNameNS("*", RO_DS);

		ManagedMap<String, BeanDefinition> roDsBeanDefinitionMap = new ManagedMap<String, BeanDefinition>();

		if (ro_dsNodes == null || ro_dsNodes.getLength() <= 0) {
			throw new IllegalStateException("每个ds_group要求必须至少有一个wr_ds节点 !");
		}

		for (int i = 0; i < ro_dsNodes.getLength(); i++) {
			RootBeanDefinition beanDefinition = new RootBeanDefinition();
			beanDefinition.setBeanClass(DsConfig.class);
			beanDefinition.setLazyInit(false);
			Node ro_dsNode = ro_dsNodes.item(i);
			if (ro_dsNode.getNodeType() == Node.ELEMENT_NODE) {
				Element ro_dsElement = (Element) ro_dsNode;
				String name = ro_dsElement.getAttribute("name");
				String weight = ro_dsElement.getAttribute("weight");
				String refId = ro_dsElement.getAttribute("ref");
				String type = ro_dsElement.getAttribute("type");
				beanDefinition.getPropertyValues().addPropertyValue("name", name);
				beanDefinition.getPropertyValues().addPropertyValue("weight", weight);
				beanDefinition.getPropertyValues().addPropertyValue("refDataSource", new RuntimeBeanReference(refId));
				beanDefinition.getPropertyValues().addPropertyValue("type", type);
				roDsBeanDefinitionMap.put(name, beanDefinition);
			}
		}

		return roDsBeanDefinitionMap;
	}

	public Class<?> getBeanClass() {
		return beanClass;
	}

	public boolean isRequired() {
		return required;
	}

}