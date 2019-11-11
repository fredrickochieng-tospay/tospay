package net.tospay.auth.interfaces;

import net.tospay.auth.api.response.PaymentValidationResponse;
import net.tospay.auth.api.response.TospayException;
import net.tospay.auth.model.TospayUser;

public interface PaymentListener {
    default void onPaymentDetails(PaymentValidationResponse response) {

    }

    default void onPaymentSuccess() {

    }

    default void onPaymentFailed(TospayException exception) {

    }

    default void onAccountSelected(AccountType accountType) {

    }

    default void onLoginSuccess(TospayUser user) {

    }
}
