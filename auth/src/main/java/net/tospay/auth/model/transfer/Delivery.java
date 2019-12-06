package net.tospay.auth.model.transfer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Delivery {

    @SerializedName("account")
    @Expose
    private Account account;

    @SerializedName("charge")
    @Expose
    private Charge charge;

    @SerializedName("order")
    @Expose
    private Order order;

    @SerializedName("total")
    @Expose
    private Total total;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Charge getCharge() {
        return charge;
    }

    public void setCharge(Charge charge) {
        this.charge = charge;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Total getTotal() {
        return total;
    }

    public void setTotal(Total total) {
        this.total = total;
    }

}
