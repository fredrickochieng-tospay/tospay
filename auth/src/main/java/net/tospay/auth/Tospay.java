package net.tospay.auth;

import android.content.Context;

import net.tospay.auth.api.listeners.BaseListener;
import net.tospay.auth.api.response.Error;
import net.tospay.auth.api.response.TospayException;
import net.tospay.auth.app.SharedPrefManager;
import net.tospay.auth.utils.Utils;

import java.io.IOException;
import java.util.Objects;

import io.reactivex.disposables.CompositeDisposable;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

public abstract class Tospay {

    private SharedPrefManager mSharedPrefManager;
    private Context context;

    public Tospay(Context context) {
        this.context = context;
        if (mSharedPrefManager == null)
            this.mSharedPrefManager = SharedPrefManager.getInstance(context);
    }

    /**
     * Parse json response from server
     *
     * @param errorStr - json string
     * @return tospay exception
     */
    public TospayException getTospayException(String errorStr) {
        return Utils.getGsonParser().fromJson(errorStr, TospayException.class);
    }

    /**
     * Returns shared preference
     *
     * @return shared preference
     */
    public SharedPrefManager getSharedPrefManager() {
        return mSharedPrefManager;
    }

    /**
     * @return Context
     */
    public Context getContext() {
        return context;
    }

    /**
     * Generic way to catch 401 response
     *
     * @param code      -  response code
     * @param errorBody - response error body
     * @param listener  - callback
     * @param <T>       - generic callback
     */
    public <T extends BaseListener> void parseError(int code, ResponseBody errorBody, T listener) {
        if (code == 401) {
            listener.onUnAuthenticated();
        } else {
            try {
                if (errorBody != null) {
                    listener.onError(getTospayException(errorBody.string()));
                }
            } catch (IOException e) {
                e.printStackTrace();
                listener.onError(new TospayException(e));
            }
        }
    }

    /**
     * Catches request exceptions
     *
     * @param throwable - thrown exception
     * @param listener  - callback
     * @param <T>       - generic callback
     */
    public <T extends BaseListener> void parseError(Throwable throwable, T listener) {
        if (throwable instanceof HttpException) {
            HttpException exception = (HttpException) throwable;
            if (exception.code() == 401) {
                listener.onUnAuthenticated();
            } else {
                try {
                    String json = Objects.requireNonNull(exception.response().errorBody()).string();
                    listener.onError(new TospayException(getTospayException(json)));

                } catch (Exception ex) {
                    ex.printStackTrace();
                    listener.onError(new TospayException(throwable));
                }
            }
        } else {
            listener.onError(new TospayException(throwable));
        }
    }
}
