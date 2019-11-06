package net.tospay.auth.ui.account;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import net.tospay.auth.databinding.ListItemWalletViewBinding;
import net.tospay.auth.model.Wallet;

public class WalletViewHolder extends RecyclerView.ViewHolder {

    private ListItemWalletViewBinding binding;

    private WalletViewHolder(ListItemWalletViewBinding binding, OnAccountItemClickListener listener) {
        super(binding.getRoot());
        this.binding = binding;
        binding.getRoot().setOnClickListener(view -> listener.onAccountType(binding.getWallet()));
    }

    public void onBind(Wallet wallet) {
        binding.setWallet(wallet);
        binding.executePendingBindings();
    }

    public static WalletViewHolder create(LayoutInflater inflater, ViewGroup parent, OnAccountItemClickListener listener) {
        ListItemWalletViewBinding itemWalletViewBinding = ListItemWalletViewBinding.inflate(inflater, parent, false);
        return new WalletViewHolder(itemWalletViewBinding, listener);
    }
}
