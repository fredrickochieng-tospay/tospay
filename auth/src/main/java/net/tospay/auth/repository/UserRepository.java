package net.tospay.auth.repository;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import net.tospay.auth.api.request.LoginRequest;
import net.tospay.auth.api.request.OtpRequest;
import net.tospay.auth.api.request.RegisterRequest;
import net.tospay.auth.api.request.ResendEmailRequest;
import net.tospay.auth.api.request.VerifyEmailRequest;
import net.tospay.auth.api.request.VerifyPhoneRequest;
import net.tospay.auth.api.response.ApiResponse;
import net.tospay.auth.api.response.Result;
import net.tospay.auth.api.service.UserService;
import net.tospay.auth.model.TospayUser;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.util.AppExecutors;
import net.tospay.auth.remote.util.NetworkBoundResource;
import net.tospay.auth.utils.AbsentLiveData;

public class UserRepository {

    private final UserService mUserService;
    private final AppExecutors mAppExecutors;

    /**
     * @param mAppExecutors -AppExecutor
     * @param mUserService  - User service
     */
    public UserRepository(AppExecutors mAppExecutors, UserService mUserService) {
        this.mUserService = mUserService;
        this.mAppExecutors = mAppExecutors;
    }

    public LiveData<Resource<TospayUser>> login(LoginRequest request) {
        return new NetworkBoundResource<TospayUser, Result<TospayUser>>(mAppExecutors) {

            private TospayUser resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result<TospayUser> item) {
                resultsDb = item.getData();
            }

            @Override
            protected boolean shouldFetch(@Nullable TospayUser data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<TospayUser> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<TospayUser>() {
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
            protected LiveData<ApiResponse<Result<TospayUser>>> createCall() {
                return mUserService.login(request);
            }
        }.asLiveData();
    }

    public LiveData<Resource<TospayUser>> register(RegisterRequest request) {
        return new NetworkBoundResource<TospayUser, Result<TospayUser>>(mAppExecutors) {

            private TospayUser resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result<TospayUser> item) {
                resultsDb = item.getData();
            }

            @Override
            protected boolean shouldFetch(@Nullable TospayUser data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<TospayUser> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<TospayUser>() {
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
            protected LiveData<ApiResponse<Result<TospayUser>>> createCall() {
                return mUserService.register(request);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Result>> verifyEmail(VerifyEmailRequest request) {

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
                return mUserService.verifyEmail(request);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Result>> resendEmailToken(ResendEmailRequest request) {

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
                return mUserService.resendEmailToken(request);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Result>> verifyPhone(VerifyPhoneRequest request) {

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
                return mUserService.verifyPhone(request);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Result>> resendPhoneToken(OtpRequest request) {

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
                return mUserService.resendOtp(request);
            }
        }.asLiveData();
    }
}
