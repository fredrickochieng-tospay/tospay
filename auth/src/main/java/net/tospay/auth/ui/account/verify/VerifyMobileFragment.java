package net.tospay.auth.ui.account.verify;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.api.response.Result;
import net.tospay.auth.databinding.FragmentVerifyMobileBinding;
import net.tospay.auth.model.Account;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.ui.GatewayViewModelFactory;
import net.tospay.auth.ui.base.BaseFragment;

import org.w3c.dom.Text;


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
        GatewayViewModelFactory factory = new GatewayViewModelFactory(getGatewayRepository());
        viewModel = ViewModelProviders.of(this, factory).get(MobileMoneyViewModel.class);
        return viewModel;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = getViewDataBinding();
        mBinding.setMobileViewModel(viewModel);
        viewModel.setNavigator(this);
        account = VerifyMobileFragmentArgs.fromBundle(getArguments())
                .getAccount();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onConfirmClick(View view) {
        mBinding.codeEditText.setError(null);

        String code = mBinding.codeEditText.getText().toString();
        if (TextUtils.isEmpty(code)) {
            mBinding.codeEditText.setError("required");
            return;
        }

        progressDialog.setMessage("Verifying OTP. Please wait...");
        progressDialog.show();

        viewModel.verifyAccount(account.getId(), code);
        viewModel.getVerifyResourceLiveData().observe(this, resultResource -> {
            if (resultResource != null) {
                switch (resultResource.status) {
                    case ERROR:
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), resultResource.message, Toast.LENGTH_SHORT).show();
                        break;

                    case SUCCESS:
                        progressDialog.dismiss();
                        NavHostFragment.findNavController(this).navigateUp();
                        break;
                }
            }
        });
    }

    @Override
    public void onResendClick(View view) {
        progressDialog.setMessage("Resend OTP. Please check your phone...");
        progressDialog.show();

        viewModel.resend(account.getId());
        viewModel.getResendResourceLiveData().observe(this, resultResource -> {
            if (resultResource != null) {
                switch (resultResource.status) {
                    case ERROR:
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), resultResource.message, Toast.LENGTH_SHORT).show();
                        break;

                    case SUCCESS:
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "OTP Sent. Please check your SMS", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }
}
