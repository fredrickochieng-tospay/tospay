package net.tospay.auth.remote.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import net.tospay.auth.model.Account;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.request.MobileAccountVerificationRequest;
import net.tospay.auth.remote.request.MobileRequest;
import net.tospay.auth.remote.response.ApiResponse;
import net.tospay.auth.remote.response.MobileResponse;
import net.tospay.auth.remote.response.Result;
import net.tospay.auth.remote.service.MobileService;
import net.tospay.auth.remote.util.AppExecutors;
import net.tospay.auth.remote.util.NetworkBoundResource;
import net.tospay.auth.utils.AbsentLiveData;

import java.util.List;
import java.util.Map;

public class MobileRepository {

    private final MobileService mMobileService;
    private final AppExecutors mAppExecutors;

    /**
     * @param mAppExecutors  -AppExecutor
     * @param mMobileService - Mobile service
     */
    public MobileRepository(AppExecutors mAppExecutors,
                            MobileService mMobileService) {
        this.mAppExecutors = mAppExecutors;
        this.mMobileService = mMobileService;
    }

    public LiveData<Resource<MobileResponse>> link(String bearerToken,
                                                   MobileRequest request) {
        return new NetworkBoundResource<MobileResponse, Result<MobileResponse>>(mAppExecutors) {

            private MobileResponse resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result<MobileResponse> item) {
                resultsDb = item.getData();
            }

            @Override
            protected boolean shouldFetch(@Nullable MobileResponse data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<MobileResponse> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<MobileResponse>() {
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
            protected LiveData<ApiResponse<Result<MobileResponse>>> createCall() {
                return mMobileService.link(bearerToken, request);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Result>> resend(String bearerToken,
                                             Map<String, Object> param) {
        return new NetworkBoundResource<Result, Result>(mAppExecutors) {

            private Result resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result item) {
                resultsDb = item;
            }

            @Override
            protected boolean shouldFetch(@Nullable Result data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<Result> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<Result>() {
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
            protected LiveData<ApiResponse<Result>> createCall() {
                return mMobileService.resend(bearerToken, param);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Result>> verify(String bearerToken,
                                             MobileAccountVerificationRequest request) {
        return new NetworkBoundResource<Result, Result>(mAppExecutors) {

            private Result resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result item) {
                resultsDb = item;
            }

            @Override
            protected boolean shouldFetch(@Nullable Result data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<Result> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<Result>() {
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
            protected LiveData<ApiResponse<Result>> createCall() {
                return mMobileService.verify(bearerToken, request);
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Account>>> accounts(String bearerToken) {
        return new NetworkBoundResource<List<Account>, Result<List<Account>>>(mAppExecutors) {

            private List<Account> resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result<List<Account>> item) {
                resultsDb = item.getData();
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Account> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Account>> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<List<Account>>() {
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
            protected LiveData<ApiResponse<Result<List<Account>>>> createCall() {
                return mMobileService.accounts(bearerToken);
            }
        }.asLiveData();
    }
}
