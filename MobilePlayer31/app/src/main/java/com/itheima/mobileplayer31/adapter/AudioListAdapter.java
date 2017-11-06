package com.itheima.mobileplayer31.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itheima.mobileplayer31.R;
import com.itheima.mobileplayer31.bean.AudioItem;

/**
 * Created by ThinkPad on 2016/11/27.
 */

public class AudioListAdapter extends CursorAdapter {
    public AudioListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    public AudioListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public AudioListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.audio_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        AudioItem audioItem = AudioItem.getAudioItem(cursor);

        holder.audio_item_name.setText(audioItem.getDisplay_name());
        holder.audio_item_artist.setText(audioItem.getArtist());
    }
    class ViewHolder{

        private final TextView audio_item_artist;
        private final TextView audio_item_name;

        public ViewHolder(View view){
            audio_item_name = (TextView) view.findViewById(R.id.audio_item_name);
            audio_item_artist = (TextView) view.findViewById(R.id.audio_item_artist);
        }
    }
}
