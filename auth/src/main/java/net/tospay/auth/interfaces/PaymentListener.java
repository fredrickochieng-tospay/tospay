package net.tospay.auth.interfaces;

import net.tospay.auth.model.TospayUser;
import net.tospay.auth.remote.response.TospayException;

public interface PaymentListener {

    default void onPaymentSuccess() {

    }

    default void onPaymentFailed(TospayException exception) {

    }

    default void onLoginSuccess(TospayUser user) {

    }

    default void onLoginFailed() {

    }
}
