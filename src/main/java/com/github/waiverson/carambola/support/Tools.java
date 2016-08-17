package com.github.waiverson.carambola.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
    public static String fromSimpleTag(String somethingWithinATag) {
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

    public static String fromHtml(String text) {
        String ls = "\n";
        return text.replaceAll("<br[\\s]*/>", ls).replaceAll("<BR[\\s]*/>", ls)
                .replaceAll("<span[^>]*>", "").replaceAll("</span>", "")
                .replaceAll("<pre>", "").replaceAll("</pre>", "")
                .replaceAll("&nbsp;", " ").replaceAll("&gt;", ">")
                .replaceAll("&amp;", "&").replaceAll("&lt;", "<")
                .replaceAll("&nbsp;", " ");
    }

    public static boolean regex(String text, String expr) {
        try {
            Pattern p = Pattern.compile(expr);
            boolean find = p.matcher(text).find();
            return find;
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Invaild regex " +expr);
        }
    }

    public static String wrapInDiv(String body) {
        return String.format("<div>%s</div>", body);
    }

    /**
     * Substitutions:
     * <table border="1">
     * <tr><td><code>&lt;pre></code> and <code>&lt;/pre></code></td><td><code>""</code></td></tr>
     * <tr><td><code>&lt;</code></td><td><code>&amp;lt;</code></td></tr>
     * <tr><td><code>\n</code></td><td><code>&lt;br /></code></td></tr>
     * <tr><td><code>&nbsp;</code> <i>(space)</i></td><td><code>&amp;nbsp;</code></td></tr>
     * <tr><td><code>-----</code> <i>(5 hyphens)</i></td><td><code>&lt;hr /></code></td></tr>
     * </table>
     *
     * @param text
     *            some text.
     * @return the html.
     */
    public static String toHtml(String text) {
        return text.replaceAll("<pre>", "").replaceAll("</pre>", "")
                .replaceAll("<", "&lt;").replaceAll(">", "&gt;")
                .replaceAll("\n", "<br/>").replaceAll("\t", "    ")
                .replaceAll(" ", "&nbsp;").replaceAll("-----", "<hr/>");
    }

    /**
     * @param c
     *            some text
     * @return the text within <code>&lt;code></code> tags.
     */
    public static String toCode(String c) {
        return "<code>" + c + "</code>";
    }

    /**
     * @param string a string
     * @return the string htmlified as a dsl label.
     */
    public static String toHtmlLabel(String string) {
        return "<i><span class='dsl_label'>" + string + "</span></i>";
    }

    /**
     * @param expected
     *            the expected value
     * @param typeAdapter
     *            the body adapter for the cell
     * @param formatter
     *            the formatter
     * @param minLenForToggle
     *            the value determining whether the content should be rendered
     *            as a collapseable section.
     * @return the formatted content for a cell with a wrong expectation
     */
    public static String makeContentForWrongCell(String expected,
                                                 RestDataTypeAdapter typeAdapter, CellFormatter<?> formatter,
                                                 int minLenForToggle) {
        StringBuffer sb = new StringBuffer();
        sb.append(Tools.toHtml(expected));
        if (formatter.isDisplayActual()) {
            sb.append(toHtml("\n"));
            sb.append(formatter.label("expected"));
            String actual = typeAdapter.toString();
            sb.append(toHtml("-----"));
            sb.append(toHtml("\n"));
            if (minLenForToggle >= 0 && actual.length() > minLenForToggle) {
                sb.append(makeToggleCollapseable("toggle actual", toHtml(actual)));
            } else {
                sb.append(toHtml(actual));
            }
            sb.append(toHtml("\n"));
            sb.append(formatter.label("actual"));
        }
        List<String> errors = typeAdapter.getErrors();
        if (errors.size() > 0) {
            sb.append(toHtml("-----"));
            sb.append(toHtml("\n"));
            for (String e : errors) {
                sb.append(toHtml(e + "\n"));
            }
            sb.append(toHtml("\n"));
            sb.append(formatter.label("errors"));
        }
        return sb.toString();
    }

    /**
     * @param expected the expected value
     * @param typeAdapter the body type adaptor
     * @param formatter the formatter
     *            the value determining whether the content should be rendered
     *            as a collapseable section.
     * @param minLenForToggle
     *            the value determining whether the content should be rendered
     *            as a collapseable section.
     * @return the formatted content for a cell with a right expectation
     */
    public static String makeContentForRightCell(String expected,
                                                 RestDataTypeAdapter typeAdapter, CellFormatter<?> formatter,
                                                 int minLenForToggle) {
        StringBuffer sb = new StringBuffer();
        sb.append(toHtml(expected));
        String actual = typeAdapter.toString();
        if (formatter.isDisplayActual() && !expected.equals(actual)) {
            sb.append(toHtml("\n"));
            sb.append(formatter.label("expected"));
            sb.append(toHtml("-----"));
            sb.append(toHtml("\n"));
            if (minLenForToggle >= 0 && actual.length() > minLenForToggle) {
                sb.append(makeToggleCollapseable("toggle actual", toHtml(actual)));
            } else {
                sb.append(toHtml(actual));
            }
            sb.append(toHtml("\n"));
            sb.append(formatter.label("actual"));
        }
        return sb.toString();
    }

    /**
     * @param message
     *            the message to be included in the collapsable section header.
     * @param content
     *            the content collapsed.
     * @return a string with the html/js code to implement a collapsable section
     *         in fitnesse.
     */
    public static String makeToggleCollapseable(String message, String content) {
        Random random = new Random();
        String id = Integer.toString(content.hashCode())
                + Long.toString(random.nextLong());
        StringBuffer sb = new StringBuffer();
        sb.append("<a href=\"javascript:toggleCollapsable('" + id + "');\">");
        sb.append("<img src='/files/images/collapsableClosed.gif' class='left' id='img"
                + id + "'/>" + message + "</a>");
        sb.append("<div class='hidden' id='" + id + "'>").append(content)
                .append("</div>");
        return sb.toString();
    }

    /**
     * @param href
     *            a string ending up in the anchor href.
     * @param text
     *            a string within anchors.
     * @return the string htmlified as a html link.
     */
    public static String toHtmlLink(String href, String text) {
        return "<a href=" + href + ">" + text + "</a>";
    }
}
