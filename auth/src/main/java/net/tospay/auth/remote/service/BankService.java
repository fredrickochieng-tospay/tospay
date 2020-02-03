package net.tospay.auth.remote.service;

import androidx.lifecycle.LiveData;

import net.tospay.auth.model.Account;
import net.tospay.auth.remote.request.BankRequest;
import net.tospay.auth.remote.response.AccountLinkResponse;
import net.tospay.auth.remote.response.ApiResponse;
import net.tospay.auth.remote.response.Result;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface BankService {

    @POST("v1/bank/link")
    LiveData<ApiResponse<Result<AccountLinkResponse>>> link(
            @Header("Authorization") String bearer,
            @Body BankRequest request
    );

    @GET("v1/bank/accounts")
    LiveData<ApiResponse<Result<List<Account>>>> accounts(
            @Header("Authorization") String bearer
    );

    @POST("v1/bank/delete")
    LiveData<ApiResponse<Result>> delete(
            @Header("Authorization") String bearer,
            @Body Map<String, Object> request
    );
}
