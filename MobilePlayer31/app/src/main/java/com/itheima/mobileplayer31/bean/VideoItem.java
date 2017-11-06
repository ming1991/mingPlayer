package com.itheima.mobileplayer31.bean;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ThinkPad on 2016/11/24.
 */

public class VideoItem implements Serializable{
    private String title;
    private String path;//播放路径
    private int duration;
    private int size;
    //根据特定位置的cursor获取整个播放列表
    public static ArrayList<VideoItem> getVideoItems(Cursor cursor){
        //创建集合
        ArrayList<VideoItem> videoItems = new ArrayList<VideoItem>();
        //判断cursor是否为空
        if(cursor==null||cursor.getCount()==0) return videoItems;
        //移动游标到-1位
        cursor.moveToPosition(-1);
        //解析cursor添加到集合中
        while (cursor.moveToNext()){
            VideoItem videoItem = getVideoItem(cursor);
            videoItems.add(videoItem);
        }
        //返回集合
        return videoItems;
    }
    //根据特定位置游标的cursor获取Videoitem
    public static VideoItem getVideoItem(Cursor cursor){
        //创建Videoitem
        VideoItem videoItem = new VideoItem();
        //判断cursor是否为空
        if(cursor==null||cursor.getCount()==0) return videoItem;
        //解析添加到字段中
        videoItem.path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        videoItem.title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
        videoItem.duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
        videoItem.size = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
        //返回videoitem
        return videoItem;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
