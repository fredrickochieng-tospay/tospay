package net.tospay.auth.utils;

import java.text.NumberFormat;

public class StringUtil {

    public static String getCurrency(String currency, double amount) {
        return currency + " " + NumberFormat.getInstance().format(amount);
    }

    public static String getCurrency(double amount) {
        return NumberFormat.getInstance().format(amount);
    }

    public static String getCurrency(int amount) {
        return NumberFormat.getInstance().format(amount);
    }
}
