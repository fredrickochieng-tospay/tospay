package net.tospay.auth.viewmodel;

import androidx.lifecycle.MutableLiveData;

import net.tospay.auth.model.Merchant;
import net.tospay.auth.model.Payment;
import net.tospay.auth.ui.base.BaseViewModel;

import javax.inject.Inject;

public class PaymentViewModel extends BaseViewModel {

    private MutableLiveData<Merchant> merchantMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Payment> transactionMutableLiveData = new MutableLiveData<>();

    @Inject
    public PaymentViewModel() {

    }

    public MutableLiveData<Merchant> merchant() {
        return merchantMutableLiveData;
    }

    public MutableLiveData<Payment> transaction() {
        return transactionMutableLiveData;
    }
}
