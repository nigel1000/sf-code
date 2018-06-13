package cn.sf.auto.mybatis.util;

import lombok.NonNull;

/**
 * Created by nijianfeng on 18/1/29.
 */
public class NameUtils {

    public static final char UNDERLINE = '_';

    public static String ruleConvert(String input, @NonNull String fromRule, @NonNull String toRule) {
        if (fromRule.equals(toRule)) {
            return input;
        }
        if (Constants.camel.equals(fromRule) && Constants.underLine.equals(toRule)) {
            return camel2UnderLine(input);
        }
        if (Constants.underLine.equals(fromRule) && Constants.camel.equals(toRule)) {
            return underLine2Camel(input);
        }
        return input;
    }

    private static String camel2UnderLine(String input) {
        if (input == null || "".equals(input.trim())) {
            return "";
        }
        int len = input.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = input.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i != 0) {
                    sb.append(UNDERLINE);
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String underLine2Camel(String input) {
        if (input == null || "".equals(input.trim())) {
            return "";
        }
        int len = input.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = input.charAt(i);
            if (c == UNDERLINE) {
                if (++i < len) {
                    sb.append(Character.toUpperCase(input.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String firstUpper(String input) {
        if (input == null || "".equals(input.trim())) {
            return "";
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1, input.length());
    }

    public static String firstLower(String input) {
        if (input == null || "".equals(input.trim())) {
            return "";
        }
        return input.substring(0, 1).toLowerCase() + input.substring(1, input.length());
    }

}
