package net.tospay.auth.remote.service;

import androidx.lifecycle.LiveData;

import net.tospay.auth.model.transfer.Amount;
import net.tospay.auth.model.transfer.Transfer;
import net.tospay.auth.remote.response.ApiResponse;
import net.tospay.auth.remote.response.Result;
import net.tospay.auth.remote.response.TransferResponse;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * @author Freddy Genicho
 * <p>Contains all the library payment endpoints</p>
 */
public interface PaymentService {

    @GET("v1/payment/fetch/{paymentId}")
    LiveData<ApiResponse<Result<Transfer>>> details(
            @Path("paymentId") String paymentId
    );

    @POST("v1/payment/charge-lookup")
    LiveData<ApiResponse<Result<Amount>>> paymentChargeLookup(
            @Header("Authorization") String bearer,
            @Body Transfer transfer
    );

    @POST("v1/transfer/execute-payment/{paymentId}")
    LiveData<ApiResponse<Result<TransferResponse>>> pay(
            @Header("Authorization") String bearer,
            @Path("paymentId") String paymentId,
            @Body Transfer transfer
    );

    @POST("v1/transfer/charge-lookup")
    LiveData<ApiResponse<Result<Amount>>> transferChargeLookup(
            @Header("Authorization") String bearer,
            @Body Transfer transfer
    );

    @POST("v1/transfer/execute-transfer")
    LiveData<ApiResponse<Result<TransferResponse>>> transfer(
            @Header("Authorization") String bearer,
            @Body Transfer transfer
    );
}
