package com.itheima.mobileplayer31.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by ThinkPad on 2016/11/24.
 */

public class ToastUtil {

    private static Toast toast;

    public static void toast(Context context, String msg){
        if(toast==null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        }else {
            toast.setText(msg);
        }
        toast.show();
    }
}
