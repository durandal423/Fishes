package com.example.fishes.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;


public class JoystickView extends View {
    private Paint basePaint;
    private Paint hatPaint;
    private int baseRadius;
    private int hatRadius;
    private float centerX, centerY;
    private float hatX, hatY;
    private float touchX, touchY;
    private float angle;   // 摇杆角度
    private float strength; // 摇杆力度（0~1）

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        basePaint.setColor(Color.GRAY);
        hatPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hatPaint.setColor(Color.DKGRAY);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        centerX = w / 2f;
        centerY = h / 2f;
        baseRadius = (int) (Math.min(w, h) / 2f * 0.6); // 基座半径
        hatRadius = (int) (baseRadius * 0.5);         // 摇杆半径
        // 初始摇杆位置在中心
        hatX = centerX;
        hatY = centerY;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        // 绘制摇杆基座
        canvas.drawCircle(centerX, centerY, baseRadius, basePaint);
        // 绘制摇杆柄
        canvas.drawCircle(hatX, hatY, hatRadius, hatPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchX = event.getX();
        touchY = event.getY();
        float dx = touchX - centerX;
        float dy = touchY - centerY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            if (distance < baseRadius) {
                // 手指在摇杆基座范围内，直接将杆设置在手指位置
                hatX = touchX;
                hatY = touchY;
            } else {
                // 超出基座，则将杆限制在圆周上
                hatX = centerX + (dx / distance) * baseRadius;
                hatY = centerY + (dy / distance) * baseRadius;
            }
            // 计算当前角度和力度
            angle = (float) Math.atan2(dy, dx);
            strength = Math.min(distance, baseRadius) / baseRadius;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            // 手指离开，摇杆复位
            hatX = centerX;
            hatY = centerY;
            angle = 0;
            strength = 0;
        }

        invalidate();
        return true;
    }

    // 获取当前角度（弧度）
    public float getAngle() {
        return angle;
    }

    // 获取当前力度（0~1）
    public float getStrength() {
        return strength;
    }
}
