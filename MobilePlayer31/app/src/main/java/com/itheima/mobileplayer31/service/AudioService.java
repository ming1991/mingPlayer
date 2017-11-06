package com.itheima.mobileplayer31.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.itheima.mobileplayer31.R;
import com.itheima.mobileplayer31.bean.AudioItem;
import com.itheima.mobileplayer31.ui.activity.AudioPlayActivity;
import com.itheima.mobileplayer31.ui.activity.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import de.greenrobot.event.EventBus;

/**
 * Created by ThinkPad on 2016/11/27.
 */

public class AudioService extends Service {
    public static final int MODE_ALL = 0;
    public static final int MODE_SINGLE = 1;
    public static final int MODE_RANDOM = 2;

    private static final int FROM_CONTENT = 7;
    private static final int FROM_PRE = 8;
    private static final int FROM_NEXT = 9;
    private int play_mode = 0;
    private ArrayList<AudioItem> audioItems;
    private int position=-2;
    private MediaPlayer mediaPlayer;
    private AudioBinder binder;
    private SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();
        binder = new AudioBinder();
        sp = getSharedPreferences("audio", MODE_PRIVATE);
        play_mode = sp.getInt("mode", 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int from = intent.getIntExtra("from",-1);
        switch (from){
            case FROM_CONTENT:
                binder.notifyUpdateUI();
                break;
            case FROM_PRE:
                binder.playPre();
                break;
            case FROM_NEXT:
                binder.playNext();
                break;
            default:
                int pos = intent.getIntExtra("position", -1);
                if (pos != position) {
                    position = pos;
                    audioItems = (ArrayList<AudioItem>) intent.getSerializableExtra("audioItems");
                    //播放歌曲
                    binder.playItem();
                }else{
//            Toast.makeText(this, ("通知界面更新"), Toast.LENGTH_SHORT).show();
                    binder.notifyUpdateUI();
                }
                break;
        }

        //START_STICKY  粘性 强制杀死service后 尝试重新启动 不传递原来的intent
        //START_STICKY_COMPATIBILITY  START_STICKY的低版本兼容
        //START_NOT_STICKY 非粘性 强制杀死service后 不会尝试重新启动
        //START_REDELIVER_INTENT  强制杀死service后 尝试重新启动 传递原来的intent
//        return super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("执行了onbind");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        System.out.println("执行了onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class AudioBinder extends Binder implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
        public void playItem() {
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            try {
                mediaPlayer.setDataSource(audioItems.get(position).getPath());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //准备完成
        @Override
        public void onPrepared(MediaPlayer mp) {
            //开始播放
            mediaPlayer.start();
            //通知界面更新
            notifyUpdateUI();
            //显示通知
            showNotification();
        }
        //显示通知
        private void showNotification() {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(0,getPersonNotification());
        }
        //自定义notification
        private Notification getPersonNotification() {
            Notification.Builder builder = new Notification.Builder(AudioService.this);
            builder.setSmallIcon(R.drawable.notification_music_playing);
            builder.setTicker("正在播放歌曲"+audioItems.get(position).getDisplay_name());
            builder.setWhen(System.currentTimeMillis());
            builder.setContentIntent(getPendingIntent());
            builder.setOngoing(true);
            builder.setContent(getRemoteViews());//自定义布局的notification
            return builder.getNotification();
        }
        //自定义notification布局
        private RemoteViews getRemoteViews() {
            RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.notification);
            remoteViews.setTextViewText(R.id.notification_title,audioItems.get(position).getDisplay_name());
            remoteViews.setTextViewText(R.id.notification_artist,audioItems.get(position).getArtist());
            remoteViews.setOnClickPendingIntent(R.id.notification_pre,getPrePendingIntent());
            remoteViews.setOnClickPendingIntent(R.id.notification_next,getNextPendingIntent());
            return remoteViews;
        }
        //通知栏点击事件
        private PendingIntent getPendingIntent() {
            Intent intentA = new Intent(AudioService.this, MainActivity.class);
            Intent intentB = new Intent(AudioService.this,AudioPlayActivity.class);
            intentB.putExtra("from",FROM_CONTENT);
            Intent[] intents = new Intent[]{intentA,intentB};
            PendingIntent pendingIntent = PendingIntent.getActivities(AudioService.this,0,intents,PendingIntent.FLAG_UPDATE_CURRENT);
            return pendingIntent;
        }
        //通知栏下一曲点击事件
        private PendingIntent getNextPendingIntent() {
            Intent intent = new Intent(AudioService.this,AudioService.class);
            intent.putExtra("from",FROM_NEXT);
            PendingIntent pendingIntent = PendingIntent.getService(AudioService.this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            return pendingIntent;
        }
        //通知栏上一曲点击事件
        private PendingIntent getPrePendingIntent() {
            Intent intent = new Intent(AudioService.this,AudioService.class);
            intent.putExtra("from",FROM_PRE);
            PendingIntent pendingIntent = PendingIntent.getService(AudioService.this,2,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            return pendingIntent;
        }
        //通知界面更新
        private void notifyUpdateUI() {
            EventBus.getDefault().post(audioItems.get(position));
        }

        //true 当前正在播放 flase 暂停
        public boolean isPlaying() {
            return mediaPlayer.isPlaying();
        }

        //暂停
        public void pause() {
            mediaPlayer.pause();
        }

        //开始播放
        public void start() {
            mediaPlayer.start();
        }

        //获取当前播放进度
        public int getProgress() {
            return mediaPlayer.getCurrentPosition();
        }

        //获取音乐总时长
        public int getDuration() {
            return mediaPlayer.getDuration();
        }

        //跳转到指定位置播放
        public void seekTo(int progress) {
            mediaPlayer.seekTo(progress);
        }

        //播放完成监听回调
        @Override
        public void onCompletion(MediaPlayer mp) {
            //自动播放下一曲
            autoPlayNext();
        }

        //自动播放下一曲
        private void autoPlayNext() {
            switch (play_mode) {
                case MODE_ALL:
//                    if(position==audioItems.size()-1){
//                        position=0;
//                    }else {
//                        position++;
//                    }

                    position = (position + 1) % audioItems.size();
                    break;
                case MODE_SINGLE:
                    break;
                case MODE_RANDOM:
                    position = new Random().nextInt(audioItems.size());
                    break;
            }
            playItem();
        }

        //获取播放模式
        public int getPlayMode() {
            return play_mode;
        }

        //设置播放模式
        public void setMode(int mode) {
            AudioService.this.play_mode = mode;
            //保存当前播放模式
            sp.edit().putInt("mode", play_mode).commit();
        }

        //播放上一曲
        public void playPre() {
            switch (play_mode) {
                case MODE_RANDOM:
                    position = new Random().nextInt(audioItems.size());
                    break;
                default:
                    if (position == 0) {
                        position = audioItems.size() - 1;
                    } else {
                        position--;
                    }
                    break;
            }
            playItem();
        }

        //播放下一曲
        public void playNext() {
            switch (play_mode) {
                case MODE_RANDOM:
                    position = new Random().nextInt(audioItems.size());
                    break;
                default:
                    position = (position + 1) % audioItems.size();
                    break;
            }
            playItem();
        }
        //获取当前播放列表
        public ArrayList<AudioItem> getAudioItems() {
            return audioItems;
        }
        //播放指定位置的歌曲
        public void playPosition(int position) {
            AudioService.this.position = position;
            playItem();
        }
    }
}
