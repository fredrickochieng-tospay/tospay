package net.tospay.auth.ui.auth.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputLayout;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.databinding.FragmentLoginBinding;
import net.tospay.auth.model.TospayUser;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.UserRepository;
import net.tospay.auth.remote.service.UserService;
import net.tospay.auth.ui.base.BaseFragment;
import net.tospay.auth.utils.EmailValidator;
import net.tospay.auth.utils.SharedPrefManager;
import net.tospay.auth.viewmodelfactory.UserViewModelFactory;

public class LoginFragment extends BaseFragment<FragmentLoginBinding, LoginViewModel>
        implements LoginNavigator {

    private LoginViewModel mViewModel;
    private TextInputLayout emailInputLayout;
    private TextInputLayout passwordInputLayout;
    private EditText emailEditText;
    private EditText passwordEditText;
    private NavController navController;

    private String email;
    private String password;

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
        UserRepository repository = new UserRepository(getAppExecutors(),
                ServiceGenerator.createService(UserService.class, getContext()));
        UserViewModelFactory factory = new UserViewModelFactory(repository);
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

        }

        @Override
        public void afterTextChanged(Editable s) {
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

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!TextUtils.isEmpty(s)) {
                if (s.length() < 6) {
                    passwordInputLayout.setErrorEnabled(true);
                    passwordInputLayout.setError(getString(R.string.invalid_password));
                } else {
                    passwordInputLayout.setErrorEnabled(false);
                    passwordInputLayout.setError(null);
                }
            }
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

        if (getSharedPrefManager().read(SharedPrefManager.KEY_REMEMBER_ME, false)) {
            if (getSharedPrefManager().getActiveUser() != null) {
                emailEditText.setText(getSharedPrefManager().getActiveUser().getEmail());
            }
        }

        navController = Navigation.findNavController(view);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                mListener.onLoginFailed();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(callback);

        mBinding.rememberMeCheckbox.setOnCheckedChangeListener((compoundButton, b) ->
                getSharedPrefManager().save(SharedPrefManager.KEY_REMEMBER_ME, b));
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
        emailInputLayout.setError(null);
        passwordInputLayout.setError(null);

        if (!isEmailValid(emailEditText.getText().toString())) {
            emailInputLayout.setError(getString(R.string.invalid_email));
            return;
        }

        if (!isPasswordValid(passwordEditText.getText().toString())) {
            passwordInputLayout.setError(getString(R.string.invalid_password));
            return;
        }

        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();

        mViewModel.getEmail().setValue(email);
        mViewModel.getPassword().setValue(password);
        mViewModel.login();
        mViewModel.getResponseLiveData().observe(this, this::handleResponse);
    }

    private void handleResponse(Resource<TospayUser> resource) {
        if (resource != null) {
            switch (resource.status) {

                case LOADING:
                    hideKeyboard();
                    mViewModel.setLoadingTitle("Authenticating. Please wait...");
                    mViewModel.setIsLoading(true);
                    mViewModel.setIsError(false);
                    break;

                case ERROR:
                    passwordEditText.setText(null);
                    mViewModel.setIsLoading(false);
                    mViewModel.setIsError(true);
                    mViewModel.setErrorMessage(resource.message);
                    break;

                case SUCCESS:
                    mViewModel.setIsLoading(false);
                    mViewModel.setIsError(false);

                    TospayUser user = resource.data;
                    if (user != null) {
                        getSharedPrefManager().setActiveUser(user);
                        getSharedPrefManager().save(SharedPrefManager.KEY_EMAIL, email);
                        getSharedPrefManager().save(SharedPrefManager.KEY_PASSWORD, password);

                        if (!user.isEmailVerified()) {
                            navController.navigate(R.id.navigation_email_verification);
                        } else if (!user.isPhoneVerified()) {
                            navController.navigate(R.id.navigation_phone_verification);
                        } else {
                            mListener.onLoginSuccess(user);
                        }
                    }

                    break;
            }
        }
    }

    @Override
    public void signUp(View view) {
        navController.navigate(LoginFragmentDirections.actionNavigationLoginToNavigationRegister());
    }

    @Override
    public void forgotPassword(View view) {
        navController.navigate(LoginFragmentDirections.actionNavigationLoginToNavigationForgotPassword());
    }
}
