package net.tospay.auth.remote.service;

import androidx.lifecycle.LiveData;

import net.tospay.auth.remote.request.MobileAccountVerificationRequest;
import net.tospay.auth.remote.request.MobileRequest;
import net.tospay.auth.remote.request.PaymentRequest;
import net.tospay.auth.remote.response.AccountResponse;
import net.tospay.auth.remote.response.ApiResponse;
import net.tospay.auth.remote.response.MobileResponse;
import net.tospay.auth.remote.response.PaymentResponse;
import net.tospay.auth.remote.response.PaymentValidationResponse;
import net.tospay.auth.remote.response.Result;
import net.tospay.auth.remote.response.TransferResponse;
import net.tospay.auth.remote.response.WalletTransactionResponse;
import net.tospay.auth.remote.response.WithdrawResponse;
import net.tospay.auth.model.Country;
import net.tospay.auth.model.Network;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GatewayService {

    @GET("v1/allowed-countries")
    LiveData<ApiResponse<Result<List<Country>>>> countries();

    @GET("v1/mobile-countries")
    LiveData<ApiResponse<Result<List<Country>>>> mobileCountries(
            @Header("Authorization") String bearer
    );

    @GET("v1/mobile-operators/{countryId}")
    LiveData<ApiResponse<Result<List<Network>>>> networks(
            @Header("Authorization") String bearer,
            @Path("countryId") Integer countryId
    );

    @POST("v1/validate-payment")
    LiveData<ApiResponse<Result<PaymentValidationResponse>>> validate(
            @Body Map<String, String> request
    );

    @GET("v1/fetch-accounts")
    LiveData<ApiResponse<Result<AccountResponse>>> accounts(
            @Header("Authorization") String bearer
    );

    @POST("v1/pay")
    LiveData<ApiResponse<Result<PaymentResponse>>> pay(
            @Header("Authorization") String bearer,
            @Body PaymentRequest request
    );

    @POST("v1/link-mobile")
    LiveData<ApiResponse<Result<MobileResponse>>> linkMobileAccount(
            @Header("Authorization") String bearer,
            @Body MobileRequest request
    );

    @POST("v1/resend-verify-mobile")
    LiveData<ApiResponse<Result>> resendVerificationCode(
            @Header("Authorization") String bearer,
            @Body Map<String, Object> request
    );

    @POST("v1/verify-mobile")
    LiveData<ApiResponse<Result>> verifyMobileAccount(
            @Header("Authorization") String bearer,
            @Body MobileAccountVerificationRequest request
    );

    @POST("v1/transfer")
    LiveData<ApiResponse<Result<TransferResponse>>> transfer(
            @Header("Authorization") String bearer,
            @Body Map<String, Object> request
    );

    @POST("v1/withdraw")
    LiveData<ApiResponse<Result<WithdrawResponse>>> withdraw(
            @Header("Authorization") String bearer,
            @Body Map<String, Object> request
    );

    @POST("v1/topup")
    LiveData<ApiResponse<Result>> topup(
            @Header("Authorization") String bearer,
            @Body Map<String, Object> request
    );

    @GET("v1/wallet-transactions")
    LiveData<ApiResponse<Result<WalletTransactionResponse>>> fetchWalletTransactions(
            @Header("Authorization") String bearer
    );

    @POST("v1/firebase-token")
    LiveData<ApiResponse<Result>> saveFcmToken(
            @Header("Authorization") String bearer,
            @Body Map<String, String> request
    );
}
