package net.tech.tripplanner.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;


public class AppSession {

    private SharedPreferences sharedPref;
    private Editor editor;
    private String SHARED = "App_Preferences";
    public String App_history_places = "App history picked place IDs";


    public AppSession(Context context) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }


    public ArrayList<String> Get_Arraykey(String key) {
        ArrayList<String> array = new ArrayList<String>();
        String str = sharedPref.getString(key, TextUtils.join(",", array));
        String[] arr = TextUtils.split(str, ",");
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
            editor.putString(key, TextUtils.join(",", array));
        }

        editor.apply();
    }
}
