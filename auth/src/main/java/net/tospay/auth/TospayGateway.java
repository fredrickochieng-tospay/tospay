package net.tospay.auth;

import android.content.Context;

import androidx.annotation.NonNull;

import net.tospay.auth.api.TospayClient;
import net.tospay.auth.api.listeners.ListAccountListener;
import net.tospay.auth.api.listeners.CountryListener;
import net.tospay.auth.api.listeners.MobileAccountListener;
import net.tospay.auth.api.listeners.NetworkListener;
import net.tospay.auth.api.listeners.OnPaymentValidationListener;
import net.tospay.auth.api.listeners.ResponseListener;
import net.tospay.auth.api.listeners.WalletTransactionListener;
import net.tospay.auth.api.request.MobileAccountVerificationRequest;
import net.tospay.auth.api.request.MobileRequest;
import net.tospay.auth.api.request.PaymentValidationRequest;
import net.tospay.auth.api.response.AccountResponse;
import net.tospay.auth.api.response.Result;
import net.tospay.auth.api.response.TospayException;
import net.tospay.auth.api.response.PaymentValidationResponse;
import net.tospay.auth.api.response.WalletTransactionResponse;
import net.tospay.auth.model.Country;
import net.tospay.auth.model.Network;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TospayGateway extends Tospay {

    public TospayGateway(Context context) {
        super(context);
    }

    /**
     * Static instance for TospayGateway
     *
     * @param context
     * @return
     */
    public static TospayGateway getInstance(Context context) {
        return new TospayGateway(context);
    }

    /**
     * Fetches available countries
     *
     * @param listener - callback
     */
    public void fetchCountries(CountryListener listener) {
        TospayClient.getGatewayService(getContext())
                .countries()
                .enqueue(new Callback<Result<List<Country>>>() {
                    @Override
                    public void onResponse(@NonNull Call<Result<List<Country>>> call,
                                           @NonNull Response<Result<List<Country>>> response) {
                        if (response.isSuccessful()) {
                            listener.onCountries(response.body().getData());
                        } else {
                            parseError(response.code(), response.errorBody(), listener);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Result<List<Country>>> call, @NonNull Throwable t) {
                        listener.onError(new TospayException(t.getMessage(), t));
                    }
                });
    }

    /**
     * Fetches network operators of a country
     *
     * @param countryId - selected country id
     * @param listener  - callback
     */
    public void fetchMobileOperators(int countryId, NetworkListener listener) {
        TospayClient.getGatewayService(getContext())
                .networks(countryId)
                .enqueue(new Callback<Result<List<Network>>>() {
                    @Override
                    public void onResponse(@NonNull Call<Result<List<Network>>> call,
                                           @NonNull Response<Result<List<Network>>> response) {
                        if (response.isSuccessful()) {
                            listener.onNetworks(response.body().getData());
                        } else {
                            parseError(response.code(), response.errorBody(), listener);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Result<List<Network>>> call, @NonNull Throwable t) {
                        listener.onError(new TospayException(t.getMessage(), t));
                    }
                });
    }

    /**
     * Add a user mobile account
     *
     * @param country  - selected country
     * @param network  - selected network
     * @param phone    - user mobile no.
     * @param name     - user name
     * @param listener - callback
     */
    public void linkMobile(Country country, Network network, String phone, String name, MobileAccountListener listener) {
        MobileRequest request = new MobileRequest();
        request.setCountry(country);
        request.setNetwork(network);
        request.setNumber(phone);
        request.setAlias(name);

        TospayClient.getGatewayService(getContext())
                .linkMobile(request)
                .enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(@NonNull Call<Result> call,
                                           @NonNull Response<Result> response) {
                        if (response.isSuccessful()) {
                            Map<String, String> map = (Map<String, String>) response.body().getData();
                            listener.onAccountAdded(map.get("id"));
                        } else {
                            parseError(response.code(), response.errorBody(), listener);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Result> call, @NonNull Throwable t) {
                        listener.onError(new TospayException(t.getMessage(), t));
                    }
                });
    }

    public void verifyMobileAccount(String accountId, String code, ResponseListener listener) {
        MobileAccountVerificationRequest request = new MobileAccountVerificationRequest(accountId, code);
        TospayClient.getGatewayService(getContext())
                .verifyMobile(request)
                .enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(@NonNull Call<Result> call,
                                           @NonNull Response<Result> response) {
                        if (response.isSuccessful()) {
                            listener.onSuccess();
                        } else {
                            parseError(response.code(), response.errorBody(), listener);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Result> call, @NonNull Throwable e) {
                        e.printStackTrace();
                        listener.onError(new TospayException(e));
                    }
                });
    }

    public void resendMobileVerificationCode(String accountId, ResponseListener listener) {
        Map<String, String> request = new HashMap<>();
        request.put("id", accountId);

        TospayClient.getGatewayService(getContext())
                .resendMobileVerificationCode(request)
                .enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(@NonNull Call<Result> call,
                                           @NonNull Response<Result> response) {
                        if (response.isSuccessful()) {
                            listener.onSuccess();
                        } else {
                            parseError(response.code(), response.errorBody(), listener);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Result> call, @NonNull Throwable e) {
                        e.printStackTrace();
                        listener.onError(new TospayException(e));
                    }
                });
    }

    /**
     * Fetches user accounts
     *
     * @param listener - callback
     */
    public void fetchAccounts(ListAccountListener listener) {
        TospayClient.getGatewayService(getContext())
                .fetchAccounts()
                .enqueue(new Callback<Result<AccountResponse>>() {
                    @Override
                    public void onResponse(@NonNull Call<Result<AccountResponse>> call,
                                           @NonNull Response<Result<AccountResponse>> response) {
                        if (response.isSuccessful()) {
                            listener.onAccounts(response.body().getData());
                        } else {
                            parseError(response.code(), response.errorBody(), listener);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Result<AccountResponse>> call, @NonNull Throwable t) {
                        listener.onError(new TospayException(t.getMessage(), t));
                    }
                });
    }

    /**
     * Checks validity of a payment token
     *
     * @param token    - payment token
     * @param listener - callback
     */
    public void validatePayment(String token, OnPaymentValidationListener listener) {
        TospayClient.getGatewayService(getContext())
                .validatePayment(new PaymentValidationRequest(token))
                .enqueue(new Callback<Result<PaymentValidationResponse>>() {
                    @Override
                    public void onResponse(@NonNull Call<Result<PaymentValidationResponse>> call,
                                           @NonNull Response<Result<PaymentValidationResponse>> response) {
                        if (response.isSuccessful()) {
                            listener.onValidationSuccess(response.body().getData());
                        } else {
                            parseError(response.code(), response.errorBody(), listener);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Result<PaymentValidationResponse>> call, @NonNull Throwable t) {
                        listener.onError(new TospayException(t.getMessage(), t));
                    }
                });
    }

    public void topup(String type, String accountId, String amount, String currency, ResponseListener listener) {
        Map<String, Object> request = new HashMap<>();
        request.put("type", type);

        Map<String, String> account = new HashMap<>();
        account.put("id", accountId);
        request.put("account", account);

        Map<String, String> transaction = new HashMap<>();
        transaction.put("currency", currency);
        transaction.put("amount", amount);
        request.put("transaction", transaction);

        TospayClient.getGatewayService(getContext())
                .topup(request)
                .enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(@NonNull Call<Result> call, @NonNull Response<Result> response) {
                        if (response.isSuccessful()) {
                            listener.onSuccess();
                        } else {
                            parseError(response.code(), response.errorBody(), listener);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Result> call, @NonNull Throwable t) {
                        listener.onError(new TospayException(t));
                    }
                });
    }

    public void fetchWalletTransactions(WalletTransactionListener listener) {
        TospayClient.getGatewayService(getContext()).fetchWalletTransactions().enqueue(new Callback<Result<WalletTransactionResponse>>() {
            @Override
            public void onResponse(@NonNull Call<Result<WalletTransactionResponse>> call, @NonNull Response<Result<WalletTransactionResponse>> response) {
                if (response.isSuccessful()) {
                    WalletTransactionResponse result = response.body().getData();
                    listener.onWalletTransactions(result.getWallets(), result.getTransactions());
                } else {
                    parseError(response.code(), response.errorBody(), listener);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<WalletTransactionResponse>> call, @NonNull Throwable t) {
                listener.onError(new TospayException(t));
            }
        });
    }
}
