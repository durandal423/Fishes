package com.example.fishes.model;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.fishes.view.GameSurface;
import com.example.fishes.view.JoystickView;

public class PlayerFish extends Fish {
    private JoystickView joystick;

    public void setJoystick(JoystickView joystick) {
        this.joystick = joystick;
    }

    private boolean accelerating = false;


    public PlayerFish(Context context, int resId) {
        super(context, resId,
                (float) context.getResources().getDisplayMetrics().widthPixels / 2,
                (float) context.getResources().getDisplayMetrics().heightPixels / 2,
                5); // 初始化速度5
    }

    public void setAccelerating(boolean accelerating) {
        this.accelerating = accelerating;
    }

    @Override
    public void update() {
        float angle = joystick.getAngle();
        float strength = joystick.getStrength();
        float speed = accelerating ? 10f : 5f;
        vx = (float) (Math.cos(angle) * strength * speed);
        vy = (float) (Math.sin(angle) * strength * speed);
        if (x < radius) {
            x = radius;  // 把位置修正到边界
            vx = 0;
        } else if (x > GameSurface.SCREEN_WIDTH - radius) {
            x = GameSurface.SCREEN_WIDTH - radius;
            vx = 0;
        }

        if (y < radius) {
            y = radius;
            vy = 0;
        } else if (y > GameSurface.SCREEN_HEIGHT - radius) {
            y = GameSurface.SCREEN_HEIGHT - radius;
            vy = 0;
        }
        super.update();
    }
}
