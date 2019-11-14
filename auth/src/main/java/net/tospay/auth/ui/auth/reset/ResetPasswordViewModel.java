package net.tospay.auth.ui.auth.reset;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.tospay.auth.R;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.repository.UserRepository;
import net.tospay.auth.remote.response.Result;
import net.tospay.auth.ui.base.BaseViewModel;

public class ResetPasswordViewModel extends BaseViewModel<ResetPasswordNavigator> implements View.OnClickListener {

    private MutableLiveData<String> mEmail;
    public ObservableBoolean enableLoginButton;
    private final UserRepository userRepository;

    private LiveData<Resource<Result>> resetResourceLiveData;
    private LiveData<Resource<Result>> resendResourceLiveData;

    public ResetPasswordViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.enableLoginButton = new ObservableBoolean(false);
        this.mEmail = new MutableLiveData<>();
    }

    public MutableLiveData<String> getEmail() {
        return mEmail;
    }

    public LiveData<Resource<Result>> getResetResourceLiveData() {
        return resetResourceLiveData;
    }

    public LiveData<Resource<Result>> getResendResourceLiveData() {
        return resendResourceLiveData;
    }

    public void resend() {
        resendResourceLiveData = userRepository.forgotPassword(mEmail.getValue());
    }

    public void reset(String code, String password) {
        resetResourceLiveData = userRepository.resetPassword(mEmail.getValue(), code, password);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_reset_password) {
            getNavigator().onResetPasswordClick(view);

        } else if (id == R.id.btn_resend) {
            getNavigator().onResendClick(view);
        }
    }
}
