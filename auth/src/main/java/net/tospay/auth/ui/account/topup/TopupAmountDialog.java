package net.tospay.auth.ui.account.topup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.tospay.auth.R;
import net.tospay.auth.anim.ViewAnimation;
import net.tospay.auth.databinding.DialogTopupAmountBinding;
import net.tospay.auth.databinding.DialogTopupBinding;
import net.tospay.auth.model.Wallet;
import net.tospay.auth.model.transfer.Account;
import net.tospay.auth.model.transfer.Amount;
import net.tospay.auth.model.transfer.OrderInfo;
import net.tospay.auth.model.transfer.Store;
import net.tospay.auth.model.transfer.Transfer;
import net.tospay.auth.ui.account.AccountViewModel;
import net.tospay.auth.utils.SharedPrefManager;
import net.tospay.auth.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static androidx.fragment.app.DialogFragment.STYLE_NORMAL;

public class TopupAmountDialog extends BottomSheetDialogFragment {

    private static final String KEY_WALLET = "wallet";

    private AccountViewModel mViewModel;
    private DialogTopupAmountBinding mBinding;
    private SharedPrefManager mSharedPrefManager;
    private Transfer transfer;
    private Wallet wallet;
    private Amount charge;
    private net.tospay.auth.model.Account selectedAccount;
    private double withdrawalAmount = 0;
    private String currency;

    //source
    private List<Store> sources;
    private Store source;

    public TopupAmountDialog() {
        // Required empty public constructor
    }

    public static TopupAmountDialog newInstance(Wallet wallet) {
        TopupAmountDialog fragment = new TopupAmountDialog();
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
            if (wallet != null) {
                currency = wallet.getCurrency();
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_topup_amount, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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


    private void performChargeLookup() {
        charge = null;
        sources = new ArrayList<>();
        source = new Store();

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
        source.setOrder(amount);
        source.setTotal(amount);

        sources.add(source);
        transfer.setSource(sources);

        //delivery
        List<Store> deliveries = new ArrayList<>();

        Amount walletAmount = new Amount();
        walletAmount.setAmount(String.valueOf(withdrawalAmount));
        walletAmount.setCurrency(wallet.getCurrency());

        Store delivery = new Store();
        delivery.setAccount(new Account(wallet.getId(), Account.TYPE_WALLET, wallet.getCurrency()));
        delivery.setOrder(amount);
        delivery.setTotal(amount);
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
                        charge = resource.data;

                        mBinding.paymentSummeryTextView.setVisibility(View.VISIBLE);
                        mBinding.chargeTitleView.setVisibility(View.VISIBLE);
                        mBinding.chargeTextView.setVisibility(View.VISIBLE);
                        mBinding.chargeTextView.setText(String.format("%s %s", charge.getCurrency(), charge.getAmount()));

                        withdrawalAmount += Double.parseDouble(charge.getAmount());
                        source.setTotal(new Amount(String.valueOf(withdrawalAmount), currency));

                        mBinding.totalTitleView.setVisibility(View.VISIBLE);
                        mBinding.totalTextView.setVisibility(View.VISIBLE);
                        mBinding.totalTextView.setText(String.format("%s %s", currency, withdrawalAmount));

                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        break;
                }
            }
        });
    }

}
