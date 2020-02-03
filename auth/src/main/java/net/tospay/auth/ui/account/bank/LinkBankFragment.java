package net.tospay.auth.ui.account.bank;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.databinding.FragmentLinkBankBinding;
import net.tospay.auth.model.Country;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.BankRepository;
import net.tospay.auth.remote.repository.GatewayRepository;
import net.tospay.auth.remote.service.BankService;
import net.tospay.auth.ui.base.BaseFragment;
import net.tospay.auth.ui.dialog.country.CountryDialog;
import net.tospay.auth.viewmodelfactory.BankViewModelFactory;

public class LinkBankFragment extends BaseFragment<FragmentLinkBankBinding, BankViewModel>
        implements CountryDialog.CountrySelectedListener, BankNavigator {


    private Country country = null;
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
        BankService service = ServiceGenerator.createService(BankService.class, getContext());
        BankRepository repository = new BankRepository(getAppExecutors(), service);
        BankViewModelFactory factory = new BankViewModelFactory(repository);
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
}
