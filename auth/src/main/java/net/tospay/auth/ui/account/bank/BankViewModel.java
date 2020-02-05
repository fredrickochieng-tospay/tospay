package net.tospay.auth.ui.account.bank;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.tospay.auth.R;
import net.tospay.auth.model.Bank;
import net.tospay.auth.model.Country;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.repository.BankRepository;
import net.tospay.auth.remote.repository.GatewayRepository;
import net.tospay.auth.remote.request.BankRequest;
import net.tospay.auth.remote.response.AccountLinkResponse;
import net.tospay.auth.ui.base.BaseViewModel;

import java.util.List;

public class BankViewModel extends BaseViewModel<BankNavigator> implements View.OnClickListener {

    private MutableLiveData<Country> country = new MutableLiveData<>();
    private MutableLiveData<Bank> bank = new MutableLiveData<>();

    private BankRepository bankRepository;
    private GatewayRepository gatewayRepository;

    private LiveData<Resource<AccountLinkResponse>> linkResourceLiveData;
    private LiveData<Resource<List<Bank>>> banksResourceLiveData;

    public BankViewModel(BankRepository bankRepository, GatewayRepository gatewayRepository) {
        this.bankRepository = bankRepository;
        this.gatewayRepository = gatewayRepository;
    }

    public BankViewModel(GatewayRepository gatewayRepository) {
        this.gatewayRepository = gatewayRepository;
    }

    public MutableLiveData<Country> getCountry() {
        return country;
    }

    public MutableLiveData<Bank> getBank() {
        return bank;
    }

    public LiveData<Resource<AccountLinkResponse>> getLinkResourceLiveData() {
        return linkResourceLiveData;
    }

    public void link(Country country, Bank bank, String accountNo, String phone) {
        BankRequest request = new BankRequest();
        request.setCountry(country);
        request.setBank(bank);
        request.setAccountNumber(accountNo);
        request.setPhone(phone);

        linkResourceLiveData = bankRepository.link(getBearerToken().get(), request);
    }

    public LiveData<Resource<List<Bank>>> getBanksResourceLiveData() {
        return banksResourceLiveData;
    }

    public void banks(String iso) {
        banksResourceLiveData = gatewayRepository.banks(getBearerToken().get(), iso);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.selectCountryTextView) {
            getNavigator().onSelectCountryClick(view);

        } else if (view.getId() == R.id.selectBankTextView) {
            getNavigator().onSelectBankClick(view);

        } else if (view.getId() == R.id.btn_save) {
            getNavigator().onDone(view);
        }
    }
}
