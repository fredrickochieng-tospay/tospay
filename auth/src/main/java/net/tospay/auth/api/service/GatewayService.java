package net.tospay.auth.api.service;

import net.tospay.auth.api.request.MobileAccountVerificationRequest;
import net.tospay.auth.api.request.MobileRequest;
import net.tospay.auth.api.request.PaymentValidationRequest;
import net.tospay.auth.api.response.AccountResponse;
import net.tospay.auth.api.response.PaymentValidationResponse;
import net.tospay.auth.api.response.Result;
import net.tospay.auth.api.response.WalletTransactionResponse;
import net.tospay.auth.model.Country;
import net.tospay.auth.model.Network;

import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GatewayService {

    @GET("v1/allowed-countries")
    Call<Result<List<Country>>> countries();

    @GET("v1/mobile-operators/{countryId}")
    Call<Result<List<Network>>> networks(@Path("countryId") Integer countryId);

    @POST("v1/link-mobile")
    Call<Result> linkMobile(@Body MobileRequest request);

    @POST("v1/verify-mobile")
    Call<Result> verifyMobile(@Body MobileAccountVerificationRequest request);

    @POST("v1/resend-verify-mobile")
    Call<Result> resendMobileVerificationCode(@Body Map<String, String> request);

    @GET("v1/fetch-accounts")
    Call<Result<AccountResponse>> fetchAccounts();

    @POST("v1/validate-payment")
    Call<Result<PaymentValidationResponse>> validatePayment(@Body PaymentValidationRequest request);

    @POST("v1/topup")
    Call<Result> topup(@Body Map<String, Object> request);

    @GET("v1/wallet-transactions")
    Call<Result<WalletTransactionResponse>> fetchWalletTransactions();
}
