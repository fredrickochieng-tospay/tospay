package net.tospay.auth.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import net.tospay.auth.R;
import net.tospay.auth.R2;
import net.tospay.auth.TospayAuth;
import net.tospay.auth.api.listeners.UserListener;
import net.tospay.auth.api.response.TospayException;
import net.tospay.auth.model.TospayUser;
import net.tospay.auth.utils.EmailValidator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

public class LoginFragment extends BaseFragment {

    @BindView(R2.id.emailFooter)
    TextView emailFooter;

    @BindView(R2.id.passwordFooter)
    TextView passwordFooter;

    @BindView(R2.id.emailEditText)
    EditText emailEditText;

    @BindView(R2.id.passwordEditText)
    EditText passwordEditText;

    @BindView(R2.id.warning_layout)
    ConstraintLayout warning_layout;

    @BindView(R2.id.warning_text_view)
    TextView warning_text_view;

    @BindView(R2.id.loader)
    FrameLayout loadingProgressBar;

    private Activity mActivity;
    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        navController = Navigation.findNavController(view);

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
                        emailFooter.setText(getString(R.string.invalid_email));
                    }
                } else {
                    emailFooter.setVisibility(View.VISIBLE);
                    emailFooter.setText(getString(R.string.invalid_email));
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
                    passwordFooter.setText(getString(R.string.invalid_password));
                } else {
                    passwordFooter.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean validateInputs() {
        emailFooter.setVisibility(View.INVISIBLE);
        passwordFooter.setVisibility(View.INVISIBLE);

        if (TextUtils.isEmpty(emailEditText.getText())) {
            emailFooter.setVisibility(View.VISIBLE);
            emailFooter.setText(getString(R.string.invalid_email));
            return false;
        }

        if (TextUtils.isEmpty(passwordEditText.getText())) {
            passwordFooter.setVisibility(View.VISIBLE);
            passwordFooter.setText(getString(R.string.invalid_password));
            return false;
        }

        return true;
    }

    @OnClick(R2.id.btn_login)
    void login(View view) {
        if (!validateInputs()) {
            return;
        }

        hideKeyBoard(mActivity, view);
        untouchable(mActivity);

        warning_layout.setVisibility(View.GONE);
        loadingProgressBar.setVisibility(View.VISIBLE);

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        TospayAuth.getInstance(view.getContext()).login(email, password, new UserListener() {
            @Override
            public void onUser(TospayUser user) {
                touchable(mActivity);
                loadingProgressBar.setVisibility(View.GONE);
                if (!user.isEmailVerified()) {
                    navController.navigate(R.id.navigation_email_verification);
                } else if (!user.isPhoneVerified()) {
                    navController.navigate(R.id.navigation_phone_verification);
                } else {
                    navController.navigate(R.id.navigation_account_selection);
                }
            }

            @Override
            public void onError(TospayException error) {
                touchable(mActivity);
                loadingProgressBar.setVisibility(View.GONE);
                warning_layout.setVisibility(View.VISIBLE);
                warning_text_view.setText(error.getErrorMessage());
            }

            @Override
            public void onUnAuthenticated() {

            }
        });
    }

    @OnClick(R2.id.btn_sign_up)
    void signUp() {
        navController.navigate(R.id.navigation_register);
    }
}
