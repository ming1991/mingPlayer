package com.itheima.mobileplayer31.util;

import android.database.Cursor;

/**
 * Created by ThinkPad on 2016/11/24.
 */

public class CursorUtil  {
    public static void cursorLog(Cursor cursor){
        while (cursor.moveToNext()){
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                Logutil.logD("CursorLog","key="+cursor.getColumnName(i)+",value="+cursor.getString(i));
            }
        }
    }
}
