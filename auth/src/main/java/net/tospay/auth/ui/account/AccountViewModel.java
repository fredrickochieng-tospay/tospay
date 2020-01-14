package net.tospay.auth.ui.account;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.model.transfer.Amount;
import net.tospay.auth.model.transfer.Charge;
import net.tospay.auth.model.transfer.Transfer;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.repository.AccountRepository;
import net.tospay.auth.remote.repository.PaymentRepository;
import net.tospay.auth.ui.base.BaseViewModel;

import java.util.List;

public class AccountViewModel extends BaseViewModel<AccountNavigator>
        implements SwipeRefreshLayout.OnRefreshListener {

    private ObservableBoolean isEmpty;

    private AccountRepository accountRepository;
    private PaymentRepository paymentRepository;

    private LiveData<Resource<List<AccountType>>> accountsResourceLiveData;
    private LiveData<Resource<Amount>> amountResourceLiveData;
    private LiveData<Resource<String>> paymentResourceLiveData;
    private LiveData<Resource<String>> transferResourceLiveData;
    private MutableLiveData<Transfer> transfer;
    private MutableLiveData<Charge> charge;

    public AccountViewModel(AccountRepository accountRepository, PaymentRepository paymentRepository) {
        this.accountRepository = accountRepository;
        this.paymentRepository = paymentRepository;
        this.isEmpty = new ObservableBoolean();
        this.transfer = new MutableLiveData<>();
        this.charge = new MutableLiveData<>();
    }

    public MutableLiveData<Transfer> getTransfer() {
        return transfer;
    }

    public LiveData<Resource<List<AccountType>>> getAccountsResourceLiveData() {
        return accountsResourceLiveData;
    }

    public LiveData<Resource<String>> getPaymentResourceLiveData() {
        return paymentResourceLiveData;
    }

    public MutableLiveData<Charge> getCharge() {
        return charge;
    }

    public void fetchAccounts(boolean showWallet) {
        String bearerToken = getBearerToken().get();
        accountsResourceLiveData = accountRepository.accounts(bearerToken, showWallet);
    }

    public void pay(String paymentId, Transfer transfer) {
        String bearerToken = getBearerToken().get();
        paymentResourceLiveData = paymentRepository.pay(bearerToken, paymentId, transfer);
    }

    public void chargeLookup(Transfer transfer, String type) {
        amountResourceLiveData = paymentRepository.chargeLookup(getBearerToken().get(), transfer, type);
    }

    public LiveData<Resource<Amount>> getAmountResourceLiveData() {
        return amountResourceLiveData;
    }

    public void topup(Transfer transfer) {
        transferResourceLiveData = paymentRepository.transfer(getBearerToken().get(), transfer);
    }

    public LiveData<Resource<String>> getTransferResourceLiveData() {
        return transferResourceLiveData;
    }

    @Override
    public void onRefresh() {
        getNavigator().onRefresh();
    }

    public ObservableBoolean getIsEmpty() {
        return isEmpty;
    }

    public void setIsEmpty(boolean isEmpty) {
        this.isEmpty.set(isEmpty);
    }
}
