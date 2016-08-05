package com.github.waiverson.carambola.support;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by waievrson on 2016/8/4.
 */
public class Url {

    private URL baseUrl;

    public Url(String url) {
        try {
            if (url == null || "".equals(url.trim())) {
            throw new IllegalArgumentException("Null or empty input: " + url);
        }
            String u = url;
            if (url.endsWith("/")) {
                u = url.substring(0, u.length()-1);
            }
            baseUrl = new URL(u);
            if("".equals((baseUrl.getHost()))) {
                throw new IllegalArgumentException("No Host specified in base URL: " + url);
            }
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed base URL: " + url, e);
        }
    }

    public URL getUrl() {
        return baseUrl;
    }

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

    public String getResource() {
        String res = getUrl().getPath().trim();
        if (res.isEmpty()) {
            return "/";
        }
        return res;
    }




}
