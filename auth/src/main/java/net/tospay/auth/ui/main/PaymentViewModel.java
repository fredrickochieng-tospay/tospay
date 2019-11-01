package net.tospay.auth.ui.main;

import androidx.lifecycle.MutableLiveData;

import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.model.Merchant;
import net.tospay.auth.model.PaymentTransaction;
import net.tospay.auth.ui.base.BaseViewModel;


public class PaymentViewModel extends BaseViewModel {

    private MutableLiveData<Merchant> merchantMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<PaymentTransaction> transactionMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<AccountType> accountTypeMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<String> paymentTokenLiveData = new MutableLiveData<>();

    public PaymentViewModel() {

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
}
