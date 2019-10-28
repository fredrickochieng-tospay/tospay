package net.tospay.auth.api.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.tospay.auth.model.Transaction;
import net.tospay.auth.model.Wallet;

import java.util.List;

public class WalletTransactionResponse {

    @SerializedName("wallets")
    @Expose
    private List<Wallet> wallets = null;

    @SerializedName("transactions")
    @Expose
    private List<Transaction> transactions = null;

    public List<Wallet> getWallets() {
        return wallets;
    }

    public void setWallets(List<Wallet> wallets) {
        this.wallets = wallets;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
