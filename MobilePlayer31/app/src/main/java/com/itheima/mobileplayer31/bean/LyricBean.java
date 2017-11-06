package com.itheima.mobileplayer31.bean;

/**
 * Created by ThinkPad on 2016/11/28.
 */

public class LyricBean {
    private  int startTime;
    private String content;

    public LyricBean(int startTime, String content) {
        this.startTime = startTime;
        this.content = content;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
