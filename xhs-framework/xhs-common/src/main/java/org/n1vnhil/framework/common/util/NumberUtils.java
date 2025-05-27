package org.n1vnhil.framework.common.util;

public class NumberUtils {

    public static String formatNumberString(long num) {
        if(num < 10000) {
            return String.valueOf(num);
        } else if(num < 100000000) {
            StringBuilder sb = new StringBuilder();
            sb.append(num / 10000);
            if(num % 10000 == 0) return sb.append("万").toString();
            return sb.append(".")
                    .append(num % 10000)
                    .append("万")
                    .toString();
        } else {
            return "9999万";
        }
    }
}
