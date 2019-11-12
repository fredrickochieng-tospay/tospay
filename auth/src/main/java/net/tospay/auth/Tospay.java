package net.tospay.auth;

import android.content.Context;
import android.content.Intent;

import net.tospay.auth.model.TospayUser;
import net.tospay.auth.ui.main.TospayActivity;
import net.tospay.auth.utils.SharedPrefManager;

import static net.tospay.auth.utils.Constants.KEY_TOKEN;

public class Tospay {

    private String token;
    private Context context;

    private Tospay(Context context) {
        this.context = context;
    }

    public static Tospay getInstance(Context context) {
        return new Tospay(context);
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
     * sets payment validation token
     *
     * @param token - validation token
     * @return this class instance
     */
    public Tospay setPaymentToken(String token) {
        this.token = token;
        return this;
    }

    /**
     * @return tospay payment activity intent
     */
    public Intent getPaymentIntent() {
        if (context == null)
            throw new RuntimeException("Context can not be null");

        Intent intent = new Intent(context, TospayActivity.class);
        intent.putExtra(KEY_TOKEN, token);
        return intent;
    }
}
