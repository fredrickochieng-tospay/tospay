package net.tospay.auth.model.transfer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Source implements Parcelable {

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

    @SerializedName("amount")
    @Expose
    private Amount amount;

    public Source() {
    }

    protected Source(Parcel in) {
        account = in.readParcelable(Account.class.getClassLoader());
        charge = in.readParcelable(Charge.class.getClassLoader());
        order = in.readParcelable(Order.class.getClassLoader());
        total = in.readParcelable(Total.class.getClassLoader());
        amount = in.readParcelable(Amount.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(account, flags);
        dest.writeParcelable(charge, flags);
        dest.writeParcelable(order, flags);
        dest.writeParcelable(total, flags);
        dest.writeParcelable(amount, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Source> CREATOR = new Creator<Source>() {
        @Override
        public Source createFromParcel(Parcel in) {
            return new Source(in);
        }

        @Override
        public Source[] newArray(int size) {
            return new Source[size];
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

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Source{" +
                "account=" + account +
                ", charge=" + charge +
                ", order=" + order +
                ", total=" + total +
                ", amount=" + amount +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Source)) {
            return false;
        }

        Source source = (Source) obj;
        return this.account.equals(source.account);
    }
}