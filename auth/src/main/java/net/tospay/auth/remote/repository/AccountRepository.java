package net.tospay.auth.remote.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.model.Account;
import net.tospay.auth.model.AccountTitle;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.response.AccountResponse;
import net.tospay.auth.remote.response.ApiResponse;
import net.tospay.auth.remote.response.Result;
import net.tospay.auth.remote.service.AccountService;
import net.tospay.auth.remote.util.AppExecutors;
import net.tospay.auth.remote.util.NetworkBoundResource;
import net.tospay.auth.utils.AbsentLiveData;

import java.util.ArrayList;
import java.util.List;

public class AccountRepository {

    private final AccountService mAccountService;
    private final AppExecutors mAppExecutors;

    /**
     * @param mAppExecutors   - AppExecutor
     * @param mAccountService - Account service
     */
    public AccountRepository(AppExecutors mAppExecutors, AccountService mAccountService) {
        this.mAppExecutors = mAppExecutors;
        this.mAccountService = mAccountService;
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
                return mAccountService.accounts(bearerToken);
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
     * Retrieve user accounts
     *
     * @return LiveData
     */
    public LiveData<Resource<List<Account>>> accounts(String bearerToken) {
        return new NetworkBoundResource<List<Account>, Result<AccountResponse>>(mAppExecutors) {

            private List<Account> resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result<AccountResponse> item) {
                AccountResponse response = item.getData();

                List<Account> accounts = new ArrayList<>();

                for (Account account : response.getMobile()) {
                    account.setAccountType(AccountType.MOBILE);
                    accounts.add(account);
                }

                for (Account account : response.getCard()) {
                    account.setAccountType(AccountType.CARD);
                    accounts.add(account);
                }

                for (Account account : response.getBank()) {
                    account.setAccountType(AccountType.BANK);
                    accounts.add(account);
                }

                resultsDb = accounts;
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
            protected LiveData<ApiResponse<Result<AccountResponse>>> createCall() {
                return mAccountService.accounts(bearerToken);
            }

        }.asLiveData();
    }

}
