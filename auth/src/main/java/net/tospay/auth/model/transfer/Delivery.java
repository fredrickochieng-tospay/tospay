package net.tospay.auth.model.transfer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Delivery implements Parcelable {

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

    public Delivery() {
    }

    protected Delivery(Parcel in) {
        account = in.readParcelable(Account.class.getClassLoader());
        charge = in.readParcelable(Charge.class.getClassLoader());
        order = in.readParcelable(Order.class.getClassLoader());
        total = in.readParcelable(Total.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(account, flags);
        dest.writeParcelable(charge, flags);
        dest.writeParcelable(order, flags);
        dest.writeParcelable(total, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Delivery> CREATOR = new Creator<Delivery>() {
        @Override
        public Delivery createFromParcel(Parcel in) {
            return new Delivery(in);
        }

        @Override
        public Delivery[] newArray(int size) {
            return new Delivery[size];
        }
    };

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

    @Override
    public String toString() {
        return "Delivery{" +
                "account=" + account +
                ", charge=" + charge +
                ", order=" + order +
                ", total=" + total +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Delivery)) {
            return false;
        }

        Delivery delivery = (Delivery) obj;
        return this.account.equals(delivery.account);
    }
}
