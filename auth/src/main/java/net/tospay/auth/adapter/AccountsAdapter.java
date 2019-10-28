package net.tospay.auth.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.tospay.auth.R;
import net.tospay.auth.R2;
import net.tospay.auth.model.Account;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.ViewHolder> {

    private List<Account> accounts;
    private OnAccountListener mAccountListener;

    public AccountsAdapter(List<Account> accounts) {
        this.accounts = accounts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_account_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Account account = accounts.get(position);
        holder.bind(account);
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
        notifyItemRangeChanged(0, accounts.size());
    }

    public void setAccountListener(OnAccountListener mAccountListener) {
        this.mAccountListener = mAccountListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.networkTextView)
        TextView networkTextView;

        @BindView(R2.id.truncTextView)
        TextView truncTextView;



        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Account account) {

            networkTextView.setText(account.getNetwork());
            truncTextView.setText(account.getTrunc());

            itemView.setOnClickListener(view -> {
                if (mAccountListener != null) {
                    mAccountListener.onAccountSelected(account);
                }
            });
        }
    }

    public interface OnAccountListener {
        void onAccountSelected(Account account);
    }
}
