package net.tospay.auth.ui.summary;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.tospay.auth.R;
import net.tospay.auth.remote.response.PaymentValidationResponse;
import net.tospay.auth.model.Merchant;
import net.tospay.auth.model.PaymentTransaction;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.repository.GatewayRepository;
import net.tospay.auth.ui.base.BaseViewModel;

import java.util.HashMap;
import java.util.Map;


public class SummaryViewModel extends BaseViewModel<SummaryNavigator>
        implements View.OnClickListener {

    private GatewayRepository repository;
    private LiveData<Resource<PaymentValidationResponse>> responseLiveData;
    private MutableLiveData<Merchant> merchantMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<PaymentTransaction> transactionMutableLiveData = new MutableLiveData<>();

    public SummaryViewModel(GatewayRepository repository) {
        this.repository = repository;
    }

    public void validatePaymentToken(String token) {
        Map<String, String> param = new HashMap<>();
        param.put("token", token);

        setIsLoading(true);
        responseLiveData = repository.validate(getBearerToken().get(), param);
    }

    public LiveData<Resource<PaymentValidationResponse>> getResponseLiveData() {
        return responseLiveData;
    }

    public MutableLiveData<Merchant> merchant() {
        return merchantMutableLiveData;
    }

    public MutableLiveData<PaymentTransaction> transaction() {
        return transactionMutableLiveData;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_continue) {
            getNavigator().onContinue(view);
        }
    }
}
