package net.tospay.auth.ui.auth.phone;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.tospay.auth.R;
import net.tospay.auth.remote.request.OtpRequest;
import net.tospay.auth.remote.request.VerifyPhoneRequest;
import net.tospay.auth.remote.response.Result;
import net.tospay.auth.model.TospayUser;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.repository.UserRepository;
import net.tospay.auth.ui.base.BaseViewModel;

public class PhoneViewModel extends BaseViewModel<PhoneNavigator> implements View.OnClickListener {

    public MutableLiveData<TospayUser> user = new MutableLiveData<>();
    public ObservableBoolean enableLoginButton;
    private UserRepository userRepository;
    private LiveData<Resource<Result>> verifyResourceLiveData;
    private LiveData<Resource<Result>> resendResourceLiveData;

    public PhoneViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.enableLoginButton = new ObservableBoolean(false);
    }

    public LiveData<Resource<Result>> getVerifyResourceLiveData() {
        return verifyResourceLiveData;
    }

    public LiveData<Resource<Result>> getResendResourceLiveData() {
        return resendResourceLiveData;
    }


    public void verify(String code) {
        setIsError(false);
        String phone = user.getValue().getPhone();
        verifyResourceLiveData = userRepository.verifyPhone(new VerifyPhoneRequest(phone, code));
    }

    public void resend() {
        setIsError(false);
        String phone = user.getValue().getPhone();
        resendResourceLiveData = userRepository.resendPhoneToken(new OtpRequest(phone));
    }

    public MutableLiveData<TospayUser> getUser() {
        return user;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btn_verify_phone) {
            getNavigator().onVerifyClick(view);

        } else if (id == R.id.btn_resend) {
            getNavigator().onResendClick(view);
        }
    }

    public void setUser(MutableLiveData<TospayUser> user) {
        this.user = user;
    }
}
