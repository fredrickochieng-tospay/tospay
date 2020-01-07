package net.tospay.auth.ui.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.google.android.material.snackbar.Snackbar;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.anim.ViewAnimation;
import net.tospay.auth.databinding.FragmentAccountSelectionBinding;
import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.interfaces.PaymentListener;
import net.tospay.auth.model.Account;
import net.tospay.auth.model.transfer.Transfer;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.AccountRepository;
import net.tospay.auth.remote.response.TospayException;
import net.tospay.auth.remote.service.AccountService;
import net.tospay.auth.ui.auth.AuthActivity;
import net.tospay.auth.ui.base.BaseFragment;
import net.tospay.auth.viewmodelfactory.AccountViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import static net.tospay.auth.utils.Constants.KEY_SHOW_WALLET;

public class AccountSelectionFragment extends BaseFragment<FragmentAccountSelectionBinding, AccountViewModel>
        implements OnAccountItemClickListener, PaymentListener, AccountNavigator {

    private AccountViewModel mViewModel;
    private FragmentAccountSelectionBinding mBinding;
    private boolean isRotate = false;
    private Transfer transfer;

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
        AccountRepository repository = new AccountRepository(getAppExecutors(),
                ServiceGenerator.createService(AccountService.class));
        AccountViewModelFactory factory = new AccountViewModelFactory(repository);
        mViewModel = ViewModelProviders.of(this, factory).get(AccountViewModel.class);
        return mViewModel;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = getViewDataBinding();
        mBinding.setAccountViewModel(mViewModel);

        if (getArguments() != null) {
            transfer = AccountSelectionFragmentArgs.fromBundle(getArguments()).getTransfer();
            mViewModel.getTransfer().setValue(transfer);
        }

        List<AccountType> accountTypes = new ArrayList<>();
        AccountAdapter adapter = new AccountAdapter(accountTypes, this);
        mBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mBinding.recyclerView.setAdapter(adapter);

        mViewModel.setNavigator(this);
        fetchAccounts();

        ViewAnimation.init(mBinding.fabLinkCard);
        ViewAnimation.init(mBinding.fabLinkMobile);

        mBinding.fabAdd.setOnClickListener(view1 -> {
            isRotate = ViewAnimation.rotateFab(view1, !isRotate);
            if (isRotate) {
                ViewAnimation.showIn(mBinding.fabLinkCard);
                ViewAnimation.showIn(mBinding.fabLinkMobile);
            } else {
                ViewAnimation.showOut(mBinding.fabLinkCard);
                ViewAnimation.showOut(mBinding.fabLinkMobile);
            }
        });

        mBinding.fabLinkCard.setOnClickListener(view12 ->
                NavHostFragment.findNavController(this)
                        .navigate(AccountSelectionFragmentDirections
                                .actionNavigationAccountSelectionToNavigationLinkCardAccount())
        );

        mBinding.fabLinkMobile.setOnClickListener(view13 ->
                NavHostFragment.findNavController(this)
                        .navigate(AccountSelectionFragmentDirections
                                .actionNavigationAccountSelectionToNavigationLinkMobileAccount())
        );

        mBinding.btnBackImageView.setOnClickListener(view1 -> Navigation.findNavController(view)
                .navigateUp());

        mBinding.btnPay.setOnClickListener(view14 -> {
            if (adapter.getSelectedAccountType() != null) {

            } else {
                Snackbar.make(mBinding.container, "Source of funds not selected", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAccounts() {
        mViewModel.fetchAccounts(true);
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
            }
        }
    }

    @Override
    public void onVerifyClick(View view, AccountType accountType) {
        AccountSelectionFragmentDirections.ActionNavigationAccountSelectionToNavigationVerifyMobile
                action = AccountSelectionFragmentDirections
                .actionNavigationAccountSelectionToNavigationVerifyMobile((Account) accountType);

        NavHostFragment.findNavController(this).navigate(action);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AuthActivity.REQUEST_CODE_LOGIN) {
            if (resultCode == Activity.RESULT_OK) {
                reloadBearerToken();
                fetchAccounts();
            } else {
                mListener.onPaymentFailed(new TospayException("Invalid credentials"));
            }
        }
    }

    @Override
    public void onRefresh() {
        fetchAccounts();
    }
}
