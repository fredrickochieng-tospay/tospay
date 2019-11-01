package net.tospay.auth.ui.account;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;

import net.tospay.auth.R;
import net.tospay.auth.databinding.FragmentAccountSelectionBinding;
import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.interfaces.OnAccountItemClickListener;
import net.tospay.auth.interfaces.PaymentListener;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.ui.GatewayViewModelFactory;
import net.tospay.auth.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class AccountSelectionFragment extends BaseFragment<FragmentAccountSelectionBinding, AccountViewModel>
        implements OnAccountItemClickListener, PaymentListener {

    private AccountViewModel mViewModel;
    private FragmentAccountSelectionBinding mBinding;

    public AccountSelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public int getBindingVariable() {
        return BR.accountViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_account_selection;
    }

    @Override
    public AccountViewModel getViewModel() {
        GatewayViewModelFactory factory = new GatewayViewModelFactory(getGatewayRepository());
        mViewModel = ViewModelProviders.of(this, factory).get(AccountViewModel.class);
        return mViewModel;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = getViewDataBinding();
        mBinding.setAccountViewModel(mViewModel);

        List<AccountType> accountTypes = new ArrayList<>();
        AccountAdapter adapter = new AccountAdapter(accountTypes, this);
        mBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mBinding.recyclerView.setAdapter(adapter);

        mViewModel.fetchAccounts();
        mViewModel.getResourceLiveData().observe(this, this::handleResponse);
    }

    private void handleResponse(Resource<List<AccountType>> resource) {
        if (resource != null) {
            switch (resource.status) {
                case ERROR:
                    mViewModel.setIsLoading(false);
                    mViewModel.setIsError(true);
                    mViewModel.setErrorMessage(resource.message);
                    break;

                case LOADING:
                    mViewModel.setIsLoading(true);
                    mViewModel.setIsError(false);
                    break;

                case SUCCESS:
                    mViewModel.setIsLoading(false);
                    mViewModel.setIsError(false);
                    if (resource.data != null && resource.data.size() > 0) {
                        mBinding.setResource(resource);
                    } else {
                        mViewModel.setIsEmpty(true);
                    }

                    break;

                case RE_AUTHENTICATE:
                    mViewModel.setIsLoading(false);
                    mViewModel.setIsError(true);
                    mViewModel.setErrorMessage(resource.message);
                    openActivityOnTokenExpire();
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void onAccountType(AccountType accountType) {
        if (mListener != null) {
            mListener.onAccountSelected(accountType);
        }
    }

    @Override
    public void onVerifyClick(View view, AccountType accountType) {

    }

    @Override
    public void onAddAccount(int accountType) {
        switch (accountType) {
            case AccountType.MOBILE:
                NavHostFragment.findNavController(this)
                        .navigate(R.id.navigation_link_mobile_account);
                break;

            case AccountType.CARD:
                NavHostFragment.findNavController(this)
                        .navigate(R.id.navigation_link_card_account);
                break;
        }
    }
}
