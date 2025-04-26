package com.example.fishes.model;

import android.content.Context;

import com.example.fishes.view.GameSurface;

public class EnemyFish extends Fish {
    public EnemyFish(Context context, int resId, float speed) {
        super(context, resId,
                0,
                (float) (Math.random() * context.getResources().getDisplayMetrics().heightPixels),
                speed);
        this.vx = speed;
        this.vy = (float) ((Math.random() - 0.5) * 2 * speed);
    }
}
