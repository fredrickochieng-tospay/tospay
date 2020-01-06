package net.tospay.auth.remote.service;

import androidx.lifecycle.LiveData;

import net.tospay.auth.model.transfer.Transfer;
import net.tospay.auth.remote.response.ApiResponse;
import net.tospay.auth.remote.response.Result;

import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PaymentService {

    @GET("v1/payment/fetch/{paymentId}")
    LiveData<ApiResponse<Result<Transfer>>> details(@Path("paymentId") String paymentId);
}
