package com.itheima.mobileplayer31.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itheima.mobileplayer31.R;
import com.itheima.mobileplayer31.bean.VideoItem;
import com.itheima.mobileplayer31.util.StringUtil;

/**
 * Created by ThinkPad on 2016/11/24.
 */

public class VideoListAdapter extends CursorAdapter{
    public VideoListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    public VideoListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public VideoListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }
    // 解析xml生成view cursor游标已经移动到当前条目的position
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }
    //view值得初始化
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        VideoItem videoItem = VideoItem.getVideoItem(cursor);

        holder.video_item_name.setText(videoItem.getTitle());
        holder.video_item_duration.setText(StringUtil.parseDuration(videoItem.getDuration()));
        holder.video_item_size.setText(Formatter.formatFileSize(context,videoItem.getSize()));
    }
    class ViewHolder{

        private final TextView video_item_size;
        private final TextView video_item_duration;
        private final TextView video_item_name;

        public ViewHolder(View view){
            video_item_name = (TextView) view.findViewById(R.id.video_item_name);
            video_item_duration = (TextView) view.findViewById(R.id.video_item_duration);
            video_item_size = (TextView) view.findViewById(R.id.video_item_size);
        }
    }
}
