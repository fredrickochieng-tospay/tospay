package net.tospay.auth.ui.account;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.model.Account;
import net.tospay.auth.model.Wallet;
import net.tospay.auth.ui.base.BaseAdapter;

import java.util.List;

public class AccountAdapter extends BaseAdapter<RecyclerView.ViewHolder, AccountType> {

    private List<AccountType> mAccountTypes;
    private final OnAccountItemClickListener onAccountTypeSelectedListener;

    public AccountAdapter(List<AccountType> accountTypes, OnAccountItemClickListener onAccountTypeSelectedListener) {
        this.onAccountTypeSelectedListener = onAccountTypeSelectedListener;
        this.mAccountTypes = accountTypes;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {

            case AccountType.BANK:
            case AccountType.CARD:
            case AccountType.MOBILE:
                return AccountViewHolder.create(inflater, parent, onAccountTypeSelectedListener);

            default:
                return WalletViewHolder.create(inflater, parent, onAccountTypeSelectedListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AccountType accountType = mAccountTypes.get(position);

        switch (getItemViewType(position)) {

            case AccountType.WALLET:
                ((WalletViewHolder) holder).onBind((Wallet) accountType);
                break;

            case AccountType.BANK:
            case AccountType.CARD:
            case AccountType.MOBILE:
                ((AccountViewHolder) holder).onBind((Account) accountType);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mAccountTypes != null ? mAccountTypes.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return mAccountTypes.get(position).getType();
    }

    @Override
    public void setData(List<AccountType> accountTypes) {
        this.mAccountTypes = accountTypes;
        notifyDataSetChanged();
    }
}
