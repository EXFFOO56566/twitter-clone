package com.scappy.twlight.LiveBroadcastSystem.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.scappy.twlight.LiveBroadcastSystem.Constants;


public class PrefManager {
    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }
}
