package net.tospay.auth.ui.account.topup;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.tospay.auth.R;
import net.tospay.auth.anim.ViewAnimation;
import net.tospay.auth.databinding.DialogTopupBinding;
import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.model.Wallet;
import net.tospay.auth.model.transfer.Account;
import net.tospay.auth.model.transfer.Amount;
import net.tospay.auth.model.transfer.Charge;
import net.tospay.auth.model.transfer.Delivery;
import net.tospay.auth.model.transfer.Order;
import net.tospay.auth.model.transfer.OrderInfo;
import net.tospay.auth.model.transfer.Source;
import net.tospay.auth.model.transfer.Total;
import net.tospay.auth.model.transfer.Transfer;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.AccountRepository;
import net.tospay.auth.remote.repository.PaymentRepository;
import net.tospay.auth.remote.service.AccountService;
import net.tospay.auth.remote.service.PaymentService;
import net.tospay.auth.remote.util.AppExecutors;
import net.tospay.auth.ui.account.AccountAdapter;
import net.tospay.auth.ui.account.AccountViewModel;
import net.tospay.auth.ui.account.OnAccountItemClickListener;
import net.tospay.auth.utils.SharedPrefManager;
import net.tospay.auth.utils.Utils;
import net.tospay.auth.viewmodelfactory.AccountViewModelFactory;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

public class TopupDialog extends BottomSheetDialogFragment implements OnAccountItemClickListener {

    public static final String TAG = "TopupDialog";
    private static final String KEY_WALLET = "wallet";

    private AccountViewModel mViewModel;
    private DialogTopupBinding mBinding;
    private SharedPrefManager mSharedPrefManager;
    private Transfer transfer;
    private Wallet wallet;
    private Charge charge;
    private net.tospay.auth.model.Account selectedAccount;
    private double withdrawalAmount = 0;
    private NumberFormat numberFormat;
    private String currency;

    //source
    private List<Source> sources;
    private Source source;

    public TopupDialog() {
        // Required empty public constructor
    }

    public static TopupDialog newInstance(Wallet wallet) {
        TopupDialog fragment = new TopupDialog();
        Bundle args = new Bundle();
        args.putParcelable(KEY_WALLET, wallet);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);
        mSharedPrefManager = SharedPrefManager.getInstance(getContext());

        transfer = new Transfer();
        transfer.setType(Transfer.TOPUP);

        if (getArguments() != null) {
            wallet = getArguments().getParcelable(KEY_WALLET);
        }

        numberFormat = NumberFormat.getCurrencyInstance();
        numberFormat.setMaximumFractionDigits(0);
        numberFormat.setCurrency(Currency.getInstance(wallet.getCurrency()));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_topup, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppExecutors mAppExecutors = new AppExecutors();

        AccountRepository accountRepository = new AccountRepository(mAppExecutors,
                ServiceGenerator.createService(AccountService.class));

        PaymentRepository paymentRepository = new PaymentRepository(mAppExecutors,
                ServiceGenerator.createService(PaymentService.class));

        AccountViewModelFactory factory =
                new AccountViewModelFactory(accountRepository, paymentRepository);

        mViewModel = ViewModelProviders.of(this, factory).get(AccountViewModel.class);
        mBinding.setAccountViewModel(mViewModel);

        String bearerToken = "Bearer " + mSharedPrefManager.getAccessToken();
        mViewModel.setBearerToken(bearerToken);

        List<AccountType> accountTypes = new ArrayList<>();
        AccountAdapter adapter = new AccountAdapter(accountTypes, this);
        mBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mBinding.recyclerView.setAdapter(adapter);

        fetchAccounts();

