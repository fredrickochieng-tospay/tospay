package net.tospay.auth.ui.auth.phone;

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
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.UserRepository;
import net.tospay.auth.remote.service.UserService;
import net.tospay.auth.ui.base.BaseFragment;
import net.tospay.auth.viewmodelfactory.UserViewModelFactory;

public class PhoneVerificationFragment extends BaseFragment<FragmentPhoneVerificationBinding, PhoneViewModel>
        implements PhoneNavigator {

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
        UserRepository repository = new UserRepository(getAppExecutors(),
                ServiceGenerator.createService(UserService.class, getContext()));
        UserViewModelFactory factory = new UserViewModelFactory(repository);
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
                if (charSequence.length() >= 5) {
                    mBinding.codeInputLayout.setError(null);
                    mViewModel.getOtp().setValue(charSequence.toString());
                } else {
                    mBinding.codeInputLayout.setError(getString(R.string.invalid_otp));
                }
            } else {
                mBinding.codeInputLayout.setError(getString(R.string.invalid_otp));
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
    }

    @Override
    public void onVerifyClick(View view) {
        mBinding.codeInputLayout.setError(null);

        String code = mBinding.codeEditText.getText().toString();
        mViewModel.getOtp().setValue(code);

        if (TextUtils.isEmpty(code)) {
            mBinding.codeInputLayout.setError(getString(R.string.invalid_otp));
            return;
        }

        if (code.length() < 5) {
            mBinding.codeInputLayout.setError(getString(R.string.invalid_otp));
            return;
        }

        mViewModel.verify();
        mViewModel.getVerifyResourceLiveData().observe(this, resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        hideKeyboard();
                        mViewModel.setIsLoading(true);
                        mViewModel.setIsError(false);
                        break;

                    case ERROR:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        break;

                    case SUCCESS:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(false);
                        tospayUser.setPhoneVerified(true);
                        getSharedPrefManager().setActiveUser(tospayUser);
                        NavHostFragment.findNavController(this).navigate(
                                PhoneVerificationFragmentDirections.actionNavigationPhoneVerificationToNavigationLogin());
                        break;
                }
            }
        });
    }

    @Override
    public void onResendClick(View view) {
        mViewModel.resend();
        mViewModel.getResendResourceLiveData().observe(this, resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        hideKeyboard();
                        mViewModel.setIsLoading(true);
                        mViewModel.setIsError(false);
                        break;

                    case ERROR:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        break;

                    case SUCCESS:
                        mViewModel.setIsError(false);
                        mViewModel.setIsLoading(false);
                        Toast.makeText(getContext(), "Please check your phone for OTP", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }
}
