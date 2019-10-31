package net.tospay.auth.viewmodel;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.tospay.auth.TospayGateway;
import net.tospay.auth.api.listeners.ListAccountListener;
import net.tospay.auth.api.response.AccountResponse;
import net.tospay.auth.api.response.TospayException;
import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.model.Account;
import net.tospay.auth.model.AccountTitle;
import net.tospay.auth.model.Merchant;
import net.tospay.auth.model.Payment;
import net.tospay.auth.model.Wallet;

import java.util.ArrayList;
import java.util.List;

public class AccountViewModel extends BaseViewModel
        implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    public ObservableBoolean isEmpty;

    private MutableLiveData<List<AccountType>> accountTypes = new MutableLiveData<>();
    private List<AccountType> accountTypeList = new ArrayList<>();
    private AccountTitle title;
    private List<Account> accountList;

    public AccountViewModel() {
        this.isEmpty = new ObservableBoolean();
    }

    public MutableLiveData<List<AccountType>> getAccountTypes() {
        return accountTypes;
    }

    public void fetchAccounts(TospayGateway tospayGateway) {
        setIsLoading(true);

        tospayGateway.fetchAccounts(new ListAccountListener() {
            @Override
            public void onAccounts(AccountResponse accounts) {
                setIsLoading(false);
                accountTypeList.clear();

                if (accounts.getWallet() != null) {
                    if (!accounts.getWallet().isEmpty()) {
                        title = new AccountTitle();
                        title.setName("Wallet Account");
                        Wallet wallet = accounts.getWallet().get(0);
                        accountTypeList.add(title);
                        accountTypeList.add(wallet);
                    }
                }

                if (accounts.getMobile() != null) {
                    if (!accounts.getMobile().isEmpty()) {
                        title = new AccountTitle();
                        title.setName("Mobile Accounts");
                        accountList = setAccountType(accounts.getMobile(), "mobile");
                        accountTypeList.add(title);
                        accountTypeList.addAll(accountList);
                    }
                }

                if (accounts.getCard() != null) {
                    if (!accounts.getCard().isEmpty()) {
                        title = new AccountTitle();
                        title.setName("Card Accounts");
                        accountList = setAccountType(accounts.getMobile(), "card");
                        accountTypeList.add(title);
                        accountList = setAccountType(accounts.getBank(), "card");
                    }
                }

                if (accounts.getBank() != null) {
                    if (!accounts.getBank().isEmpty()) {
                        title = new AccountTitle();
                        title.setName("Bank Accounts");
                        accountList = setAccountType(accounts.getMobile(), "bank");
                        accountTypeList.add(title);
                        accountList = setAccountType(accounts.getBank(), "bank");
                    }
                }

                isEmpty.set(accountTypeList.isEmpty());
                accountTypes.setValue(accountTypeList);
            }

            @Override
            public void onError(TospayException error) {
                setIsLoading(false);
            }
        });
    }

    private List<Account> setAccountType(List<Account> accounts, String type) {
        for (Account account : accounts) {
            account.setAccountType(type);
        }

        return accounts;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onRefresh() {
        //fetchAccounts();
    }
}
