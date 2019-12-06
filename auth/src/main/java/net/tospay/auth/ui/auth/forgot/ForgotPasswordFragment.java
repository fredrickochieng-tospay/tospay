package net.tospay.auth.ui.auth.forgot;

import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.databinding.FragmentForgotPasswordBinding;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.response.Result;
import net.tospay.auth.ui.UserViewModelFactory;
import net.tospay.auth.ui.base.BaseFragment;
import net.tospay.auth.utils.EmailValidator;
import net.tospay.auth.utils.NetworkUtils;

public class ForgotPasswordFragment extends BaseFragment<FragmentForgotPasswordBinding, ForgotPasswordViewModel>
        implements ForgotPasswordNavigator {

    private ForgotPasswordViewModel mViewModel;
    private FragmentForgotPasswordBinding mBinding;
    private TextInputLayout emailInputLayout;
    private EditText emailEditText;
    private ProgressDialog mProgressDialog;
    private NavController navController;

    @Override
    public int getBindingVariable() {
        return BR.forgotPasswordViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_forgot_password;
    }

    @Override
    public ForgotPasswordViewModel getViewModel() {
        UserViewModelFactory factory = new UserViewModelFactory(getUserRepository());
        mViewModel = ViewModelProviders.of(this, factory).get(ForgotPasswordViewModel.class);
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
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding = getViewDataBinding();
        mBinding.setForgotPasswordViewModel(mViewModel);
        mViewModel.setNavigator(this);

        emailEditText = mBinding.emailEditText;
        emailInputLayout = mBinding.emailInputLayout;
        emailEditText.addTextChangedListener(emailTextWatcher);

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage("Send verification code. Please wait...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);

        navController = Navigation.findNavController(view);
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

    @Override
    public void onForgotPasswordClick(View view) {
        emailInputLayout.setError(null);

        String email = emailEditText.getText().toString();
        if (!isEmailValid(email)) {
            emailInputLayout.setError(getString(R.string.invalid_email));
            return;
        }

        mViewModel.getEmail().setValue(email);

        if (NetworkUtils.isNetworkAvailable(view.getContext())) {
            mViewModel.forgotPassword();
            mViewModel.getResponseLiveData().observe(this, this::handleResponse);
        } else {
            Snackbar.make(mBinding.container, getString(R.string.internet_error), Snackbar.LENGTH_LONG).show();
        }
    }

    private void handleResponse(Resource<Result> resource) {
        if (resource != null) {
            switch (resource.status) {
                case ERROR:
                    mProgressDialog.dismiss();
                    mViewModel.setIsError(true);
                    mViewModel.setErrorMessage(resource.message);
                    break;

                case LOADING:
                    hideKeyboard();
                    mProgressDialog.show();
                    mViewModel.setIsLoading(true);
                    mViewModel.setIsError(false);
                    break;

                case SUCCESS:
                    mProgressDialog.dismiss();
                    mViewModel.setIsError(false);
                    Toast.makeText(getContext(), "OTP sent to your email", Toast.LENGTH_SHORT).show();

                    ForgotPasswordFragmentDirections.ActionNavigationForgotPasswordToNavigationResetPassword
                            action = ForgotPasswordFragmentDirections
                            .actionNavigationForgotPasswordToNavigationResetPassword(mViewModel.getEmail().getValue());

                    navController.navigate(action);
                    break;
            }
        }
    }

    @Override
    public void onLoginClick(View view) {
        navController.navigateUp();
    }
}
