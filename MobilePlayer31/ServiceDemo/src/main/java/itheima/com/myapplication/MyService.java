package itheima.com.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by ThinkPad on 2016/11/27.
 */

public class MyService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("onCreate");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("onBind");
        return new MyBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        System.out.println("onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("onDestroy");
    }

    public void sayHello(){
        System.out.println("hello");
    }

    class MyBinder extends Binder{
        public void sayHelloPro(){
            sayHello();
        }
    }
}

