package net.tech.tripplanner.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ashwini on 4/8/2017.
 */

public class POIItem {
    @SerializedName(value = "data", alternate = {"Things To Do", "Places To Visit", "hotel"})
    @Expose
    private List<Result> data;

    public List<Result> getData() {
        return data;
    }

    public void setData(List<Result> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "POIItem{" +
                "data=" + data +
                '}';
    }
}
