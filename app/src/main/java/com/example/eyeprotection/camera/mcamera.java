package com.example.eyeprotection.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.graphics.SurfaceTexture;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Semaphore;

public class mcamera implements SurfaceTexture.OnFrameAvailableListener {
    private static final String TAG = "slient_camera";
    private SurfaceTexture surfaceTexture;
    private Camera mcamera;
    private byte[] photoData;
    Semaphore semaphore = new Semaphore(0);
    int cnt = 0;
    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {

    }
    public mcamera(){}

    public void onCreate(){
        Log.i(TAG,"SlientCamera-surface, onCreate()");
        surfaceTexture = new SurfaceTexture(10);
        surfaceTexture.setOnFrameAvailableListener(this);
    }

    public boolean openCamera(){
        mcamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        if (mcamera == null){
            Log.e(TAG,"openCamera,mCamera == null");
            return false;
        }
        mcamera.setDisplayOrientation(0);
        Camera.Parameters params = mcamera.getParameters();
        params.setPreviewFormat(ImageFormat.NV21);
        params.setZoom(0);
        mcamera.setParameters(params);
        try{
            mcamera.setPreviewTexture(surfaceTexture);
            mcamera.startPreview();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
        if (mcamera == null){
            Log.e(TAG,"openCamera,mCamera == null");
            return false;
        }
        Log.e(TAG,"openCamera succeed");
        return true;
    }

    public byte[] takePicture(){
        if(mcamera == null){
            Log.e(TAG,"takePicture(),camera == null");
            return null;
        }
        Log.i(TAG,"takepic will do");
        mcamera.takePicture(null,null,mPictureCallback);
        Log.e(TAG,"semaphore.acquire()");
        try{
            semaphore.acquire();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        Log.i(TAG,"will do data");
//        Bitmap bitmap = BitmapFactory.decodeByteArray(photoData, 0, photoData.length);
//        Matrix matrix = new Matrix();
//        matrix.preRotate(-90);
//        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//        Log.i(TAG,"take picture completed");
        return photoData;
    }

    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.i(TAG,"mPictureCallback");
            photoData = data;
            semaphore.release();
            Log.e(TAG,"semaphore release()");
        }
    };

    public void savePicture(String filename) {
        // 将得到的照片进行270°旋转，使其竖直
        Bitmap bitmap = BitmapFactory.decodeByteArray(photoData, 0, photoData.length);
        Matrix matrix = new Matrix();
        matrix.preRotate(-90);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), filename);
        cnt++;
        try {
            // 创建输出流
            FileOutputStream fos = new FileOutputStream(file);

            // 将 Bitmap 压缩成 JPEG 格式，并写入输出流
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            // 关闭输出流
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        String msg = "savePicture\t" + filename + "\tsucceed";
        Log.e(TAG,msg);
//        mcamera.stopPreview();
//        mcamera.release();
//        mcamera = null;
    }

}
