package net.tospay.auth.ui.account;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import net.tospay.auth.databinding.ListItemTitleViewBinding;
import net.tospay.auth.model.AccountTitle;

public class TitleViewHolder extends RecyclerView.ViewHolder {

    private ListItemTitleViewBinding binding;

    private TitleViewHolder(ListItemTitleViewBinding binding, OnAccountItemClickListener listener) {
        super(binding.getRoot());
        this.binding = binding;
        binding.btnAdd.setOnClickListener(view ->
                listener.onAddAccount(binding.getTitle().getAccountType()));
    }

    public void onBind(AccountTitle title) {
        binding.setTitle(title);
        binding.executePendingBindings();
    }

    public static TitleViewHolder create(LayoutInflater inflater, ViewGroup parent, OnAccountItemClickListener listener) {
        ListItemTitleViewBinding itemTitleViewBinding =
                ListItemTitleViewBinding.inflate(inflater, parent, false);
        return new TitleViewHolder(itemTitleViewBinding, listener);
    }
}
