package net.tospay.auth.ui.account;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.repository.GatewayRepository;
import net.tospay.auth.ui.base.BaseViewModel;

import java.util.List;

public class AccountViewModel extends BaseViewModel<AccountNavigator>
        implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private ObservableBoolean isEmpty;
    private GatewayRepository mGatewayRepository;
    private LiveData<Resource<List<AccountType>>> resourceLiveData;

    public ObservableBoolean enableLoginButton;
    private ObservableBoolean showWallet;

    private MutableLiveData<String> phone = new MutableLiveData<>();

    public MutableLiveData<String> getPhone() {
        return phone;
    }

    public AccountViewModel(GatewayRepository mGatewayRepository) {
        this.mGatewayRepository = mGatewayRepository;
        this.isEmpty = new ObservableBoolean();
        this.showWallet = new ObservableBoolean(true);
        this.enableLoginButton = new ObservableBoolean(false);
    }

    public void fetchAccounts(boolean showWallet) {
        this.showWallet.set(showWallet);
        String bearerToken = getBearerToken().get();
        resourceLiveData = mGatewayRepository.accounts(bearerToken, showWallet);
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
