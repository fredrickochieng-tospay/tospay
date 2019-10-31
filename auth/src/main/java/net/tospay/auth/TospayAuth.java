package net.tospay.auth;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import net.tospay.auth.api.TospayClient;
import net.tospay.auth.api.listeners.QrListener;
import net.tospay.auth.api.listeners.TokenRefreshListener;
import net.tospay.auth.api.listeners.UserListener;
import net.tospay.auth.api.listeners.VerificationListener;
import net.tospay.auth.api.request.LoginRequest;
import net.tospay.auth.api.request.OtpRequest;
import net.tospay.auth.api.request.RefreshTokenRequest;
import net.tospay.auth.api.request.RegisterRequest;
import net.tospay.auth.api.request.ResendEmailRequest;
import net.tospay.auth.api.request.VerifyEmailRequest;
import net.tospay.auth.api.request.VerifyPhoneRequest;
import net.tospay.auth.api.response.QrResponse;
import net.tospay.auth.api.response.Result;
import net.tospay.auth.api.response.TospayException;
import net.tospay.auth.model.Country;
import net.tospay.auth.model.Token;
import net.tospay.auth.model.TospayUser;
import net.tospay.auth.ui.activity.TospayActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.tospay.auth.utils.Constants.KEY_TOKEN;

public class TospayAuth extends Tospay {

    private static final String TAG = TospayAuth.class.getSimpleName();
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
     * Updates locally stored user
     *
     * @param tospayUser - current logged in user
     */
    public void updateCurrentUser(TospayUser tospayUser) {
        getSharedPrefManager().setActiveUser(tospayUser);
    }

    /**
     * Authenticates user
     *
     * @param email    - user email
     * @param password - user password
     * @param listener - callback
     */
    public void login(String email, String password, UserListener listener) {
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);

        TospayClient.getUserService(getContext())
                .login(request)
                .enqueue(new Callback<Result<TospayUser>>() {
                    @Override
                    public void onResponse(@NonNull Call<Result<TospayUser>> call,
                                           @NonNull Response<Result<TospayUser>> response) {
                        if (response.isSuccessful()) {
                            TospayUser user = response.body().getData();
                            getSharedPrefManager().setActiveUser(user);
                            listener.onUser(user);

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
                    public void onFailure(@NonNull Call<Result<TospayUser>> call, @NonNull Throwable t) {
                        listener.onError(new TospayException(t.getMessage(), t));
                    }
                });
    }

    /**
     * Registers a new user
     *
     * @param firstName - user firstname
     * @param lastName  - user lastname
     * @param email     - user valid email
     * @param password  - user secret password
     * @param phone     - user personal mobile no.
     * @param country   - user country of origin
     * @param listener  - callback
     */
    public void register(String firstName, String lastName, String email, String password,
                         String phone, Country country, UserListener listener) {

        RegisterRequest request = new RegisterRequest();
        request.setFirstname(firstName);
        request.setLastname(lastName);
        request.setEmail(email);
        request.setPassword(password);
        request.setPhone(phone);
        request.setCountry(country);

        TospayClient.getUserService(getContext())
                .register(request)
                .enqueue(new Callback<Result<TospayUser>>() {
                    @Override
                    public void onResponse(@NonNull Call<Result<TospayUser>> call,
                                           @NonNull Response<Result<TospayUser>> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                TospayUser user = response.body().getData();
                                getSharedPrefManager().setActiveUser(user);
                                listener.onUser(user);
                            }
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
                    public void onFailure(@NonNull Call<Result<TospayUser>> call, @NonNull Throwable t) {
                        listener.onError(new TospayException(t.getMessage(), t));
                    }
                });
    }

    /**
     * Requests user email verification
     *
     * @param email    - user valid email
     * @param code     - code sent to email
     * @param listener - callback
     */
    public void verifyEmail(String email, String code, VerificationListener listener) {
        TospayClient.getUserService(getContext())
                .verifyEmail(new VerifyEmailRequest(email, code))
                .enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(@NonNull Call<Result> call,
                                           @NonNull Response<Result> response) {
                        if (response.isSuccessful()) {
                            listener.onSuccess();
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
                    public void onFailure(@NonNull Call<Result> call, @NonNull Throwable t) {
                        listener.onError(new TospayException(t.getMessage(), t));
                    }
                });
    }

    /**
     * Resend email verification code
     *
     * @param email    - user valid email
     * @param listener - callback
     */
    public void resendEmailVerificationCode(String email, VerificationListener listener) {
        TospayClient.getUserService(getContext())
                .resendEmailToken(new ResendEmailRequest(email))
                .enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(@NonNull Call<Result> call,
                                           @NonNull Response<Result> response) {
                        if (response.isSuccessful()) {
                            listener.onSuccess();
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
                    public void onFailure(@NonNull Call<Result> call, @NonNull Throwable t) {
                        listener.onError(new TospayException(t.getMessage(), t));
                    }
                });
    }

    /**
     * Requests user phone verification
     *
     * @param phone    - user phone number
     * @param code     - code sent to email
     * @param listener - callback
     */
    public void verifyPhone(String phone, String code, VerificationListener listener) {
        TospayClient.getUserService(getContext())
                .verifyPhone(new VerifyPhoneRequest(phone, code))
                .enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(@NonNull Call<Result> call,
                                           @NonNull Response<Result> response) {
                        if (response.isSuccessful()) {
                            listener.onSuccess();
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
                    public void onFailure(@NonNull Call<Result> call, @NonNull Throwable t) {
                        listener.onError(new TospayException(t.getMessage(), t));
                    }
                });
    }

    /**
     * Resend phone verification code
     *
     * @param phone    - user valid email
     * @param listener - callback
     */
    public void resendOtp(String phone, VerificationListener listener) {
        TospayClient.getUserService(getContext())
                .resendOtp(new OtpRequest(phone))
                .enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(@NonNull Call<Result> call,
                                           @NonNull Response<Result> response) {
                        if (response.isSuccessful()) {
                            listener.onSuccess();
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
                    public void onFailure(@NonNull Call<Result> call, @NonNull Throwable t) {
                        listener.onError(new TospayException(t.getMessage(), t));
                    }
                });

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
    public void fetchUser(UserListener listener) {
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
    }

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
