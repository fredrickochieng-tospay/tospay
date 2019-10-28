package net.tospay.auth.api.listeners;

import net.tospay.auth.model.Transaction;
import net.tospay.auth.model.Wallet;

import java.util.List;

public interface WalletTransactionListener extends BaseListener {

    void onWalletTransactions(List<Wallet> wallets, List<Transaction> transactions);

}
