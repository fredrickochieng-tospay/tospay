package net.tospay.auth.ui.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.databinding.FragmentAccountSelectionBinding;
import net.tospay.auth.event.NotificationEvent;
import net.tospay.auth.event.Payload;
import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.interfaces.PaymentListener;
import net.tospay.auth.model.Wallet;
import net.tospay.auth.model.transfer.Account;
import net.tospay.auth.model.transfer.Amount;
import net.tospay.auth.model.transfer.Store;
import net.tospay.auth.model.transfer.Transfer;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.exception.TospayException;
import net.tospay.auth.remote.repository.AccountRepository;
import net.tospay.auth.remote.repository.PaymentRepository;
import net.tospay.auth.remote.response.TransferResponse;
import net.tospay.auth.remote.service.AccountService;
import net.tospay.auth.remote.service.PaymentService;
import net.tospay.auth.ui.account.topup.TopupAccountSelectionDialog;
import net.tospay.auth.ui.account.topup.TopupAmountDialog;
import net.tospay.auth.ui.auth.AuthActivity;
import net.tospay.auth.ui.auth.pin.PinActivity;
import net.tospay.auth.ui.base.BaseFragment;
import net.tospay.auth.ui.dialog.TransferDialog;
import net.tospay.auth.utils.Constants;
import net.tospay.auth.utils.Utils;
import net.tospay.auth.viewmodelfactory.AccountViewModelFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class AccountSelectionFragment extends BaseFragment<FragmentAccountSelectionBinding, AccountViewModel>
        implements OnAccountItemClickListener, PaymentListener, AccountNavigator,
        TopupAccountSelectionDialog.OnAccountListener, TopupAmountDialog.OnTopupListener {

    private AccountViewModel mViewModel;
    private FragmentAccountSelectionBinding mBinding;
    private Amount charge;
    private Transfer transfer;
    private Account account;
    private String paymentId;
    private List<Store> sources;
    private double withdrawalAmount = 0;
    private String status = NotificationEvent.STATUS_FAILED, title = "", message = "Error Unknown";
    private Wallet topupWallet;
    private TransferResponse transferResponse;

    private final Handler handler = new Handler();
    private Runnable runnable;
    private boolean shouldRun = true, isSocketNotified = false;
    private int count = 0;

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
                ServiceGenerator.createService(AccountService.class, getContext()));

        PaymentRepository paymentRepository = new PaymentRepository(getAppExecutors(),
                ServiceGenerator.createService(PaymentService.class, getContext()));

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
        mBinding.setName(getSharedPrefManager().getActiveUser().getName());
        mViewModel.setNavigator(this);

        if (getArguments() != null) {
            transfer = AccountSelectionFragmentArgs.fromBundle(getArguments()).getTransfer();
            paymentId = AccountSelectionFragmentArgs.fromBundle(getArguments()).getPaymentId();
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

        mBinding.layoutLinkBank.setOnClickListener(view15 -> {
            NavHostFragment.findNavController(this)
                    .navigate(AccountSelectionFragmentDirections
                            .actionNavigationAccountSelectionToNavigationLinkBank());
        });

        mBinding.btnBackImageView.setOnClickListener(view1 ->
                Navigation.findNavController(view).navigateUp());

        mBinding.btnPay.setOnClickListener(view14 -> executePayment());
        mBinding.swipeRefreshLayout.setOnRefreshListener(this::fetchAccounts);

        fetchAccounts();
    }

    private void fetchAccounts() {
        mViewModel.fetchAccounts(true);
        mViewModel.getAccountsResourceLiveData().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case ERROR:
                        mBinding.swipeRefreshLayout.setRefreshing(false);
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        break;

                    case LOADING:
                        mViewModel.setIsLoading(true);
                        mViewModel.setIsError(false);
                        break;

                    case SUCCESS:
                        mBinding.swipeRefreshLayout.setRefreshing(false);
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(false);
                        if (resource.data != null && resource.data.size() > 0) {
                            mBinding.setResource(resource);
                        } else {
                            mViewModel.setIsEmpty(true);
                        }
                        break;

                    case RE_AUTHENTICATE:
                        mBinding.swipeRefreshLayout.setRefreshing(false);
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
        this.topupWallet = wallet;
        TopupAccountSelectionDialog.newInstance().show(getChildFragmentManager(), TopupAccountSelectionDialog.TAG);
    }

    @Override
    public void onAccountSelectedListener(AccountType accountType) {
        this.account = new Account();
        mViewModel.getSource().setValue(null);

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
        if (requestCode == AuthActivity.REQUEST_CODE_LOGIN || requestCode == PinActivity.REQUEST_PIN) {
            if (resultCode == Activity.RESULT_OK) {
                reloadBearerToken();
                fetchAccounts();
            }
        }
    }

    @Override
    public void onRefresh() {
        fetchAccounts();
    }

    private void performChargeLookup() {
        Transfer chargeTransfer = new Transfer();
        chargeTransfer.setType(Transfer.PAYMENT);
        chargeTransfer.setOrderInfo(transfer.getOrderInfo());
        Amount amount = transfer.getOrderInfo().getAmount();

        //sources
        sources = new ArrayList<>();
        Store source = new Store();
        source.setAccount(account);
        source.setOrder(amount);
        source.setTotal(amount);
        sources.add(source);

        //deliveries
        List<Store> deliveries = new ArrayList<>();
        Store delivery = transfer.getDelivery().get(0);
        delivery.setOrder(amount);
        delivery.setTotal(amount);
        deliveries.add(delivery);
        chargeTransfer.setDelivery(deliveries);
        chargeTransfer.setSource(sources);

        mViewModel.chargeLookup(chargeTransfer, Transfer.PAYMENT);
        mViewModel.getAmountResourceLiveData().observe(this, resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
                        charge = resource.data;
                        mViewModel.getCharge().setValue(charge);

                        withdrawalAmount = Double.valueOf(chargeTransfer.getOrderInfo().getAmount().getAmount());
                        withdrawalAmount += Double.parseDouble(charge.getAmount());

                        source.setCharge(charge);
                        source.setTotal(new Amount(String.valueOf(withdrawalAmount), delivery.getAccount().getCurrency()));
                        sources.set(0, source);
                        mViewModel.getSource().setValue(source);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        break;

                    case RE_AUTHENTICATE:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        openActivityOnTokenExpire();
                        break;
                }
            }
        });
    }

    private void executePayment() {
        Transfer payload = new Transfer();
        payload.setSource(sources);

        isSocketNotified = false;
        shouldRun = true;
        count = 0;

        mViewModel.pay(paymentId, payload);
        mViewModel.getPaymentResourceLiveData().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
                        mViewModel.setIsError(false);
                        transferResponse = resource.data;
                        assert transferResponse != null;
                        if (transferResponse.getHtml() != null) {
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            CardPaymentDialog.newInstance(transferResponse.getHtml())
                                    .show(getChildFragmentManager(), CardPaymentDialog.TAG);
                        } else {
                            new Handler().postDelayed(this::triggerTimer, 5000);
                        }
                        break;

                    case RE_AUTHENTICATE:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        openActivityOnTokenExpire();
                        break;
                }
            }
        });
    }

    private void triggerTimer() {
        if (!isSocketNotified) {
            runnable = new Runnable() {

                @Override
                public void run() {
                    checkTransactionStatus();

                    if (shouldRun) {
                        handler.postDelayed(this, Constants.RETRY_DELAY);
                    } else {
                        handler.removeCallbacks(runnable);
                    }
                }
            };

            handler.post(runnable);
        }
    }

    private void checkTransactionStatus() {
        if (shouldRun) {
            if (count <= Constants.RETRY_MAX) {
                mViewModel.status(transferResponse);
                mViewModel.getTransferStatusResourceLiveData().observe(getViewLifecycleOwner(), resource -> {
                    if (resource != null) {
                        switch (resource.status) {
                            case ERROR:
                                mViewModel.setIsLoading(false);
                                mViewModel.setIsError(true);
                                mViewModel.setErrorMessage(resource.message);
                                handler.removeCallbacks(runnable);
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                break;

                            case SUCCESS:
                                count++;
                                if (resource.data != null) {
                                    status = resource.data.getStatus();
                                    if (status.equals(NotificationEvent.STATUS_SUCCESS)) {
                                        status = NotificationEvent.STATUS_SUCCESS;
                                        message = "Congratulation! Your Topup was successful";
                                        completeProcessing();
                                    } else {
                                        status = NotificationEvent.STATUS_FAILED;
                                        message = "Failed to complete transaction. Please try again";
                                    }
                                } else {
                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    handler.removeCallbacks(runnable);
                                    mViewModel.setIsLoading(false);
                                    mViewModel.setIsError(true);
                                    mViewModel.setErrorMessage("Unable to process this transaction");
                                }
                                break;

                            case RE_AUTHENTICATE:
                                shouldRun = false;
                                isSocketNotified = false;
                                mViewModel.setIsLoading(false);
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                startActivityForResult(new Intent(getContext(), PinActivity.class), PinActivity.REQUEST_PIN);
                                break;
                        }
                    }
                });
            } else {
                completeProcessing();
            }
        } else {
            completeProcessing();
        }
    }

    private void completeProcessing() {
        handler.removeCallbacksAndMessages(null);
        if (status.equals(NotificationEvent.STATUS_SUCCESS)) {
            mListener.onPaymentSuccess(transferResponse, title, message);
        } else {
            mListener.onPaymentFailed(new TospayException(message));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPaymentNotification(NotificationEvent event) {
        if (event != null) {
            Payload payload = event.getPayload();
            switch (payload.getTopic()) {
                case NotificationEvent.TOPUP:
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    if (!payload.getStatus().equals(NotificationEvent.STATUS_FAILED)) {
                        fetchAccounts();
                    } else {
                        TransferDialog.newInstance(event).show(getChildFragmentManager(), TransferDialog.TAG);
                    }
                    break;

                case NotificationEvent.PAYMENT:
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    title = event.getNotification().getTitle();
                    message = event.getNotification().getBody();

                    shouldRun = false;
                    isSocketNotified = true;
                    handler.removeCallbacksAndMessages(null);

                    mViewModel.setIsLoading(false);
                    if (payload.getStatus().equals(NotificationEvent.STATUS_FAILED)) {
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(payload.getReason());
                    } else {
                        mListener.onPaymentSuccess(transferResponse, title, message);
                    }
                    break;
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
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onAccount(net.tospay.auth.model.Account account) {
        if (topupWallet == null) {
            return;
        }

        TopupAmountDialog.newInstance(topupWallet, account)
                .show(getChildFragmentManager(), TopupAmountDialog.TAG);
    }

    @Override
    public void onVerifyAccount(net.tospay.auth.model.Account account) {
        AccountSelectionFragmentDirections.ActionNavigationAccountSelectionToNavigationVerifyMobile
                action = AccountSelectionFragmentDirections
                .actionNavigationAccountSelectionToNavigationVerifyMobile(account);

        NavHostFragment.findNavController(this).navigate(action);
    }

    @Override
    public void onTopupSuccess(TransferResponse transferResponse) {
        MpesaLoadingDialog.newInstance(transferResponse)
                .show(getChildFragmentManager(), MpesaLoadingDialog.TAG);
    }
}
