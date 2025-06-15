package com.example.fishes.model;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.fishes.view.GameSurface;
import com.example.fishes.view.JoystickView;

public class PlayerFish extends Fish {
    private JoystickView joystick;
    public static final float[] PLAYER_SCALE = {
            1.00f,
            302f/223f,
            380f/223f,
            490f/223f,
            490f/223f,
            720f/223f
    };
    public void setJoystick(JoystickView joystick) {
        this.joystick = joystick;
    }

    private boolean accelerating = false;


    public PlayerFish(Context context, int resId, int resId2) {
        super(context,0.2f, resId, resId2,
                (float) context.getResources().getDisplayMetrics().widthPixels / 2,
                (float) context.getResources().getDisplayMetrics().heightPixels / 2);
    }

    public void setAccelerating(boolean accelerating) {
        this.accelerating = accelerating;
    }

    public void grow(float factor) {
        scaling *= factor;
        int newWidth = (int) (bitmap_l.getWidth() * factor);
        int newHeight = (int) (bitmap_l.getHeight() * factor);
        bitmap_l = Bitmap.createScaledBitmap(bitmap_l, newWidth, newHeight, true);
        bitmap_r = Bitmap.createScaledBitmap(bitmap_r, newWidth, newHeight, true);
        radius = newWidth / 2;
        System.out.println("Player.radius: "+radius);
    }

    @Override
    public void update() {
        float angle = joystick.getAngle();
        float strength = joystick.getStrength();
        float speed = accelerating ? 10f : 5f;
        vx = (float) (Math.cos(angle) * strength * speed);
        vy = (float) (Math.sin(angle) * strength * speed);

        super.update();

        if (x < radius) {
            x = radius;  // 把位置修正到边界
        } else if (x > GameSurface.SCREEN_WIDTH - radius) {
            x = GameSurface.SCREEN_WIDTH - radius;
        }

        if (y < radius) {
            y = radius;
        } else if (y > GameSurface.SCREEN_HEIGHT - radius) {
            y = GameSurface.SCREEN_HEIGHT - radius;
        }
    }
}
