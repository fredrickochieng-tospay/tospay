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

    /**
     * Get flag from ascii code
     *
     * @param countryCode -  country code
     * @return country flag
     */
    public static String flag(String countryCode) {
        int flagOffset = 0x1F1E6;
        int asciiOffset = 0x41;

        int firstChar = Character.codePointAt(countryCode, 0) - asciiOffset + flagOffset;
        int secondChar = Character.codePointAt(countryCode, 1) - asciiOffset + flagOffset;

        return new String(Character.toChars(firstChar)) + new String(Character.toChars(secondChar));
    }
}
