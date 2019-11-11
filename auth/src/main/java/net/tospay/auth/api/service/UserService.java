package net.tospay.auth.api.service;

import androidx.lifecycle.LiveData;

import net.tospay.auth.api.request.LoginRequest;
import net.tospay.auth.api.request.OtpRequest;
import net.tospay.auth.api.request.RefreshTokenRequest;
import net.tospay.auth.api.request.RegisterRequest;
import net.tospay.auth.api.request.ResendEmailRequest;
import net.tospay.auth.api.request.VerifyEmailRequest;
import net.tospay.auth.api.request.VerifyPhoneRequest;
import net.tospay.auth.api.response.ApiResponse;
import net.tospay.auth.api.response.QrResponse;
import net.tospay.auth.api.response.Result;
import net.tospay.auth.model.Token;
import net.tospay.auth.model.TospayUser;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserService {

    @POST("v3/user/token/refresh")
    Call<Result<Token>> refreshToken(@Body RefreshTokenRequest request);

    @POST("v3/account/info/qr")
    Call<Result<QrResponse>> qrInfo(@Body Map<String, String> request);

    @POST("v3/user/login")
    LiveData<ApiResponse<Result<TospayUser>>> login(@Body LoginRequest request);

    @POST("v3/user/register")
    LiveData<ApiResponse<Result<TospayUser>>> register(@Body RegisterRequest request);

    @POST("v3/user/verify/email")
    LiveData<ApiResponse<Result>> verifyEmail(@Body VerifyEmailRequest request);

    @POST("v3/user/verify/email/resend")
    LiveData<ApiResponse<Result>> resendEmailToken(@Body ResendEmailRequest request);

    @POST("v3/user/verify/phone")
    LiveData<ApiResponse<Result>> verifyPhone(@Body VerifyPhoneRequest request);

    @POST("v3/user/phone/resend")
    LiveData<ApiResponse<Result>> resendOtp(@Body OtpRequest request);

    @GET("v3/user/profile")
    LiveData<ApiResponse<Result<TospayUser>>> user();

}
