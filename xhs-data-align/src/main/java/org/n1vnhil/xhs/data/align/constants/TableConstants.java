package org.n1vnhil.xhs.data.align.constants;

public class TableConstants {

    private static final String TABLE_NAME_SEPARATE = "_";

    public static String buildTableNameSuffix(String date, Long hashKey) {
        return date + TABLE_NAME_SEPARATE + hashKey;
    }

}
