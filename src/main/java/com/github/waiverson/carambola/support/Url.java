package com.github.waiverson.carambola.support;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Facade to {@link java.net.URL}. Just to offer a REST oriented interface.
 *
 * @author xyc
 *
 */
public class Url {

    private URL baseUrl;

    /**
     * @param url
     *            the string representation of url.
     */
    public Url(String url) {
        try {
            if (url == null || "".equals(url.trim())) {
                throw new IllegalArgumentException("Null or empty input: "
                        + url);
            }
            String u = url;
            if (url.endsWith("/")) {
                u = url.substring(0, u.length() - 1);
            }
            baseUrl = new URL(u);
            if ("".equals(baseUrl.getHost())) {
                throw new IllegalArgumentException(
                        "No host specified in base URL: " + url);
            }
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed base URL: " + url, e);
        }
    }

    /**
     * @return the base url
     */
    public URL getUrl() {
        return baseUrl;
    }

    @Override
    public String toString() {
        return getUrl().toExternalForm();
    }

    /**
     * @return the resource
     */
    public String getResource() {
        String res = getUrl().getPath().trim();
        if (res.isEmpty()) {
            return "/";
        }
        return res;
    }

    /**
     *
     * @return the base url.
     */
    public String getBaseUrl() {
        String path = getResource().trim();
        if (path.length() == 0 || path.equals("/")) {
            return toString();
        }
        int index = toString().indexOf(getResource());
        if (index >= 0) {
            return toString().substring(0, index);
        } else {
            throw new IllegalStateException("Invalid URL");
        }
    }

    /**
     * builds a url
     *
     * @param file
     *            the file
     * @return the full url.
     */
    public URL buildURL(String file) {
        try {
            return new URL(baseUrl, file);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL part: " + file);
        }
    }

}