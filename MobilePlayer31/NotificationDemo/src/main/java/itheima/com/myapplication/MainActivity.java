package itheima.com.myapplication;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

public class MainActivity extends Activity {

    private NotificationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String from = getIntent().getStringExtra("from");
        if(from!=null){
            Toast.makeText(this, from, Toast.LENGTH_SHORT).show();
        }
    }
    public void click(View view){
        switch (view.getId()){
            case R.id.show:
                showNotification();
                break;
            case R.id.hide:
                hideNotification();
                break;
        }
    }
    //隐藏通知
    private void hideNotification() {
        manager.cancel(0);
    }

    //显示通知
    private void showNotification() {
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0,getPersonNotification());
    }
    //自定义notification
    private Notification getPersonNotification() {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.notification_music_playing);
        builder.setTicker("正在播放歌曲红日");
        builder.setWhen(System.currentTimeMillis());
//        builder.setContentTitle("红日");
//        builder.setContentText("李克勤");
        builder.setContentIntent(getPendingIntent());
        builder.setOngoing(true);
        builder.setContent(getRemoteViews());//自定义布局的notification
        return builder.getNotification();
    }
    //自定义notification布局
    private RemoteViews getRemoteViews() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.notification);
        remoteViews.setTextViewText(R.id.notification_title,"红日");
        remoteViews.setTextViewText(R.id.notification_artist,"李克勤");
        remoteViews.setOnClickPendingIntent(R.id.notification_pre,getPrePendingIntent());
        remoteViews.setOnClickPendingIntent(R.id.notification_next,getNextPendingIntent());
        return remoteViews;
    }


    //3.0后创建notification
    private Notification getNewNotification() {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.notification_music_playing);
        builder.setTicker("正在播放歌曲红日");
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle("红日");
        builder.setContentText("李克勤");
        builder.setContentIntent(getPendingIntent());
        builder.setOngoing(true);
        return builder.getNotification();
    }

    //3.0前创建notification
    private Notification getNotification() {
        Notification notification = new Notification(R.drawable.notification_music_playing,"正在播放歌曲哈哈",System.currentTimeMillis());
        notification.setLatestEventInfo(this,"北京","汪峰",getPendingIntent());
        notification.flags = Notification.FLAG_ONGOING_EVENT;//不能滑动隐藏通知
        return notification;
    }
    //通知栏点击事件
    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("from","从通知栏主体点击进来");
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
    //通知栏下一曲点击事件
    private PendingIntent getNextPendingIntent() {
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("from","从通知栏下一曲点击进来");
        PendingIntent pendingIntent = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
    //通知栏上一曲点击事件
    private PendingIntent getPrePendingIntent() {
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("from","从通知栏上一曲点击进来");
        PendingIntent pendingIntent = PendingIntent.getActivity(this,2,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
}
