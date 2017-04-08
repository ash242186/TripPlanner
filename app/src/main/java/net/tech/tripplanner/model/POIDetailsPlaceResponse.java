package net.tech.tripplanner.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ashwini on 4/8/2017.
 */

public class POIDetailsPlaceResponse {
    @SerializedName(value = "data")
    @Expose
    private Result data;

    public Result getData() {
        return data;
    }

    public void setData(Result data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "POIDetailsPlaceResponse{" +
                "data=" + data +
                '}';
    }
}
