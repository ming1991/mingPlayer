package com.itheima.mobileplayer31.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.itheima.mobileplayer31.R;
import com.itheima.mobileplayer31.adapter.PopListAdapter;
import com.itheima.mobileplayer31.ui.activity.AudioPlayActivity;

/**
 * Created by ThinkPad on 2016/11/28.
 */

public class PlayListPop extends PopupWindow {
    public PlayListPop(Context context, PopListAdapter adapter, AdapterView.OnItemClickListener onItemClickListener){
        View view = LayoutInflater.from(context).inflate(R.layout.play_list,null,false);
        final ListView pop_list = (ListView) view.findViewById(R.id.pop_list);
        pop_list.setAdapter(adapter);
        pop_list.setOnItemClickListener(onItemClickListener);
        //设置布局
        setContentView(view);
        //设置宽度和高度
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置popwindow能获取焦点
        setFocusable(true);
        //设置背景 响应后退按钮
        setBackgroundDrawable(new BitmapDrawable());
        //设置显示和隐藏动画
        setAnimationStyle(R.style.pop);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getY()<pop_list.getTop()){
                    dismiss();
                }
                return true;
            }
        });
    }
}
