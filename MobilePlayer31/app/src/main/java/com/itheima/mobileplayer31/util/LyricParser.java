package com.itheima.mobileplayer31.util;

import com.itheima.mobileplayer31.bean.LyricBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by ThinkPad on 2016/11/28.
 */

public class LyricParser {

    private static BufferedReader bfr;

    public static ArrayList<LyricBean> parseLyric(File file) {
        //创建歌词集合
        ArrayList<LyricBean> lyricBeens = new ArrayList<LyricBean>();
        //判断歌词file是否存在
        if (!file.exists()) {
            lyricBeens.add(new LyricBean(0, "歌词加载出错"));
            return lyricBeens;
        }
        //解析歌词添加到集合中
        try {
            //reader对象可以读取一行  制定编码格式
            bfr = new BufferedReader(new InputStreamReader(new FileInputStream(file), "gbk"));
            String line = bfr.readLine();
            while (line != null) {
                //解析一行歌词
                ArrayList<LyricBean> lyrics = parseLine(line);
                lyricBeens.addAll(lyrics);
                //读取下一行
                line = bfr.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bfr != null) {
                try {
                    bfr.close();
                    bfr = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //歌词集合排序
        Collections.sort(lyricBeens, new Comparator<LyricBean>() {
            @Override
            public int compare(LyricBean o1, LyricBean o2) {
                int o1T = o1.getStartTime();
                int o2T = o2.getStartTime();
                return o1T-o2T;
            }
        });
        //返回歌词集合
        return lyricBeens;
    }

    //解析一行歌词  [00:48.08 [02:27.15 经过你快乐时少烦恼多
    private static ArrayList<LyricBean> parseLine(String line) {
        //创建歌词集合
        ArrayList<LyricBean> lyricBeens = new ArrayList<LyricBean>();
        //解析
        String[] arr = line.split("]");
        //获取歌词内容
        String content = arr[arr.length - 1];
        for (int i = 0; i < arr.length - 1; i++) {
            int startTime = parseTime(arr[i]);
            LyricBean lyricBean = new LyricBean(startTime, content);
            lyricBeens.add(lyricBean);
        }
        //返回
        return lyricBeens;
    }

    //解析时间 [00:48.08
    private static int parseTime(String s) {
        String time = s.substring(1);
        String[] arr = time.split(":");
        String hour;
        String min;
        String sec;
        int finalTime;
        if (arr.length == 3) {
            hour = arr[0];
            min = arr[1];
            sec = arr[2];
            finalTime = (int) (Integer.parseInt(hour) * 60 * 60 * 1000 + Integer.parseInt(min) * 60 * 1000 + Float.parseFloat(sec) * 1000);
        } else {
            min = arr[0];
            sec = arr[1];
            finalTime = (int) (Integer.parseInt(min) * 60 * 1000 + Float.parseFloat(sec) * 1000);
        }

        return finalTime;
    }
}
