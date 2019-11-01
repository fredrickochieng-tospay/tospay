package net.tospay.auth.ui.account;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import net.tospay.auth.databinding.ListItemAccountViewBinding;
import net.tospay.auth.interfaces.OnAccountItemClickListener;
import net.tospay.auth.model.Account;

public class AccountViewHolder extends RecyclerView.ViewHolder {

    private ListItemAccountViewBinding binding;

    private AccountViewHolder(ListItemAccountViewBinding binding, OnAccountItemClickListener onAccountTypeSelectedListener) {
        super(binding.getRoot());
        this.binding = binding;

        binding.getRoot().setOnClickListener(view -> {
            if (binding.getAccount().isVerified()) {
                onAccountTypeSelectedListener.onAccountType(binding.getAccount());
            }
        });

        binding.btnVerifyPhone.setOnClickListener(view ->
                onAccountTypeSelectedListener.onVerifyClick(view, binding.getAccount())
        );
    }

    public void onBind(Account account) {
        binding.setAccount(account);
        binding.executePendingBindings();
    }

    public static AccountViewHolder create(LayoutInflater inflater, ViewGroup parent,
                                           OnAccountItemClickListener onAccountTypeSelectedListener) {

        ListItemAccountViewBinding itemAccountViewBinding =
                ListItemAccountViewBinding.inflate(inflater, parent, false);

        return new AccountViewHolder(itemAccountViewBinding, onAccountTypeSelectedListener);
    }
}
