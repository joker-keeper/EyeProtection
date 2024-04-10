package com.example.eyeprotection.camera;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.eyeprotection.R;

/*
    这个服务是在后台运行的，我希望他能处理相机拍照以及照片处理等，又因为服务是在主线程上的，为了不影响性能，另起两个线程。
 */
public class SlientCamera extends Service {
    private static final String TAG = "slient_camera";
    private takePicThread takePicthread;
    // private photoQuene photos = new photoQuene();
    private static final int NOTIFICATION_ID = 123;
    private static final String CHANNEL_ID = "my_channel_id";
    private String channelName = "takePicService";
    private ProcessImgThread processImgThread;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"slientcamera,oncreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"slientcamera,onStartCommand()");
//        // 创建通知渠道（适用于 Android 8.0 及以上版本）
//        createNotificationChannel();
//        Notification notification = createNotification();
//        // 将服务提升为前台服务
//        startForeground(NOTIFICATION_ID, notification);
//        takePicthread = new takePicThread(photos);
//        Log.i(TAG,"takePicThread will start.");
//        takePicthread.start();
//        Log.i(TAG,"takePicThread has start");
//        return START_STICKY;

//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext()).setAutoCancel(true);// 点击后让通知将消失
//        mBuilder.setContentText("测试");
//        mBuilder.setContentTitle("测试");
//        mBuilder.setSmallIcon(R.drawable.ic_launcher_foreground);
//        mBuilder.setWhen(System.currentTimeMillis());//通知产生的时间，会在通知信息里显示
//        mBuilder.setPriority(Notification.PRIORITY_DEFAULT);//设置该通知优先级
//        mBuilder.setOngoing(false);//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
//        mBuilder.setDefaults(Notification.DEFAULT_ALL);//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            String channelId = "channelId" + System.currentTimeMillis();
//            NotificationChannel channel = new NotificationChannel(channelId, getResources().getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
//            manager.createNotificationChannel(channel);
//            mBuilder.setChannelId(channelId);
//        }
//        mBuilder.setContentIntent(null);
//        startForeground(222, mBuilder.build());
//        //takePicthread = new takePicThread(photos);
//        Log.i(TAG,"takePicThread will start.");
//        //takePicthread.start();
//        Log.i(TAG,"takePicThread has start");
//
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 创建一个 NotificationChannel

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,channelName, NotificationManager.IMPORTANCE_DEFAULT);
            // 自定义设置通知声音、震动等
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200});
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            // 构建一个 Notification
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("视力保护app正在拍照")
                    .setContentText("服务正在运行...")
                    .setSmallIcon(R.drawable.baseline_double_arrow)
                    .build();
            // 启动前台服务
            // 通知栏标识符 前台进程对象唯一SERVICE_ID
            startForeground(NOTIFICATION_ID, notification);
            // 开启拍照线程
            takePicthread = new takePicThread();
            takePicthread.start();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // 开启处理图片线程
            processImgThread = new ProcessImgThread();
            processImgThread.start();
        } else {
            startService(intent); // API < 26 直接启动服务即可
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Camera Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("My Foreground Service")
                .setContentText("Service is running in the foreground")
                .setSmallIcon(R.drawable.ic_launcher_foreground);

        return builder.build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"slientcamera,onDestroy()");
        if(takePicthread!=null){
            takePicthread.interrupt();
        }
    }

}
