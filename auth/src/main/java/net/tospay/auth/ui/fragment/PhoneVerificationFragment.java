package net.tospay.auth.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import net.tospay.auth.R;
import net.tospay.auth.TospayAuth;
import net.tospay.auth.api.listeners.VerificationListener;
import net.tospay.auth.api.response.TospayException;
import net.tospay.auth.databinding.FragmentPhoneVerificationBinding;
import net.tospay.auth.model.TospayUser;
import net.tospay.auth.viewmodel.ErrorViewModel;

public class PhoneVerificationFragment extends BaseFragment {

    private ErrorViewModel mErrorViewModel;
    private TospayUser tospayUser;
    private TospayAuth auth;

    private ConstraintLayout warning_layout;
    private TextView warning_text;
    private FrameLayout loader;

    private FragmentPhoneVerificationBinding mBinding;
    private Activity mActivity;

    public PhoneVerificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mActivity = getActivity();
        this.auth = TospayAuth.getInstance(getContext());
        this.tospayUser = auth.getCurrentUser();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_phone_verification, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mErrorViewModel = ViewModelProviders.of(this).get(ErrorViewModel.class);

        mBinding.setUser(tospayUser);
        mBinding.setErrorViewModel(mErrorViewModel);

        mErrorViewModel.getErrorMessage().observe(this, s -> {
            if (s != null) {
                touchable(mActivity);
                loader.setVisibility(View.GONE);
                warning_layout.setVisibility(View.VISIBLE);
                warning_text.setText(s);
            }
        });

        warning_layout = mBinding.error.warningLayout;
        warning_text = mBinding.error.warningTextView;
        loader = mBinding.loadingLayout.loader;

        mBinding.btnVerifyPhone.setOnClickListener(this::onVerifyClicked);
        mBinding.btnResend.setOnClickListener(this::resend);
    }

    private void onVerifyClicked(View view) {
        /*String otp = otpView.getText().toString();

        warning_layout.setVisibility(View.INVISIBLE);

        if (TextUtils.isEmpty(otp)) {
            warning_layout.setVisibility(View.VISIBLE);
            warning_text.setText("Code required");
            return;
        }

        if (otp.length() < 6) {
            warning_layout.setVisibility(View.VISIBLE);
            warning_text.setText("Code is too short");
            return;
        }

        hideKeyBoard(mActivity, view);
        untouchable(mActivity);

        verifyPhone(otp);*/
    }

    private void verifyPhone(String code) {

        loader.setVisibility(View.VISIBLE);
        warning_layout.setVisibility(View.INVISIBLE);

        auth.verifyPhone(tospayUser.getPhone(), code, new VerificationListener() {
            @Override
            public void onSuccess() {
                tospayUser.setPhoneVerified(true);
                auth.updateCurrentUser(tospayUser);

                NavHostFragment.findNavController(PhoneVerificationFragment.this)
                        .navigate(R.id.navigation_login);
            }

            @Override
            public void onError(TospayException exception) {
                mErrorViewModel.getErrorMessage().setValue(exception.getErrorMessage());
            }
        });

    }

    public void resend(View view) {
        loader.setVisibility(View.VISIBLE);
        warning_layout.setVisibility(View.INVISIBLE);

        hideKeyBoard(mActivity, view);
        untouchable(mActivity);

        auth.resendOtp(tospayUser.getPhone(), new VerificationListener() {
            @Override
            public void onSuccess() {
                touchable(mActivity);
                loader.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Please check your SMS", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(TospayException exception) {
                mErrorViewModel.getErrorMessage().setValue(exception.getErrorMessage());
            }
        });
    }
}
