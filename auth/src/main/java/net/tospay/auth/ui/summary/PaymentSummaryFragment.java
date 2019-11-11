package net.tospay.auth.ui.summary;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.api.response.PaymentValidationResponse;
import net.tospay.auth.api.response.TospayException;
import net.tospay.auth.databinding.FragmentPaymentSummaryBinding;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.ui.GatewayViewModelFactory;
import net.tospay.auth.ui.base.BaseFragment;

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
        mBinding = getViewDataBinding();
        mViewModel = getViewModel();
        mViewModel.setNavigator(this);

        mViewModel.getIsLoggedIn().set(!getSharedPrefManager().isTokenExpiredOrAlmost());

        mBinding.setSummaryViewModel(mViewModel);
        mViewModel.validate(token);
        mViewModel.getResponseLiveData().observe(this, this::handleResponse);

        navController = Navigation.findNavController(view);
    }

    private void handleResponse(Resource<PaymentValidationResponse> resource) {
        if (resource != null) {
            switch (resource.status) {
                case ERROR:
                    mViewModel.setIsLoading(false);
                    mViewModel.setIsError(true);
                    mViewModel.setErrorMessage(resource.message);
                    mListener.onPaymentFailed(new TospayException(resource.message));
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
        GatewayViewModelFactory factory = new GatewayViewModelFactory(getGatewayRepository());
        mViewModel = ViewModelProviders.of(this, factory).get(SummaryViewModel.class);
        return mViewModel;
    }

    @Override
    public void onContinue(View view) {
        navController.navigate(R.id.navigation_account_selection);
    }
}
