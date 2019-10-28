package net.tospay.auth.interfaces;

import net.tospay.auth.api.response.PaymentValidationResponse;
import net.tospay.auth.api.response.TospayException;

public interface PaymentListener {
    void onPaymentDetails(PaymentValidationResponse response);

    void onPaymentSuccess();

    void onPaymentFailed(TospayException exception);
}
