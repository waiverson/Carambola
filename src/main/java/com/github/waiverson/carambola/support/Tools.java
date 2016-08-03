package com.github.waiverson.carambola.support;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by waiverson on 2016/8/2.
 */
public class Tools {

    private Tools() {}


    public static Map<String, String> convertStringToMap(final String expStr,
                                                         final String nvSep, final String entrySep, boolean cleanTags) {
        String sanitisedExpStr = expStr.trim();
        sanitisedExpStr = removeOpenEscape(sanitisedExpStr);
        sanitisedExpStr = removeCloseEscape(sanitisedExpStr);
        sanitisedExpStr = sanitisedExpStr.trim();
        String[] nvpArray = sanitisedExpStr.split(entrySep);
        Map<String, String> ret = new HashMap<String, String>();
        for (String nvp : nvpArray) {
            try {
                nvp = nvp.trim();
                if ("".equals(nvp)) {
                    continue;
                }
                nvp = removeOpenEscape(nvp).trim();
                nvp = removeCloseEscape(nvp).trim();
                String[] nvpArr = nvp.split(nvSep);
                String k, v;
                k = nvpArr[0].trim();
                v = "";
                if (nvpArr.length == 2) {
                    v = nvpArr[1].trim();
                }
                else if (nvpArr.length > 2) {
                    int pos = nvp.indexOf(nvSep) + nvSep.length();
                    v = nvp.substring(pos).trim();
                }
                if (cleanTags) {
                    ret.put(k, fromSimpleTag(v));
                }
                else {
                    ret.put(k, v);
                }
            }
            catch (RuntimeException e) {
                throw new IllegalArgumentException(
                        "Each entry in the must be separated by '"
                                + entrySep
                                + "' and each entry must be expressed as a name"
                                + nvSep + "value"
                );
            }
        }
        return ret;
    }

    /**
     * @param somethingWithinATag
     *            some text enclosed in some html tag.
     * @return the text within the tag.
     */
    private static String fromSimpleTag(String somethingWithinATag) {
        return somethingWithinATag.replaceAll("<[^>]+>", "").replace("<[^>]+>", "");
    }

    private static String removeCloseEscape(String str) {
        return trimStartEnd("-!", str);
    }

    private static String removeOpenEscape(String str) {
        return trimStartEnd("!-", str);
    }

    private static String trimStartEnd(String pattern, String str) {
        if (str.startsWith(pattern)) {
            str = str.substring(2);
        }
        if (str.endsWith(pattern)) {
            str = str.substring(0, str.length() - 2);
        }
        return str;
    }

}
