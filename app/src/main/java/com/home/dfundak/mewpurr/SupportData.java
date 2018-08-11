package com.home.dfundak.mewpurr;

import com.home.dfundak.mewpurr.Class.Alarm;

public class SupportData {
    public static String DB_NAME = "mewpurr";
    public static String API_KEY = "iX_PdN_3siXeqgAgPwxAszSv-kMnxSuC";
    public static String COLLECTION_NAME = "alarms";

    public static String getAddressAPI() {
        String baseUrl = String.format("https://api.mlab.com/api/1/databases/%s/collections/%s", DB_NAME, COLLECTION_NAME);
        StringBuilder stringBuilder = new StringBuilder(baseUrl);
        stringBuilder.append("?apiKey=" + API_KEY);
        return stringBuilder.toString();
    }

    public static String getAddressSingle(Alarm alarm) {
        String baseUrl = String.format("https://api.mlab.com/api/1/databases/%s/collections/%s", DB_NAME, COLLECTION_NAME);
        StringBuilder stringBuilder = new StringBuilder(baseUrl);
        stringBuilder.append("/" + alarm.get_id().getOid() + "?apiKey=" + API_KEY);
        return stringBuilder.toString();
    }
}
