package net.tospay.auth.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;

import net.tospay.auth.R;
import net.tospay.auth.TospayGateway;
import net.tospay.auth.adapter.AccountAdapter;
import net.tospay.auth.databinding.FragmentAccountSelectionBinding;
import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.interfaces.OnAccountItemClickListener;
import net.tospay.auth.ui.dialog.LinkAccountDialogFragment;
import net.tospay.auth.viewmodel.AccountViewModel;

import java.util.ArrayList;
import java.util.List;

public class AccountSelectionFragment extends Fragment
        implements LinkAccountDialogFragment.LinkAccountListener, OnAccountItemClickListener {

    private TospayGateway tospayGateway;
    private List<AccountType> accountTypes;
    private AccountAdapter adapter;
    private FragmentAccountSelectionBinding mBinding;

    public AccountSelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.tospayGateway = TospayGateway.getInstance(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_account_selection, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AccountViewModel mAccountViewModel = ViewModelProviders.of(this)
                .get(AccountViewModel.class);

        mBinding.setAccountViewModel(mAccountViewModel);
        mBinding.setLifecycleOwner(this);

        this.accountTypes = new ArrayList<>();
        this.adapter = new AccountAdapter(accountTypes, this);

        mBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mBinding.recyclerView.setAdapter(adapter);

        mAccountViewModel.fetchAccounts(tospayGateway);
        mAccountViewModel.getAccountTypes().observe(this, accountTypes -> {
            if (accountTypes != null) {
                this.accountTypes = accountTypes;
                adapter.setAccountTypeList(accountTypes);
            }
        });

        mBinding.btnAddAccount.setOnClickListener(view1 ->
                LinkAccountDialogFragment.newInstance()
                        .show(getChildFragmentManager(), LinkAccountDialogFragment.TAG));
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

    @Override
    public void onAccountType(AccountType accountType) {

    }

    @Override
    public void onVerifyClick(View view, AccountType accountType) {

    }
}
