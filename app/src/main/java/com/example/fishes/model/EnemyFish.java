package com.example.fishes.model;

import android.content.Context;

import com.example.fishes.R;
import com.example.fishes.view.GameSurface;

public class EnemyFish extends Fish {
    private final EnemyFishType enemyFishType;
    public EnemyFish(Context context, EnemyFishType enemyFishType) {
        super(context, enemyFishType.scaling, enemyFishType.resId,
                (Math.random() < 0.5) ? 0 : GameSurface.SCREEN_WIDTH,
                (float) (Math.random() * context.getResources().getDisplayMetrics().heightPixels));
        this.enemyFishType = enemyFishType;
        float speed = enemyFishType.speed;
        this.vx = x == 0 ? speed: -speed;
    }

    public enum EnemyFishType {
        SMALL_FISH(R.drawable.enemy_small, 50, 0.2f, 1.5f);

        public final int resId;
        public final int experience;
        public final float scaling;
        public final float speed;

        EnemyFishType(int resId, int experience, float scaling, float speed) {
            this.resId = resId;
            this.experience = experience;
            this.scaling = scaling;
            this.speed = speed;
        }
    }

    public EnemyFishType getEnemyFishType() {
        return enemyFishType;
    }
}
