package net.tospay.auth.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Utils {

    private static Gson gson;

    /**
     * Add specific parsing to gson
     *
     * @return new instance of {@link Gson}
     */
    public static Gson getGsonParser() {
        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gson = gsonBuilder.create();
        }
        return gson;
    }
}
