package com.github.waiverson.carambola.support;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.params.HostParams;
import org.apache.commons.httpclient.params.HttpClientParams;


/**
 * Created by waiverson on 2016/8/3.
 */


public class HttpClientBuilder {
    /**
     * default value of the socket timeout: 3000ms.
     */
    public static final Integer DEFAULT_SO_TO = 3000;
    /**
     * default value of the proxy port: 80.
     */
    public static final Integer DEFAULT_PROXY_PORT = 80;

    /**
     * @param config the {@link Config} containing the client configuration paramteres.
     * @return an instance of an {@link HttpClient}.
     */
    public HttpClient createHttpClient(final Config config) {
        HttpClient client = createConfiguredClient(config);
        if (config != null) {
            configureHost(config, client);
            configureCredentials(config, client);
        }
        return client;
    }

    private HttpClient createConfiguredClient(final Config config) {
        HttpClientParams params = new HttpClientParams();
        params.setSoTimeout(DEFAULT_SO_TO);
        if (config != null) {
            params.setSoTimeout(config.getAsInteger("http.client.connection.timeout", DEFAULT_SO_TO));
        }
        HttpClient client = new HttpClient(params);
        return client;
    }

    private void configureHost(final Config config, HttpClient client) {
        HostConfiguration hostConfiguration = client.getHostConfiguration();
        String proxyHost = config.get("http.proxy.host");
        if (proxyHost != null) {
            int proxyPort = config.getAsInteger("http.proxy.port", DEFAULT_PROXY_PORT);
            hostConfiguration.setProxy(proxyHost, proxyPort);
        }
        HostParams hostParams = new HostParams();
        hostConfiguration.setParams(hostParams);
    }

    private void configureCredentials(final Config config, HttpClient client) {
        String username = config.get("http.basicauth.username");
        String password = config.get("http.basicauth.password");
        if (username != null && password != null) {
            Credentials defaultcreds = new UsernamePasswordCredentials(username, password);
            client.getState().setCredentials(AuthScope.ANY, defaultcreds);
        }
    }
}

