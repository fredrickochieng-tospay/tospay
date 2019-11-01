package net.tospay.auth.ui.account;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.repository.GatewayRepository;
import net.tospay.auth.ui.base.BaseViewModel;

import java.util.List;

public class AccountViewModel extends BaseViewModel
        implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private ObservableBoolean isEmpty;
    private GatewayRepository mGatewayRepository;
    private LiveData<Resource<List<AccountType>>> resourceLiveData;

    public AccountViewModel(GatewayRepository mGatewayRepository) {
        this.mGatewayRepository = mGatewayRepository;
        this.isEmpty = new ObservableBoolean();
    }

    public void fetchAccounts() {
        resourceLiveData = mGatewayRepository.accounts((String) getBearerToken().get());
    }

    public LiveData<Resource<List<AccountType>>> getResourceLiveData() {
        return resourceLiveData;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onRefresh() {
        fetchAccounts();
    }

    public ObservableBoolean getIsEmpty() {
        return isEmpty;
    }

    public void setIsEmpty(boolean isEmpty) {
        this.isEmpty.set(isEmpty);
    }
}
