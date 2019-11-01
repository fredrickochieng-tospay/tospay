package net.tospay.auth.ui.summary;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.tospay.auth.R;
import net.tospay.auth.api.response.PaymentResult;
import net.tospay.auth.model.Merchant;
import net.tospay.auth.model.PaymentTransaction;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.repository.GatewayRepository;
import net.tospay.auth.ui.base.BaseViewModel;

import java.util.HashMap;
import java.util.Map;


public class SummaryViewModel extends BaseViewModel<SummaryNavigator>
        implements View.OnClickListener {

    private GatewayRepository repository;
    private LiveData<Resource<PaymentResult>> responseLiveData;
    private MutableLiveData<Merchant> merchantMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<PaymentTransaction> transactionMutableLiveData = new MutableLiveData<>();
    private ObservableBoolean isLoggedIn = new ObservableBoolean(false);

    public SummaryViewModel(GatewayRepository repository) {
        this.repository = repository;
    }

    public void validate(String token) {
        Map<String, String> param = new HashMap<>();
        param.put("token", token);
        responseLiveData = repository.validate(param);
    }

    public LiveData<Resource<PaymentResult>> getResponseLiveData() {
        return responseLiveData;
    }

    public MutableLiveData<Merchant> merchant() {
        return merchantMutableLiveData;
    }

    public MutableLiveData<PaymentTransaction> transaction() {
        return transactionMutableLiveData;
    }

    public ObservableBoolean getIsLoggedIn() {
        return isLoggedIn;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btn_login) {
            getNavigator().onLogin(view);

        } else if (id == R.id.btn_sign_up) {
            getNavigator().onSignUp(view);

        } else if (id == R.id.btn_continue) {
            getNavigator().onContinue(view);

        } else if (id == R.id.btn_cancel) {
            getNavigator().onCancel(view);
        }
    }
}
