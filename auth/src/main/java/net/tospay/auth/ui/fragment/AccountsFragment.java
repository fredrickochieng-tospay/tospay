package net.tospay.auth.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.tospay.auth.R;
import net.tospay.auth.R2;
import net.tospay.auth.TospayGateway;
import net.tospay.auth.adapter.AccountsAdapter;
import net.tospay.auth.api.listeners.ListAccountListener;
import net.tospay.auth.api.response.AccountResponse;
import net.tospay.auth.api.response.TospayException;
import net.tospay.auth.model.Account;
import net.tospay.auth.ui.dialog.LinkAccountDialogFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountsFragment extends Fragment implements LinkAccountDialogFragment.LinkAccountListener, ListAccountListener {

    @BindView(R2.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R2.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R2.id.noAccountsLayout)
    LinearLayout noAccountsLayout;

    private List<Account> accounts;
    private AccountsAdapter adapter;

    public AccountsFragment() {
        // Required empty public constructor
    }

    public static AccountsFragment newInstance() {
        return new AccountsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.accounts = new ArrayList<>();
        this.adapter = new AccountsAdapter(accounts);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_accounts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        swipeRefreshLayout.setRefreshing(true);
        recyclerView.setAdapter(adapter);

        TospayGateway.getInstance(view.getContext()).fetchAccounts(this);

    }

    @OnClick({R2.id.btn_add_accounts})
    public void addAccounts() {
        LinkAccountDialogFragment.newInstance()
                .show(getChildFragmentManager(), LinkAccountDialogFragment.TAG);
    }

    @Override
    public void onMobileAccount(View view) {

    }

    @Override
    public void onBankAccount(View view) {

    }

    @Override
    public void onCreditCardAccount(View view) {

    }

    @Override
    public void onAccounts(AccountResponse accountResponse) {
        noAccountsLayout.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);

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
        noAccountsLayout.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(getContext(), error.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }
}
