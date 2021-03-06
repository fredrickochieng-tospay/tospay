package net.tospay.auth.ui.auth.phone;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.databinding.FragmentPhoneVerificationBinding;
import net.tospay.auth.model.TospayUser;
import net.tospay.auth.ui.UserViewModelFactory;
import net.tospay.auth.ui.base.BaseFragment;

public class PhoneVerificationFragment extends BaseFragment<FragmentPhoneVerificationBinding, PhoneViewModel>
        implements PhoneNavigator {

    private ProgressDialog mProgressDialog;
    private TospayUser tospayUser;
    private FragmentPhoneVerificationBinding mBinding;
    private PhoneViewModel mViewModel;

    public PhoneVerificationFragment() {
        // Required empty public constructor
    }

    @Override
    public int getBindingVariable() {
        return BR.phoneViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_phone_verification;
    }

    @Override
    public PhoneViewModel getViewModel() {
        UserViewModelFactory factory = new UserViewModelFactory(getUserRepository());
        mViewModel = ViewModelProviders.of(this, factory).get(PhoneViewModel.class);
        return mViewModel;
    }

    private TextWatcher otpTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (!TextUtils.isEmpty(charSequence)) {
                if (charSequence.length() >= 6) {
                    mViewModel.enableLoginButton.set(true);
                } else {
                    mViewModel.enableLoginButton.set(false);
                }
            } else {
                mViewModel.enableLoginButton.set(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = getViewDataBinding();
        mBinding.setPhoneViewModel(mViewModel);
        mViewModel.setNavigator(this);
        mBinding.codeEditText.addTextChangedListener(otpTextWatcher);

        tospayUser = getSharedPrefManager().getActiveUser();
        mViewModel.getUser().setValue(tospayUser);

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onVerifyClick(View view) {
        hideKeyboard();

        mProgressDialog.setMessage("Verifying OTP. Please wait...");
        mProgressDialog.show();

        String code = mBinding.codeEditText.getText().toString();
        mViewModel.verify(code);
        mViewModel.getVerifyResourceLiveData().observe(this, resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case ERROR:
                        mProgressDialog.dismiss();
                        mViewModel.setIsError(true);
                        Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                        break;

                    case LOADING:
                        mViewModel.setIsLoading(true);
                        mViewModel.setIsError(false);
                        break;

                    case SUCCESS:
                        mViewModel.setIsError(false);
                        tospayUser.setPhoneVerified(true);
                        getSharedPrefManager().setActiveUser(tospayUser);
                        mProgressDialog.dismiss();

                        NavHostFragment
                                .findNavController(this).navigate(
                                PhoneVerificationFragmentDirections
                                        .actionNavigationPhoneVerificationToNavigationLogin());

                        break;
                }
            }
        });
    }

    @Override
    public void onResendClick(View view) {
        hideKeyboard();

        mBinding.codeEditText.setText(null);

        mProgressDialog.setMessage("Resending OTP. Please wait...");
        mProgressDialog.show();

        mViewModel.resend();
        mViewModel.getResendResourceLiveData().observe(this, resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case ERROR:
                        mProgressDialog.dismiss();
                        mViewModel.setIsError(true);
                        Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                        break;

                    case LOADING:
                        mViewModel.setIsLoading(true);
                        mViewModel.setIsError(false);
                        break;

                    case SUCCESS:
                        mViewModel.setIsError(false);
                        mProgressDialog.dismiss();
                        Toast.makeText(getContext(), "Please check your phone for OTP", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }
}
