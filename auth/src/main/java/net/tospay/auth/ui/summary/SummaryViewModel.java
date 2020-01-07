package net.tospay.auth.ui.summary;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.tospay.auth.model.transfer.Transfer;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.repository.PaymentRepository;
import net.tospay.auth.ui.base.BaseViewModel;


public class SummaryViewModel extends BaseViewModel<SummaryNavigator>
        implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private final PaymentRepository paymentRepository;
    private LiveData<Resource<Transfer>> detailsResourceLiveData;
    private MutableLiveData<Transfer> transfer;

    public SummaryViewModel(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
        this.transfer = new MutableLiveData<>();
    }

    public MutableLiveData<Transfer> getTransfer() {
        return transfer;
    }

    public void details(String paymentId) {
        detailsResourceLiveData = paymentRepository.details(paymentId);
    }

    public LiveData<Resource<Transfer>> getDetailsResourceLiveData() {
        return detailsResourceLiveData;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

    }

    @Override
    public void onRefresh() {
        getNavigator().onRefresh();
    }
}
