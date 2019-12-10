package net.tospay.auth.remote.service;

import androidx.lifecycle.LiveData;

import net.tospay.auth.model.Account;
import net.tospay.auth.model.Wallet;
import net.tospay.auth.remote.response.AccountResponse;
import net.tospay.auth.remote.response.ApiResponse;
import net.tospay.auth.remote.response.Result;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface AccountService {

    @GET("v1/fetch-accounts")
    LiveData<ApiResponse<Result<AccountResponse>>> accounts(
            @Header("Authorization") String bearer
    );

    @GET("v1/accounts/{type}")
    LiveData<ApiResponse<Result<List<Account>>>> accounts(
            @Header("Authorization") String bearer,
            @Path("type") String type
    );
}
