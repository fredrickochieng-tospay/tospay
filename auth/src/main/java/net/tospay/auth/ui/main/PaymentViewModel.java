package net.tospay.auth.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.tospay.auth.remote.response.PaymentValidationResponse;
import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.model.Merchant;
import net.tospay.auth.model.PaymentTransaction;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.repository.GatewayRepository;
import net.tospay.auth.ui.base.BaseViewModel;

import java.util.HashMap;
import java.util.Map;


public class PaymentViewModel extends BaseViewModel {

    private MutableLiveData<Merchant> merchantMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<PaymentTransaction> transactionMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<AccountType> accountTypeMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<String> paymentTokenLiveData = new MutableLiveData<>();
    private LiveData<Resource<PaymentValidationResponse>> responseLiveData;

    private GatewayRepository gatewayRepository;

    public PaymentViewModel(GatewayRepository gatewayRepository) {
        this.gatewayRepository = gatewayRepository;
    }

    public MutableLiveData<Merchant> getMerchantMutableLiveData() {
        return merchantMutableLiveData;
    }

    public MutableLiveData<PaymentTransaction> getTransactionMutableLiveData() {
        return transactionMutableLiveData;
    }

    public MutableLiveData<AccountType> getAccountTypeMutableLiveData() {
        return accountTypeMutableLiveData;
    }

    public MutableLiveData<String> getPaymentTokenLiveData() {
        return paymentTokenLiveData;
    }

    public void checkTransactionStatus(String token) {
        Map<String, String> param = new HashMap<>();
        param.put("token", token);
        responseLiveData = gatewayRepository.validate(param);
    }

    public LiveData<Resource<PaymentValidationResponse>> getResponseLiveData() {
        return responseLiveData;
    }
}
