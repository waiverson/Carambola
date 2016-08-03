package com.github.waiverson.carambola.support;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by waiverson on 2016/8/2.
 */
public final class Config {

    public static final String DEFAULT_CONFIG_NAME = "default";

    private static final Map<String, Config> CONFIGURATIONS = new HashMap<String, Config>();

    private final String name;
    private Map<String, String> data;

    private Config(final String name) {
        this.name = name;
        this.data = new HashMap<String, String>();
    }

    public static Config getConfig() {
        return getConfig(DEFAULT_CONFIG_NAME);
    }

    public String getName() {return name;}

    public void add(String key, String value) {data.put(key, value);}

    public String get(String key) {return data.get(key);}

    public String get(String key, String def) {
        String v = get(key);
        if (v == null) {
            v = def;
        }
        return v;
    }

    public Long getAsLong(String key, Long def) {
        String val = get(key);
        try {
            return Long.parseLong(val);
        }
        catch (NumberFormatException e) {
            return def;
        }
    }

    public Boolean getAsBoolean(String key, Boolean def) {
        String val = get(key);
        if (val ==null) {
            return def;
        }
        return Boolean.parseBoolean(val);
    }

    public Integer getAsInteger(String key, Integer def) {
        String val = get(key);
        try {
            return Integer.parseInt(val);
        }
        catch (NumberFormatException e) {
            return  def;
        }
    }

    public Map<String, String> getAsMap(String key, Map<String, String> def) {
        String val = get(key);
        try {
            return Tools.convertStringToMap(val, "\n", "=", true);
        }
        catch (RuntimeException e) {
            return def;
        }
    }

    public void clear() {data.clear();}

    @Override
    public String toString() {return "[name=" + getName() + "]" + data.toString();}



    public static Config getConfig(String name) {
        if (name == null) {
            name = DEFAULT_CONFIG_NAME;
        }
        Config namedConfig = CONFIGURATIONS.get(name);
        if (namedConfig == null) {
            namedConfig = new Config(name);
            CONFIGURATIONS.put(name, namedConfig);
        }
        return namedConfig;
    }



}
