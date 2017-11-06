package com.itheima.mobileplayer31.bean;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ThinkPad on 2016/11/27.
 */

public class AudioItem implements Serializable{
    private String path;
    private String display_name;
    private String artist;
    //根据特定位置的cursor解析整个播放列表
    public static ArrayList<AudioItem> getAudioItems(Cursor cursor){
        //创建集合
        ArrayList<AudioItem> audioItems = new ArrayList<AudioItem>();
        //判断cursor是否为空
        if(cursor==null||cursor.getCount()==0) return audioItems;
        //移动游标到-1
        cursor.moveToPosition(-1);
        //解析cursor添加到集合中
        while (cursor.moveToNext()){
            AudioItem audioItem = getAudioItem(cursor);
            audioItems.add(audioItem);
        }
        //返回集合
        return audioItems;
    }
    //根据特定位置的cursor获取audioitem
    public static AudioItem getAudioItem(Cursor cursor){
        //创建audioitem
        AudioItem audioItem = new AudioItem();
        //判断cursor是否为空
        if(cursor==null||cursor.getCount()==0) return audioItem;
        //解析添加到字段中
        audioItem.path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
        audioItem.display_name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
        audioItem.display_name = audioItem.display_name.substring(0,audioItem.display_name.lastIndexOf("."));
        audioItem.artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
        //返回audioitem
        return audioItem;
    }
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
