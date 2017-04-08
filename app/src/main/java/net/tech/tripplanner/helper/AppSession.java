package net.tech.tripplanner.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class AppSession {

    private SharedPreferences sharedPref;
    private Editor editor;
    private String SHARED = "App_Preferences";
    public String App_history_places = "App history picked place IDs";


    public AppSession(Context context) {
        sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
    }


    public ArrayList<String> Get_Arraykey(String key) {
        ArrayList<String> array = new ArrayList<String>();
        String str = sharedPref.getString(key, StringUtils.join(array, ","));
        String[] arr = StringUtils.split(str, ",");
        array.addAll(Arrays.asList(arr));
        return array;
    }

    public void Set_Arraykey(String key, String value) {
        editor = sharedPref.edit();
        Set<String> array = new HashSet<String>();
        try {
            array.addAll(Get_Arraykey(key));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (value != null) {
            array.add(value);
            editor.putString(key, StringUtils.join(value, ","));
        }

        editor.apply();
    }
}
