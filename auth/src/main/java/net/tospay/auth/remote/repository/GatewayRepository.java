package net.tospay.auth.remote.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import net.tospay.auth.remote.request.MobileAccountVerificationRequest;
import net.tospay.auth.remote.request.MobileRequest;
import net.tospay.auth.remote.request.PaymentRequest;
import net.tospay.auth.remote.response.AccountResponse;
import net.tospay.auth.remote.response.ApiResponse;
import net.tospay.auth.remote.response.MobileResponse;
import net.tospay.auth.remote.response.PaymentResponse;
import net.tospay.auth.remote.response.PaymentValidationResponse;
import net.tospay.auth.remote.response.Result;
import net.tospay.auth.remote.response.WalletTransactionResponse;
import net.tospay.auth.remote.service.GatewayService;
import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.model.Account;
import net.tospay.auth.model.AccountTitle;
import net.tospay.auth.model.Country;
import net.tospay.auth.model.Network;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.util.AppExecutors;
import net.tospay.auth.remote.util.NetworkBoundResource;
import net.tospay.auth.utils.AbsentLiveData;

import java.util.ArrayList;
import java.util.HashMap;
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

                //Wallets
                if (showWallet) {
                    title = new AccountTitle();
                    title.setName("myWallet Accounts");
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
                accountList = setAccountType(accounts.getCard(), AccountType.CARD);
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
                        if (accountType == AccountType.CARD) {
                            account.setVerified(true);
                        }
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
