package com.example.eyeprotection;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class optometry extends AppCompatActivity {

    private ImageView EPicture;
    private TextView result;
    private Button blur;
    private float rst = 4.0f;

    // 记录图片E的旋转角度顺序，也就是检测顺序
    // 这里做个规定，方向对应的旋转角度如下：右-0.0f，下-90.0f，上--90.0f，左-180.0f
    private float[] rotations = {0.0f,90.0f,-90.0f,180.0f,0.0f,180.0f,0.0f,-90.0f};
    // 记录图片E的尺寸大小，尺寸也对应着眼睛度数，这里的尺寸没有经过研究，先暂时用着，单位为dp。
    // todo: 更改E尺寸大小关系
    private float[] size = {100,85,70,55,45,35,25,15};
    private int current_picture = 0;    // 记录当前是第几张图片,从0开始


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optometry);
        EPicture = findViewById(R.id.optometry_E);
        result = findViewById(R.id.optometry_result);
        blur = findViewById(R.id.optometry_blur);
        ImageButton down = findViewById(R.id.optometry_down);
        ImageButton up = findViewById(R.id.optometry_up);
        ImageButton left = findViewById(R.id.optometry_left);
        ImageButton right = findViewById(R.id.optometry_right);

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_click_function(4);
            }
        });

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_click_function(3);
            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_click_function(2);
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_click_function(1);
            }
        });

        blur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 这里简化处理，点击看不清按钮后就是最终结果，可以弹出提示框显示最终得分，然后跳转到首页
                // todo: 更改加强检测逻辑
                AlertDialog.Builder builder = new AlertDialog.Builder(optometry.this);
                builder.setTitle("提示");
                String msg = "您的视力度数为："+result.getText();
                builder.setMessage(msg);
                builder.setPositiveButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击返回按钮返回到主页面
                        Intent intent = new Intent();
                        intent.setClass(optometry.this,homePage.class);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("重来", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击按钮重新测试,关闭对话框
                        dialog.dismiss();
                        Intent intent = new Intent();
                        intent.setClass(optometry.this,optometry.class);
                        startActivity(intent);
                    }
                });
                builder.show();
            }
        });


    }

    /**
     * @param type:按钮的类型，1-向右，2-向左，3-向下，4-向上
     */
    private void button_click_function(int type) {
    /*
                需要先判断点击按钮的类型和图片的朝向是否一致。
                这里检测视力的机制有一点复杂，在图片较大的情况下，点错就噶，直接给出最终得分然后结束换一只眼，点对继续。
                在图片较小的情况下，点错三次结束，点对三次继续。
                并在点击后更改图片尺寸和朝向以及分数。
                 */
        if(consistent(type)){
            current_picture++;
            // 设置旋转角度
            EPicture.setRotation(rotations[current_picture]);
            // 设置图片大小
            ViewGroup.LayoutParams params = EPicture.getLayoutParams();
            float newWidthInDp = size[current_picture];
            float newHeightInDp = size[current_picture];
            float scale = getResources().getDisplayMetrics().density;
            params.width = (int)(newWidthInDp*scale+0.5f);
            params.height = (int)(newHeightInDp*scale+0.5f);
            EPicture.setLayoutParams(params);
            // 更新结果分数
            rst+=0.1f;
            String formattedValue = String.format("%.1f",rst);
            result.setText(formattedValue);
        }
        else{

        }
    }

    /**
     * @param type:按钮的类型，1-向右，2-向左，3-向下，4-向上
     */
    private boolean consistent(int type) {
        float rotation = EPicture.getRotation();
        boolean rst = true;
        switch (type){
            case 1:
                rst = rotation == 0.0f;
                break;
            case 2:
                rst = rotation == 180.0f;
                break;
            case 3:
                rst = rotation == 90.0f;
                break;
            case 4:
                rst = rotation == -90.0f;
                break;
        }
        return rst;


    }
}