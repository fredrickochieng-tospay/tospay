package net.tospay.auth.remote.service;

import androidx.lifecycle.LiveData;

import net.tospay.auth.remote.response.AccountResponse;
import net.tospay.auth.remote.response.ApiResponse;
import net.tospay.auth.remote.response.Result;

import retrofit2.http.GET;
import retrofit2.http.Header;

public interface AccountService {

    @GET("v1/combined/accounts")
    LiveData<ApiResponse<Result<AccountResponse>>> accounts(
            @Header("Authorization") String bearer
    );
}
