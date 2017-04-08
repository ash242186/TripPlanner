package net.tech.tripplanner.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ashwini on 4/8/2017.
 */

public class POIPlacesResponse {
    @SerializedName(value = "data")
    @Expose
    private POIItem data;

    public POIItem getData() {
        return data;
    }

    public void setData(POIItem data) {
        this.data = data;
    }


    @Override
    public String toString() {
        return "POIPlacesResponse{" +
                "data=" + data +
                '}';
    }
}
