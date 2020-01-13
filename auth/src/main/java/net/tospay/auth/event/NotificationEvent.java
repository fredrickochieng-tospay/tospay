package net.tospay.auth.event;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationEvent implements Parcelable {

    @SerializedName("data")
    @Expose
    private Data data;

    @SerializedName("notification")
    @Expose
    private Notification notification;

    public NotificationEvent() {
    }

    protected NotificationEvent(Parcel in) {
        data = in.readParcelable(Data.class.getClassLoader());
        notification = in.readParcelable(Notification.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(data, flags);
        dest.writeParcelable(notification, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NotificationEvent> CREATOR = new Creator<NotificationEvent>() {
        @Override
        public NotificationEvent createFromParcel(Parcel in) {
            return new NotificationEvent(in);
        }

        @Override
        public NotificationEvent[] newArray(int size) {
            return new NotificationEvent[size];
        }
    };

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    @Override
    public String toString() {
        return "NotificationEvent{" +
                "data=" + data +
                ", notification=" + notification +
                '}';
    }
}
