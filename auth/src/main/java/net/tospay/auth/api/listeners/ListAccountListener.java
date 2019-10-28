package net.tospay.auth.api.listeners;

import net.tospay.auth.api.response.AccountResponse;
import net.tospay.auth.api.response.TospayException;

public interface ListAccountListener extends BaseListener {

    void onAccounts(AccountResponse accounts);

}
