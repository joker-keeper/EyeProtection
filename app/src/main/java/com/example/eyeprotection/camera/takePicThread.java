package com.example.eyeprotection.camera;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/*
* 该线程用来拍照，具体包括相机初始化，相机拍照，照片存储等
* */
public class takePicThread extends Thread{
    // private photoQuene photos;
    private mcamera camera;
    private static final String TAG = "slient_camera";
    int cnt = 0;

//    public takePicThread(photoQuene q){
//        photos = q;
//        camera = new mcamera();
//    }
    public takePicThread(){
        camera = new mcamera();
    }

    @Override
    public void run() {
        super.run();
        // 相机初始化
        camera.onCreate();
        // 这里循环条件直接设为true了，因为相机拍照是和整个线程的生命周期一致的，这个线程不会说中途停下来做其他的
//        while(true){
//            try {
//                if(photos.isFull()){
//                    Log.e(TAG,"photos is full");
//                    break;
//                }
//                Log.e(TAG,"photos isn't full");
//                Bitmap photo = camera.takePicture();
//                photos.addPhoto(photo);
//                Log.e(TAG,"will sleep 500ms");
//                Thread.sleep(500);
//                Log.e(TAG,"have sleep 500ms");
//            }catch (InterruptedException e){
//                e.printStackTrace();
//                break;
//            }
//        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                camera.openCamera();
                if(photoQuene.isFull()){
                    Log.e(TAG,"photos is full");
                }else{
                    Log.e(TAG,"photos isn't full");
                    byte[] photo = camera.takePicture();
                    photoQuene.addPhoto(photo);
                    //photos.addPhoto(photo);
                    // String name = "img" + cnt + ".jpg";
                    // cnt++;
                    // camera.savePicture(name);
                }
            }
        },0,1000);
    }
}
