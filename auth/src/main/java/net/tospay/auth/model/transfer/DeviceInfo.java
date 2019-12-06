package net.tospay.auth.model.transfer;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeviceInfo {

    @SerializedName("ip")
    @Expose
    private List<String> ip = null;

    @SerializedName("userAgent")
    @Expose
    private String userAgent;

    public List<String> getIp() {
        return ip;
    }

    public void setIp(List<String> ip) {
        this.ip = ip;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}