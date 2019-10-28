package net.tospay.auth.ui.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import net.tospay.auth.R;
import net.tospay.auth.TospayGateway;
import net.tospay.auth.adapter.AccountsAdapter;
import net.tospay.auth.api.listeners.ListAccountListener;
import net.tospay.auth.api.response.AccountResponse;
import net.tospay.auth.api.response.TospayException;
import net.tospay.auth.databinding.FragmentAccountSelectionBinding;
import net.tospay.auth.model.Account;
import net.tospay.auth.model.Merchant;
import net.tospay.auth.model.Payment;
import net.tospay.auth.model.TospayUser;
import net.tospay.auth.ui.activity.TospayAuthClient;
import net.tospay.auth.ui.dialog.LinkAccountDialogFragment;
import net.tospay.auth.viewmodel.PaymentViewModel;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountSelectionFragment extends Fragment implements
        LinkAccountDialogFragment.LinkAccountListener, ListAccountListener {

    private TospayGateway tospayGateway;

    private LinearLayout loadingLayout;
    private NestedScrollView nestedScrollView;
    private List<Account> accounts;
    private AccountsAdapter adapter;

    private FragmentAccountSelectionBinding mBinding;
    private Account selectAccount;

    public AccountSelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.tospayGateway = TospayGateway.getInstance(getContext());
        this.accounts = new ArrayList<>();
        this.adapter = new AccountsAdapter(accounts);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_account_selection, container, false);
        return mBinding.getRoot();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PaymentViewModel paymentViewModel = ViewModelProviders.of(this)
                .get(PaymentViewModel.class);

        mBinding.setViewmodel(paymentViewModel);
        mBinding.setLifecycleOwner(this);

        if (getArguments() != null) {
            AccountSelectionFragmentArgs args
                    = AccountSelectionFragmentArgs.fromBundle(getArguments());

            TospayUser user = args.getUser();
            mBinding.setUser(user);
        }

        loadingLayout = mBinding.loadingLayout;
        nestedScrollView = mBinding.nestedScrollView;

        mBinding.recyclerView.setAdapter(adapter);
        adapter.setAccountListener(account -> {
            selectAccount = account;
            mBinding.btnPay.setEnabled(selectAccount != null);
        });

        fetchAccounts();

        Payment transaction = ((TospayAuthClient) getActivity()).getTransaction();
        paymentViewModel.transaction().setValue(transaction);

        Merchant merchant = ((TospayAuthClient) getActivity()).getMerchant();
        paymentViewModel.merchant().setValue(merchant);

        mBinding.btnAddAccount.setOnClickListener(view1 ->
                LinkAccountDialogFragment.newInstance().show(getChildFragmentManager(), LinkAccountDialogFragment.TAG));

        mBinding.btnPay.setOnClickListener(view12 -> {
            if (selectAccount != null) {
                executePayment(selectAccount);
            } else {
                Toast.makeText(getContext(), "Unable to process payment. Please select and try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void executePayment(Account selectAccount) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Processing payment. Please wait...");
        // progressDialog.show();
    }

    private void fetchAccounts() {
        tospayGateway.fetchAccounts(this);
    }

    @Override
    public void onAccounts(AccountResponse accountResponse) {
        nestedScrollView.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.GONE);

        if (accountResponse.getMobile() != null) {
            this.accounts.addAll(accountResponse.getMobile());
        }

        if (accountResponse.getCard() != null) {
            this.accounts.addAll(accountResponse.getCard());
        }

        if (accountResponse.getBank() != null) {
            this.accounts.addAll(accountResponse.getBank());
        }

        this.adapter.setAccounts(accounts);
    }

    @Override
    public void onError(TospayException error) {
        loadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void onMobileAccount(View view) {
        NavHostFragment.findNavController(this)
                .navigate(R.id.navigation_link_mobile_account);
    }

    @Override
    public void onBankAccount(View view) {

    }

    @Override
    public void onCreditCardAccount(View view) {

    }
}
