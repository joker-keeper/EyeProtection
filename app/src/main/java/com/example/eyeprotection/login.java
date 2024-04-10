package com.example.eyeprotection;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class login extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // 检查用户是否授予相应权限：拍照和读写文件
        if(!hasPermissions()){
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            },REQUEST_PERMISSIONS);
        }else{
            Toast.makeText(this, "Permissions have", Toast.LENGTH_SHORT).show();
        }

        Button login_btn = findViewById(R.id.login_btn);
        Button lg_signup_btn = findViewById(R.id.lg_signup_btn);
        // 登录成功跳转到主页
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(login.this,homePage.class);
                startActivity(intent);
            }
        });
        //点击注册按钮跳转到注册页面
        lg_signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(login.this,signUp.class);
                startActivity(intent);
            }
        });
    }

    private boolean hasPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            // Check if all permissions are granted
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                // 这里先简单跳转到主页面


            } else {
                // Permissions denied, show message and close app
                Toast.makeText(this, "Permissions denied. Closing app.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
