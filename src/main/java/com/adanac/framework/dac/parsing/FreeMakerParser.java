package com.adanac.framework.dac.parsing;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import com.adanac.framework.dac.exception.DalException;
import com.adanac.framework.dac.parsing.support.method.DalHash;
import com.adanac.framework.dac.parsing.support.method.DalMod;
import com.adanac.framework.dac.parsing.support.method.DalRange;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateModel;

/**
 * 
 * FreeMaker解析<br>
 * 
 * @author 12010065
 * 
 */
public class FreeMakerParser {
	private static final String DEFAULT_TEMPLATE_KEY = "default_template_key";
	private static final String DEFAULT_TEMPLATE_EXPRESSION = "default_template_expression";
	private static final Configuration CONFIGURER = new Configuration();

	static {
		CONFIGURER.setClassicCompatible(true);
		CONFIGURER.setSharedVariable("hash", new DalHash());
		CONFIGURER.setSharedVariable("mod", new DalMod());
		CONFIGURER.setSharedVariable("range", new DalRange());
		CONFIGURER.setSharedVariable("HASH", new DalHash());
		CONFIGURER.setSharedVariable("MOD", new DalMod());
		CONFIGURER.setSharedVariable("RANGE", new DalRange());
	}

	public static void setSharedVariable(String name, TemplateModel tm) {
		CONFIGURER.setSharedVariable(name, tm);
	}

	/**
	 * 配置SQL表达式缓存
	 */
	private static Map<String, Template> templateCache = new HashMap<String, Template>();
	/**
	 * 分库表达式缓存
	 */
	private static Map<String, Template> expressionCache = new HashMap<String, Template>();

	public static String process(String expression, Object root) {
		StringReader reader = null;
		StringWriter out = null;
		Template template = null;
		try {
			if (expressionCache.get(expression) != null) {
				template = expressionCache.get(expression);
			}
			if (template == null) {
				template = createTemplate(DEFAULT_TEMPLATE_EXPRESSION, new StringReader(expression));
				expressionCache.put(expression, template);
			}
			out = new StringWriter();
			template.process(root, out);
			return out.toString();
		} catch (Exception e) {
			throw new DalException(e);
		} finally {
			if (reader != null) {
				reader.close();
			}
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				return null;
			}
		}
	}

	private static Template createTemplate(String templateKey, StringReader reader) throws IOException {
		Template template = new Template(DEFAULT_TEMPLATE_KEY, reader, CONFIGURER);
		template.setNumberFormat("#");
		return template;
	}

	public static String process(Map<String, Object> root, String sql, String sqlId) {
		StringReader reader = null;
		StringWriter out = null;
		Template template = null;
		try {
			if (templateCache.get(sqlId) != null) {
				template = templateCache.get(sqlId);
			}
			if (template == null) {
				reader = new StringReader(sql);
				template = createTemplate(DEFAULT_TEMPLATE_KEY, reader);
				templateCache.put(sqlId, template);
			}
			out = new StringWriter();
			template.process(root, out);
			return out.toString();
		} catch (Exception e) {
			throw new DalException(e);
		} finally {
			if (reader != null) {
				reader.close();
			}
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				return null;
			}
		}
	}
}
