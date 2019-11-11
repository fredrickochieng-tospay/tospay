package net.tospay.auth;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import net.tospay.auth.api.TospayClient;
import net.tospay.auth.api.listeners.QrListener;
import net.tospay.auth.api.listeners.TokenRefreshListener;
import net.tospay.auth.api.listeners.UserListener;
import net.tospay.auth.api.request.RefreshTokenRequest;
import net.tospay.auth.api.response.QrResponse;
import net.tospay.auth.api.response.Result;
import net.tospay.auth.api.response.TospayException;
import net.tospay.auth.model.Token;
import net.tospay.auth.model.TospayUser;
import net.tospay.auth.ui.main.TospayActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.tospay.auth.utils.Constants.KEY_TOKEN;

public class TospayAuth extends Tospay {

    private String token;

    private TospayAuth(Context context) {
        super(context);
    }

    public static TospayAuth getInstance(Context context) {
        return new TospayAuth(context);
    }

    /**
     * Retrieves logged in user
     *
     * @return - user object or null if not logged in
     */
    public TospayUser getCurrentUser() {
        return getSharedPrefManager().getActiveUser();
    }


    /**
     * logout out user
     */
    public void logout() {
        getSharedPrefManager().setActiveUser(null);
    }

    /**
     * Retrieves user profile
     *
     * @param listener - callback
     */
    /*public void fetchUser(UserListener listener) {
        TospayClient.getUserService(getContext())
                .user()
                .enqueue(new Callback<Result<TospayUser>>() {
                    @Override
                    public void onResponse(@NonNull Call<Result<TospayUser>> call,
                                           @NonNull Response<Result<TospayUser>> response) {
                        if (response.isSuccessful()) {
                            TospayUser user = getCurrentUser();
                            TospayUser serverUser = response.body().getData();
                            user.setFirstname(serverUser.getFirstname());
                            user.setLastname(serverUser.getLastname());
                            user.setCountryCode(serverUser.getCountryCode());
                            user.setPhone(serverUser.getPhone());
                            user.setProfilePic(serverUser.getProfilePic());

                            getSharedPrefManager().setActiveUser(user);

                            listener.onUser(user);
                        } else {
                            if (response.code() == 401) {
                                listener.onUnAuthenticated();
                            } else {
                                try {
                                    if (response.errorBody() != null) {
                                        listener.onError(getTospayException(response.errorBody().string()));
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    listener.onError(new TospayException(e));
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Result<TospayUser>> call, @NonNull Throwable t) {
                        listener.onError(new TospayException(t.getMessage(), t));
                    }
                });
    }*/

    /**
     * Refreshed bearer token
     *
     * @param listener - callback
     */
    public void refreshToken(TokenRefreshListener listener) {
        if (getCurrentUser() != null) {
            TospayClient.getUserService(getContext())
                    .refreshToken(new RefreshTokenRequest(getCurrentUser().getRefreshToken()))
                    .enqueue(new Callback<Result<Token>>() {
                        @Override
                        public void onResponse(@NonNull Call<Result<Token>> call,
                                               @NonNull Response<Result<Token>> response) {
                            if (response.isSuccessful()) {
                                Token token = response.body().getData();

                                TospayUser user = getCurrentUser();
                                user.setToken(token.getToken());
                                user.setRefreshToken(token.getRefreshToken());
                                user.setExpiredAt(token.getExpiredAt());

                                getSharedPrefManager().setActiveUser(user);

                                listener.onSuccess();
                            } else {
                                listener.onFailed();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Result<Token>> call, @NonNull Throwable t) {
                            listener.onFailed();
                        }
                    });
        } else {
            listener.onFailed();
        }
    }

    public void getQrInfo(String result, QrListener listener) {
        Map<String, String> request = new HashMap<>();
        request.put("qr", result);

        TospayClient.getUserService(getContext())
                .qrInfo(request)
                .enqueue(new Callback<Result<QrResponse>>() {
                    @Override
                    public void onResponse(@NonNull Call<Result<QrResponse>> call, @NonNull Response<Result<QrResponse>> response) {
                        if (response.isSuccessful()) {
                            listener.onSuccess(response.body().getData());
                        } else {
                            try {
                                if (response.errorBody() != null) {
                                    listener.onError(getTospayException(response.errorBody().string()));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                listener.onError(new TospayException(e));
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Result<QrResponse>> call, @NonNull Throwable t) {
                        listener.onError(new TospayException(t.getMessage(), t));
                    }
                });
    }

    /**
     * sets payment validation token
     *
     * @param token - validation token
     * @return this class instance
     */
    public TospayAuth setPaymentToken(String token) {
        this.token = token;
        return this;
    }

    /**
     * @return tospay payment activity intent
     */
    public Intent getPaymentIntent() {
        if (getContext() == null)
            throw new RuntimeException("Context can not be null");

        Intent intent = new Intent(getContext(), TospayActivity.class);
        intent.putExtra(KEY_TOKEN, token);
        return intent;
    }
}
