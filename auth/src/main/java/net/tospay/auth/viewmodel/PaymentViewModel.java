package net.tospay.auth.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.tospay.auth.model.Merchant;
import net.tospay.auth.model.Payment;

public class PaymentViewModel extends ViewModel {

    private MutableLiveData<Merchant> merchantMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Payment> transactionMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(true);

    public MutableLiveData<Merchant> merchant() {
        return merchantMutableLiveData;
    }

    public MutableLiveData<Payment> transaction() {
        return transactionMutableLiveData;
    }

    public MutableLiveData<Boolean> isLoading() {
        return isLoading;
    }
}
