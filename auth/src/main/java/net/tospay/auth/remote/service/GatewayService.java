package net.tospay.auth.remote.service;

import androidx.lifecycle.LiveData;

import net.tospay.auth.model.Bank;
import net.tospay.auth.model.Country;
import net.tospay.auth.model.Network;
import net.tospay.auth.remote.response.ApiResponse;
import net.tospay.auth.remote.response.Result;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Header;
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

    @GET("v1/config/bank-countries")
    LiveData<ApiResponse<Result<List<Country>>>> bankCountries(
            @Header("Authorization") String bearer
    );

    @GET("v1/config/bank-operators/{iso}")
    LiveData<ApiResponse<Result<List<Bank>>>> banks(
            @Header("Authorization") String bearer,
            @Path("iso") String iso
    );
}
