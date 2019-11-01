package net.tospay.auth.interfaces;

import net.tospay.auth.api.response.PaymentResult;
import net.tospay.auth.api.response.TospayException;

public interface PaymentListener {
    default void onPaymentDetails(PaymentResult response) {

    }

    default void onPaymentSuccess() {

    }

    default void onPaymentFailed(TospayException exception) {

    }

    default void onAccountSelected(AccountType accountType) {

    }
}
