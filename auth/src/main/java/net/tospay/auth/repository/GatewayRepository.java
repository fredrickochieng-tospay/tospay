package net.tospay.auth.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import net.tospay.auth.api.response.AccountResponse;
import net.tospay.auth.api.response.ApiResponse;
import net.tospay.auth.api.response.PaymentResult;
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
import java.util.Collection;
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
    public LiveData<Resource<PaymentResult>> validate(Map<String, String> param) {
        return new NetworkBoundResource<PaymentResult, Result<PaymentResult>>(mAppExecutors) {

            private PaymentResult resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result<PaymentResult> item) {
                resultsDb = item.getData();
            }

            @Override
            protected boolean shouldFetch(@Nullable PaymentResult data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<PaymentResult> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<PaymentResult>() {
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
            protected LiveData<ApiResponse<Result<PaymentResult>>> createCall() {
                return mGatewayService.validate(param);
            }
        }.asLiveData();
    }


    /**
     * Retrieve user accounts
     *
     * @return LiveData
     */
    public LiveData<Resource<List<AccountType>>> accounts(String bearerToken) {
        return new NetworkBoundResource<List<AccountType>, Result<AccountResponse>>(mAppExecutors) {

            private List<AccountType> resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result<AccountResponse> item) {
                AccountResponse accounts = item.getData();

                List<AccountType> accountTypeList = new ArrayList<>();
                AccountTitle title;
                List<Account> accountList;

                //Wallets
                title = new AccountTitle();
                title.setName("Wallet Account");
                title.setAccountType(AccountType.WALLET);
                accountTypeList.add(title);
                accountTypeList.addAll(accounts.getWallet());

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
}
