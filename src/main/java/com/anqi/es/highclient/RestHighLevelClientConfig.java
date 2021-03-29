package com.anqi.es.highclient;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.Node;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.log4j.BasicConfigurator;
/**
 * @author anqi
 */
@Configuration
@PropertySource("classpath:es-config.properties")
public class RestHighLevelClientConfig {

    @Value("${es.host}")
    private String host;
    @Value("${es.port}")
    private int port;
    
    @Value("${es.user}")
    private String user;
    @Value("${es.password}")
    private String password;
    
    @Value("${es.scheme}")
    private String scheme;
    @Value("${es.token}")
    private String token;
    @Value("${es.charset}")
    private String charSet;
    @Value("${es.client.connectTimeOut}")
    private int connectTimeOut;
    @Value("${es.client.socketTimeout}")
    private int socketTimeout;

    @Bean
    public RestClientBuilder restClientBuilder() {
        BasicConfigurator.configure();
        // 设置验证信息，填写账号及密码
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(user, password));
        // 初始化 RestClient, hostName 和 port 填写集群的内网 VIP 地址与端口
        
        RestClientBuilder restClientBuilder = RestClient.builder(
                new HttpHost(host, port, scheme)
        );

        Header[] defaultHeaders = new Header[]{
                new BasicHeader("Accept", "*/*"),
                new BasicHeader("Charset", charSet),
                //设置token 是为了安全 网关可以验证token来决定是否发起请求 我们这里只做象征性配置
                new BasicHeader("E_TOKEN", token)
        };
        restClientBuilder.setDefaultHeaders(defaultHeaders);
        restClientBuilder.setFailureListener(new RestClient.FailureListener(){
            @Override
            public void onFailure(Node node) {
                System.out.println("监听某个es节点失败");
            }
        });
        
        // 设置认证信息
        restClientBuilder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                });
        
        restClientBuilder.setRequestConfigCallback(builder ->
                builder.setConnectTimeout(connectTimeOut).setSocketTimeout(socketTimeout));
        return restClientBuilder;
    }

    @Bean
    public RestHighLevelClient restHighLevelClient(RestClientBuilder restClientBuilder) {
        return new RestHighLevelClient(restClientBuilder);
    }
}
