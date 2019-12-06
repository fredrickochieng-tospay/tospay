package net.tospay.auth.ui.summary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.GatewayRepository;
import net.tospay.auth.remote.response.PaymentValidationResponse;
import net.tospay.auth.remote.response.TospayException;
import net.tospay.auth.databinding.FragmentPaymentSummaryBinding;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.service.GatewayService;
import net.tospay.auth.ui.GatewayViewModelFactory;
import net.tospay.auth.ui.auth.AuthActivity;
import net.tospay.auth.ui.base.BaseFragment;
import net.tospay.auth.utils.NetworkUtils;

import static net.tospay.auth.utils.Constants.KEY_TOKEN;

public class PaymentSummaryFragment extends BaseFragment<FragmentPaymentSummaryBinding, SummaryViewModel>
        implements SummaryNavigator {

    private FragmentPaymentSummaryBinding mBinding;
    private String token;
    private NavController navController;
    private SummaryViewModel mViewModel;

    public PaymentSummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.token = getArguments().getString(KEY_TOKEN);
        }
    }

    @Override
    public void onDestroy() {
        mBinding.unbind();
        super.onDestroy();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        mBinding = getViewDataBinding();
        mViewModel = getViewModel();
        mViewModel.setNavigator(this);
        mBinding.setSummaryViewModel(mViewModel);

        validatePaymentToken();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                mListener.onPaymentFailed(new TospayException("Transaction canceled"));
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(callback);
    }

    private void showNetworkErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Connection Failed");
        builder.setMessage(getString(R.string.internet_error));
        builder.setPositiveButton("Retry", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            validatePaymentToken();
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            mListener.onPaymentFailed(new TospayException("Transaction canceled"));
        });
        builder.show();
    }

    private void validatePaymentToken() {
        if (NetworkUtils.isNetworkAvailable(getContext())) {
            mViewModel.validatePaymentToken(token);
            mViewModel.getResponseLiveData().observe(this, this::handleResponse);
        } else {
            showNetworkErrorDialog();
        }
    }

    private void handleResponse(Resource<PaymentValidationResponse> resource) {
        if (resource != null) {
            switch (resource.status) {
                case ERROR:
                    mViewModel.setIsLoading(false);
                    mViewModel.setIsError(true);
                    mViewModel.setErrorMessage(resource.message);
                    //mListener.onPaymentFailed(new TospayException(resource.message));

                    break;

                case LOADING:
                    mViewModel.setIsLoading(true);
                    mViewModel.setIsError(false);
                    break;

                case SUCCESS:
                    mViewModel.setIsLoading(false);
                    mViewModel.setIsError(false);
                    if (resource.data != null) {
                        mViewModel.merchant().setValue(resource.data.getMerchant());
                        mViewModel.transaction().setValue(resource.data.getPaymentTransaction());
                        mListener.onPaymentDetails(resource.data);
                    }
                    break;

                case RE_AUTHENTICATE:
                    startActivityForResult(new Intent(getContext(), AuthActivity.class), AuthActivity.REQUEST_CODE_LOGIN);
                    break;
            }
        }
    }

    @Override
    public int getBindingVariable() {
        return BR.summaryViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_payment_summary;
    }

    @Override
    public SummaryViewModel getViewModel() {
        GatewayRepository repository = new GatewayRepository(getAppExecutors(),
                ServiceGenerator.createService(GatewayService.class));
        GatewayViewModelFactory factory = new GatewayViewModelFactory(repository);
        mViewModel = ViewModelProviders.of(this, factory).get(SummaryViewModel.class);
        return mViewModel;
    }

    @Override
    public void onContinue(View view) {
        navController.navigate(R.id.navigation_account_selection);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AuthActivity.REQUEST_CODE_LOGIN) {
            if (resultCode == Activity.RESULT_OK) {
                reloadBearerToken();
                validatePaymentToken();
            }
        }
    }
}
