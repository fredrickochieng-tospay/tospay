package net.tospay.auth.ui.confirm;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.api.request.PaymentRequest;
import net.tospay.auth.api.response.PaymentResponse;
import net.tospay.auth.api.response.TospayException;
import net.tospay.auth.databinding.FragmentConfirmBinding;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.Status;
import net.tospay.auth.ui.GatewayViewModelFactory;
import net.tospay.auth.ui.base.BaseFragment;
import net.tospay.auth.ui.main.TospayActivity;

public class ConfirmFragment extends BaseFragment<FragmentConfirmBinding, ConfirmViewModel>
        implements ConfirmNavigator {

    private ConfirmViewModel mViewModel;
    private ProgressDialog progressDialog;

    public ConfirmFragment() {
        // Required empty public constructor
    }

    @Override
    public int getBindingVariable() {
        return BR.confirmViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_confirm;
    }

    @Override
    public ConfirmViewModel getViewModel() {
        GatewayViewModelFactory factory = new GatewayViewModelFactory(getGatewayRepository());
        mViewModel = ViewModelProviders.of(this, factory).get(ConfirmViewModel.class);
        return mViewModel;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel.getMerchant().setValue(((TospayActivity) getActivity()).getMerchant());
        mViewModel.getTransaction().setValue(((TospayActivity) getActivity()).getPaymentTransaction());
        mViewModel.setNavigator(this);

        PaymentRequest request = ConfirmFragmentArgs.fromBundle(getArguments()).getPayment();
        request.setPaymentToken(((TospayActivity) getActivity()).getPaymentToken());

        mViewModel.getPayment().setValue(request);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Making payment. Please wait...");
    }

    @Override
    public void onConfirm(View view) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Making payment. Please wait...");
        progressDialog.show();

        mViewModel.pay();
        mViewModel.getResourceLiveData().observe(this, resource -> {
            Log.e("TAG", "onConfirm: "+resource );
            if (resource != null) {
                switch (resource.status) {
                    case ERROR:
                        progressDialog.dismiss();
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                        break;

                    case LOADING:
                        progressDialog.dismiss();
                        mViewModel.setIsLoading(true);
                        mViewModel.setIsError(false);
                        break;

                    case SUCCESS:
                        progressDialog.dismiss();
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(false);
                        mListener.onPaymentSuccess();
                        break;
                }
            }
        });
    }
}
