package com.itheima.mobileplayer31.ui.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.itheima.mobileplayer31.R;
import com.itheima.mobileplayer31.adapter.AudioListAdapter;
import com.itheima.mobileplayer31.bean.AudioItem;
import com.itheima.mobileplayer31.ui.activity.AudioPlayActivity;

import java.util.ArrayList;

/**
 * Created by ThinkPad on 2016/11/24.
 */

public class AudioFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private ListView audioList;
    private AudioListAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_audio;
    }

    @Override
    protected void initData() {
        ContentResolver resolver  = context.getContentResolver();
//        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{
//                MediaStore.Audio.Media._ID,
//                MediaStore.Audio.Media.DATA,
//                MediaStore.Audio.Media.DISPLAY_NAME,
//                MediaStore.Audio.Media.ARTIST
//        }, null, null, null);
        //打印结果
//        CursorUtil.cursorLog(cursor);
        AsyncQueryHandler handler = new AsyncQueryHandler(resolver) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                ((AudioListAdapter)cookie).swapCursor(cursor);
            }
        };
        handler.startQuery(0,adapter, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.ARTIST
        }, null, null, null);
    }

    @Override
    protected void initListener() {
        adapter = new AudioListAdapter(context,null);
        audioList.setAdapter(adapter);
        audioList.setOnItemClickListener(this);
    }

    @Override
    protected void initView() {
        audioList = (ListView) findViewById(R.id.audioList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.changeCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //获取播放列表
        ArrayList<AudioItem> audioItems = AudioItem.getAudioItems((Cursor) parent.getItemAtPosition(position));
        Intent intent = new Intent(context,AudioPlayActivity.class);
        intent.putExtra("audioItems",audioItems);
        intent.putExtra("position",position);
        startActivity(intent);
    }
}
