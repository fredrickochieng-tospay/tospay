package net.tospay.auth.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import net.tospay.auth.api.request.MobileAccountVerificationRequest;
import net.tospay.auth.api.request.MobileRequest;
import net.tospay.auth.api.request.PaymentRequest;
import net.tospay.auth.api.response.AccountResponse;
import net.tospay.auth.api.response.ApiResponse;
import net.tospay.auth.api.response.MobileResponse;
import net.tospay.auth.api.response.PaymentResponse;
import net.tospay.auth.api.response.PaymentValidationResponse;
import net.tospay.auth.api.response.Result;
import net.tospay.auth.api.service.GatewayService;
import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.model.Account;
import net.tospay.auth.model.AccountTitle;
import net.tospay.auth.remote.Resource;
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

    /**
     * validate payment request
     *
     * @param param request
     * @return LiveData
     */
    public LiveData<Resource<PaymentValidationResponse>> validate(Map<String, String> param) {
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
                return mGatewayService.validate(param);
            }
        }.asLiveData();
    }

    /**
     * Retrieve user accounts
     *
     * @return LiveData
     */
    public LiveData<Resource<List<AccountType>>> accounts(String bearerToken, boolean showWallet) {
        return new NetworkBoundResource<List<AccountType>, Result<AccountResponse>>(mAppExecutors) {

            private List<AccountType> resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result<AccountResponse> item) {
                AccountResponse accounts = item.getData();

                List<AccountType> accountTypeList = new ArrayList<>();
                AccountTitle title;
                List<Account> accountList;

                if (showWallet) {
                    //Wallets
                    title = new AccountTitle();
                    title.setName("Wallet Account");
                    title.setAccountType(AccountType.WALLET);
                    accountTypeList.add(title);
                    accountTypeList.addAll(accounts.getWallet());
                }

                //Mobile Accounts
                title = new AccountTitle();
                title.setName("Mobile Accounts");
                title.setAccountType(AccountType.MOBILE);
                accountList = setAccountType(accounts.getMobile(), AccountType.MOBILE);
                accountTypeList.add(title);
                accountTypeList.addAll(accountList);

                //Card accounts
                title = new AccountTitle();
                title.setName("Card Accounts");
                title.setAccountType(AccountType.CARD);
                accountTypeList.add(title);
                accountList = setAccountType(accounts.getBank(), AccountType.CARD);
                accountTypeList.addAll(accountList);

                //Bank accounts
                title = new AccountTitle();
                title.setName("Bank Accounts");
                title.setAccountType(AccountType.BANK);
                accountTypeList.add(title);
                accountList = setAccountType(accounts.getBank(), AccountType.BANK);
                accountTypeList.addAll(accountList);

                resultsDb = accountTypeList;
            }

            @Override
            protected boolean shouldFetch(@Nullable List<AccountType> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<AccountType>> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<List<AccountType>>() {
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
            protected LiveData<ApiResponse<Result<AccountResponse>>> createCall() {
                return mGatewayService.accounts(bearerToken);
            }

            private List<Account> setAccountType(List<Account> accounts, int accountType) {
                if (accounts != null) {
                    for (Account account : accounts) {
                        account.setAccountType(accountType);
                    }

                    return accounts;
                }

                return new ArrayList<>();
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

    public LiveData<Resource<MobileResponse>> linkMobileAccount(String bearerToken, MobileRequest request) {
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
                return mGatewayService.linkMobileAccount(bearerToken, request);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Result>> resendVerificationCode(String bearerToken, Map<String, Object> param) {
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
                return mGatewayService.resendVerificationCode(bearerToken, param);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Result>> verifyMobile(String bearerToken, MobileAccountVerificationRequest request) {
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
                return mGatewayService.verifyMobileAccount(bearerToken, request);
            }
        }.asLiveData();
    }

}
