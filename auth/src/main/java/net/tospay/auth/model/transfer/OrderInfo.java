package net.tospay.auth.model.transfer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class OrderInfo {

    @SerializedName("amount")
    @Expose
    private Amount amount;

    @SerializedName("reference")
    @Expose
    private String reference = UUID.randomUUID().toString();

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
