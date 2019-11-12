package net.tospay.auth.ui.auth.login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputLayout;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.databinding.FragmentLoginBinding;
import net.tospay.auth.model.TospayUser;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.ui.GatewayViewModelFactory;
import net.tospay.auth.ui.UserViewModelFactory;
import net.tospay.auth.ui.base.BaseFragment;
import net.tospay.auth.utils.EmailValidator;

public class LoginFragment extends BaseFragment<FragmentLoginBinding, LoginViewModel>
        implements LoginNavigator {

    private LoginViewModel mViewModel;
    private TextInputLayout emailInputLayout;
    private TextInputLayout passwordInputLayout;
    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressDialog mProgressDialog;
    private NavController navController;

    @Override
    public int getBindingVariable() {
        return BR.loginViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    public LoginViewModel getViewModel() {
        UserViewModelFactory factory = new UserViewModelFactory(getUserRepository());
        mViewModel = ViewModelProviders.of(this, factory).get(LoginViewModel.class);
        return mViewModel;
    }

    /**
     * Validates email input on text change
     */
    private TextWatcher emailTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s)) {
                if (isEmailValid(s.toString())) {
                    emailInputLayout.setErrorEnabled(false);
                    emailInputLayout.setError(null);
                } else {
                    emailInputLayout.setErrorEnabled(true);
                    emailInputLayout.setError(getString(R.string.invalid_email));
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            validateInputs();
        }
    };

    /**
     * Validate password input on text changed
     */
    private TextWatcher passwordTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() < 6) {
                passwordInputLayout.setErrorEnabled(true);
                passwordInputLayout.setError(getString(R.string.invalid_password));
            } else {
                passwordInputLayout.setErrorEnabled(false);
                passwordInputLayout.setError(null);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            validateInputs();
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentLoginBinding mBinding = getViewDataBinding();
        mBinding.setLoginViewModel(mViewModel);
        mViewModel.setNavigator(this);

        emailEditText = mBinding.emailEditText;
        emailInputLayout = mBinding.emailInputLayout;

        passwordEditText = mBinding.passwordEditText;
        passwordInputLayout = mBinding.passwordInputLayout;

        emailEditText.addTextChangedListener(emailTextWatcher);
        passwordEditText.addTextChangedListener(passwordTextWatcher);

        validateInputs();

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage("Authenticating. Please wait...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);

        navController = Navigation.findNavController(view);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                mListener.onLoginFailed();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(callback);
    }

    /**
     * Checks if email and password are valid to enable login button
     */
    private void validateInputs() {
        if (isEmailValid(emailEditText.getText().toString()) && isPasswordValid(passwordEditText.getText().toString())) {
            mViewModel.enableLoginButton.set(true);
        } else {
            mViewModel.enableLoginButton.set(false);
        }
    }

    /**
     * Validate user email
     *
     * @param email - keyed email
     * @return true|false
     */
    private boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && EmailValidator.isEmailValid(email);
    }

    /**
     * Validates user email
     *
     * @param password - keyed password
     * @return true|false
     */
    private boolean isPasswordValid(String password) {
        return !TextUtils.isEmpty(password) && password.length() > 0;
    }

    @Override
    public void login(View view) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        mViewModel.getEmail().setValue(email);
        mViewModel.getPassword().setValue(password);

        hideKeyboard();
        mProgressDialog.show();

        mViewModel.login();
        mViewModel.getResponseLiveData().observe(this, this::handleResponse);
    }

    private void handleResponse(Resource<TospayUser> resource) {
        if (resource != null) {
            switch (resource.status) {
                case ERROR:
                    mProgressDialog.dismiss();
                    mViewModel.setIsError(true);
                    mViewModel.setErrorMessage(resource.message);
                    break;

                case LOADING:
                    mViewModel.setIsLoading(true);
                    mViewModel.setIsError(false);
                    break;

                case SUCCESS:
                    mProgressDialog.dismiss();
                    mViewModel.setIsError(false);

                    TospayUser user = resource.data;
                    getSharedPrefManager().setActiveUser(user);

                    if (!user.isEmailVerified()) {
                        navController.navigate(R.id.navigation_email_verification);

                    } else if (!user.isPhoneVerified()) {
                        navController.navigate(R.id.navigation_phone_verification);

                    } else {
                        mListener.onLoginSuccess(user);
                    }

                    break;
            }
        }
    }

    @Override
    public void signUp(View view) {
        navController.navigate(R.id.navigation_register);
    }

    @Override
    public void forgotPassword(View view) {

    }
}
