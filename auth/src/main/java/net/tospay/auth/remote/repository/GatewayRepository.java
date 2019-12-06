package net.tospay.auth.remote.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.model.Account;
import net.tospay.auth.model.AccountTitle;
import net.tospay.auth.model.Country;
import net.tospay.auth.model.Network;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.request.PaymentRequest;
import net.tospay.auth.remote.response.AccountResponse;
import net.tospay.auth.remote.response.ApiResponse;
import net.tospay.auth.remote.response.PaymentResponse;
import net.tospay.auth.remote.response.PaymentValidationResponse;
import net.tospay.auth.remote.response.Result;
import net.tospay.auth.remote.service.GatewayService;
import net.tospay.auth.remote.util.AppExecutors;
import net.tospay.auth.remote.util.NetworkBoundResource;
import net.tospay.auth.utils.AbsentLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>Handles all the calls to Tospay Gateway Service</p>
 */
public class GatewayRepository {

    private final GatewayService mGatewayService;
    private final AppExecutors mAppExecutors;

    /**
     * @param mAppExecutors   -AppExecutor
     * @param mGatewayService - Gateway service
     */
    public GatewayRepository(AppExecutors mAppExecutors, GatewayService mGatewayService) {
        this.mGatewayService = mGatewayService;
        this.mAppExecutors = mAppExecutors;
    }

    public LiveData<Resource<List<Country>>> countries(String bearerToken, boolean isOperators) {
        return new NetworkBoundResource<List<Country>, Result<List<Country>>>(mAppExecutors) {

            private List<Country> resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result<List<Country>> item) {
                resultsDb = item.getData();
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Country> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Country>> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<List<Country>>() {
                        @Override
                        protected void onActive() {
                            super.onActive();
                            setValue(resultsDb);
                        }
                    };
                }
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Result<List<Country>>>> createCall() {
                if (isOperators) {
                    return mGatewayService.mobileCountries(bearerToken);
                } else {
                    return mGatewayService.countries();
                }
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Network>>> networks(String bearerToken, String iso) {
        return new NetworkBoundResource<List<Network>, Result<List<Network>>>(mAppExecutors) {

            private List<Network> resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result<List<Network>> item) {
                resultsDb = item.getData();
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Network> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Network>> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<List<Network>>() {
                        @Override
                        protected void onActive() {
                            super.onActive();
                            setValue(resultsDb);
                        }
                    };
                }
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Result<List<Network>>>> createCall() {
                return mGatewayService.networks(bearerToken, iso);
            }
        }.asLiveData();
    }

    /**
     * validate payment request
     *
     * @param param request
     * @return LiveData
     */
    public LiveData<Resource<PaymentValidationResponse>> validate(String bearerToken, Map<String, String> param) {
        return new NetworkBoundResource<PaymentValidationResponse, Result<PaymentValidationResponse>>(mAppExecutors) {

            private PaymentValidationResponse resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result<PaymentValidationResponse> item) {
                resultsDb = item.getData();
            }

            @Override
            protected boolean shouldFetch(@Nullable PaymentValidationResponse data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<PaymentValidationResponse> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<PaymentValidationResponse>() {
                        @Override
                        protected void onActive() {
                            super.onActive();
                            setValue(resultsDb);
                        }
                    };
                }
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Result<PaymentValidationResponse>>> createCall() {
                return mGatewayService.validate(bearerToken, param);
            }
        }.asLiveData();
    }

    /**
     * @param bearerToken - bearer token
     * @param request     - request
     * @return LiveData
     */
    public LiveData<Resource<PaymentResponse>> pay(String bearerToken, PaymentRequest request) {
        return new NetworkBoundResource<PaymentResponse, Result<PaymentResponse>>(mAppExecutors) {

            private PaymentResponse resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result<PaymentResponse> item) {
                resultsDb = item.getData();
            }

            @Override
            protected boolean shouldFetch(@Nullable PaymentResponse data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<PaymentResponse> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<PaymentResponse>() {
                        @Override
                        protected void onActive() {
                            super.onActive();
                            setValue(resultsDb);
                        }
                    };
                }
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Result<PaymentResponse>>> createCall() {
                return mGatewayService.pay(bearerToken, request);
            }
        }.asLiveData();
    }
}
