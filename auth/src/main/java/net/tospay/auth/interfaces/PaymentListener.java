package net.tospay.auth.interfaces;

import net.tospay.auth.model.transfer.Transfer;
import net.tospay.auth.remote.response.TospayException;
import net.tospay.auth.model.TospayUser;

public interface PaymentListener {
    default void onPaymentDetails(Transfer transfer) {

    }

    default void onPaymentSuccess() {

    }

    default void onPaymentFailed(TospayException exception) {

    }

    default void onAccountSelected(AccountType accountType) {

    }

    default void onLoginSuccess(TospayUser user) {

    }

    default void onLoginFailed() {

    }
}
