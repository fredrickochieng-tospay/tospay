package net.tospay.auth.ui.account.mobile;

import android.app.ProgressDialog;
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
import net.tospay.auth.viewmodelfactory.MobileViewModelFactory;
import net.tospay.auth.ui.base.BaseFragment;
import net.tospay.auth.utils.NetworkUtils;

public class VerifyMobileFragment extends BaseFragment<FragmentVerifyMobileBinding, MobileMoneyViewModel>
        implements MobileMoneyNavigator {

    private MobileMoneyViewModel viewModel;
    private FragmentVerifyMobileBinding mBinding;
    private Account account;
    private ProgressDialog progressDialog;

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
                ServiceGenerator.createService(MobileService.class));
        MobileViewModelFactory factory = new MobileViewModelFactory(repository);
        viewModel = ViewModelProviders.of(this, factory).get(MobileMoneyViewModel.class);
        return viewModel;
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
                    viewModel.getOtp().setValue(charSequence.toString());
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
        mBinding.setMobileViewModel(viewModel);
        viewModel.setNavigator(this);

        if (getArguments() != null) {
            account = VerifyMobileFragmentArgs.fromBundle(getArguments()).getAccount();
            viewModel.getPhone().setValue("****" + account.getTrunc());
        }

        mBinding.codeEditText.addTextChangedListener(otpTextWatcher);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onConfirmClick(View view) {
        mBinding.codeInputLayout.setError(null);

        String code = mBinding.codeEditText.getText().toString();
        viewModel.getOtp().setValue(code);

        if (TextUtils.isEmpty(code)) {
            mBinding.codeInputLayout.setError(getString(R.string.invalid_otp));
            return;
        }

        if (code.length() < 5) {
            mBinding.codeInputLayout.setError(getString(R.string.invalid_otp));
            return;
        }

        if (NetworkUtils.isNetworkAvailable(view.getContext())) {
            viewModel.verify(account.getId());
            viewModel.getVerifyResourceLiveData().observe(this, resultResource -> {
                if (resultResource != null) {
                    switch (resultResource.status) {
                        case ERROR:
                            progressDialog.dismiss();
                            viewModel.setIsError(true);
                            viewModel.setErrorMessage(resultResource.message);
                            break;

                        case SUCCESS:
                            progressDialog.dismiss();
                            NavHostFragment.findNavController(this).navigateUp();
                            break;

                        case LOADING:
                            viewModel.setIsError(false);
                            viewModel.setIsLoading(true);
                            progressDialog.setMessage("Verifying OTP. Please wait...");
                            progressDialog.show();
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
        if (NetworkUtils.isNetworkAvailable(view.getContext())) {
            viewModel.resend(account.getId());
            viewModel.getResendResourceLiveData().observe(this, resultResource -> {
                if (resultResource != null) {
                    switch (resultResource.status) {
                        case ERROR:
                            progressDialog.dismiss();
                            viewModel.setIsLoading(false);
                            viewModel.setIsError(true);
                            viewModel.setErrorMessage(resultResource.message);
                            break;

                        case SUCCESS:
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "OTP Sent. Please check your SMS", Toast.LENGTH_SHORT).show();
                            break;

                        case LOADING:
                            viewModel.setIsError(false);
                            viewModel.setIsLoading(true);
                            progressDialog.setMessage("Resend OTP. Please check your phone...");
                            progressDialog.show();
                            break;
                    }
                }
            });

        } else {
            Snackbar.make(mBinding.container, getString(R.string.internet_error), Snackbar.LENGTH_LONG).show();
        }
    }
}
