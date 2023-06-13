package org.bitkernel.client;

import com.sun.istack.NotNull;
import lombok.Getter;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpUtils {
    private static final int MAX_TOTAL_CONNECTIONS = 200; // 最大连接数
    private static final int MAX_PER_ROUTE = 20; // 每个路由的最大连接数
    private static final int SOCKET_TIMEOUT = 5000; // socket超时时间
    private static final int CONNECT_TIMEOUT = 5000; // 连接超时时间
    private static final int CONNECTION_REQUEST_TIMEOUT = 5000; // 连接请求超时时间

    @Getter
    private static CloseableHttpClient httpClient;

    static {
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        connManager.setDefaultMaxPerRoute(MAX_PER_ROUTE);

        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(SOCKET_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                .build();

        httpClient = HttpClientBuilder.create()
                .setConnectionManager(connManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    @NotNull
    public static String sendGetRequest(@NotNull String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                return EntityUtils.toString(response.getEntity());
            } else {
                throw new IOException("Unexpected status code: " + statusCode);
            }
        }
    }
}
