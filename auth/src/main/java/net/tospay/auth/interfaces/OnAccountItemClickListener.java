package net.tospay.auth.interfaces;

import android.view.View;

public interface OnAccountItemClickListener {

    default void onAccountType(AccountType accountType) {

    }

    void onVerifyClick(View view, AccountType accountType);
}
