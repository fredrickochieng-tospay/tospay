package net.tospay.auth.model.transfer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Transfer {

    @SerializedName("chargeInfo")
    @Expose
    private ChargeInfo chargeInfo;

    @SerializedName("delivery")
    @Expose
    private List<Delivery> delivery = null;

    @SerializedName("deviceInfo")
    @Expose
    private DeviceInfo deviceInfo;

    @SerializedName("orderInfo")
    @Expose
    private OrderInfo orderInfo;

    @SerializedName("type")
    @Expose
    private String type;

    public ChargeInfo getChargeInfo() {
        return chargeInfo;
    }

    public void setChargeInfo(ChargeInfo chargeInfo) {
        this.chargeInfo = chargeInfo;
    }

    public List<Delivery> getDelivery() {
        return delivery;
    }

    public void setDelivery(List<Delivery> delivery) {
        this.delivery = delivery;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
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
