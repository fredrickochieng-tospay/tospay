package net.tospay.auth.api.service;

import net.tospay.auth.api.request.LoginRequest;
import net.tospay.auth.api.request.OtpRequest;
import net.tospay.auth.api.request.RefreshTokenRequest;
import net.tospay.auth.api.request.RegisterRequest;
import net.tospay.auth.api.request.ResendEmailRequest;
import net.tospay.auth.api.request.VerifyEmailRequest;
import net.tospay.auth.api.request.VerifyPhoneRequest;
import net.tospay.auth.api.response.QrResponse;
import net.tospay.auth.api.response.Result;
import net.tospay.auth.model.Token;
import net.tospay.auth.model.TospayUser;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserService {

    @POST("v3/user/login")
    Call<Result<TospayUser>> login(@Body LoginRequest request);

    @POST("v3/user/register")
    Call<Result<TospayUser>> register(@Body RegisterRequest request);

    @POST("v3/user/verify/email")
    Call<Result> verifyEmail(@Body VerifyEmailRequest request);

    @POST("v3/user/verify/email/resend")
    Call<Result> resendEmailToken(@Body ResendEmailRequest request);

    @POST("v3/user/verify/phone")
    Call<Result> verifyPhone(@Body VerifyPhoneRequest request);

    @POST("v3/user/phone/resend")
    Call<Result> resendOtp(@Body OtpRequest request);

    @GET("v3/user/profile")
    Call<Result<TospayUser>> user();

    @POST("v3/user/token/refresh")
    Call<Result<Token>> refreshToken(@Body RefreshTokenRequest request);

    @POST("v3/account/info/qr")
    Call<Result<QrResponse>> qrInfo(@Body Map<String, String> request);

}
