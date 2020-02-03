package net.tospay.auth;

import android.content.Context;
import android.content.Intent;

import net.tospay.auth.model.TospayUser;
import net.tospay.auth.ui.auth.AuthActivity;
import net.tospay.auth.ui.main.TospayActivity;
import net.tospay.auth.utils.Constants;
import net.tospay.auth.utils.SharedPrefManager;

import static net.tospay.auth.utils.Constants.KEY_TAC_URL;
import static net.tospay.auth.utils.Constants.KEY_TOKEN;

public class Tospay {

    private String token;
    private String tacUrl;
    private Context context;

    private Tospay(Context context) {
        this.context = context;
    }

    /**
     * Creates an instance of this class
     *
     * @param context - application context
     * @return this
     */
    public static Tospay getInstance(Context context) {
        return new Tospay(context);
    }

    /**
     * Returns an instance of shared pref manager
     *
     * @param context - application context
     * @return SharedPrefManager
     */
    public SharedPrefManager getSharedPrefManager(Context context) {
        return SharedPrefManager.getInstance(context);
    }

    /**
     * Returns currently logged in user
     *
     * @return TospayUser
     */
    public TospayUser getCurrentUser() {
        return SharedPrefManager.getInstance(context).getActiveUser();
    }

    /**
     * Logs out user
     */
    public void signOut() {
        SharedPrefManager.getInstance(context).setActiveUser(null);
    }

    /**
     * sets payment validation token
     *
     * @param token - validation token
     * @return this class instance
     */
    public Tospay setPaymentToken(String token) {
        if (token == null) throw new RuntimeException("Payment token cannot be null");
        this.token = token;
        return this;
    }

    public Tospay setTermsAndConditionsUrl(String tacUrl) {
        if (tacUrl == null) throw new RuntimeException("Please provide terms and condition url");
        this.tacUrl = tacUrl;
        SharedPrefManager.getInstance(context).save(KEY_TAC_URL, tacUrl);
        return this;
    }

    /**
     * @return tospay payment activity intent
     */
    public Intent getPaymentIntent() {
        if (context == null) throw new RuntimeException("Context can not be null");
        Intent intent = new Intent(context, TospayActivity.class);
        intent.putExtra(KEY_TOKEN, token);
        intent.putExtra(KEY_TAC_URL, tacUrl);
        return intent;
    }

    /**
     * @return tospay authentication activity intent
     */
    public Intent getAuthenticationIntent() {
        if (context == null) throw new RuntimeException("Context can not be null");
        Intent intent = new Intent(context, AuthActivity.class);
        intent.putExtra(KEY_TAC_URL, tacUrl);
        return intent;
    }
}
