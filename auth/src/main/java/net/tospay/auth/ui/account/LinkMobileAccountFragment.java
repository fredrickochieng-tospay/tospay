package net.tospay.auth.ui.account;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.databinding.FragmentLinkMobileAccountBinding;
import net.tospay.auth.model.Account;
import net.tospay.auth.model.Country;
import net.tospay.auth.model.Network;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.GatewayRepository;
import net.tospay.auth.remote.service.GatewayService;
import net.tospay.auth.ui.GatewayViewModelFactory;
import net.tospay.auth.ui.account.verify.MobileMoneyNavigator;
import net.tospay.auth.ui.account.verify.MobileMoneyViewModel;
import net.tospay.auth.ui.base.BaseFragment;
import net.tospay.auth.ui.dialog.country.CountryDialog;
import net.tospay.auth.ui.dialog.network.NetworkDialog;
import net.tospay.auth.utils.NetworkUtils;

public class LinkMobileAccountFragment extends BaseFragment<FragmentLinkMobileAccountBinding, MobileMoneyViewModel>
        implements CountryDialog.CountrySelectedListener, NetworkDialog.NetworkSelectedListener, MobileMoneyNavigator {

    private ProgressDialog progressDialog;
    private Country country = null;
    private Network network = null;

    private FragmentLinkMobileAccountBinding mBinding;
    private MobileMoneyViewModel viewModel;


    public LinkMobileAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public int getBindingVariable() {
        return BR.mobileMoneyViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_link_mobile_account;
    }

    @Override
    public MobileMoneyViewModel getViewModel() {
        GatewayRepository repository = new GatewayRepository(getAppExecutors(),
                ServiceGenerator.createService(GatewayService.class));
        GatewayViewModelFactory factory = new GatewayViewModelFactory(repository);
        viewModel = ViewModelProviders.of(this, factory).get(MobileMoneyViewModel.class);
        return viewModel;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = getViewDataBinding();
        mBinding.setMobileMoneyViewModel(viewModel);
        viewModel.setNavigator(this);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Adding account. Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onCountrySelected(Country country) {
        this.country = country;
        viewModel.getCountry().setValue(country);
    }

    @Override
    public void onNetworkSelected(Network network) {
        this.network = network;
        viewModel.getNetwork().setValue(network);
    }

    @Override
    public void onSelectCountryClick(View view) {
        CountryDialog.newInstance(true)
                .show(getChildFragmentManager(), CountryDialog.TAG);
    }

    @Override
    public void onNetworkCountryClick(View view) {
        if (country == null) {
            mBinding.selectCountryTextView.setError("Please select country");
            return;
        }

        NetworkDialog.newInstance(country.getIso()).show(getChildFragmentManager(), NetworkDialog.TAG);
    }

    @Override
    public void onConfirmClick(View view) {
        mBinding.selectCountryTextView.setError(null);
        mBinding.selectNetworkTextView.setError(null);

        if (country == null) {
            mBinding.selectCountryTextView.setError("Country is required");
            return;
        }

        if (network == null) {
            mBinding.selectNetworkTextView.setError("Network is required");
            return;
        }

        String phone = mBinding.phoneEditText.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(getContext(), "Phone is required", Toast.LENGTH_SHORT).show();
            return;
        }

        String alias = mBinding.nameEditText.getText().toString();

        Account account = new Account();
        account.setAlias(alias);
        account.setNetwork(network.getOperator());
        account.setTrunc(phone);

        if (NetworkUtils.isNetworkAvailable(view.getContext())) {
            viewModel.link(phone, mBinding.nameEditText.getText().toString());
            viewModel.getMobileResourceLiveData().observe(this, resource -> {
                if (resource != null) {
                    switch (resource.status) {
                        case SUCCESS:
                            progressDialog.dismiss();

                            if (resource.data != null) {
                                account.setId(resource.data.getId());
                            }

                            AccountSelectionFragmentDirections
                                    .ActionNavigationAccountSelectionToNavigationVerifyMobile action =
                                    AccountSelectionFragmentDirections
                                            .actionNavigationAccountSelectionToNavigationVerifyMobile(account);

                            NavHostFragment.findNavController(this).navigate(action);

                            break;

                        case ERROR:
                            progressDialog.dismiss();
                            viewModel.setIsError(true);
                            viewModel.setErrorMessage(resource.message);
                            break;

                        case LOADING:
                            progressDialog.show();
                            viewModel.setIsError(false);
                            break;
                    }
                }
            });
        } else {
            Snackbar.make(mBinding.container, getString(R.string.internet_error), Snackbar.LENGTH_LONG).show();
        }
    }
}
