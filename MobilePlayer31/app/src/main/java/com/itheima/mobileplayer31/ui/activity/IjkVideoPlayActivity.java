package com.itheima.mobileplayer31.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.itheima.mobileplayer31.R;
import com.itheima.mobileplayer31.bean.VideoItem;
import com.itheima.mobileplayer31.util.StringUtil;

import java.util.ArrayList;

import itheima.com.mylibrary.ijkplayer.media.IjkVideoView;
import tv.danmaku.ijk.media.player.IMediaPlayer;
/**
 * Created by ThinkPad on 2016/11/24.
 */

public class IjkVideoPlayActivity extends BaseActivity implements IMediaPlayer.OnPreparedListener, SeekBar.OnSeekBarChangeListener, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnInfoListener, IMediaPlayer.OnErrorListener {
    private static final int MSG_UPDATE_TIME = 0;
    private static final int MSG_UPDATE_PROGRESS = 2;
    private static final int MSG_BUFFER_PROGRESS = 3;
    private static final int MSG_HIDE = 1;
    private IjkVideoView videoView;
    private ImageView video_player_state;
    private TextView video_player_title;
    private BatteryReceiver receiver;
    private ImageView video_player_battery;
    private TextView video_player_time;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_TIME:
                    startUpdateTime();
                    break;
                case MSG_HIDE:
                    hide();
                    break;
                case MSG_UPDATE_PROGRESS:
                    startUpdateProgress();
                    break;
                case MSG_BUFFER_PROGRESS:
                    startUpdateBufferPercent();
                    break;
            }
        }
    };
    private SeekBar video_player_volume_sk;
    private ImageView video_player_mute;
    private int maxVolume;
    private AudioManager audioManager;
    private int markVolume;
    private float startY;
    private int windowH;
    private int windowW;
    private int startVolume;
    private float startAlpha;
    private View video_cover;
    private LinearLayout video_bottom;
    private LinearLayout video_top;
    private int topH;
    private int bottomH;
    private GestureDetector detector;
    private boolean isHide = false;
    private SeekBar video_player_progress_sk;
    private TextView video_player_progress;
    private TextView video_player_duration;
    private int duration;
    private int position;
    private ArrayList<VideoItem> videoItems;
    private VideoItem videoItem;
    private ImageView video_player_next;
    private ImageView video_player_pre;
    private ImageView video_player_screenState;
    private boolean isFullScreen = false;
    private int videoH;
    private int videoW;
    private LinearLayout video_loading;
    private Uri uri;
    private ProgressBar video_buffer;


    @Override
    protected void initData() {
//        VideoItem videoItem = (VideoItem) getIntent().getSerializableExtra("videoItem");
//        videoView.setVideoPath(videoItem.getPath());
//        video_player_title.setText(videoItem.getTitle());
//        videoView.start();
        uri = getIntent().getData();
        if(uri !=null){
            videoView.setVideoURI(uri);
            video_player_title.setText(uri.getPath());
            //显示加载进度
            video_loading.setVisibility(View.VISIBLE);
        }else {
            videoItems = (ArrayList<VideoItem>) getIntent().getSerializableExtra("videoItems");
            position = getIntent().getIntExtra("position", -1);
            playItem();
        }
        //初始化电池电量
//        Intent.ACTION_BATTERY_CHANGED
        receiver = new BatteryReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, filter);

        //更新时间
        startUpdateTime();
        //初始化音量
        initVolume();

        //获取屏幕宽高
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        windowW = point.x;
        windowH = point.y;

        //调节屏幕亮度
        ViewCompat.setAlpha(video_cover, 0f);

        //获取上下栏目高度
        initTopAndBottomHeigth();
    }
    //播放当前位置视频
    private void playItem() {
        //处理上一曲按钮状态
//        if(position==-1||position==0||videoItems==null||videoItems.size()<2){
//            video_player_pre.setEnabled(false);
//        }else {
//            video_player_pre.setEnabled(true);
//        }
        video_player_pre.setEnabled(position!=-1&&position!=0&&videoItems!=null&&videoItems.size()>1);
//        if(position==-1||videoItems==null||videoItems.size()<2){
//            video_player_pre.setEnabled(false);
//            video_player_next.setEnabled(false);
//        }else if(position==0){
//            video_player_pre.setEnabled(false);
//        }else if(position==videoItems.size()-1){
//            video_player_next.setEnabled(false);
//        }

//        if(position==-1||videoItems==null||videoItems.size()<2||position==videoItems.size()-1){
//            video_player_next.setEnabled(false);
//        }else {
//            video_player_next.setEnabled(true);
//        }
        video_player_next.setEnabled(position!=-1&&videoItems!=null&&videoItems.size()>1&&position!=videoItems.size()-1);
        //判断是否能播放
        if(position==-1||videoItems==null||videoItems.size()==0) return;
        //获取videoitem
        videoItem = videoItems.get(position);
        //设置播放路径
        videoView.setVideoPath(videoItem.getPath());
        //设置视频标题
        video_player_title.setText(videoItem.getTitle());
    }

    //获取上下栏目高度
    private void initTopAndBottomHeigth() {
        //getHeight 需要执行完onmeasure和onlayuout之后才能获取
//        int topH = video_top.getHeight();
//        int bottomH = video_bottom.getHeight();
        //第一种：手动测量view高度 布局多层嵌套可能不准确
//        video_top.measure(0,0);
//        video_bottom.measure(0,0);
//        int topH = video_top.getMeasuredHeight();
//        int bottomH = video_bottom.getMeasuredHeight();
//        System.out.println("topH="+topH+",bottomH="+bottomH);
        //第二种：添加布局绘制监听
//        video_top.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                //移除监听
//                video_top.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                //获取高度
//                int topH = video_top.getHeight();
//                System.out.println("topH="+topH);
//            }
//        });
//        video_bottom.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                //移除监听
//                video_bottom.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                //获取高度
//                int bottomH = video_bottom.getHeight();
//                System.out.println("bottomH="+bottomH);
//            }
//        });

        //第三种：
        video_top.post(new Runnable() {
            @Override
            public void run() {
                topH = video_top.getHeight();
                bottomH = video_bottom.getHeight();
            }
        });
    }

    //第四种：onresume之后一段时间
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        int topH = video_top.getHeight();
//        int bottomH = video_bottom.getHeight();
//        System.out.println("topH=" + topH + ",bottomH=" + bottomH);
//    }

    //    @Override
