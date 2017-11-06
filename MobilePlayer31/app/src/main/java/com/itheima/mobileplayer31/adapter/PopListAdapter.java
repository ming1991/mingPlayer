package com.itheima.mobileplayer31.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.itheima.mobileplayer31.R;
import com.itheima.mobileplayer31.bean.AudioItem;

import java.util.ArrayList;

/**
 * Created by ThinkPad on 2016/11/28.
 */

public class PopListAdapter extends BaseAdapter {
    private ArrayList<AudioItem> audioItems = new ArrayList<AudioItem>();
    public void updateItems(ArrayList<AudioItem> audioItems){
        this.audioItems.clear();
        this.audioItems.addAll(audioItems);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return audioItems.size();
    }

    @Override
    public AudioItem getItem(int position) {
        return audioItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pop_item,parent,false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.pop_item_title.setText(audioItems.get(position).getDisplay_name());
        holder.pop_item_artist.setText(audioItems.get(position).getArtist());
        return convertView;
    }
    class ViewHolder{

        private final TextView pop_item_artist;
        private final TextView pop_item_title;

        public ViewHolder(View view){
            pop_item_title = (TextView) view.findViewById(R.id.pop_item_title);
            pop_item_artist = (TextView) view.findViewById(R.id.pop_item_artist);
        }
    }
}
