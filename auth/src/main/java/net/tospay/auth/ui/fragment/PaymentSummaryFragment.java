package net.tospay.auth.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import net.tospay.auth.R;
import net.tospay.auth.TospayGateway;
import net.tospay.auth.api.listeners.OnPaymentValidationListener;
import net.tospay.auth.api.response.PaymentValidationResponse;
import net.tospay.auth.api.response.TospayException;
import net.tospay.auth.databinding.FragmentPaymentSummaryBinding;
import net.tospay.auth.interfaces.PaymentListener;
import net.tospay.auth.viewmodel.PaymentViewModel;

import static net.tospay.auth.utils.Constants.KEY_TOKEN;

public class PaymentSummaryFragment extends Fragment implements OnPaymentValidationListener {

    private FragmentPaymentSummaryBinding mBinding;
    private PaymentViewModel paymentViewModel;
    private PaymentListener mListener;

    private String token;
    private NavController navController;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment_summary, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        paymentViewModel = ViewModelProviders.of(this)
                .get(PaymentViewModel.class);

        mBinding.setViewmodel(paymentViewModel);
        mBinding.setLifecycleOwner(this);

        TospayGateway.getInstance(view.getContext())
                .validatePayment(token, this);

        mBinding.btnLogin.setOnClickListener(view1 -> navController.navigate(R.id.navigation_login));

        mBinding.btnSignUp.setOnClickListener(view1 -> navController.navigate(R.id.navigation_register));

        mBinding.goBackLayout.setOnClickListener(view1 ->
                mListener.onPaymentFailed(new TospayException("Canceled")));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof PaymentListener) {
            mListener = (PaymentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PaymentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        mBinding.unbind();
        super.onDestroy();
    }

    @Override
    public void onValidationSuccess(PaymentValidationResponse response) {
        paymentViewModel.isLoading().setValue(false);
        paymentViewModel.merchant().setValue(response.getMerchant());
        paymentViewModel.transaction().setValue(response.getTransaction());

        mListener.onPaymentDetails(response);
    }

    @Override
    public void onError(TospayException error) {
        mListener.onPaymentFailed(error);
    }
}
