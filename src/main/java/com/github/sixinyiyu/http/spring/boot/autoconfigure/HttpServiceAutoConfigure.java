/**
 * Project Name: http-spring-boot-autoconfigure
 * File Name:    HttpServiceAutoConfigure.java
 * Package Name: com.github.sixinyiyu.http.spring.boot.autoconfigure
 * Date:         2019年6月22日下午10:39:12
 * Copyright (c) 2019, 思馨呓渝 All Rights Reserved.
*/

package com.github.sixinyiyu.http.spring.boot.autoconfigure;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.java.Log;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * ClassName: HttpServiceAutoConfigure <br/>
 * Function:  TODO ADD FUNCTION <br/>
 * Reason:    TODO ADD REASON <br/>
 * Date:      2019年6月22日 下午10:39:12 <br/>
 * @author    sixinyiyu@gmail.com
 * @version   1.0
 * @since     JDK 1.8
 */
@Configuration
@ConditionalOnClass(OkHttpClient.class)
@Log
public class HttpServiceAutoConfigure {
	
	protected static final Charset UTF8 = StandardCharsets.UTF_8;

	private OkHttpClient httpClient;
	
	@PreDestroy
	public void destroy() {
		if(httpClient != null) {
			httpClient.dispatcher().executorService().shutdown();
			httpClient.connectionPool().evictAll();
		}
	}
	
	private void doBuildOkhttpClient() {
		HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
		logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)
				.readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).retryOnConnectionFailure(true)
				.sslSocketFactory(Objects.requireNonNull(getTrustedSSLSocketFactory()), x509TrustManager()).hostnameVerifier(DO_NOT_VERIFY)
				.addNetworkInterceptor(logInterceptor);
		
		httpClient = builder.build();
	}
	
	@Bean("httpClient")
    @ConditionalOnMissingBean
	public OkHttpClient okHttpClient() {
		doBuildOkhttpClient();
		log.info("Initializing HttpClient");
		return httpClient;
	}

	@Bean
	public X509TrustManager x509TrustManager() {
		return new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		};
	}

	private HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	private SSLSocketFactory getTrustedSSLSocketFactory() {
		try {
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, new TrustManager[] { x509TrustManager() }, new java.security.SecureRandom());
			return sslContext.getSocketFactory();
		} catch (KeyManagementException | NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Bean
	public ConnectionPool pool() {
		return new ConnectionPool(200, 5, TimeUnit.MINUTES);
	}
	
}

