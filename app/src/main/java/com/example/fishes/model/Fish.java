package com.example.fishes.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.example.fishes.view.GameSurface;

public class Fish {
    protected float x, y;
    protected float vx, vy;
    protected int radius;
    protected Bitmap bitmap;
    protected Context context;

    public Fish(Context context, int resId, float initX, float initY, float speed) {
        this.context = context;
        bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        this.radius = bitmap.getWidth() / 2; // 简化：假设图片宽度等于直径
        this.x = initX;
        this.y = initY;
        // 随机或预设速度方向，默认向右
        this.vx = speed;
        this.vy = 0;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x - (float) bitmap.getWidth() /2, y - (float) bitmap.getHeight() /2, null);
    }

    public void update() {
        x += vx;
        y += vy;
    }

    // 圆形检测碰撞
    public boolean collidesWith(Fish other) {
        float dx = x - other.x;
        float dy = y - other.y;
        float distanceSq = dx * dx + dy * dy;
        float radiusSum = this.radius + other.radius;
        return distanceSq < radiusSum * radiusSum;
    }

    public boolean isLargerThan(Fish other) {
        return this.radius > other.radius;
    }

    public boolean isOutOfScreen() {
        return x < -radius || x > GameSurface.SCREEN_WIDTH + radius
                || y < -radius || y > GameSurface.SCREEN_HEIGHT + radius;
    }
}
