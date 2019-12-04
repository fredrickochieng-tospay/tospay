package net.tospay.auth.ui.auth.reset;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputLayout;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.databinding.FragmentResetPasswordBinding;
import net.tospay.auth.ui.UserViewModelFactory;
import net.tospay.auth.ui.base.BaseFragment;

public class ResetPasswordFragment extends BaseFragment<FragmentResetPasswordBinding, ResetPasswordViewModel>
        implements ResetPasswordNavigator {

    private ResetPasswordViewModel mViewModel;
    private TextInputLayout passwordInputLayout;
    private EditText passwordEditText;
    private FragmentResetPasswordBinding mBinding;
    private ProgressDialog mProgressDialog;
    private NavController navController;

    @Override
    public int getBindingVariable() {
        return BR.resetPasswordViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_reset_password;
    }

    @Override
    public ResetPasswordViewModel getViewModel() {
        UserViewModelFactory factory = new UserViewModelFactory(getUserRepository());
        mViewModel = ViewModelProviders.of(this, factory).get(ResetPasswordViewModel.class);
        return mViewModel;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = getViewDataBinding();
        mBinding.setResetPasswordViewModel(mViewModel);
        mViewModel.setNavigator(this);
        mBinding.codeEditText.addTextChangedListener(otpTextWatcher);
        String email = ResetPasswordFragmentArgs.fromBundle(getArguments()).getEmail();


        passwordEditText = mBinding.passwordEditText;
        passwordInputLayout = mBinding.passwordInputLayout;
        passwordEditText.addTextChangedListener(passwordTextWatcher);

        mViewModel.getEmail().setValue(email);

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);

        navController = Navigation.findNavController(view);
    }

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
        }
    };

    private TextWatcher otpTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            /*if (!TextUtils.isEmpty(charSequence)) {
                if (charSequence.length() >= 6) {
                    mViewModel.enableLoginButton.set(true);
                } else {
                    mViewModel.enableLoginButton.set(false);
                }
            } else {
                mViewModel.enableLoginButton.set(false);
            }*/
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

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
    public void onResetPasswordClick(View view) {

        mViewModel.reset(mBinding.codeEditText.getText().toString(), mBinding.passwordEditText.getText().toString());
        mViewModel.getResetResourceLiveData().observe(this, resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case ERROR:
                        mProgressDialog.dismiss();
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                        break;

                    case LOADING:
                        hideKeyboard();
                        mProgressDialog.setMessage("Resending verification code. Please wait...");
                        mProgressDialog.show();
                        mViewModel.setIsLoading(true);
                        mViewModel.setIsError(false);
                        break;

                    case SUCCESS:
                        mProgressDialog.dismiss();
                        mViewModel.setIsError(false);
                        Toast.makeText(getContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                        navController.navigate(ResetPasswordFragmentDirections.actionNavigationResetPasswordToNavigationLogin());
                        break;
                }
            }
        });
    }

    @Override
    public void onResendClick(View view) {
        hideKeyboard();
        mProgressDialog.setMessage("Resending verification code. Please wait...");
        mProgressDialog.show();

        mViewModel.resend();
        mViewModel.getResendResourceLiveData().observe(this, resource -> {
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
                        Toast.makeText(getContext(), "OTP sent to your email", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }
}
