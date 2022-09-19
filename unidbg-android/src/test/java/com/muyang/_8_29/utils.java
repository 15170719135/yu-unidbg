package com.muyang._8_29;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class utils {
    public static final String FIELD_DELIMITER = "&";
    public static final String KEY_VALUE_DELIMITER = "=";

    private static final char[] f84322a = "0123456789ABCDEF".toCharArray();


    private static boolean a(char c2, String str) {
        return (c2 >= 'A' && c2 <= 'Z') || (c2 >= 'a' && c2 <= 'z') || !((c2 < '0' || c2 > '9') && "-_.~".indexOf(c2) == -1 && (str == null || str.indexOf(c2) == -1));
    }

    static String r(Map<String, String> map) {
        String str;
        if (!(map instanceof SortedMap)) {
            map = new TreeMap(map);
        }
        StringBuilder sb = new StringBuilder(256);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            if (!key.isEmpty()) {
                sb.append(urlEncode(key));
                sb.append(KEY_VALUE_DELIMITER);
                String value = entry.getValue();
                if (value == null) {
                    str = "";
                } else {
                    str = urlEncode(value);
                }
                sb.append(str);
                sb.append(FIELD_DELIMITER);
            }
        }
        int length = sb.length();
        if (length > 0) {
            sb.deleteCharAt(length - 1);
        }
        if (length == 0) {
            return null;
        }
        return sb.toString();
    }
    public static String urlEncode(String str) {
        return b(str, null);
    }

    static String b(String str, String str2) {
        StringBuilder sb = null;
        if (str == null) {
            return null;
        }
        int length = str.length();
        int i = 0;
        while (i < length) {
            int i2 = i;
            while (i2 < length && a(str.charAt(i2), str2)) {
                i2++;
            }
            if (i2 != length) {
                if (sb == null) {
                    sb = new StringBuilder();
                }
                if (i2 > i) {
                    sb.append((CharSequence) str, i, i2);
                }
                i = i2 + 1;
                while (i < length && !a(str.charAt(i), str2)) {
                    i++;
                }
                try {
                    byte[] bytes = str.substring(i2, i).getBytes("UTF-8");
                    int length2 = bytes.length;
                    for (int i3 = 0; i3 < length2; i3++) {
                        sb.append('%');
                        char[] cArr = f84322a;
                        sb.append(cArr[(bytes[i3] & 240) >> 4]);
                        sb.append(cArr[bytes[i3] & 15]);
                    }
                } catch (UnsupportedEncodingException e2) {
                    throw new AssertionError(e2);
                }
            } else if (i == 0) {
                return str;
            } else {
                sb.append((CharSequence) str, i, length);
                return sb.toString();
            }
        }
        return sb == null ? str : sb.toString();
    }
}
