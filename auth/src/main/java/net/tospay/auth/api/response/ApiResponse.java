package net.tospay.auth.api.response;


import android.util.Log;

import androidx.annotation.Nullable;

import net.tospay.auth.utils.Utils;

import java.io.IOException;

import retrofit2.Response;

/**
 * Common class used by API responses.
 */

public class ApiResponse<T> {

    private static final String TAG = "ApiResponse";

    public final int code;

    @Nullable
    public final T body;

    @Nullable
    public final String errorMessage;

    public ApiResponse(Throwable error) {
        code = 500;
        body = null;
        errorMessage = error.getMessage();
    }

    public ApiResponse(Response<T> response) {
        code = response.code();
        if (response.isSuccessful()) {
            body = response.body();
            errorMessage = null;
        } else {
            String message = null;
            if (response.errorBody() != null) {
                try {
                    message = response.errorBody().string();
                    TospayException exception = getTospayException(message);
                    message = exception.getErrorMessage();
                    Log.e(TAG, "ApiResponse: " + message);
                } catch (IOException ignored) {
                    Log.e(TAG, "error while parsing response: ", ignored);
                }
            }

            if (message == null || message.trim().length() == 0) {
                message = response.message();
            }

            errorMessage = message;
            body = null;
        }

    }

    /**
     * Checks response code
     *
     * @return true
     */
    public boolean isSuccessful() {
        return code >= 200 && code < 300;
    }

    /**
     * Parse json response from server
     *
     * @param errorStr - json string
     * @return tospay exception
     */
    private TospayException getTospayException(String errorStr) {
        return Utils.getGsonParser().fromJson(errorStr, TospayException.class);
    }
}
