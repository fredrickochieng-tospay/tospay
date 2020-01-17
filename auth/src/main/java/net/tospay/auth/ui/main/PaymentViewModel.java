package net.tospay.auth.ui.main;

import androidx.lifecycle.MutableLiveData;

import net.tospay.auth.remote.repository.GatewayRepository;
import net.tospay.auth.ui.base.BaseViewModel;

public class PaymentViewModel extends BaseViewModel {

    private MutableLiveData<String> paymentTokenLiveData = new MutableLiveData<>();
    private final GatewayRepository gatewayRepository;

    public PaymentViewModel(GatewayRepository gatewayRepository) {
        this.gatewayRepository = gatewayRepository;
    }

    public MutableLiveData<String> getPaymentTokenLiveData() {
        return paymentTokenLiveData;
    }
}
