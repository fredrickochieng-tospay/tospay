package net.tospay.auth.remote.service;

import androidx.lifecycle.LiveData;

import net.tospay.auth.model.Country;
import net.tospay.auth.model.Network;
import net.tospay.auth.remote.request.MobileAccountVerificationRequest;
import net.tospay.auth.remote.request.MobileRequest;
import net.tospay.auth.remote.request.PaymentRequest;
import net.tospay.auth.remote.response.AccountResponse;
import net.tospay.auth.remote.response.ApiResponse;
import net.tospay.auth.remote.response.MobileResponse;
import net.tospay.auth.remote.response.PaymentResponse;
import net.tospay.auth.remote.response.PaymentValidationResponse;
import net.tospay.auth.remote.response.Result;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GatewayService {

    @GET("v1/config/countries")
    LiveData<ApiResponse<Result<List<Country>>>> countries();

    @GET("v1/config/mobile-countries")
    LiveData<ApiResponse<Result<List<Country>>>> mobileCountries(
            @Header("Authorization") String bearer
    );

    @GET("v1/config/mobile-operators/{iso}")
    LiveData<ApiResponse<Result<List<Network>>>> networks(
            @Header("Authorization") String bearer,
            @Path("iso") String iso
    );

    @POST("v1/validate-payment")
    LiveData<ApiResponse<Result<PaymentValidationResponse>>> validate(
            @Header("Authorization") String bearer,
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

}
