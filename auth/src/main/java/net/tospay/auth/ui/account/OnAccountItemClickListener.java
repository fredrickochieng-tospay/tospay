package net.tospay.auth.ui.account;

import android.view.View;

import net.tospay.auth.interfaces.AccountType;

public interface OnAccountItemClickListener {

    void onVerifyClick(View view, AccountType accountType);

}
