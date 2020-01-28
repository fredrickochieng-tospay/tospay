package net.tospay.auth.ui.account.mobile;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.databinding.FragmentVerifyMobileBinding;
import net.tospay.auth.model.Account;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.MobileRepository;
import net.tospay.auth.remote.service.MobileService;
import net.tospay.auth.ui.base.BaseFragment;
import net.tospay.auth.utils.NetworkUtils;
import net.tospay.auth.viewmodelfactory.MobileViewModelFactory;

public class VerifyMobileFragment extends BaseFragment<FragmentVerifyMobileBinding, MobileMoneyViewModel>
        implements MobileMoneyNavigator {

    private MobileMoneyViewModel mViewModel;
    private FragmentVerifyMobileBinding mBinding;
    private Account account;

    public VerifyMobileFragment() {
        // Required empty public constructor
    }

    @Override
    public int getBindingVariable() {
        return BR.mobileMoneyViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_verify_mobile;
    }

    @Override
    public MobileMoneyViewModel getViewModel() {
        MobileRepository repository = new MobileRepository(getAppExecutors(),
                ServiceGenerator.createService(MobileService.class, getContext()));
        MobileViewModelFactory factory = new MobileViewModelFactory(repository);
        mViewModel = ViewModelProviders.of(this, factory).get(MobileMoneyViewModel.class);
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
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = getViewDataBinding();
        mBinding.setMobileViewModel(mViewModel);
        mViewModel.setNavigator(this);

        if (getArguments() != null) {
            account = VerifyMobileFragmentArgs.fromBundle(getArguments()).getAccount();
            mViewModel.getPhone().setValue("****" + account.getTrunc());
        }

        mBinding.codeEditText.addTextChangedListener(otpTextWatcher);
    }

    @Override
    public void onConfirmClick(View view) {
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

        if (NetworkUtils.isNetworkAvailable(view.getContext())) {
            mViewModel.verify(account.getId());
            mViewModel.getVerifyResourceLiveData().observe(this, resultResource -> {
                if (resultResource != null) {
                    switch (resultResource.status) {
                        case LOADING:
                            mViewModel.setIsError(false);
                            mViewModel.setIsLoading(true);
                            break;

                        case ERROR:
                            mViewModel.setIsLoading(false);
                            mViewModel.setIsError(true);
                            mViewModel.setErrorMessage(resultResource.message);
                            break;

                        case SUCCESS:
                            mViewModel.setIsLoading(false);
                            mViewModel.setIsError(false);
                            NavHostFragment.findNavController(this).navigateUp();
                            break;
                    }
                }
            });
        } else {
            Snackbar.make(mBinding.container, getString(R.string.internet_error), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResendClick(View view) {
        mViewModel.resend(account.getId());
        mViewModel.getResendResourceLiveData().observe(this, resultResource -> {
            if (resultResource != null) {
                switch (resultResource.status) {
                    case LOADING:
                        mViewModel.setIsError(false);
                        mViewModel.setIsLoading(true);
                        break;

                    case ERROR:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resultResource.message);
                        break;

                    case SUCCESS:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(false);
                        Toast.makeText(getContext(), "OTP Sent. Please check your SMS", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }
}
