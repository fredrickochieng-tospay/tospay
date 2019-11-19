package net.tospay.auth.utils;

public class CountryFlagUtils {

    private CountryFlagUtils() {

    }

    public static String flag(String countryCode) {
        int flagOffset = 0x1F1E6;
        int asciiOffset = 0x41;

        int firstChar = Character.codePointAt(countryCode, 0) - asciiOffset + flagOffset;
        int secondChar = Character.codePointAt(countryCode, 1) - asciiOffset + flagOffset;

        return new String(Character.toChars(firstChar)) + new String(Character.toChars(secondChar));
    }
}
