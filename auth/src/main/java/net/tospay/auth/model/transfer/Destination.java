package net.tospay.auth.model.transfer;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Destination {

    @SerializedName("amount")
    @Expose
    private Amount amount;

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

}
