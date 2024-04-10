package com.example.eyeprotection;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class arrowView extends View {
    private Paint paint;
    private Path path;
    private float rotateDegree = 0;
    public arrowView(Context context) {
        super(context);
        init();
    }

    public arrowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(android.R.color.black));

        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        float startX = width / 4f;
        float startY = height / 2f;
        float endX = width * 3f / 4f;
        float endY = height / 2f;

        // 绘制直线
        canvas.drawLine(startX, startY, endX, endY, paint);

        // 绘制箭头头部
        float arrowSize = 20;
        float arrowAngle = (float) Math.toRadians(45);
        float arrowX = endX - arrowSize * (float) Math.cos(arrowAngle);
        float arrowY = endY - arrowSize * (float) Math.sin(arrowAngle);

        path.reset();
        path.moveTo(endX, endY);
        path.lineTo(arrowX, arrowY);
        path.lineTo(arrowX - arrowSize * (float) Math.cos(arrowAngle - Math.PI / 2),
                arrowY - arrowSize * (float) Math.sin(arrowAngle - Math.PI / 2));
        path.lineTo(arrowX - arrowSize * (float) Math.cos(arrowAngle + Math.PI / 2),
                arrowY - arrowSize * (float) Math.sin(arrowAngle + Math.PI / 2));
        path.close();

        canvas.drawPath(path, paint);
    }

    public void setRotationDegrees(float degrees) {
        rotateDegree = degrees;
        invalidate();
    }
}
