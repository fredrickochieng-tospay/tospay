package net.tospay.auth.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import net.tospay.auth.R;
import net.tospay.auth.model.TospayUser;
import net.tospay.auth.utils.Utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;


/**
 * Class providing app specific sharedPreference settings.
 */
public class SharedPrefManager {

    private static final String TAG = SharedPrefManager.class.getSimpleName();
    private static final String PREF_ACTIVE_USER = "pref_active_user";
    private static final String KEY_TOKEN_EXPIRY = "token_expiry";

    private static SharedPreferences sharedPref;
    private Context context;

    private SharedPrefManager(Context context) {
        this.context = context;
    }

    public static SharedPrefManager getInstance(Context context) {
        return new SharedPrefManager(context);
    }

    /**
     * Get active user info.
     *
     * @return user or null if nobody logged in.
     */
    public TospayUser getActiveUser() {
        SharedPreferences prefs = getSettings();
        String json = prefs.getString(PREF_ACTIVE_USER, "");
        if (json.isEmpty() || "null".equals(json)) {
            return null;
        } else {
            return Utils.getGsonParser().fromJson(json, TospayUser.class);
        }
    }

    /**
     * Set active user.
     *
     * @param user active user or null for disable user.
     */
    public void setActiveUser(TospayUser user) {
        if (user != null) {
            if (user.getExpiredAt() != null) {
                setTokenExpiryTime(user.getExpiredAt());
            }
        }

        String json = Utils.getGsonParser().toJson(user);
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(PREF_ACTIVE_USER, json);
        editor.apply();
    }

    /**
     * Obtain preferences instance.
     *
     * @return base instance of app SharedPreferences.
     */
    private SharedPreferences getSettings() {
        if (sharedPref == null) {
            sharedPref = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        }

        return sharedPref;
    }

    /**
     * Read stored value
     *
     * @param name - key
     * @param def  - default value
     * @return boolean
     */
    public boolean read(String name, boolean def) {
        SharedPreferences prefs = getSettings();
        return prefs.getBoolean(name, def);
    }

    /**
     * save shared pref data
     *
     * @param name  - key
     * @param value - value
     */
    public void save(String name, boolean value) {
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putBoolean(name, value);
        editor.apply();
    }

    /**
     * Token expiry time
     *
     * @param expireAt - long time
     */
    private void setTokenExpiryTime(long expireAt) {
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putLong(KEY_TOKEN_EXPIRY, expireAt);
        editor.apply();
    }

    /**
     * Checks token expiry ststus
     *
     * @return -boolean
     */
    public boolean isTokenExpiredOrAlmost() {
        SharedPreferences prefs = getSettings();
        long expireAt = prefs.getLong(KEY_TOKEN_EXPIRY, 0);
        if (expireAt == 0) {
            return true;
        } else {
            Date date = new Date(expireAt * 1000L);
            long diff = date.getTime() - Calendar.getInstance().getTime().getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            Log.e(TAG, "minutes: " + minutes);
            return diff < 5;
        }
    }
}
