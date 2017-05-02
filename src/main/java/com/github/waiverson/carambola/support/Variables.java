package com.github.waiverson.carambola.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import fit.Fixture;

/**
 * Facade to FitNesse global symbols map.
 *
 * @author xyc
 */
public abstract class Variables {
    /**
     * pattern matching a variable name: {@code \%([a-zA-Z0-9_]+)\%}
     */
    public static Pattern SPECIAL_REGEX_CHARS = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]");
    public static final Pattern VARIABLES_PATTERN = Pattern.compile("\\%([a-zA-Z0-9_]+)\\%");
    private static final String FIT_NULL_VALUE = fitSymbolForNull();
    private String nullValue = "null";

    /**
     * initialises variables with default config. See @link
     * {@link #Variables(Config)}
     */
    Variables() {
        this(Config.getConfig());
    }

    /**
     * initialises the variables. reade
     * {@code restfixture.null.value.representation} to know how to render
     * {@code null}s.
     *
     * @param c
     */
    Variables(Config c) {
        if (c != null) {
            this.nullValue = c.get("restfixture.null.value.representation", "null");
        }
    }

    /**
     * puts a value.
     *
     * @param label
     * @param val
     */
    abstract public void put(String label, String val);

    /**
     * gets a value.
     *
     * @param label
     * @return the value.
     */
    abstract public String get(String label);

    /**
     * replaces a text with variable values.
     * @param text
     * @return the substituted text.
     */
    public final String substitute(String text) {
        if (text == null) {
            return null;
        }
        Matcher m = VARIABLES_PATTERN.matcher(text);
        Map<String, String> replacements = new HashMap<String, String>();
        while (m.find()) {
            int gc = m.groupCount();
            if (gc == 1) {
                String g0 = m.group(0);
                String g1 = m.group(1);
                String value = get(g1);
                if (FIT_NULL_VALUE.equals(value)) {
                    value = nullValue;
                }
                replacements.put(g0, value);
            }
        }
        String newText = text;
        for (Entry<String, String> en : replacements.entrySet()) {
            String k = en.getKey();
            String replacement = replacements.get(k);
            if (replacement != null) {
                // this fixes issue #118
                String sanitisedReplacement = SPECIAL_REGEX_CHARS.matcher(replacement).replaceAll("\\\\$0");;
                newText = newText.replaceAll(k, sanitisedReplacement);
            }
        }
        return newText;
    }

/*
    private static String fitSymbolForNull() {
        final String k = "somerandomvaluetogettherepresentationofnull-1234567890";
        Fixture.setSymbol(k, null);
        return Fixture.getSymbol(k).toString();
    }
*/

    /**
     * @param s
     * @return the null representation if the input is null.
     */
    public final String replaceNull(String s) {
        if (s == null) {
            return nullValue;
        }
        return s;
    }
}




