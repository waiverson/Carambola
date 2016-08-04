package com.github.waiverson.carambola;

import com.github.waiverson.carambola.support.Config;
import com.github.waiverson.carambola.Carambola.Runner;
import com.github.waiverson.carambola.support.*;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import smartrics.rest.client.RestClient;
import smartrics.rest.client.RestClientImpl;
import smartrics.rest.client.RestRequest;
import smartrics.rest.client.RestResponse;


/**
 * Created by waiverson on 2016/8/3.
 */
public class PartsFactory {
    private final BobyTypeAdapterFactory bobyTypeAdapterFactory;

    public PartsFactory(final RunnerVariablesProvider variablesProvider) {
        this.bobyTypeAdapterFactory = new BobyTypeAdapterFactory(variablesProvider);
    }

    public RestClient buildRestClient(final Config config) {
        HttpClient httpClient = new HttpClientBuilder().createHttpClient(config);
        return new RestClientImpl(httpClient) {
            @Override
            protected URI createUri(String uriString, boolean escaped) throws URIException {
                boolean useNewHttpUriFactory = config.getAsBoolean("http.client.use.new.http.uri.factory", false);
                if (useNewHttpUriFactory) {
                    return new HttpURL(uriString);
                }
                return super.createUri(uriString, escaped);
            }

            @Override
            public String getMethodClassnameFromMethodName(String mName) {
                boolean useOverriddenHttpMethodImpl = config.getAsBoolean("http.client.use.new.http.uri.factory", false);
                if (useOverriddenHttpMethodImpl) {
                    return String.format("carambola.support.http.%sMethod", mName);
                }
                return super.getMethodClassnameFromMethodName(mName);
            }
        };
    }

    public RestRequest buildRestRequest() {return new RestRequest();}

    /**
     * Builds the appropriate formatter for a type of runner on this
     * RestFixture.
     *
     * @param runner
     *            the runner used to execute this Carambola
     * @return a formatter instance of CellFormatter
     */
    public CellFormatter<?> buildCellFormatter(Runner runner) {
        if (runner == null) {
            throw  new IllegalArgumentException("Runner is null");
        }
        if (Runner.DSL.equals(runner)) {
            return new DslFormatter();
        }
        if (Runner.TABLE.equals(runner)) {
            return new TableFormatter();
        }
        throw new IllegalArgumentException("Runner " + runner.name() + "not supported");
    }

    public BodyTypeAdapter buildBodyTypeAdapter(ContentType ct, String charset) {
        return bobyTypeAdapterFactory.getBodyTypeAdapter(ct, charset);
    }



}
