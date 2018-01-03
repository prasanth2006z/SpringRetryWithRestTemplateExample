package com.mcx;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;

import com.mcx.service.MCXService;
import com.mcx.service.McxRetryService;
import com.mcx.service.impl.MCXServiceImpl;
import com.mcx.service.impl.McxRetryServiceImpl;
@Configuration
public class AppConfig {

	@Bean
	public TrustStrategy trustStrategy() {
		return new TrustStrategy() {
			@Override
			public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
				return true;
			}
		};
	}

	// SSL context for secure connections can be created either based on
	// system or application specific properties.
	@Bean
	public SSLContext sslcontext() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		return SSLContexts.custom().loadTrustMaterial(null, trustStrategy()).build();
	}

	@Bean
	public SSLConnectionSocketFactory sslConnectionFactory()
			throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		return new SSLConnectionSocketFactory(sslcontext(), NoopHostnameVerifier.INSTANCE);
	}

	// Create a registry of custom connection socket factories for supported
	// protocol schemes.
	@Bean
	public Registry<ConnectionSocketFactory> socketFactoryRegistry()
			throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		return RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE)
				.register("https", sslConnectionFactory()).build();
	}

	@Bean
	public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager()
			throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(
				socketFactoryRegistry());
		poolingHttpClientConnectionManager.setMaxTotal(10);// max connection
		poolingHttpClientConnectionManager.setDefaultMaxPerRoute(20);
		poolingHttpClientConnectionManager.setValidateAfterInactivity(50);
		return poolingHttpClientConnectionManager;
	}

	@Bean
	public CloseableHttpClient httpclient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		return HttpClients.custom().setConnectionManager(poolingHttpClientConnectionManager())
				.setSSLContext(sslcontext()).build();
	}
	
	public ClientHttpRequestFactory getClientHttpRequestFactory() throws KeyManagementException, KeyStoreException, NoSuchAlgorithmException {
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		clientHttpRequestFactory.setHttpClient(httpclient());
		clientHttpRequestFactory.setReadTimeout(5000);
		clientHttpRequestFactory.setConnectTimeout(5000);
		return clientHttpRequestFactory;
	}

	@Bean
	public RestTemplate restTemplate() throws KeyManagementException, KeyStoreException, NoSuchAlgorithmException {
		return new RestTemplate(getClientHttpRequestFactory());
	}

	@Bean
	public MCXService mcxService() {
		return new MCXServiceImpl();
	}
	
	
	@Bean
	public McxRetryService mcxRetryService() {
		return new McxRetryServiceImpl();
	}

}
