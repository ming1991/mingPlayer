package com.itheima.mobileplayer31.util;

import android.util.Log;

/**
 * Created by ThinkPad on 2016/11/24.
 */

public class Logutil {
    private static boolean showLog = true;
    public static void logD(String tag,String msg){
        if(showLog) {
            Log.d(tag, msg);
        }
    }
}
