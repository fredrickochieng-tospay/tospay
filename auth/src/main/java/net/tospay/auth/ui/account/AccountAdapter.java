package net.tospay.auth.ui.account;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.tospay.auth.databinding.ListItemAccountViewBinding;
import net.tospay.auth.databinding.ListItemWalletViewBinding;
import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.model.Account;
import net.tospay.auth.model.Wallet;
import net.tospay.auth.ui.base.BaseAdapter;

import java.util.List;

public class AccountAdapter extends BaseAdapter<RecyclerView.ViewHolder, AccountType> {

    private List<AccountType> mAccountTypes;
    private final OnAccountItemClickListener listener;
    private int mSelectedItem = -1;

    public AccountAdapter(List<AccountType> accountTypes, OnAccountItemClickListener listener) {
        this.listener = listener;
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
                ListItemAccountViewBinding itemAccountViewBinding =
                        ListItemAccountViewBinding.inflate(inflater, parent, false);
                return new AccountViewHolder(itemAccountViewBinding, listener);

            default:
                ListItemWalletViewBinding itemWalletViewBinding =
                        ListItemWalletViewBinding.inflate(inflater, parent, false);
                return new WalletViewHolder(itemWalletViewBinding, listener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AccountType accountType = mAccountTypes.get(position);

        switch (getItemViewType(position)) {

            case AccountType.WALLET:
                ((WalletViewHolder) holder).onBind((Wallet) accountType);
                ((WalletViewHolder) holder).getBinding().radioButton.setChecked(position == mSelectedItem);
                break;

            case AccountType.BANK:
            case AccountType.CARD:
            case AccountType.MOBILE:
                ((AccountViewHolder) holder).onBind((Account) accountType);
                ((AccountViewHolder) holder).getBinding().radioButton.setChecked(position == mSelectedItem);
                break;
        }
    }

    AccountType getSelectedAccountType() {
        if (mSelectedItem > -1) {
            return mAccountTypes.get(mSelectedItem);
        }

        return null;
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

    public class WalletViewHolder extends RecyclerView.ViewHolder {

        private ListItemWalletViewBinding mBinding;

        private WalletViewHolder(ListItemWalletViewBinding binding, OnAccountItemClickListener mListener) {
            super(binding.getRoot());
            this.mBinding = binding;

            View.OnClickListener onClickListener = view -> {
                mSelectedItem = getAdapterPosition();
                mListener.onAccountSelectedListener(mBinding.getWallet());
                notifyDataSetChanged();
            };

            mBinding.btnTopup.setOnClickListener(view -> mListener.onTopupClick(mBinding.getWallet()));
            mBinding.bgLayout.setOnClickListener(onClickListener);
            mBinding.radioButton.setOnClickListener(onClickListener);
        }

        public ListItemWalletViewBinding getBinding() {
            return mBinding;
        }

        void onBind(Wallet wallet) {
            mBinding.setWallet(wallet);
            mBinding.executePendingBindings();
        }
    }

    private class AccountViewHolder extends RecyclerView.ViewHolder {

        private ListItemAccountViewBinding mBinding;

        private AccountViewHolder(ListItemAccountViewBinding mBinding, OnAccountItemClickListener mListener) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;

            View.OnClickListener onClickListener = view -> {
                mSelectedItem = getAdapterPosition();
                if (mBinding.getAccount().getType() == AccountType.MOBILE) {
                    if (!mBinding.getAccount().isVerified()
                            && !mBinding.getAccount().getState().equalsIgnoreCase("ACTIVE")) {
                        mSelectedItem = -1;
                    }
                }

                notifyDataSetChanged();
            };

            mBinding.getRoot().setOnClickListener(onClickListener);
            mBinding.radioButton.setOnClickListener(onClickListener);
            mBinding.btnVerifyPhone.setOnClickListener(view ->
                    mListener.onVerifyClick(mBinding.getAccount())
            );
        }

        public ListItemAccountViewBinding getBinding() {
            return mBinding;
        }

        void onBind(Account account) {
            mBinding.setAccount(account);
            mBinding.executePendingBindings();
        }
    }
}
