package com.adanac.framework.dac.parsing;

import java.util.Properties;

/**
 * 描述：属性解析类
 * 
 * @author 13092011
 */
public class PropertyParser {

	public static String parse(String string, Properties variables) {
		VariableTokenHandler handler = new VariableTokenHandler(variables);
		GenericTokenParser parser = new GenericTokenParser("${", "}", handler);
		return parser.parse(string);
	}

	/**
	 * 描述：变量处理器
	 * 
	 * @author 作者 13092011
	 */
	private static class VariableTokenHandler implements TokenHandler {
		private Properties variables;

		public VariableTokenHandler(Properties variables) {
			this.variables = variables;
		}

		public String handleToken(String content) {
			if (variables != null && variables.containsKey(content)) {
				return variables.getProperty(content);
			}
			return "${" + content + "}";
		}
	}
}