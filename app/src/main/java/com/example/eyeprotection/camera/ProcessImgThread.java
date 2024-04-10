package com.example.eyeprotection.camera;

import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/*
这个线程是用来处理队列中的图片，将图片发送给服务器
 */
public class ProcessImgThread extends Thread{
    private String ServerUrl = "http://10.11.54.79:11010/upload";
    private OkHttpClient client = new OkHttpClient();
    private static final String TAG = "slient_camera_processImg";


    @Override
    public void run() {
        super.run();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            byte[] imgData;
            int cnt = 0;
            @Override
            public void run() {
                if(photoQuene.isEmpty()){
                    Log.i(TAG,"images is empty.");
                }else{
                    Log.i(TAG,"will send img");
                    imgData = photoQuene.getPhoto();
                    RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"),imgData);
                    Request request = new Request.Builder()
                            .url(ServerUrl)
                            .post(requestBody)
                            .build();
                    // 这里我们发送图片不关心发送的结果如何，因为图片帧很多，不缺一两帧
                    // 这个发送照片的过程是异步的，不影响我们的使用
                    Log.i(TAG,"have send img");
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e(TAG,"image send failed",e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                // 返回码201表示有凝视点坐标数据返回
                                if (response.code() == 201){
                                    Log.i(TAG,"response code == 201");
                                    // 解析服务端返回的数据，获取坐标信息
                                    if(response.body() == null){
                                        Log.e(TAG,"response body is null");
                                        response.body().close();
                                        return;
                                    }
                                    Log.i(TAG,"response body isn't null");
                                    String responseData = response.body().string();
                                    Log.i(TAG,responseData);
                                    JSONObject responseDataJson; // 服务端返回的是一个数组[1, 2]
                                    double x,y;
                                    try {
                                        responseDataJson = new JSONObject(responseData);
                                        JSONArray data = responseDataJson.getJSONArray("data");
                                        JSONArray coordinates = data.getJSONArray(0); // 获取坐标数组
                                        x = coordinates.getDouble(0); // 获取 x 坐标
                                        y = coordinates.getDouble(1); // 获取 y 坐标
                                    } catch (JSONException e) {
                                        Log.e(TAG,"jsonExceprtion",e);
                                        throw new RuntimeException(e);
                                    }
                                    Log.i(TAG,"addgazepoint");
                                    // 注意这里的点是指距离摄像头的二维平面内的点，而不是像素点，因此应该是double类型
                                    gazePointQueue.addGazePoint(x,y);
                                    String msg = "x:"+x+",y:"+y;
                                    Log.i("slient_camera_gazePoint",msg);
                                }else{
                                    Log.i(TAG,"response code == 200");
                                }
                                  } else {
                                      // 请求失败处理
                                      Log.e(TAG, "Request failed with code: " + response.code());
                                  }
                                  response.body().close();
                        }
                    });
                }
            }
        },0,1000);

    }
}
