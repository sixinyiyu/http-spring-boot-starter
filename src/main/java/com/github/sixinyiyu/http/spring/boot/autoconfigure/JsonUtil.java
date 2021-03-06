/**
 * Project Name: http-spring-boot-autoconfigure
 * File Name:    JsonUtil.java
 * Package Name: com.github.sixinyiyu.http.spring.boot.autoconfigure
 * Date:         2019年6月22日下午10:39:12
 * Copyright (c) 2019, 思馨呓渝 All Rights Reserved.
 */
package com.github.sixinyiyu.http.spring.boot.autoconfigure;

import org.springframework.util.StringUtils;

/**
 * ClassName: JsonUtil
 * Function:  Json工具
 * Reason:    TODO ADD REASON
 * Date:      2019年6月22日 下午10:39:12
 * @author    sixinyiyu@gmail.com
 * @version   1.0
 * @since     JDK 1.8
 */
abstract class JsonUtil {

	/**
	 * 格式化json串
	 * @param  jsonStr 待格式json
	 * @return 格式化后字符串
	 */
	static String formatJson(String jsonStr) {
		if (!StringUtils.hasText(jsonStr))
			return "";
		StringBuilder sb = new StringBuilder();
		char last = '\0';
		char current = '\0';
		int indent = 0;
		for (int i = 0; i < jsonStr.length(); i++) {
			last = current;
			current = jsonStr.charAt(i);
			// 遇到{ [换行，且下一行缩进
			switch (current) {
			case '{':
			case '[':
				sb.append(current);
				sb.append('\n');
				indent++;
				addIndentBlank(sb, indent);
				break;
			// 遇到} ]换行，当前行缩进
			case '}':
			case ']':
				sb.append('\n');
				indent--;
				addIndentBlank(sb, indent);
				sb.append(current);
				break;
			// 遇到,换行
			case ',':
				sb.append(current);
				if (last != '\\') {
					sb.append('\n');
					addIndentBlank(sb, indent);
				}
				break;
			default:
				sb.append(current);
			}
		}
		return sb.toString();
	}

	/**
	 * 添加space
	 * @param  sb 拼接
	 * @param  indent 间隔
	 */
	private static void addIndentBlank(StringBuilder sb, int indent) {
		for (int i = 0; i < indent; i++) {
			sb.append('\t');
		}
	}

	/**
	 * http 请求数据返回 json 中中文字符为 unicode 编码转汉字转码
	 * @param  theString 字符
	 * @return 转码后字符串
	 */
	private static String decodeUnicode(String theString) {
		char aChar;
		int len = theString.length();
		StringBuilder outBuffer = new StringBuilder(len);
		for (int x = 0; x < len;) {
			aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = theString.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException("Malformed   \\uxxxx   encoding.");
						}

					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();
	}
}
