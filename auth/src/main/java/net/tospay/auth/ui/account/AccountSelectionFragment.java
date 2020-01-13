package net.tospay.auth.ui.account;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
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
import net.tospay.auth.model.Wallet;
import net.tospay.auth.model.transfer.Account;
import net.tospay.auth.model.transfer.Amount;
import net.tospay.auth.model.transfer.Charge;
import net.tospay.auth.model.transfer.Delivery;
import net.tospay.auth.model.transfer.Order;
import net.tospay.auth.model.transfer.PartnerInfo;
import net.tospay.auth.model.transfer.Source;
import net.tospay.auth.model.transfer.Total;
import net.tospay.auth.model.transfer.Transfer;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.AccountRepository;
import net.tospay.auth.remote.repository.PaymentRepository;
import net.tospay.auth.remote.response.TospayException;
import net.tospay.auth.remote.service.AccountService;
import net.tospay.auth.remote.service.PaymentService;
import net.tospay.auth.ui.account.topup.TopupDialog;
import net.tospay.auth.ui.auth.AuthActivity;
import net.tospay.auth.ui.base.BaseFragment;
import net.tospay.auth.utils.Utils;
import net.tospay.auth.viewmodelfactory.AccountViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class AccountSelectionFragment extends BaseFragment<FragmentAccountSelectionBinding, AccountViewModel>
        implements OnAccountItemClickListener, PaymentListener, AccountNavigator {

    private static final String TAG = "AccountSelectionFragmen";

    private AccountViewModel mViewModel;
    private FragmentAccountSelectionBinding mBinding;
    private boolean isRotate = false;
    private Charge charge;
    private Transfer transfer;
    private Account account;
    private PartnerInfo partnerInfo;
    private String paymentId;
    private ProgressDialog progressDialog;

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
        AccountRepository accountRepository = new AccountRepository(getAppExecutors(),
                ServiceGenerator.createService(AccountService.class));

        PaymentRepository paymentRepository = new PaymentRepository(getAppExecutors(),
                ServiceGenerator.createService(PaymentService.class));

        AccountViewModelFactory factory =
                new AccountViewModelFactory(accountRepository, paymentRepository);

        mViewModel = ViewModelProviders.of(this, factory).get(AccountViewModel.class);
        return mViewModel;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = getViewDataBinding();
        mBinding.setAccountViewModel(mViewModel);
        mViewModel.setNavigator(this);

        progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);

        if (getArguments() != null) {
            transfer = AccountSelectionFragmentArgs.fromBundle(getArguments()).getTransfer();
            partnerInfo = transfer.getChargeInfo().getPartnerInfo();
            paymentId = AccountSelectionFragmentArgs.fromBundle(getArguments()).getPaymentId();
            mViewModel.getTransfer().setValue(transfer);
        }

        List<AccountType> accountTypes = new ArrayList<>();
        AccountAdapter adapter = new AccountAdapter(accountTypes, this);
        mBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mBinding.recyclerView.setAdapter(adapter);

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

        mBinding.btnBackImageView.setOnClickListener(view1 ->
                Navigation.findNavController(view).navigateUp());

        mBinding.btnPay.setOnClickListener(view14 -> {
            AccountType accountType = adapter.getSelectedAccountType();
            account = new Account();

            if (accountType != null) {
                if (accountType instanceof Wallet) {
                    Wallet wallet = (Wallet) adapter.getSelectedAccountType();
                    account.setType("wallet");
                    account.setId(wallet.getId());
                    account.setCurrency(wallet.getCurrency());

                } else {
                    String currency = "KES";
                    if (account.getCurrency() != null) {
                        currency = account.getCurrency();
                    }

                    account.setCurrency(currency);
                    account.setId(account.getId());
                    account.setType(Utils.getAccountType(accountType.getType()));
                }

                performChargeLookup();
            } else {
                Snackbar.make(mBinding.container, "Source of funds not selected", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAccounts() {
        mViewModel.fetchAccounts(true);
        mViewModel.getAccountsResourceLiveData().observe(this, this::handleResponse);
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
    public void onTopupClick(Wallet wallet) {
        TopupDialog.newInstance(wallet).show(getChildFragmentManager(), TopupDialog.TAG);
    }

    @Override
    public void onVerifyClick(AccountType accountType) {
        AccountSelectionFragmentDirections.ActionNavigationAccountSelectionToNavigationVerifyMobile
                action = AccountSelectionFragmentDirections
                .actionNavigationAccountSelectionToNavigationVerifyMobile((net.tospay.auth.model.Account) accountType);

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

    private void performChargeLookup() {
        Transfer chargeTransfer = transfer;

        Amount amount = transfer.getOrderInfo().getAmount();
        Order order = new Order(amount);
        Total total = new Total(amount);

        //sources
        List<Source> sources = new ArrayList<>();
        Source source = new Source();
        source.setAccount(account);
        source.setOrder(order);
        source.setTotal(total);
        sources.add(source);

        //delivery
        Delivery delivery = transfer.getDelivery().get(0);
        delivery.setAccount(partnerInfo.getAccount());
        delivery.getAccount().setCurrency(partnerInfo.getAmount().getCurrency());

        List<Delivery> deliveries = new ArrayList<>();
        delivery.setOrder(order);
        delivery.setTotal(total);
        deliveries.add(delivery);
        chargeTransfer.setDelivery(deliveries);

        chargeTransfer.setSource(sources);
        chargeTransfer.setMerchant(null);
        chargeTransfer.setChargeInfo(null);

        mViewModel.chargeLookup(chargeTransfer);
        mViewModel.getAmountResourceLiveData().observe(this, resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        progressDialog.setMessage("Fetching charges. Please wait...");
                        progressDialog.show();
                        break;

                    case SUCCESS:
                        progressDialog.dismiss();
                        charge = new Charge(resource.data);
                        break;

                    case ERROR:
                        Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        break;
                }
            }
        });
    }

    public void pay() {
        mViewModel.pay(paymentId, transfer);
        mViewModel.getPaymentResourceLiveData().observe(this, new Observer<Resource<String>>() {
            @Override
            public void onChanged(Resource<String> resource) {
                Log.e(TAG, "onChanged: " + resource);
            }
        });
    }
}
