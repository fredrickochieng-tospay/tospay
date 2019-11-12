package net.tospay.auth.ui.auth.email;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.tospay.auth.R;
import net.tospay.auth.remote.request.ResendEmailRequest;
import net.tospay.auth.remote.request.VerifyEmailRequest;
import net.tospay.auth.remote.response.Result;
import net.tospay.auth.model.TospayUser;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.repository.UserRepository;
import net.tospay.auth.ui.base.BaseViewModel;

public class EmailViewModel extends BaseViewModel<EmailNavigator> implements View.OnClickListener {

    public MutableLiveData<TospayUser> user = new MutableLiveData<>();
    public ObservableBoolean enableLoginButton;
    private UserRepository userRepository;
    private LiveData<Resource<Result>> verifyResourceLiveData;
    private LiveData<Resource<Result>> resendResourceLiveData;

    public EmailViewModel(UserRepository userRepository) {
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
        String email = user.getValue().getEmail();
        verifyResourceLiveData = userRepository.verifyEmail(new VerifyEmailRequest(email, code));
    }

    public void resend() {
        setIsError(false);
        String email = user.getValue().getEmail();
        resendResourceLiveData = userRepository.resendEmailToken(new ResendEmailRequest(email));
    }

    public MutableLiveData<TospayUser> getUser() {
        return user;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btn_verify_email) {
            getNavigator().onVerifyClick(view);

        } else if (id == R.id.btn_resend) {
            getNavigator().onResendClick(view);
        }
    }
}
