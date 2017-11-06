package com.itheima.mobileplayer31.ui.activity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.itheima.mobileplayer31.R;
import com.itheima.mobileplayer31.adapter.PopListAdapter;
import com.itheima.mobileplayer31.bean.AudioItem;
import com.itheima.mobileplayer31.service.AudioService;
import com.itheima.mobileplayer31.util.StringUtil;
import com.itheima.mobileplayer31.view.LyricView;
import com.itheima.mobileplayer31.view.PlayListPop;

import de.greenrobot.event.EventBus;

/**
 * Created by ThinkPad on 2016/11/27.
 */

public class AudioPlayActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener, AdapterView.OnItemClickListener, LyricView.OnProgressChangeListener {
    private static final int MSG_UPDATE_PROGRESS = 0;
    private static final int MSG_PLAY_LYRIC = 1;
    private AudioService.AudioBinder binder;
    private ImageView audio_player_state;
    private TextView audio_player_artist;
    private TextView audio_player_title;
    private ImageView audio_player_osc;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_UPDATE_PROGRESS:
                    startUpdateProgress();
                    break;
                case MSG_PLAY_LYRIC:
                    startPlayLyric();
                    break;
            }
        }
    };
    private int duration;
    private TextView audio_player_progress;
    private SeekBar audio_player_progress_sk;
    private ImageView audio_player_mode;
    private ImageView audio_player_pre;
    private ImageView audio_player_next;
    private AudioConnection connection;
    private ImageView audio_player_list;
    private LyricView audio_playe_lyric;

    //定义eventbus方法
    public void onEventMainThread(AudioItem audioItem){
        //更新播放状态按钮
        updatePlayStateBtn();
        //更新歌曲名
        audio_player_title.setText(audioItem.getDisplay_name());
        //更新歌手名称
        audio_player_artist.setText(audioItem.getArtist());
        //开启示波器
        startOsc();
        //获取当前的总时长
        duration = binder.getDuration();
        //设置进度条最大值
        audio_player_progress_sk.setMax(duration);
        //更新进度
        startUpdateProgress();
        //更新播放模式
        updatePlayModeBtn();
        //设置歌词文件
        audio_playe_lyric.setFile(audioItem.getDisplay_name());
        //歌词播放
        startPlayLyric();
    }
    //开始播放歌词
    private void startPlayLyric() {
        //获取当前进度
        int progress = binder.getProgress();
        //播放歌词
        audio_playe_lyric.playLyric(progress,duration);
        //定时获取
        handler.sendEmptyMessage(MSG_PLAY_LYRIC);
    }

    //开始更新进度
    private void startUpdateProgress() {
        //获取当前进度
        int progress = binder.getProgress();
        //设置进度
        updateProgress(progress);
        //定时更新
        handler.sendEmptyMessageDelayed(MSG_UPDATE_PROGRESS,500);
    }
    //更新进度
    private void updateProgress(int progress) {
        audio_player_progress.setText(StringUtil.parseDuration(progress)+"/"+StringUtil.parseDuration(duration));
        //设置进度条
        audio_player_progress_sk.setProgress(progress);
    }

    //开启示波器
    private void startOsc() {
        //设置background
//        AnimationDrawable background = (AnimationDrawable) audio_player_osc.getBackground();
        //设置src
        AnimationDrawable drawable = (AnimationDrawable) audio_player_osc.getDrawable();
        drawable.start();
    }

    @Override
    protected void initData() {
        //注册EventBus
        EventBus.getDefault().register(this);

//        ArrayList<AudioItem> audioItems = (ArrayList<AudioItem>) getIntent().getSerializableExtra("audioItems");
//        int position = getIntent().getIntExtra("position", -1);

        //先stasrtService 再bindservice
//        Intent intent = new Intent(this, AudioService.class);
//        intent.putExtra("audioItems",audioItems);
//        intent.putExtra("position",position);
        //第二种创建intent
        Intent intent = new Intent(getIntent());
        intent.setClass(this,AudioService.class);
        connection = new AudioConnection();
        bindService(intent, connection, Service.BIND_AUTO_CREATE);
        startService(intent);
    }

    @Override
    protected void initListener() {
        audio_player_state.setOnClickListener(this);
        audio_player_progress_sk.setOnSeekBarChangeListener(this);
        audio_player_mode.setOnClickListener(this);
        audio_player_pre.setOnClickListener(this);
        audio_player_next.setOnClickListener(this);
        audio_player_list.setOnClickListener(this);
        audio_playe_lyric.setOnProgressChangeListener(this);
    }

    @Override
    protected void initView() {
        audio_player_state = (ImageView) findViewById(R.id.audio_player_state);
        audio_player_title = (TextView) findViewById(R.id.audio_player_title);
        audio_player_artist = (TextView) findViewById(R.id.audio_player_artist);
        audio_player_osc = (ImageView) findViewById(R.id.audio_player_osc);
        audio_player_progress = (TextView) findViewById(R.id.audio_player_progress);
        audio_player_progress_sk = (SeekBar) findViewById(R.id.audio_player_progress_sk);
        audio_player_mode = (ImageView) findViewById(R.id.audio_player_mode);
        audio_player_pre = (ImageView) findViewById(R.id.audio_player_pre);
        audio_player_next = (ImageView) findViewById(R.id.audio_player_next);
        audio_player_list = (ImageView) findViewById(R.id.audio_player_list);
        audio_playe_lyric = (LyricView) findViewById(R.id.audio_playe_lyric);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_audio_player;
    }

    @Override
    protected void processClick(View v) {
        switch (v.getId()){
            case R.id.audio_player_state:
                updatePlayState();
                break;
            case R.id.audio_player_mode:
                updatePlayMode();
                break;
            case R.id.audio_player_pre:
                binder.playPre();
                break;
            case R.id.audio_player_next:
                binder.playNext();
                break;
            case R.id.audio_player_list:
                showPlayList();
                break;
        }
    }
    //显示播放列表
    private void showPlayList() {
        PopListAdapter adapter = new PopListAdapter();
        adapter.updateItems(binder.getAudioItems());
        PlayListPop pop = new PlayListPop(this,adapter,this);
        pop.showAtLocation(audio_player_title, Gravity.CENTER,0,0);
    }

    //更新播放模式 全部循环-单曲循环-随机播放
    private void updatePlayMode() {
        //获取当前播放模式
        int mode = binder.getPlayMode();
        //切换模式
        binder.setMode((mode+1)%(AudioService.MODE_RANDOM+1));
        //切换模式图标
        updatePlayModeBtn();
    }
    //设置播放模式图标
    private void updatePlayModeBtn() {
        //获取当前播放模式
        int mode = binder.getPlayMode();
        //设置
        switch (mode){
            case AudioService.MODE_ALL:
                audio_player_mode.setImageResource(R.drawable.audio_all_selector);
                break;
            case AudioService.MODE_SINGLE:
                audio_player_mode.setImageResource(R.drawable.audio_single_selector);
                break;
            case AudioService.MODE_RANDOM:
                audio_player_mode.setImageResource(R.drawable.audio_random_selector);
                break;
        }
    }

    //切换播放状态
    private void updatePlayState() {
        //获取当前播放状态
        boolean isPlaying = binder.isPlaying();
        //切换
        if(isPlaying){
            binder.pause();
        }else {
            binder.start();
        }
        //切换播放状态按钮图标
        updatePlayStateBtn();
    }
    //根据播放状态切换图标
    private void updatePlayStateBtn() {
        System.out.println("updatebtn的binder="+binder);
        //获取当前播放状态
        boolean playing = binder.isPlaying();

        //切换
        if(playing){
            audio_player_state.setImageResource(R.drawable.audio_play_selector);
            //开启更新
            handler.sendEmptyMessageDelayed(MSG_UPDATE_PROGRESS,500);
        }else {
            audio_player_state.setImageResource(R.drawable.audio_pause_selector);
            //停止更新
            handler.removeMessages(MSG_UPDATE_PROGRESS);
        }
    }

    /**
     * 进度更新
     * @param seekBar
     * @param progress  更新后的进度
     * @param fromUser true 用户操作 false 代码操作
     **/
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(!fromUser) return;
        if(seekBar.getId()==R.id.audio_player_progress_sk){
            //跳转到progress位置播放
            binder.seekTo(progress);
            //更新进度
            updateProgress(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
    //播放列表条目点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        binder.playPosition(position);
    }

    @Override
    public void onProgressChange(int progress) {
        //跳转到制定位置播放
        binder.seekTo(progress);
        //更新播放进度条
        updateProgress(progress);
    }

    class AudioConnection implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (AudioService.AudioBinder) service;
            System.out.println("binder="+binder);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //反注册EventBus
        EventBus.getDefault().unregister(this);

        handler.removeCallbacksAndMessages(null);
    }
}
