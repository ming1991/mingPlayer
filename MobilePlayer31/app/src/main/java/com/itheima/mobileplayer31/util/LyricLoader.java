package com.itheima.mobileplayer31.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by ThinkPad on 2016/11/28.
 */

public class LyricLoader {
    //歌词文件夹
    private static final File dir = new File(Environment.getExternalStorageDirectory(),"/Download/Lyric");
    public static File loadLyricFile(String display_name){
        return new File(dir,display_name+".lrc");
    }
}
