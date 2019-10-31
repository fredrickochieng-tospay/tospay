package net.tospay.auth.adapter.viewholder;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import net.tospay.auth.databinding.ListItemTitleViewBinding;
import net.tospay.auth.model.AccountTitle;

public class TitleViewHolder extends RecyclerView.ViewHolder {

    private ListItemTitleViewBinding binding;

    public TitleViewHolder(ListItemTitleViewBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void onBind(AccountTitle title) {
        binding.setTitle(title);
        binding.executePendingBindings();
    }

    public static TitleViewHolder create(LayoutInflater inflater, ViewGroup parent) {
        ListItemTitleViewBinding itemTitleViewBinding =
                ListItemTitleViewBinding.inflate(inflater, parent, false);
        return new TitleViewHolder(itemTitleViewBinding);
    }
}
