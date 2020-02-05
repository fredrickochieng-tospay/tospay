package net.tospay.auth.ui.account.bank;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.databinding.FragmentLinkBankBinding;
import net.tospay.auth.model.Bank;
import net.tospay.auth.model.Country;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.BankRepository;
import net.tospay.auth.remote.repository.GatewayRepository;
import net.tospay.auth.remote.service.BankService;
import net.tospay.auth.remote.service.GatewayService;
import net.tospay.auth.ui.base.BaseFragment;
import net.tospay.auth.ui.dialog.country.CountryDialog;
import net.tospay.auth.viewmodelfactory.BankViewModelFactory;

public class LinkBankFragment extends BaseFragment<FragmentLinkBankBinding, BankViewModel>
        implements CountryDialog.CountrySelectedListener, BankDialog.OnBankSelectedListener, BankNavigator {

    private Country country = null;
    private Bank bank = null;
    private FragmentLinkBankBinding mBinding;
    private BankViewModel mViewModel;

    public LinkBankFragment() {
        // Required empty public constructor
    }

    @Override
    public int getBindingVariable() {
        return BR.bankViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_link_bank;
    }

    @Override
    public BankViewModel getViewModel() {
        BankService bankService = ServiceGenerator.createService(BankService.class, getContext());
        GatewayService gatewayService = ServiceGenerator.createService(GatewayService.class, getContext());

        BankRepository bankRepository = new BankRepository(getAppExecutors(), bankService);
        GatewayRepository gatewayRepository = new GatewayRepository(getAppExecutors(), gatewayService);

        BankViewModelFactory factory = new BankViewModelFactory(bankRepository, gatewayRepository);
        mViewModel = ViewModelProviders.of(this, factory).get(BankViewModel.class);
        return mViewModel;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = getViewDataBinding();
        mBinding.setBankViewModel(mViewModel);
        mViewModel.setNavigator(this);
    }

    @Override
    public void onCountrySelected(Country country) {
        this.country = country;
        mViewModel.getCountry().setValue(country);
    }

    @Override
    public void onSelectCountryClick(View view) {
        CountryDialog.newInstance(GatewayRepository.CountryType.MOBILE)
                .show(getChildFragmentManager(), CountryDialog.TAG);
    }

    @Override
    public void onSelectBankClick(View view) {
        if (country == null) {
            mBinding.selectCountryTextView.setError("Please select country");
            return;
        }

        BankDialog.newInstance(country.getIso()).show(getChildFragmentManager(), BankDialog.TAG);
    }

    @Override
    public void onDone(View view) {
        mBinding.selectCountryTextView.setError(null);
        mBinding.selectBankTextView.setError(null);
        mBinding.phoneEditText.setError(null);
        mBinding.phoneInputLayout.setErrorEnabled(false);
        mBinding.phoneInputLayout.setError(null);
        mBinding.accountNoInputLayout.setErrorEnabled(false);
        mBinding.accountNoInputLayout.setError(null);

        if (country == null) {
            mBinding.selectCountryTextView.setError("Country is required");
            return;
        }

        if (bank == null) {
            mBinding.selectBankTextView.setError("Network is required");
            return;
        }

        String accountNo = mBinding.accountNoEditText.getText().toString();
        if (TextUtils.isEmpty(accountNo)) {
            mBinding.accountNoInputLayout.setErrorEnabled(true);
            mBinding.accountNoInputLayout.setError("Account Number is required");
            return;
        }

        String phone = mBinding.phoneEditText.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            mBinding.phoneInputLayout.setErrorEnabled(true);
            mBinding.phoneInputLayout.setError("Phone is required");
            return;
        }

        mViewModel.link(country, bank, phone, accountNo);
        mViewModel.getLinkResourceLiveData().observe(this, resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        mViewModel.setIsLoading(true);
                        mViewModel.setIsError(false);
                        break;

                    case SUCCESS:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(false);
                        break;

                    case ERROR:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        break;

                    case RE_AUTHENTICATE:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        openActivityOnTokenExpire();
                        break;
                }
            }
        });
    }

    @Override
    public void onBankSelected(Bank bank) {
        this.bank = bank;
        mViewModel.getBank().setValue(bank);
    }
}
