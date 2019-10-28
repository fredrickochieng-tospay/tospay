package net.tospay.auth.ui.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.tospay.auth.R;
import net.tospay.auth.TospayAuth;
import net.tospay.auth.TospayGateway;
import net.tospay.auth.api.listeners.CountryListener;
import net.tospay.auth.api.listeners.UserListener;
import net.tospay.auth.api.response.TospayException;
import net.tospay.auth.databinding.FragmentRegisterBinding;
import net.tospay.auth.model.Country;
import net.tospay.auth.model.TospayUser;
import net.tospay.auth.utils.EmailValidator;
import net.tospay.auth.utils.NetworkUtils;

import java.util.List;

import butterknife.OnClick;


public class RegisterFragment extends BaseFragment implements CountryListener {

    private EditText firstNameEditText;
    private TextView firstNameFooter;

    private EditText lastNameEditText;
    private TextView lastNameFooter;

    private EditText emailEditText;
    private TextView emailFooter;

    private EditText phoneEditText;
    private TextView phoneFooter;

    private EditText passwordEditText;
    private TextView passwordFooter;

    private EditText confirmPasswordEditText;
    private TextView confirmPasswordFooter;

    private ConstraintLayout warning_layout;
    private FrameLayout loader;

    private TextView warning_text;
    private Spinner spinner;

    private List<Country> countries;
    private Country country = null;
    private TospayAuth auth;

    private FragmentRegisterBinding mBinding;
    private Activity mActivity;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mActivity = getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = TospayAuth.getInstance(getContext());

        firstNameEditText = mBinding.firstNameEditText;
        firstNameFooter = mBinding.firstNameFooter;

        lastNameEditText = mBinding.lastNameEditText;
        lastNameFooter = mBinding.lastNameFooter;

        emailEditText = mBinding.emailEditText;
        emailFooter = mBinding.emailFooter;

        phoneEditText = mBinding.phoneEditText;
        phoneFooter = mBinding.phoneFooter;

        passwordEditText = mBinding.passwordEditText;
        passwordFooter = mBinding.passwordFooter;

        spinner = mBinding.spinner;

        confirmPasswordEditText = mBinding.confirmPasswordEditText;
        confirmPasswordFooter = mBinding.confirmPasswordFooter;

        warning_layout = mBinding.error.warningLayout;
        warning_text = mBinding.error.warningTextView;

        loader = mBinding.loadingLayout.loader;

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    if (EmailValidator.isEmailValid(s.toString())) {
                        emailFooter.setVisibility(View.INVISIBLE);
                    } else {
                        emailFooter.setVisibility(View.VISIBLE);
                        emailFooter.setText(getString(net.tospay.auth.R.string.required, "Email"));
                    }
                } else {
                    emailFooter.setVisibility(View.VISIBLE);
                    emailFooter.setText(getString(net.tospay.auth.R.string.required, "Email"));
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
                if (s.length() > 0) {
                    firstNameFooter.setVisibility(View.INVISIBLE);
                } else {
                    firstNameFooter.setVisibility(View.VISIBLE);
                    firstNameFooter.setText(getString(net.tospay.auth.R.string.required, "First name"));
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
                if (s.length() > 0) {
                    lastNameFooter.setVisibility(View.INVISIBLE);
                } else {
                    lastNameFooter.setVisibility(View.VISIBLE);
                    lastNameFooter.setText(getString(net.tospay.auth.R.string.required, "Last name"));
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
                if (s.length() < 6) {
                    passwordFooter.setVisibility(View.VISIBLE);
                    passwordFooter.setText("Password should be at least 6 characters");
                } else {
                    passwordFooter.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                country = countries.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (NetworkUtils.isNetworkAvailable(view.getContext())) {
            fetchCountries();
        } else {
            Toast.makeText(view.getContext(), "Unable to fetch countries", Toast.LENGTH_SHORT).show();
        }

        mBinding.btnLogin.setOnClickListener(view1 -> NavHostFragment.findNavController(this)
                .navigate(R.id.navigation_login));

        mBinding.btnSignUp.setOnClickListener(this::register);
    }

    private void fetchCountries() {
        TospayGateway.getInstance(getContext()).fetchCountries(this);
    }

    private boolean validateInputs() {
        firstNameFooter.setVisibility(View.INVISIBLE);
        lastNameFooter.setVisibility(View.INVISIBLE);
        phoneFooter.setVisibility(View.INVISIBLE);
        emailFooter.setVisibility(View.INVISIBLE);
        passwordFooter.setVisibility(View.INVISIBLE);
        confirmPasswordFooter.setVisibility(View.INVISIBLE);

        if (TextUtils.isEmpty(firstNameEditText.getText())) {
            firstNameFooter.setVisibility(View.VISIBLE);
            firstNameFooter.setText(getString(R.string.required, "First name"));
            return false;
        }

        if (TextUtils.isEmpty(lastNameEditText.getText())) {
            lastNameFooter.setVisibility(View.VISIBLE);
            lastNameFooter.setText(getString(R.string.required, "Last name"));
            return false;
        }

        if (TextUtils.isEmpty(phoneEditText.getText())) {
            phoneFooter.setVisibility(View.VISIBLE);
            phoneFooter.setText(getString(R.string.required, "Phone number"));
            return false;
        }

        if (TextUtils.isEmpty(emailEditText.getText())) {
            emailFooter.setVisibility(View.VISIBLE);
            emailFooter.setText(getString(R.string.required, "Email"));
            return false;
        }

        if (TextUtils.isEmpty(passwordEditText.getText())) {
            passwordFooter.setVisibility(View.VISIBLE);
            passwordFooter.setText(getString(R.string.required, "Password"));
            return false;
        }

        if (TextUtils.isEmpty(confirmPasswordEditText.getText())) {
            confirmPasswordFooter.setVisibility(View.VISIBLE);
            confirmPasswordFooter.setText(getString(R.string.please_confirm_password));
            return false;
        }

        if (!passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())) {
            confirmPasswordFooter.setVisibility(View.VISIBLE);
            confirmPasswordFooter.setText(getString(R.string.passwords_do_not_match));
            return false;
        }

        return true;
    }

    private void register(View view) {
        if (!validateInputs()) {
            return;
        }

        hideKeyBoard(mActivity, view);
        untouchable(mActivity);

        warning_layout.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);

        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String phone = phoneEditText.getText().toString();

        auth.register(firstName, lastName, email, password, phone, country, new UserListener() {
            @Override
            public void onUser(TospayUser user) {
                touchable(mActivity);
                loader.setVisibility(View.GONE);

                NavHostFragment.findNavController(RegisterFragment.this)
                        .navigate(R.id.navigation_email_verification);
            }

            @Override
            public void onError(TospayException error) {
                touchable(mActivity);
                loader.setVisibility(View.GONE);
                warning_layout.setVisibility(View.VISIBLE);
                warning_text.setText(error.getErrorMessage());
            }

            @Override
            public void onUnAuthenticated() {

            }
        });
    }

    @Override
    public void onCountries(List<Country> countries) {
        this.countries = countries;
        ArrayAdapter<Country> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, countries);
        spinner.setAdapter(adapter);
    }

    @Override
    public void onError(TospayException error) {
        Toast.makeText(getContext(), error.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }
}
