package net.tospay.auth.ui.auth.register;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.databinding.FragmentRegisterBinding;
import net.tospay.auth.model.Country;
import net.tospay.auth.model.TospayUser;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.ui.UserViewModelFactory;
import net.tospay.auth.ui.base.BaseFragment;
import net.tospay.auth.ui.dialog.country.CountryDialog;
import net.tospay.auth.utils.EmailValidator;
import net.tospay.auth.utils.NetworkUtils;


public class RegisterFragment extends BaseFragment<FragmentRegisterBinding, RegisterViewModel>
        implements RegisterNavigation, CountryDialog.CountrySelectedListener {

    private RegisterViewModel mViewModel;
    private FragmentRegisterBinding mBinding;

    private EditText firstNameEditText;
    private TextInputLayout firstNameInputLayout;

    private EditText lastNameEditText;
    private TextInputLayout lastNameInputLayout;

    private EditText emailEditText;
    private TextInputLayout emailInputLayout;

    private EditText phoneEditText;
    private TextInputLayout phoneInputLayout;

    private EditText passwordEditText;
    private TextInputLayout passwordInputLayout;

    private EditText confirmPasswordEditText;
    private TextInputLayout confirmPasswordInputLayout;

    private Country country = null;
    private ProgressDialog mProgressDialog;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public int getBindingVariable() {
        return BR.registerViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_register;
    }

    @Override
    public RegisterViewModel getViewModel() {
        UserViewModelFactory factory = new UserViewModelFactory(getUserRepository());
        mViewModel = ViewModelProviders.of(this, factory).get(RegisterViewModel.class);
        return mViewModel;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = getViewDataBinding();

        mBinding.setRegisterViewModel(mViewModel);
        mViewModel.setNavigator(this);

        firstNameEditText = mBinding.firstNameEditText;
        firstNameInputLayout = mBinding.firstNameInputLayout;

        lastNameEditText = mBinding.lastNameEditText;
        lastNameInputLayout = mBinding.lastNameInputLayout;

        emailEditText = mBinding.emailEditText;
        emailInputLayout = mBinding.emailInputLayout;

        phoneEditText = mBinding.phoneEditText;
        phoneInputLayout = mBinding.phoneInputLayout;

        passwordEditText = mBinding.passwordEditText;
        passwordInputLayout = mBinding.passwordInputLayout;

        confirmPasswordEditText = mBinding.confirmPasswordEditText;
        confirmPasswordInputLayout = mBinding.confirmPasswordInputLayout;

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    if (EmailValidator.isEmailValid(s.toString())) {
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
        });

        firstNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    firstNameInputLayout.setErrorEnabled(false);
                    firstNameInputLayout.setError(null);
                } else {
                    firstNameInputLayout.setErrorEnabled(true);
                    firstNameInputLayout.setError(getString(net.tospay.auth.R.string.required, "First name"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        lastNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    lastNameInputLayout.setErrorEnabled(false);
                    lastNameInputLayout.setError(null);
                } else {
                    lastNameInputLayout.setErrorEnabled(true);
                    lastNameInputLayout.setError(getString(net.tospay.auth.R.string.required, "Last name"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mViewModel.getCountry().observe(this, country -> this.country = country);

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage("Creating account. Please wait...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(firstNameEditText.getText())) {
            firstNameInputLayout.setErrorEnabled(true);
            firstNameInputLayout.setError(getString(net.tospay.auth.R.string.required, "First name"));
            return false;
        }

        if (TextUtils.isEmpty(lastNameEditText.getText())) {
            lastNameInputLayout.setErrorEnabled(true);
            lastNameInputLayout.setError(getString(net.tospay.auth.R.string.required, "Last name"));
            return false;
        }

        if (TextUtils.isEmpty(phoneEditText.getText())) {
            phoneInputLayout.setErrorEnabled(true);
            phoneInputLayout.setError(getString(R.string.required, "Phone number"));
            return false;
        }

        if (TextUtils.isEmpty(emailEditText.getText())) {
            emailInputLayout.setErrorEnabled(true);
            emailInputLayout.setError(getString(R.string.required, "Email"));
            return false;
        }

        if (TextUtils.isEmpty(passwordEditText.getText())) {
            passwordInputLayout.setErrorEnabled(true);
            passwordInputLayout.setError(getString(R.string.required, "Password"));
            return false;
        }

        if (TextUtils.isEmpty(confirmPasswordEditText.getText())) {
            confirmPasswordInputLayout.setErrorEnabled(true);
            confirmPasswordInputLayout.setError(getString(R.string.please_confirm_password));
            return false;
        }

        if (!passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())) {
            confirmPasswordInputLayout.setErrorEnabled(true);
            confirmPasswordInputLayout.setError(getString(R.string.passwords_do_not_match));
            return false;
        }

        return true;
    }

    @Override
    public void onRegisterClick(View view) {
        if (!validateInputs()) {
            return;
        }

        hideKeyboard();

        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String phone = phoneEditText.getText().toString();

        if (NetworkUtils.isNetworkAvailable(getContext())) {
            mViewModel.register(firstName, lastName, email, password, phone, country);
            mViewModel.getResponseLiveData().observe(this, this::handleResponse);
        } else {
            Snackbar.make(mBinding.container, getString(R.string.internet_error), Snackbar.LENGTH_LONG).show();
        }
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
                    mProgressDialog.show();
                    mViewModel.setIsLoading(true);
                    mViewModel.setIsError(false);
                    break;

                case SUCCESS:
                    mProgressDialog.dismiss();
                    mViewModel.setIsError(false);

                    TospayUser user = resource.data;
                    getSharedPrefManager().setActiveUser(user);

                    NavHostFragment.findNavController(this).navigate(RegisterFragmentDirections.actionNavigationRegisterToNavigationEmailVerification());

                    break;
            }
        }
    }

    @Override
    public void onLoginClick(View view) {
        NavHostFragment.findNavController(this).navigateUp();
    }

    @Override
    public void onSelectCountry(View view) {
        CountryDialog.newInstance(false).show(getChildFragmentManager(), CountryDialog.TAG);
    }

    @Override
    public void onCountrySelected(Country country) {
        mViewModel.getCountry().setValue(country);
    }
}
