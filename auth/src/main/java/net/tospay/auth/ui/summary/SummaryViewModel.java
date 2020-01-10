package net.tospay.auth.ui.summary;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.tospay.auth.model.TospayUser;
import net.tospay.auth.model.transfer.Transfer;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.repository.PaymentRepository;
import net.tospay.auth.ui.base.BaseViewModel;

public class SummaryViewModel extends BaseViewModel<SummaryNavigator>
        implements SwipeRefreshLayout.OnRefreshListener {

    private final PaymentRepository paymentRepository;
    private LiveData<Resource<Transfer>> detailsResourceLiveData;

    private MutableLiveData<Transfer> transfer;
    private MutableLiveData<TospayUser> user;

    public SummaryViewModel(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
        this.transfer = new MutableLiveData<>();
        this.user = new MutableLiveData<>();
    }

    public MutableLiveData<Transfer> getTransfer() {
        return transfer;
    }

    public MutableLiveData<TospayUser> getUser() {
        return user;
    }

    public void details(String paymentId) {
        detailsResourceLiveData = paymentRepository.details(paymentId);
    }

    public LiveData<Resource<Transfer>> getDetailsResourceLiveData() {
        return detailsResourceLiveData;
    }

    @Override
    public void onRefresh() {
        getNavigator().onRefresh();
    }
}
