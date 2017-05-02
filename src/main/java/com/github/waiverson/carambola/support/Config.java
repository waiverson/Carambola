package com.github.waiverson.carambola.support;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by waiverson on 2016/8/2.
 */
public final class Config {
    /**
     * the default name of the named config: the actual value is
     * {@code "default"}.
     */
    public static final String DEFAULT_CONFIG_NAME = "default";

    /**
     * the static bucket where the config data is stored.
     */
    private static final Map<String, Config> CONFIGURATIONS = new HashMap<String, Config>();

    /**
     * the configuration with default name. See
     * {@link Config#DEFAULT_CONFIG_NAME};
     *
     * @return the config with default name.
     */
    public static Config getConfig() {
        return getConfig(DEFAULT_CONFIG_NAME);
    }

    /**
     * @param name
     *            the name of the config.
     * @return the named config object.
     */
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

    /**
     * this instance name.
     */
    private final String name;

    private Map<String, String> data;

    /**
     * creates a config with a given name.
     *
     * @param name
     *            the name of this config
     */
    private Config(final String name) {
        this.name = name;
        this.data = new HashMap<String, String>();
    }

    /**
     * This config name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Adds a key/value pair to a named configuration.
     *
     * @param key
     *            the key
     * @param value
     *            the value
     */
    public void add(String key, String value) {
        data.put(key, value);
    }

    /**
     * Returns a key/value from a named config.
     *
     * @param key
     *            the key
     * @return the value
     */
    public String get(String key) {
        return data.get(key);
    }

    /**
     * Returns a key/value from a named config.
     *
     * @param key
     *            the key
     * @param def
     *            the default value to return when a value for the key is not
     *            present.
     * @return the value, Returns the default if the key is not found in the map
     */
    public String get(String key, String def) {
        String v = get(key);
        if (v == null) {
            v = def;
        }
        return v;
    }

    /**
     * returns a key/value from a named config, parsed as Long.
     *
     * @param key
     *            the key
     * @param def
     *            the default value for value not existent or not parseable
     * @return a Long representing the value, def if the value cannot be parsed
     *         as Long
     */
    public Long getAsLong(String key, Long def) {
        String val = get(key);
        try {
            return Long.parseLong(val);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    /**
     * returns a key/value from a named config, parsed as Boolean.
     *
     * @param key
     *            the key
     * @param def
     *            the default value for value not existent or not parseable
     * @return a Boolean representing the value, def if the value cannot be
     *         parsed as Boolean
     */
    public Boolean getAsBoolean(String key, Boolean def) {
        String val = get(key);
        if (val == null) {
            return def;
        }
        return Boolean.parseBoolean(val);
    }

    /**
     * returns a key/value from a named config, parsed as Integer.
     *
     * @param key
     *            the key
     * @param def
     *            the default value for value not existent or not parseable
     * @return a Integer representing the value, def if the value cannot be
     *         parsed as Integer
     */
    public Integer getAsInteger(String key, Integer def) {
        String val = get(key);
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    /**
     * returns a key/value from a named config, parsed as a Map<String, String>.
     * Each line (separated by \n) in the value is parsed as {@code name=value}
     * and stored in the returned map.
     *
     * @param key
     *            the key
     * @param def
     *            the default map to return if key is not present in the config.
     * @return a map representing the key value.
     */
    public Map<String, String> getAsMap(String key, Map<String, String> def) {
        String val = get(key);
        try {
            return Tools.convertStringToMap(val, "\n", "=", true);
        } catch (RuntimeException e) {
            return def;
        }
    }

    /**
     * Clears a config store.
     */
    public void clear() {
        data.clear();
    }

    @Override
    public String toString() {
        return "[name=" + getName() + "] " + data.toString();
    }
}

