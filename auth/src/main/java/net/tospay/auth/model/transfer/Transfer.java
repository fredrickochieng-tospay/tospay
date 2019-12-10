package net.tospay.auth.model.transfer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Transfer {

    public static final String TOPUP = "TOPUP";
    public static final String WITHDRAW = "WITHDRAW";
    public static final String TRANSFER = "TRANSFER";

    @SerializedName("delivery")
    @Expose
    private List<Delivery> delivery = null;

    @SerializedName("source")
    @Expose
    private List<Source> source = null;

    @SerializedName("orderInfo")
    @Expose
    private OrderInfo orderInfo;

    @SerializedName("type")
    @Expose
    private String type;

    public List<Delivery> getDelivery() {
        return delivery;
    }

    public void setDelivery(List<Delivery> delivery) {
        this.delivery = delivery;
    }

    public OrderInfo getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