        mBinding.amountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                charge = null;
            }
        });

        mBinding.btnCancel.setOnClickListener(view1 -> dismiss());
        mBinding.btnConfirm.setOnClickListener(view12 -> {
            if (charge == null) {
                mViewModel.setIsError(true);
                mViewModel.setErrorMessage("Unable to process request. Please make sure you have entered amount and selected an account");
                return;
            }

            topupWallet();
        });
    }

    private void topupWallet() {
        mBinding.amountInputLayout.setError(null);
        mBinding.amountInputLayout.setEnabled(false);

        if (TextUtils.isEmpty(mBinding.amountEditText.getText())) {
            mBinding.amountInputLayout.setError("Amount is required");
            mBinding.amountInputLayout.setEnabled(true);
            return;
        }

        if (charge == null) {
            if (selectedAccount == null) {
                mViewModel.setIsError(true);
                mViewModel.setErrorMessage("Please select source of funds");
                return;
            }

            performChargeLookup();
        }

        source.setCharge(charge);
        sources.set(sources.indexOf(source), source);

        mViewModel.topup(transfer);
        mViewModel.getTransferResourceLiveData().observe(this, resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        mViewModel.setIsLoading(true);
                        mViewModel.setIsError(false);
                        break;

                    case SUCCESS:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(false);
                        ViewAnimation.circularReveal(mBinding.messageLayout);
                        new Handler().postDelayed(this::dismiss, 1000);
                        break;

                    case ERROR:
                        mViewModel.setIsLoading(false);
                        mViewModel.setErrorMessage(resource.message);
                        mViewModel.setIsError(true);
                        break;
                }
            }
        });
    }

    private void fetchAccounts() {
        mViewModel.fetchAccounts(false);
        mViewModel.getAccountsResourceLiveData().observe(this, this::handleResponse);
    }

    private void handleResponse(Resource<List<AccountType>> resource) {
        if (resource != null) {
            switch (resource.status) {
                case LOADING:
                    mViewModel.setIsLoading(true);
                    mViewModel.setIsError(false);
                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    mViewModel.setLoadingTitle("Loading accounts...");
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
                    if (resource.data != null && resource.data.size() > 0) {
                        mBinding.setResource(resource);
                    } else {
                        mViewModel.setIsEmpty(true);
                    }
                    break;
            }
        }
    }

    @Override
    public void onAccountSelectedListener(AccountType accountType) {
        selectedAccount = (net.tospay.auth.model.Account) accountType;
        if (TextUtils.isEmpty(mBinding.amountEditText.getText())) {
            return;
        }

        performChargeLookup();
    }

    private void performChargeLookup() {
        charge = null;
        sources = new ArrayList<>();
        source = new Source();

        currency = "KES";
        if (selectedAccount.getCurrency() != null) {
            currency = selectedAccount.getCurrency();
        }

        Account account = new Account();
        account.setCurrency(currency);
        account.setId(selectedAccount.getId());
        account.setType(Utils.getAccountType(selectedAccount.getType()));
        source.setAccount(account);

        withdrawalAmount = Double.parseDouble(mBinding.amountEditText.getText().toString());

        Amount amount = new Amount(String.valueOf(withdrawalAmount), currency);
        source.setOrder(new Order(amount));
        source.setTotal(new Total(amount));

        sources.add(source);
        transfer.setSource(sources);

        //delivery
        List<Delivery> deliveries = new ArrayList<>();

        Amount walletAmount = new Amount();
        walletAmount.setAmount(String.valueOf(withdrawalAmount));
        walletAmount.setCurrency(wallet.getCurrency());

        Order order = new Order();
        order.setAmount(amount);

        Total total = new Total();
        total.setAmount(amount);

        Delivery delivery = new Delivery();
        delivery.setAccount(new Account(wallet.getId(), Account.TYPE_WALLET, wallet.getCurrency()));
        delivery.setOrder(order);
        delivery.setTotal(total);
        deliveries.add(delivery);
        transfer.setDelivery(deliveries);

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setAmount(amount);
        orderInfo.setReference(UUID.randomUUID().toString());
        transfer.setOrderInfo(orderInfo);

        mViewModel.chargeLookup(transfer, Transfer.TOPUP);
        mViewModel.getAmountResourceLiveData().observe(this, resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        mViewModel.setLoadingTitle("Fetching Transaction charges...");
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

                        mBinding.chargeTitleView.setVisibility(View.VISIBLE);
                        mBinding.chargeTextView.setVisibility(View.VISIBLE);
                        mBinding.chargeTextView.setText(numberFormat.format(Double.parseDouble(charge.getAmount().getAmount())));

                        withdrawalAmount += Double.parseDouble(charge.getAmount().getAmount());
                        source.setTotal(new Total(new Amount(String.valueOf(withdrawalAmount), currency)));

                        mBinding.totalTitleView.setVisibility(View.VISIBLE);
                        mBinding.totalTextView.setVisibility(View.VISIBLE);
                        mBinding.totalTextView.setText(numberFormat.format(withdrawalAmount));

                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        break;
                }
            }
        });
    }
}
