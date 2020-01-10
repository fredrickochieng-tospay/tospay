package net.tospay.auth.ui.main;

import androidx.lifecycle.MutableLiveData;

import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.remote.repository.GatewayRepository;
import net.tospay.auth.ui.base.BaseViewModel;

public class PaymentViewModel extends BaseViewModel {

    private MutableLiveData<AccountType> accountTypeMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<String> paymentTokenLiveData = new MutableLiveData<>();
    //private LiveData<Resource<PaymentValidationResponse>> responseLiveData;

    private GatewayRepository gatewayRepository;

    public PaymentViewModel(GatewayRepository gatewayRepository) {
        this.gatewayRepository = gatewayRepository;
    }

    public MutableLiveData<AccountType> getAccountTypeMutableLiveData() {
        return accountTypeMutableLiveData;
    }

    public MutableLiveData<String> getPaymentTokenLiveData() {
        return paymentTokenLiveData;
    }
}
