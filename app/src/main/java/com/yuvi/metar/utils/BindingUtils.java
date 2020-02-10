package com.yuvi.metar.utils;

import android.text.TextUtils;

import java.util.Date;
import java.text.SimpleDateFormat;

public class BindingUtils {
    public static String formatDate(Date date) {
        if(date == null) {
            return "";
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            return sdf.format(date);
        }catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String showLoadingIfEmpty(String text) {
        if(TextUtils.isEmpty(text)) {
            return "Loading data, Please wait";
        } else {
            return text;
        }
    }
}
