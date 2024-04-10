package com.example.eyeprotection;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.eyeprotection.camera.SlientCamera;
import com.example.eyeprotection.camera.mcamera;

import com.example.eyeprotection.camera.photoQuene;
import com.example.eyeprotection.camera.takePicThread;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class homePage extends AppCompatActivity {

    private Thread takePicThread;
    private photoQuene photos = new photoQuene();
    private SlientCamera cameraService = new SlientCamera();
    private static final String TAG = "slient_camera";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Button btn_setting = findViewById(R.id.eyeProtection_setting);
        SwitchMaterial gazeSwitch = findViewById(R.id.switchButton);



        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                  Thread thread = new Thread(new Runnable() {
//                      @Override
//                      public void run() {
//                          OkHttpClient client = new OkHttpClient();
//                          byte[] imageData = null;
//                          // 获取资源的ID
//                          int resourceId = getResources().getIdentifier("play", "drawable", getPackageName());
//
//// 根据资源ID获取图片数据流
//                          InputStream inputStream = getResources().openRawResource(resourceId);
//
//// 将输入流转换为 byte[] 数据
//                          ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//                          byte[] buffer = new byte[1024];
//                          int length;
//                          try {
//                              while ((length = inputStream.read(buffer)) != -1) {
//                                  outputStream.write(buffer, 0, length);
//                              }
//                              imageData = outputStream.toByteArray();
//                          } catch (IOException e) {
//                              e.printStackTrace();
//                          } finally {
//                              try {
//                                  inputStream.close();
//                                  outputStream.close();
//                              } catch (IOException e) {
//                                  e.printStackTrace();
//                              }
//                          }
//
//                          if(imageData == null){
//                              Log.e(TAG,"img is null");
//                          }
//
//                          RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"),imageData);
//                          Request request = new Request.Builder()
//                                  .url("http://10.11.54.79:11010/upload")
//                                  .post(requestBody)
//                                  .build();
//                          Log.i(TAG,"have send img");
//                          client.newCall(request).enqueue(new Callback() {
//                              @Override
//                              public void onFailure(Call call, IOException e) {
//                                  Log.e(TAG,"image send failed",e);
//                              }
//
//                              @Override
//                              public void onResponse(Call call, Response response) throws IOException {
//                                  if (response.isSuccessful()) {
//                                      // 请求成功处理
//                                      Log.i(TAG, "Request successful");
//                                  } else {
//                                      // 请求失败处理
//                                      Log.e(TAG, "Request failed with code: " + response.code());
//                                  }
//                                  response.body().close();
//                              }
//                          });
//
//                      }
//                  });
//                  thread.start();
                Intent intent = new Intent();
                intent.setClass(homePage.this,eyeSetting.class);
                startActivity(intent);
            }
        });
        gazeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
//                    // 启动了眼动模式，此时开辟新的线程只用于后续照相机启动拍摄等等
//                    initCameraThread = new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            // 初始化相机并拍照
//                            try {
//                                Thread.sleep(3000);
//                            } catch (InterruptedException e) {
//                                throw new RuntimeException(e);
//                            }
//                            mcamera mcamera = new mcamera();
//                            mcamera.onCreate();
//                            mcamera.openCamera();
//                            mcamera.takePicture();
//                            // Toast.makeText(homePage.this, "have take picture.", Toast.LENGTH_LONG).show();
//                        }
//                    });
//                    initCameraThread.start();
                    Log.i(TAG,"startService");
                    if (ContextCompat.checkSelfPermission(homePage.this, android.Manifest.permission.FOREGROUND_SERVICE)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Permission not yet granted, request it
                        Log.i(TAG,"permission not have");
                        ActivityCompat.requestPermissions(homePage.this,
                                new String[]{Manifest.permission.FOREGROUND_SERVICE},
                                100);
                    }
                    if (ContextCompat.checkSelfPermission(homePage.this, android.Manifest.permission.FOREGROUND_SERVICE)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Permission not yet granted, request it
                        Log.i(TAG,"permission not have");
                    }

                    Intent intent = new Intent(homePage.this,SlientCamera.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    }else{
                        startService(intent);
                    }

                } else{
                    // 关闭眼动模式，删除之前开辟的线程(如果有)
                    if(takePicThread!=null){
                        takePicThread.interrupt();//中断线程
                        takePicThread = null;
                    }
                }
            }
        });
    }
}