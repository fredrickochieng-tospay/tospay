package net.tospay.auth.interfaces;

import net.tospay.auth.model.transfer.Transfer;
import net.tospay.auth.remote.response.TospayException;
import net.tospay.auth.model.TospayUser;

public interface PaymentListener {
    default void onPaymentDetails(Transfer transfer) {

    }

    default void onPaymentFailed(TospayException exception) {

    }

    default void onLoginSuccess(TospayUser user) {

    }

    default void onLoginFailed() {

    }
}
