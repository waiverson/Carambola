package com.github.waiverson.carambola.support;


import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Variables{

    public static Pattern SPECIAL_REGEX_CHARS = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]");
    public static final Pattern VARIABLES_PATTERN = Pattern.compile("\\%([a-zA-Z0-9]+)\\%");

    private String nullValue = "null";

    Variables() { this(Config.getConfig());}

    Variables(Config c) {
        if (c != null) {
            this.nullValue = c.get("carambola.null.value.representation", "null");
        }
    }

    abstract public void put(String label, String val);

    abstract public String get(String label);

    public final String substitute(String text) {
        if (text ==  null) {
            return null;
        }
        Matcher m = VARIABLES_PATTERN.matcher(text);
        Map<String,String>  repalcements = new HashMap<String, String>();
        while (m.find()) {
            int gc = m.groupCount();
            if (gc == 1) {
                String g0 = m.group(0);
                String g1 = m.group(1);
                String value = get(g1);
                repalcements.put(g0, value);
            }
        }
        String newText = text;
        for (Map.Entry<String, String> en : repalcements.entrySet()) {
            String k = en.getKey();
            String replacement = repalcements.get(k);
            if (replacement != null) {
                String sanitisedReplacement = SPECIAL_REGEX_CHARS.matcher(replacement).replaceAll("\\\\$0");
                newText = newText.replaceAll(k, sanitisedReplacement);
            }
        }
        return newText;
    }

    public final String replaceNull(String s) {
        if (s == null) {
            return nullValue;
        }
        return s;
    }

}



