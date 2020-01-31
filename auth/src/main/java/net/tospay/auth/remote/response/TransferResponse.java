package net.tospay.auth.remote.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransferResponse {

    @SerializedName("tId")
    @Expose
    private String tId;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("html")
    @Expose
    private String html;

    public String gettId() {
        return tId;
    }

    public void settId(String tId) {
        this.tId = tId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}
