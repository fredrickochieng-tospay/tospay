package net.tospay.auth.ui.account.verify;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.tospay.auth.R;
import net.tospay.auth.TospayGateway;
import net.tospay.auth.api.request.MobileAccountVerificationRequest;
import net.tospay.auth.api.request.MobileRequest;
import net.tospay.auth.api.response.MobileResponse;
import net.tospay.auth.api.response.Result;
import net.tospay.auth.model.Country;
import net.tospay.auth.model.Network;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.repository.GatewayRepository;
import net.tospay.auth.ui.base.BaseViewModel;

import java.util.HashMap;
import java.util.Map;

public class MobileMoneyViewModel extends BaseViewModel<MobileMoneyNavigator> implements View.OnClickListener {

    private MutableLiveData<Country> country = new MutableLiveData<>();
    private MutableLiveData<Network> network = new MutableLiveData<>();
    private MutableLiveData<String> phone = new MutableLiveData<>();

    private LiveData<Resource<MobileResponse>> mobileResourceLiveData;
    private LiveData<Resource<Result>> resendResourceLiveData;
    private LiveData<Resource<Result>> verifyResourceLiveData;

    private GatewayRepository gatewayRepository;

    public MobileMoneyViewModel(GatewayRepository gatewayRepository) {
        this.gatewayRepository = gatewayRepository;
    }

    public MutableLiveData<Country> getCountry() {
        return country;
    }

    public MutableLiveData<Network> getNetwork() {
        return network;
    }

    public MutableLiveData<String> getPhone() {
        return phone;
    }

    public LiveData<Resource<MobileResponse>> getMobileResourceLiveData() {
        return mobileResourceLiveData;
    }

    public LiveData<Resource<Result>> getResendResourceLiveData() {
        return resendResourceLiveData;
    }

    public LiveData<Resource<Result>> getVerifyResourceLiveData() {
        return verifyResourceLiveData;
    }

    public void linkAccount(String phone, String name) {
        MobileRequest request = new MobileRequest();
        request.setCountry(country.getValue());
        request.setNetwork(network.getValue());
        request.setNumber(phone);
        request.setAlias(name);

        mobileResourceLiveData = gatewayRepository.linkMobileAccount(getBearerToken().get(), request);
    }

    public void verifyAccount(String accountId, String code) {
        MobileAccountVerificationRequest request = new MobileAccountVerificationRequest(accountId, code);
        verifyResourceLiveData = gatewayRepository.verifyMobile(getBearerToken().get(), request);
    }

    public void resend(String accountId) {
        Map<String, Object> request = new HashMap<>();
        request.put("id", accountId);

        resendResourceLiveData = gatewayRepository.resendVerificationCode(getBearerToken().get(), request);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.selectCountryTextView) {
            getNavigator().onSelectCountryClick(view);

        } else if (view.getId() == R.id.selectNetworkTextView) {
            getNavigator().onNetworkCountryClick(view);

        } else if (view.getId() == R.id.btn_save) {
            getNavigator().onConfirmClick(view);

        } else if (view.getId() == R.id.btn_resend) {
            getNavigator().onResendClick(view);

        } else if (view.getId() == R.id.btn_verify_phone) {
            getNavigator().onConfirmClick(view);
        }
    }
}
