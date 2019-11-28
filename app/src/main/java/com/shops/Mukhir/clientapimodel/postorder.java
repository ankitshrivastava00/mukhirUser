
package com.shops.Mukhir.clientapimodel;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.shops.Mukhir.models.Shop;

public class postorder {

    @SerializedName("isSuccessful")
    @Expose
    private String isSuccessful;
    @SerializedName("message")
    @Expose
    private Integer message;

    @SerializedName("data")
    @Expose
    public JsonObject data;

    public String getIsSuccessful() {
        return isSuccessful;
    }

    public void setIsSuccessful(String isSuccessful) {
        this.isSuccessful = isSuccessful;
    }

    public Integer getMessage() {
        return message;
    }

    public void setMessage(Integer message) {
        this.message = message;
    }
}
