package net.tospay.auth.viewmodel;

import androidx.lifecycle.MutableLiveData;

import net.tospay.auth.model.Merchant;
import net.tospay.auth.model.Payment;

public class PaymentViewModel extends BaseViewModel {

    private MutableLiveData<Merchant> merchantMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Payment> transactionMutableLiveData = new MutableLiveData<>();


    public MutableLiveData<Merchant> merchant() {
        return merchantMutableLiveData;
    }

    public MutableLiveData<Payment> transaction() {
        return transactionMutableLiveData;
    }
}
