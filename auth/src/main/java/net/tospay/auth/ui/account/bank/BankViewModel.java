package net.tospay.auth.ui.account.bank;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.tospay.auth.R;
import net.tospay.auth.model.Country;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.repository.BankRepository;
import net.tospay.auth.remote.request.BankRequest;
import net.tospay.auth.remote.response.AccountLinkResponse;
import net.tospay.auth.ui.base.BaseViewModel;

public class BankViewModel extends BaseViewModel<BankNavigator> implements View.OnClickListener {
    private MutableLiveData<Country> country = new MutableLiveData<>();

    private final BankRepository repository;
    private LiveData<Resource<AccountLinkResponse>> resourceLiveData;

    public BankViewModel(BankRepository repository) {
        this.repository = repository;
    }

    public MutableLiveData<Country> getCountry() {
        return country;
    }

    public LiveData<Resource<AccountLinkResponse>> getResourceLiveData() {
        return resourceLiveData;
    }

    public void link(String accountNo, String phone) {
        BankRequest request = new BankRequest();
        request.setCountry(country.getValue());
        resourceLiveData = repository.link(getBearerToken().get(), request);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.selectCountryTextView) {
            getNavigator().onSelectCountryClick(view);
        }
    }
}
