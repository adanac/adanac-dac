package com.adanac.framework.rws.schema.namespace;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import com.adanac.framework.rws.schema.config.DsGroupConfig;
import com.adanac.framework.rws.schema.parse.UrfBeanDefinitionParser;

public class SNFNamespaceHandler extends NamespaceHandlerSupport {

	public void init() {
		registerBeanDefinitionParser("ds_group", new UrfBeanDefinitionParser(DsGroupConfig.class, true));
	}

}