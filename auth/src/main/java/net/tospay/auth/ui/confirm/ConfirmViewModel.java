package net.tospay.auth.ui.confirm;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.tospay.auth.R;
import net.tospay.auth.remote.request.PaymentRequest;
import net.tospay.auth.remote.response.PaymentResponse;
import net.tospay.auth.model.Merchant;
import net.tospay.auth.model.PaymentTransaction;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.repository.GatewayRepository;
import net.tospay.auth.ui.base.BaseViewModel;

public class ConfirmViewModel extends BaseViewModel<ConfirmNavigator> implements View.OnClickListener {

    private GatewayRepository gatewayRepository;
    private MutableLiveData<PaymentTransaction> transaction;
    private MutableLiveData<Merchant> merchant;
    private MutableLiveData<PaymentRequest> payment;
    private LiveData<Resource<PaymentResponse>> resourceLiveData;

    public ConfirmViewModel(GatewayRepository gatewayRepository) {
        this.gatewayRepository = gatewayRepository;
        this.transaction = new MutableLiveData<>();
        this.merchant = new MutableLiveData<>();
        this.payment = new MutableLiveData<>();
    }

    public MutableLiveData<PaymentTransaction> getTransaction() {
        return transaction;
    }

    public MutableLiveData<Merchant> getMerchant() {
        return merchant;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_confirm) {
            getNavigator().onConfirm(view);
        }
    }

    public LiveData<Resource<PaymentResponse>> getResourceLiveData() {
        return resourceLiveData;
    }

    public MutableLiveData<PaymentRequest> getPayment() {
        return payment;
    }

    public void pay() {
        resourceLiveData = gatewayRepository.pay(getBearerToken().get(), payment.getValue());
    }
}
