package com.example.fishes.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import com.example.fishes.view.GameSurface;

public class Fish {
    private boolean facingLeft = true;
    protected float x, y;
    protected float vx, vy;
    protected float scaling;
    protected int radius;
    protected Bitmap bitmap_l, bitmap_r;
    protected Context context;

    public Fish(Context context, float scaling, int resId, int resId2, float initX, float initY) {
        this.context = context;
        this.scaling = scaling;
        bitmap_l = BitmapFactory.decodeResource(context.getResources(), resId);
        bitmap_r = BitmapFactory.decodeResource(context.getResources(), resId2);

        int scaledWidth = (int) (bitmap_l.getWidth() * scaling);
        int scaledHeight = (int) (bitmap_l.getHeight() * scaling);
        bitmap_l = Bitmap.createScaledBitmap(bitmap_l, scaledWidth, scaledHeight, true);
        bitmap_r = Bitmap.createScaledBitmap(bitmap_r, scaledWidth, scaledHeight, true);

        this.radius = scaledWidth / 2;
        this.x = initX;
        this.y = initY;
        // 随机或预设速度方向，默认向右
        this.vx = 0;
        this.vy = 0;
    }

    public void setScaling(float scaling) {
        this.scaling = scaling;
    }

    public void draw(Canvas canvas) {
        if (vx < 0) {
            facingLeft = true;
        } else if (vx > 0) {
            facingLeft = false;
        }

        if (facingLeft) {
            canvas.drawBitmap(bitmap_l, x - radius, y - radius, null);
        } else {
            canvas.save();
            canvas.drawBitmap(bitmap_r, x - radius, y - radius, null);
            canvas.restore();
        }
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
        return this.radius >= other.radius;
    }

    public boolean isOutOfScreen() {
        return x < -radius || x > GameSurface.SCREEN_WIDTH + radius
                || y < -radius || y > GameSurface.SCREEN_HEIGHT + radius;
    }
}
