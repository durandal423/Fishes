package com.example.fishes.model;

import android.content.Context;

import com.example.fishes.R;
import com.example.fishes.view.GameSurface;

public class EnemyFish extends Fish {
    private final EnemyFishType enemyFishType;
    public EnemyFish(Context context, EnemyFishType enemyFishType) {
        super(context, enemyFishType.scaling,
                enemyFishType.resId,
                enemyFishType.resId2,
                (Math.random() < 0.5) ? 0 : GameSurface.SCREEN_WIDTH,
                (float) (Math.random() * context.getResources().getDisplayMetrics().heightPixels));
        this.enemyFishType = enemyFishType;
        float speed = enemyFishType.speed;
        this.vx = x == 0 ? speed: -speed;
        System.out.println("EnemyFish.radius: " + radius);
    }

    public enum EnemyFishType {
        SMALL_FISH(R.drawable.enemy_small, R.drawable.enemy_small2, 50, 0.2f, 1.5f),
        MEDIUM_FISH(R.drawable.enemy_medium, R.drawable.enemy_medium2, 80, 0.2f, 2.0f),
        LARGE_FISH (R.drawable.enemy_larger , R.drawable.enemy_larger2 , 120 , 0.2f , 1.8f),
        SHARK      (R.drawable.enemy_medium , R.drawable.enemy_medium ,180 , 0.2f , 1.5f),
        BOSS_FISH  (R.drawable.enemy_medium  , R.drawable.enemy_medium  ,300 , 0.2f , 1.20f);

        public final int resId;
        public final int resId2;
        public final int experience;
        public final float scaling;
        public final float speed;

        EnemyFishType(int resId, int resId2, int experience, float scaling, float speed) {
            this.resId = resId;
            this.resId2 = resId2;
            this.experience = experience;
            this.scaling = scaling;
            this.speed = speed;
        }
    }

    public EnemyFishType getEnemyFishType() {
        return enemyFishType;
    }
}
