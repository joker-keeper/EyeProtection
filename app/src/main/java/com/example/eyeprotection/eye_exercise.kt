package com.example.eyeprotection

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class EyeExercise : AppCompatActivity(){

    lateinit var viewFinder: PreviewView
    val TAG = "CameraX"
    private val REQ_CODE:Int = 1
    private lateinit var cameraExecutor: ExecutorService
    var cameraProvider: ProcessCameraProvider? = null  // 相机信息
    var preview: Preview? =null // 预览对象
    var cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA // 前置摄像头默认
    var camera : Camera? = null // 相机对象
    private var imageCapture: ImageCapture? = null  // 拍照用例

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eye_exercise)

        viewFinder = findViewById(R.id.viewFinder)
        initData()
//        if (allPermissionsGranted()){
//            startCamera()
//        }else{
//            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
//        }

        val button: Button = findViewById<Button>(R.id.btn_photo)
        button.setOnClickListener{
            takephoto()
        }

    }

    private fun initData() {
        // 检查相机权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            println("===>需要权限")
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                println("===>shouldShowRequestPermissionRationale")
            } else {
                println("===>not shouldShowRequestPermissionRationale")
            }
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.CAMERA),REQ_CODE)
        } else {
            println("===>已经获取到了权限")
            startCamera();
        }

    }

//    //权限类型
//    companion object{
//        private const val REQUEST_CODE_PERMISSIONS = 10
//        private val REQUIRED_PERMISSIONS = arrayOf(
//            Manifest.permission.CAMERA,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.RECORD_AUDIO
//        )
//    }
//
//    // 判断是否有所有权限
//    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all{
//        ContextCompat.checkSelfPermission(baseContext,it) == PackageManager.PERMISSION_GRANTED
//    }
//
//    //请求权限返回函数
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_CODE_PERMISSIONS){
//            if (allPermissionsGranted()){
//                startCamera()
//            }else{
//                Toast.makeText(this,"未开启权限",Toast.LENGTH_SHORT).show()
//                finish()
//            }
//        }
//    }

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera()
            println("权限获取成功")
        } else {
            println("你拒绝了权限")
        }
    }


    private fun startCamera() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            cameraProvider = cameraProviderFuture.get()

            // 预览配置
            preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }
            // 拍照用例接口
            imageCapture = ImageCapture.Builder().build()
            // 使用前置摄像头
            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            try {
                cameraProvider?.unbindAll()
                camera = cameraProvider?.bindToLifecycle(
                    this,cameraSelector,preview,imageCapture
                )
            }catch (exc: Exception){
                Log.e(TAG,"Use case binding failed",exc)
            }
        },ContextCompat.getMainExecutor(this))
    }

    private fun takephoto(){
        val outputImage = File(externalCacheDir, "output_image.jpg")
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(outputImage).build()
        imageCapture?.takePicture(outputFileOptions,ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(error: ImageCaptureException)
                {
                    Log.e(TAG, "Photo capture failed: ${error.message}", error)
                }
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(outputImage)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            })
    }

}