//    protected void onResume() {
//        super.onResume();
//        int topH = video_top.getHeight();
//        int bottomH = video_bottom.getHeight();
//        System.out.println("topH="+topH+",bottomH="+bottomH);
//    }

    private void initVolume() {
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        video_player_volume_sk.setMax(maxVolume);
        int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        updateVolume(volume);
    }

    //更新音量
    private void updateVolume(int volume) {
        video_player_volume_sk.setProgress(volume);
        //更新音量
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);//flag 1 显示系统调节控件 0 不显示
    }

    //时间更新
    private void startUpdateTime() {
        //获取当前时间
        String time = StringUtil.getCurrentTime();
        //设置时间
        video_player_time.setText(time);
        //定时更新
        handler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, 500);
    }

    @Override
    protected void initListener() {
        detector = new GestureDetector(this,new MyGestureListener());

        videoView.setOnPreparedListener(this);
        videoView.setOnInfoListener(this);
        videoView.setOnErrorListener(this);
        videoView.setOnCompletionListener(this);
//        videoView.setOnBufferingUpdateListener(this);
        video_player_state.setOnClickListener(this);
        video_player_volume_sk.setOnSeekBarChangeListener(this);
        video_player_progress_sk.setOnSeekBarChangeListener(this);
        video_player_mute.setOnClickListener(this);
        video_player_pre.setOnClickListener(this);
        video_player_next.setOnClickListener(this);
        video_player_screenState.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        videoView = (IjkVideoView) findViewById(R.id.videoView);
        video_player_state = (ImageView) findViewById(R.id.video_player_state);
        video_player_title = (TextView) findViewById(R.id.video_player_title);
        video_player_battery = (ImageView) findViewById(R.id.video_player_battery);
        video_player_time = (TextView) findViewById(R.id.video_player_time);
        video_player_mute = (ImageView) findViewById(R.id.video_player_mute);
        video_player_volume_sk = (SeekBar) findViewById(R.id.video_player_volume_sk);
        video_cover = findViewById(R.id.video_cover);
        video_top = (LinearLayout) findViewById(R.id.video_top);
        video_bottom = (LinearLayout) findViewById(R.id.video_bottom);
        video_player_duration = (TextView) findViewById(R.id.video_player_duration);
        video_player_progress = (TextView) findViewById(R.id.video_player_progress);
        video_player_progress_sk = (SeekBar) findViewById(R.id.video_player_progress_sk);
        video_player_pre = (ImageView) findViewById(R.id.video_player_pre);
        video_player_next = (ImageView) findViewById(R.id.video_player_next);
        video_player_screenState = (ImageView) findViewById(R.id.video_player_screenState);
        video_loading = (LinearLayout) findViewById(R.id.video_loading);
        video_buffer = (ProgressBar) findViewById(R.id.video_biffer);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ijk_video_player;
    }

    @Override
    protected void processClick(View v) {
        switch (v.getId()) {
            case R.id.video_player_state:
                updatePlayState();
                break;
            case R.id.video_player_mute:
                updateMute();
                break;
            case R.id.video_player_pre:
                playPre();
                break;
            case R.id.video_player_next:
                playNext();
                break;
            case R.id.video_player_screenState:
//                updateScreen();
//                videoView.updateScreen();
//                updateScreenBtn();
//                videoView.setAspectRatio(IRenderView.AR_MATCH_PARENT);
                videoView.switchAspect();
                break;
        }
    }
    //全屏切换
    private void updateScreen() {
        if(isFullScreen) {
            //全屏  设置videoview宽高为记录的宽高
            videoView.getLayoutParams().width = videoW;
            videoView.getLayoutParams().height  = videoH;
        }else {
            //非全屏 记录当前宽度和高度  设置videoview宽高为屏幕宽高
            videoW = videoView.getWidth();
            videoH = videoView.getHeight();

            videoView.getLayoutParams().width = windowW;
            videoView.getLayoutParams().height = windowH;
        }
        //刷新videoview
        videoView.requestLayout();
        //切换标记
        isFullScreen = !isFullScreen;
        //切换图标
        updateScreenBtn();
    }
    //切换全屏图标
    private void updateScreenBtn() {
//        isFullScreen = videoView.isFullScreen();
        if(isFullScreen){
            video_player_screenState.setImageResource(R.drawable.video_full_screen_selector);
        }else {
            video_player_screenState.setImageResource(R.drawable.video_default_screen_selector);
        }
    }

    //播放下一曲
    private void playNext() {
//        if(videoItems!=null&&position!=videoItems.size()-1){
            position++;
            playItem();
//        }
    }
    //播放上一曲
    private void playPre() {
//        if(position!=0) {
            position--;
            playItem();
//        }
    }

    //切换静音状态
    private void updateMute() {
        int streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (streamVolume != 0) {
            //非静音  记录当前音量  设置音量为0
            markVolume = streamVolume;
            updateVolume(0);
        } else {
            //静音状态 设置音量为记录的音量
            updateVolume(markVolume);
        }
    }

    //切换播放状态
    private void updatePlayState() {
        //获取当前播放状态
        boolean playing = videoView.isPlaying();
        //修改播放状态
        if (playing) {
            //暂停
            videoView.pause();
        } else {
            //播放
            videoView.start();
        }
        //更新播放状态的图标
        updatePlayStateBtn();
    }

    //根据当前播放状态设置播放状态图标
    private void updatePlayStateBtn() {
        boolean playing = videoView.isPlaying();
        if (playing) {
            video_player_state.setImageResource(R.drawable.video_play_selector);
            //开启更新进度
            handler.sendEmptyMessageDelayed(MSG_UPDATE_PROGRESS,500);
        } else {
            video_player_state.setImageResource(R.drawable.video_pause_selector);
            //移除定时更新进度
            handler.removeMessages(MSG_UPDATE_PROGRESS);
        }
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        //隐藏加载进度
        video_loading.setVisibility(View.GONE);
        //开启播放
        videoView.start();
        //更新播放状态按钮
        updatePlayStateBtn();
        //定时隐藏
        handler.sendEmptyMessageDelayed(MSG_HIDE,3000);
        //获取总时长
        duration = videoView.getDuration();
        //设置进度条最大值
        video_player_progress_sk.setMax(duration);
        //设置总时长
        video_player_duration.setText(StringUtil.parseDuration(duration));
        //开始更新进度
        startUpdateProgress();
        //开始更新缓冲进度
//        if(uri!=null){
//            startUpdateBufferPercent();
//        }
    }
    //更新缓冲进度
    private void startUpdateBufferPercent() {
        //获取当前缓冲进度
        int bufferPercentage = videoView.getBufferPercentage();
        float percent = bufferPercentage /(float) 100;
        int bufferProgress = (int) (percent*duration);
        //设置缓冲进度
        video_player_progress_sk.setSecondaryProgress(bufferProgress);
        //定时获取缓冲进度
        handler.sendEmptyMessageDelayed(MSG_BUFFER_PROGRESS,500);
    }

    //开始更新进度
    private void startUpdateProgress() {
        //获取当前进度
        int progress = videoView.getCurrentPosition();
        //设置进度
        updateProgress(progress);
        //定时更新
        handler.sendEmptyMessageDelayed(MSG_UPDATE_PROGRESS,500);
    }
    //更新进度数值设置进度
    private void updateProgress(int progress) {
        video_player_progress.setText(StringUtil.parseDuration(progress));
        video_player_progress_sk.setProgress(progress);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //反注册
        unregisterReceiver(receiver);
        //清空发送的消息
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * 进度改变
     *
     * @param seekBar  进度改变的seekbar
     * @param progress 改变后的进度
     * @param fromUser true 用户滑动改变  false 代码改变
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) return;
        if (seekBar.getId() == R.id.video_player_volume_sk) {
            //更新播放音量
            updateVolume(progress);
        }else if(seekBar.getId()==R.id.video_player_progress_sk){
            //跳转到指定位置播放
            videoView.seekTo(progress);
            //更新进度
            updateProgress(progress);
        }
    }

    /**
     * 手指触摸seekbar
     *
     * @param seekBar
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //取消定时隐藏
        handler.removeMessages(MSG_HIDE);
    }

    /**
     * 手指离开
     *
     * @param seekBar
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //开启定时隐藏
        handler.sendEmptyMessageDelayed(MSG_HIDE,3000);
    }
    //视频播放完成
    @Override
    public void onCompletion(IMediaPlayer mp) {
        //停止自动更新
        updatePlayStateBtn();
//        handler.removeMessages(MSG_UPDATE_PROGRESS);
        //手动设置进度为总时长
        updateProgress(duration);
    }
    //缓冲进度改变
    @Override
    public void onBufferingUpdate(IMediaPlayer mp, int percent) {
        float percentage = percent /(float) 100;
        int bufferProgress = (int) (percentage*duration);
        //设置缓冲进度
        video_player_progress_sk.setSecondaryProgress(bufferProgress);
    }

    @Override
    public boolean onInfo(IMediaPlayer mp, int what, int extra) {
        switch (what){
            case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                video_buffer.setVisibility(View.VISIBLE);
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                video_buffer.setVisibility(View.GONE);
                break;
        }
        return true;
    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("视频播放出错");
        builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
        return true;
    }

    class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            updateBatteryBtn(level);
        }
    }

    //更新点亮图标
    private void updateBatteryBtn(int level) {
        if (level < 10) {
            video_player_battery.setImageResource(R.drawable.ic_battery_0);
        } else if (level < 20) {

            video_player_battery.setImageResource(R.drawable.ic_battery_10);
        } else if (level < 40) {
            video_player_battery.setImageResource(R.drawable.ic_battery_20);

        } else if (level < 60) {
            video_player_battery.setImageResource(R.drawable.ic_battery_40);

        } else if (level < 80) {
            video_player_battery.setImageResource(R.drawable.ic_battery_60);

        } else if (level < 100) {
            video_player_battery.setImageResource(R.drawable.ic_battery_80);

        } else if (level == 100) {

            video_player_battery.setImageResource(R.drawable.ic_battery_100);
        }
    }
    //单击 按下和离开时间差 按下和离开x y距离  后续有没有继续的单击事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = event.getY();
//        开始音量=手指按下的音量
                startVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//        开始亮度=手指按下的亮度
//                startAlpha = getWindow().getAttributes().alpha;= video_cover.getAlpha();
                startAlpha = video_cover.getAlpha();
                break;
            case MotionEvent.ACTION_MOVE:
//        滑动距离=开始y-endy
                float offsetY = startY - event.getY();
//        滑动占屏幕百分比=滑动距离/屏幕高度
                float offsetPercent = offsetY / windowH;

                if (event.getX() < windowW / 2) {
//        变化音量=滑动占屏幕高度比*最大音量
                    int offsetVolume = (int) (offsetPercent * maxVolume);
//        最终音量=开始音量+变化音量
                    int finalVolume = startVolume + offsetVolume;
                    //设置音量
                    updateVolume(finalVolume);
                } else {
                    //更新亮度
//        变化亮度=滑动占屏幕高度比*最大亮度
//        最终亮度=开始亮度-变化亮度
                    float finalAlpha = startAlpha - offsetPercent;
                    if (finalAlpha >= 0 && finalAlpha <= 1) {
                        updateAlpha(finalAlpha);
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    //调节遮罩亮度
    private void updateAlpha(float finalAlpha) {
        ViewCompat.setAlpha(video_cover, finalAlpha);
    }

    //调节亮度
    public void updateBrith(float finalAlpha) {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = finalAlpha;
        getWindow().setAttributes(params);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            showOrHide();
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
//            videoView.updateScreen();
            updateScreen();
            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            updatePlayState();
        }

        //        @Override
//        public boolean onDoubleTapEvent(MotionEvent e) {
//            System.out.println("onDoubleTapEvent");
//            return super.onDoubleTapEvent(e);
//        }
        //        @Override
//        public boolean onSingleTapUp(MotionEvent e) {
//            System.out.println("onSingleTapUp");
//            return super.onSingleTapUp(e);
//        }
    }
    //显示和隐藏上下栏目
    private void showOrHide() {
        if(isHide){
            //显示
            show();
        }else {
            //隐藏
            hide();
        }
    }

    private void hide() {
        ViewCompat.animate(video_top).translationY(-topH);
        ViewCompat.animate(video_bottom).translationY(bottomH);
        isHide = true;
        //取消定时隐藏
        handler.removeMessages(MSG_HIDE);
    }

    private void show() {
        //以原点确定
        ViewCompat.animate(video_top).translationY(0);
        ViewCompat.animate(video_bottom).translationY(0);
        //以当前点确定
//        ViewCompat.animate(video_top).translationYBy(topH);
//        ViewCompat.animate(video_bottom).translationYBy(-bottomH);
        isHide = false;
        //定义隐藏
        handler.sendEmptyMessageDelayed(MSG_HIDE,3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //停止播放
        videoView.stopPlayback();
    }
}
