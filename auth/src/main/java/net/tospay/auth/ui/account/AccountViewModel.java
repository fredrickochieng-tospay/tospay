package net.tospay.auth.ui.account;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.model.transfer.Transfer;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.repository.AccountRepository;
import net.tospay.auth.ui.base.BaseViewModel;

import java.util.List;

public class AccountViewModel extends BaseViewModel<AccountNavigator>
        implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private ObservableBoolean isEmpty;
    private AccountRepository repository;
    private LiveData<Resource<List<AccountType>>> resourceLiveData;
    private MutableLiveData<Transfer> transfer;

    public AccountViewModel(AccountRepository repository) {
        this.repository = repository;
        this.isEmpty = new ObservableBoolean();
        this.transfer = new MutableLiveData<>();
    }

    public MutableLiveData<Transfer> getTransfer() {
        return transfer;
    }


    public void fetchAccounts(boolean showWallet) {
        String bearerToken = getBearerToken().get();
        resourceLiveData = repository.accounts(bearerToken, showWallet);
    }

    public LiveData<Resource<List<AccountType>>> getResourceLiveData() {
        return resourceLiveData;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onRefresh() {
        getNavigator().onRefresh();
    }

    public ObservableBoolean getIsEmpty() {
        return isEmpty;
    }

    public void setIsEmpty(boolean isEmpty) {
        this.isEmpty.set(isEmpty);
    }
}
