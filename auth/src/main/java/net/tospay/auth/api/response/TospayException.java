package net.tospay.auth.api.response;

import android.text.TextUtils;

import java.io.IOException;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TospayException extends IOException {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("error")
    @Expose
    private List<Error> error = null;

    public TospayException(String message) {
        super(message);
    }

    public TospayException(Throwable throwable) {
        super(throwable);
    }

    public TospayException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Error> getError() {
        return error;
    }

    public void setError(List<Error> error) {
        this.error = error;
    }

    public String getErrorMessage() {
        if (error != null) {
            if (!error.isEmpty()) {
                if (error.size() == 1) {
                    return error.get(0).getDesc();
                } else {
                    return TextUtils.join("\n", error);
                }
            }

            return description;
        }

        return description;
    }
}
