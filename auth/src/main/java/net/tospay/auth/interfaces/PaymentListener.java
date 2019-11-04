package net.tospay.auth.interfaces;

import net.tospay.auth.api.response.PaymentValidationResponse;
import net.tospay.auth.api.response.TospayException;

public interface PaymentListener {
    default void onPaymentDetails(PaymentValidationResponse response) {

    }

    default void onPaymentSuccess() {

    }

    default void onPaymentFailed(TospayException exception) {

    }

    default void onAccountSelected(AccountType accountType) {

    }
}
