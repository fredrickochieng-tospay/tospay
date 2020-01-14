package net.tospay.auth.ui.account;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import net.tospay.auth.databinding.FragmentAccountSelectionBinding;
import net.tospay.auth.event.NotificationEvent;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class AccountSelectionFragment extends BaseFragment<FragmentAccountSelectionBinding, AccountViewModel>
        implements OnAccountItemClickListener, PaymentListener, AccountNavigator {

    private AccountViewModel mViewModel;
    private FragmentAccountSelectionBinding mBinding;
    private Charge charge;
    private Transfer transfer, payload;
    private Account account;
    private PartnerInfo partnerInfo;
    private String paymentId;
    private NumberFormat numberFormat;
    private String currency;
    private double withdrawalAmount = 0;

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

        numberFormat = NumberFormat.getCurrencyInstance();
        numberFormat.setMaximumFractionDigits(0);

        if (getArguments() != null) {
            transfer = AccountSelectionFragmentArgs.fromBundle(getArguments()).getTransfer();
            paymentId = AccountSelectionFragmentArgs.fromBundle(getArguments()).getPaymentId();

            if (transfer.getChargeInfo() != null) {
                partnerInfo = transfer.getChargeInfo().getPartnerInfo();
                currency = partnerInfo.getAmount().getCurrency();
                numberFormat.setCurrency(Currency.getInstance(currency));
            }

            mViewModel.getTransfer().setValue(transfer);
        }

        List<AccountType> accountTypes = new ArrayList<>();
        AccountAdapter adapter = new AccountAdapter(accountTypes, this);
        mBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mBinding.recyclerView.setAdapter(adapter);

        mBinding.layoutLinkCard.setOnClickListener(view12 ->
                NavHostFragment.findNavController(this)
                        .navigate(AccountSelectionFragmentDirections
                                .actionNavigationAccountSelectionToNavigationLinkCardAccount())
        );

        mBinding.layoutLinkMobile.setOnClickListener(view13 ->
                NavHostFragment.findNavController(this)
                        .navigate(AccountSelectionFragmentDirections
                                .actionNavigationAccountSelectionToNavigationLinkMobileAccount())
        );

        mBinding.btnBackImageView.setOnClickListener(view1 ->
                Navigation.findNavController(view).navigateUp());

        mBinding.btnPay.setOnClickListener(view14 -> {
            executePayment();
        });

        fetchAccounts();
    }

    private void fetchAccounts() {
        mViewModel.fetchAccounts(true);
        mViewModel.getAccountsResourceLiveData().observe(this, resource -> {
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
                        mViewModel.setLoadingTitle("Fetching sources of funds...");
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
        });
    }

    @Override
    public void onTopupClick(Wallet wallet) {
        TopupDialog.newInstance(wallet).show(getChildFragmentManager(), TopupDialog.TAG);
    }

    @Override
    public void onAccountSelectedListener(AccountType accountType) {
        this.account = new Account();
        if (accountType instanceof Wallet) {
            Wallet wallet = (Wallet) accountType;
            account.setType("wallet");
            account.setId(wallet.getId());
            account.setCurrency(wallet.getCurrency());
        } else {
            net.tospay.auth.model.Account acc = (net.tospay.auth.model.Account) accountType;

            String currency = "KES";
            if (acc.getCurrency() != null) {
                currency = acc.getCurrency();
            }

            account.setCurrency(currency);
            account.setId(acc.getId());
            account.setType(Utils.getAccountType(accountType.getType()));
        }

        performChargeLookup();
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

        payload = chargeTransfer;

        mViewModel.chargeLookup(chargeTransfer, Transfer.PAYMENT);
        mViewModel.getAmountResourceLiveData().observe(this, resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        mViewModel.setLoadingTitle("Fetching transaction charges...");
                        mViewModel.setIsLoading(true);
                        mViewModel.setIsError(false);
                        break;

                    case ERROR:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        break;

                    case SUCCESS:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(false);
                        charge = new Charge(resource.data);
                        mViewModel.getCharge().setValue(charge);

                        withdrawalAmount = Double.valueOf(transfer.getOrderInfo().getAmount().getAmount());
                        withdrawalAmount += Double.parseDouble(charge.getAmount().getAmount());

                        source.setTotal(new Total(new Amount(String.valueOf(withdrawalAmount), currency)));
                        mBinding.totalTextView.setText(numberFormat.format(withdrawalAmount));
                        mBinding.chargeTextView.setText(numberFormat.format(
                                Double.parseDouble(charge.getAmount().getAmount())));

                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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

    private void executePayment() {
        mViewModel.pay(paymentId, payload);
        mViewModel.getPaymentResourceLiveData().observe(this, resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        mViewModel.setLoadingTitle("Processing transactions...");
                        mViewModel.setIsLoading(true);
                        mViewModel.setIsError(false);
                        break;

                    case ERROR:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        break;

                    case SUCCESS:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(false);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotification(NotificationEvent notification) {
        if (notification != null) {
            if (!notification.getData().getStatus().equals("FAILED")) {
                fetchAccounts();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
