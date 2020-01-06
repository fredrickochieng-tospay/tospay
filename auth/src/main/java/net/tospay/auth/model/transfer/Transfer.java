package net.tospay.auth.model.transfer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Transfer implements Parcelable {

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

    @SerializedName("chargeInfo")
    @Expose
    private ChargeInfo chargeInfo;

    public Transfer() {
    }

    protected Transfer(Parcel in) {
        delivery = in.createTypedArrayList(Delivery.CREATOR);
        source = in.createTypedArrayList(Source.CREATOR);
        orderInfo = in.readParcelable(OrderInfo.class.getClassLoader());
        type = in.readString();
        chargeInfo = in.readParcelable(ChargeInfo.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(delivery);
        dest.writeTypedList(source);
        dest.writeParcelable(orderInfo, flags);
        dest.writeString(type);
        dest.writeParcelable(chargeInfo, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Transfer> CREATOR = new Creator<Transfer>() {
        @Override
        public Transfer createFromParcel(Parcel in) {
            return new Transfer(in);
        }

        @Override
        public Transfer[] newArray(int size) {
            return new Transfer[size];
        }
    };

    public List<Delivery> getDelivery() {
        return delivery;
    }

    public void setDelivery(List<Delivery> delivery) {
        this.delivery = delivery;
    }

    public List<Source> getSource() {
        return source;
    }

    public void setSource(List<Source> source) {
        this.source = source;
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

    public ChargeInfo getChargeInfo() {
        return chargeInfo;
    }

    public void setChargeInfo(ChargeInfo chargeInfo) {
        this.chargeInfo = chargeInfo;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "delivery=" + delivery +
                ", source=" + source +
                ", orderInfo=" + orderInfo +
                ", type='" + type + '\'' +
                ", chargeInfo=" + chargeInfo +
                '}';
    }
}
