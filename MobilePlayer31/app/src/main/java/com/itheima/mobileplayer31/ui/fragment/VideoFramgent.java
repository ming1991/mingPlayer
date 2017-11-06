package com.itheima.mobileplayer31.ui.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.itheima.mobileplayer31.R;
import com.itheima.mobileplayer31.adapter.VideoListAdapter;
import com.itheima.mobileplayer31.bean.VideoItem;
import com.itheima.mobileplayer31.ui.activity.IjkVideoPlayActivity;
import com.itheima.mobileplayer31.util.CursorUtil;

import java.util.ArrayList;

/**
 * Created by ThinkPad on 2016/11/24.
 */

public class VideoFramgent extends BaseFragment implements AdapterView.OnItemClickListener {

    private ListView videoList;
    private VideoListAdapter adapter;

    //    private Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what){
//                case 0:
//                    Cursor cursor = (Cursor) msg.obj;
//                    //打印cursor
//                    CursorUtil.cursorLog(cursor);
//                    break;
//            }
//        }
//    };
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_video;
    }

    @Override
    protected void initData() {
        //获取sd卡上的视频列表数据
        final ContentResolver resolver = context.getContentResolver();
//        Cursor cursor = resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{
//                MediaStore.Video.Media.DATA,
//                MediaStore.Video.Media.TITLE,
//                MediaStore.Video.Media.DURATION,
//                MediaStore.Video.Media.SIZE
//        }, null, null, null);
        //打印cursor
//        CursorUtil.cursorLog(cursor);
        //thread
//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                Cursor cursor = resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{
//                        MediaStore.Video.Media.DATA,
//                        MediaStore.Video.Media.TITLE,
//                        MediaStore.Video.Media.DURATION,
//                        MediaStore.Video.Media.SIZE
//                }, null, null, null);
//                Message msg  = Message.obtain();
//                msg.what = 0;
//                msg.obj = cursor;
//                handler.sendMessage(msg);
//            }
//        }.start();
        //AsyncTask 异步任务
//        new MyTask().execute(resolver);
        //AsyncQueryHandler
        AsyncQueryHandler handler = new AsyncQueryHandler(resolver) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                //打印cursor
                //修改数据源
                //再刷新notifyDataSetChanged
                ((VideoListAdapter) cookie).swapCursor(cursor);
            }
        };
        handler.startQuery(0, adapter, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE
        }, null, null, null);
    }

    @Override
    protected void initListener() {
        adapter = new VideoListAdapter(context, null);
        videoList.setAdapter(adapter);

        videoList.setOnItemClickListener(this);
    }

    @Override
    protected void initView() {
        videoList = (ListView) findViewById(R.id.videoList);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //获取Videoitem
//        View childAt = parent.getChildAt(position);
//        VideoItem videoItem = VideoItem.getVideoItem((Cursor) parent.getItemAtPosition(position));
        //获取播放列表
        ArrayList<VideoItem> videoItems = VideoItem.getVideoItems((Cursor) parent.getItemAtPosition(position));
        Intent intent = new Intent(context,IjkVideoPlayActivity.class);
        intent.putExtra("videoItems",videoItems);
        intent.putExtra("position",position);
        startActivity(intent);
    }

    class MyTask extends AsyncTask<ContentResolver, Void, Cursor> {
        //子线程
        @Override
        protected Cursor doInBackground(ContentResolver... params) {
            Cursor cursor = params[0].query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.TITLE,
                    MediaStore.Video.Media.DURATION,
                    MediaStore.Video.Media.SIZE
            }, null, null, null);
            return cursor;
        }

        //ui线程
        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            //打印cursor
            CursorUtil.cursorLog(cursor);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //关闭cursor
        adapter.changeCursor(null);
    }
}
