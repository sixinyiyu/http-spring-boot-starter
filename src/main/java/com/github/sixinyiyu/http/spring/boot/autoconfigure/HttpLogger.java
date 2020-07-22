/**
 * Project Name: http-spring-boot-autoconfigure
 * File Name:    HttpLogger.java
 * Package Name: com.github.sixinyiyu.http.spring.boot.autoconfigure
 * Date:         2019年6月22日下午10:39:12
 * Copyright (c) 2019, 思馨呓渝 All Rights Reserved.
 */
package com.github.sixinyiyu.http.spring.boot.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import okhttp3.logging.HttpLoggingInterceptor.Logger;

@Slf4j
public class HttpLogger implements Logger {

	private StringBuilder mMessage = new StringBuilder(2000);

	@Override
	public void log(String message) {
		if (message.startsWith("--> POST")) {
			mMessage.setLength(0);
		}
		if ((message.startsWith("{") && message.endsWith("}")) || (message.startsWith("[") && message.endsWith("]"))) {
			message = JsonUtil.formatJson(message);
		}
		mMessage.append(message.concat("\n"));
		if (message.startsWith("<-- END HTTP")) {
			log.info(mMessage.toString());
		}
	}

}
