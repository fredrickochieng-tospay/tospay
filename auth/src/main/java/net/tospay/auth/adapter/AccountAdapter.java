package net.tospay.auth.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.tospay.auth.adapter.viewholder.AccountViewHolder;
import net.tospay.auth.adapter.viewholder.TitleViewHolder;
import net.tospay.auth.adapter.viewholder.WalletViewHolder;
import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.interfaces.OnAccountItemClickListener;
import net.tospay.auth.model.Account;
import net.tospay.auth.model.AccountTitle;
import net.tospay.auth.model.Wallet;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter {

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
            case AccountType.WALLET:
                return WalletViewHolder.create(inflater, parent, onAccountTypeSelectedListener);

            case AccountType.BANK:
            case AccountType.CARD:
            case AccountType.MOBILE:
                return AccountViewHolder.create(inflater, parent, onAccountTypeSelectedListener);

            default:
                return TitleViewHolder.create(inflater, parent);
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

            case AccountType.TITLE:
                ((TitleViewHolder) holder).onBind((AccountTitle) accountType);
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

    public void setAccountTypeList(List<AccountType> accountTypes) {
        this.mAccountTypes = accountTypes;
        notifyItemRangeChanged(0, accountTypes.size());
    }
}
