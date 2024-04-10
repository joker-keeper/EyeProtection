package com.example.eyeprotection;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Matrix;
import android.media.MediaParser;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainExercise extends AppCompatActivity {
    private ImageView arrow;
    private ObjectAnimator translationX;
    private ObjectAnimator rotation;
    private ImageButton exitBtn;
    private ImageButton pauseBtn;
    private ImageButton replayBtn;
    private ObjectAnimator anim1,anim2,anim3,anim4,anim_rotate;
    private boolean isAnimRun = true;

    AnimatorSet animSet = new AnimatorSet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_exercise);
        arrow = findViewById(R.id.arrow_right);
        exitBtn = findViewById(R.id.exitBtn);
        pauseBtn = findViewById(R.id.pauseBtn);
        replayBtn = findViewById(R.id.rePlayBtn);

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击暂停按钮时暂停播放动画,并切换按钮状态为播放
                if (isAnimRun){
                    animSet.pause();
                    isAnimRun = false;
                    pauseBtn.setBackgroundResource(R.drawable.play);
                }else{
                    // 点击播放按钮时重新播放动画，并切换按钮状态为暂停
                    animSet.resume();
                    isAnimRun = true;
                    pauseBtn.setBackgroundResource(R.drawable.pause);
                }
            }
        });

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击退出按钮时停止动画，返回主页
                animSet.end();
                Intent intent = new Intent();
                intent.setClass(MainExercise.this,homePage.class);
                startActivity(intent);
            }
        });

        replayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击重播按钮重新播放动画,将播放按钮设置为暂停样式
                pauseBtn.setBackgroundResource(R.drawable.pause);
                isAnimRun = true;
                animSet.end();
                arrow = findViewById(R.id.arrow_right);
                anim1.setTarget(arrow);
                animSet.start();
            }
        });
//        // 控制箭头移动的动画
//        translationX = ObjectAnimator.ofFloat(arrow,"translationX",0f,600f);
//        translationX.setDuration(2000);
//        translationX.setRepeatCount(1);
//        translationX.setInterpolator(new AccelerateDecelerateInterpolator());
//
//        // 控制箭头旋转的动画
//        rotation = ObjectAnimator.ofFloat(arrow,"rotation",0f,180f);
//        rotation.setInterpolator(new LinearInterpolator());
//        rotation.setDuration(500);

//        // 监听器控制当箭头移动到最后时从头来过
//        translationX.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                arrow.setTranslationX(0f);
//            }
//        });

//        // 延迟2秒后显示箭头
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // 显示箭头
//                ObjectAnimator animator = ObjectAnimator.ofFloat(arrow, "alpha", 0f, 1f);
//                animator.setDuration(1000); // 设置动画持续时间
//                animator.start();
//
//                // 将ImageView的可见性设置为可见
//                arrow.setVisibility(View.VISIBLE);
//            }
//        },2000);

//        Animation l2r = AnimationUtils.loadAnimation(this,R.anim.arrow_left2right);
//        arrow.startAnimation(l2r);
        // 进入眼部运动环节，控制箭头运动
       eyeExercise();
//       MediaPlayer mediaPlayer = MediaPlayer.create(MainExercise.this,R.raw.backend_exercise);
//       mediaPlayer.start();
    }

    private void eyeExercise() {
        /*
        眼部运动即按箭头方向运动，箭头按一定规律旋转角度指引眼部运动方向，中途搭配文字提醒。
        客户端拍摄照片传送到服务端等待眼动运动结果，如果方向和箭头方向一致，进入下一环节，否则给出提醒。
         */
        anim1 = (ObjectAnimator) AnimatorInflater.loadAnimator(MainExercise.this,R.animator.arrow_left2right);
        anim2 = (ObjectAnimator) AnimatorInflater.loadAnimator(MainExercise.this,R.animator.arrow_down);
        anim3 = (ObjectAnimator) AnimatorInflater.loadAnimator(MainExercise.this,R.animator.arrow_right2left);
        anim4 = (ObjectAnimator) AnimatorInflater.loadAnimator(MainExercise.this,R.animator.arrow_up);
        anim_rotate = (ObjectAnimator) AnimatorInflater.loadAnimator(MainExercise.this,R.animator.arrow_rotation);
        anim1.setTarget(arrow);
        animSet.playSequentially(anim1,anim2,anim3,anim4,anim_rotate);

        anim1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                arrow.setAlpha(1f);
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                arrow.setAlpha(0f);
                if (judgeWhetherConsistent()) {
                    // 进行下一步
                    arrow = findViewById(R.id.arrow_down);
                    anim2.setTarget(arrow);
                }else{
                    System.out.print("not Consistent!");
                    // todo: 添加具体提示措施
                }
            }
        });
        anim2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                arrow.setAlpha(1f);
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                arrow.setAlpha(0f);
                if (judgeWhetherConsistent()) {
                    // 进行下一步
                    arrow = findViewById(R.id.arrow_left);
                    anim3.setTarget(arrow);
                }else{
                    System.out.print("not Consistent!");
                    // todo: 添加具体提示措施
                }
            }
        });
        anim3.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                arrow.setAlpha(1f);
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                arrow.setAlpha(0f);
                if (judgeWhetherConsistent()) {
                    // 进行下一步
                    arrow = findViewById(R.id.arrow_up);
                    anim4.setTarget(arrow);
                }else{
                    System.out.print("not Consistent!");
                    // todo: 添加具体提示措施
                }
            }
        });
        anim4.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                arrow.setAlpha(1f);
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                arrow.setAlpha(0f);
                if (judgeWhetherConsistent()) {
                    arrow = findViewById(R.id.arrow_center);
                    anim_rotate.setTarget(arrow);
                }else{
                    System.out.print("not Consistent!");
                    // todo: 添加具体提示措施
                }
            }
        });
        anim_rotate.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                arrow.setAlpha(1f);
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                arrow.setAlpha(0f);
                if (judgeWhetherConsistent()) {

                }else{
                    System.out.print("not Consistent!");
                    // todo: 添加具体提示措施
                }
            }
        });
        animSet.start();
//        while (true){
//            // 第一步：向右运动
//            translationX = (ObjectAnimator) AnimatorInflater.loadAnimator(MainExercise.this,R.animator.arrow_left2right);
//            translationX.setTarget(arrow);
//            translationX.start();
//            translationX.addListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationStart(Animator animation) {
//                    super.onAnimationStart(animation);
//                    arrow.setAlpha(1f);
//                }
//
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    super.onAnimationEnd(animation);
//                    arrow.setAlpha(0f);
//                    arrow = findViewById(R.id.arrow_down);
//                    translationX = (ObjectAnimator) AnimatorInflater.loadAnimator(MainExercise.this,R.animator.arrow_down);
//                    translationX.setTarget(arrow);
//                    translationX.start();
//                    translationX.addListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationStart(Animator animation) {
//                            super.onAnimationStart(animation);
//                            arrow.setAlpha(1f);
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            super.onAnimationEnd(animation);
//                            arrow.setAlpha(0f);
//                            translationX.end();
//                        }
//                    });
//                }
//            });
//
//        }

    }


    private boolean judgeWhetherConsistent() {
        /*
        判断用户眼动是否和箭头指示方向一致
         */
        return true;
    }
}