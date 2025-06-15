package com.example.fishes.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fishes.R;
import com.example.fishes.manager.SoundManager;
import com.example.fishes.view.GameSurface;
import com.example.fishes.view.JoystickView;

public class GameActivity extends AppCompatActivity {
    private GameSurface gameSurface;
    private JoystickView joystickView;
    private SoundManager soundManager;
    private String username;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // 获取用户名
        username = getIntent().getStringExtra("username");

        joystickView = findViewById(R.id.joystick);
        gameSurface = findViewById(R.id.game_surface);

        gameSurface.setJoystick(joystickView);

        soundManager = new SoundManager(this);
        soundManager.startBackgroundMusic();

        findViewById(R.id.btn_boost).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                gameSurface.getPlayer().setAccelerating(true);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                gameSurface.getPlayer().setAccelerating(false);
            }
            return true;
        });

        // 1. 退出按钮调用公用方法
        findViewById(R.id.btn_quit).setOnClickListener(view -> {
            int score = gameSurface.getScore();
            exitToGameOver(score);
        });

        // 2. 把 listener 也指向同一个公用方法
        gameSurface.setGameOverListener(finalScore -> {
            // GameSurface 触发是在子线程，切回主线程执行
            runOnUiThread(() -> exitToGameOver(finalScore));
        });
    }

    /** 停止一切并跳转到 GameOverActivity */
    private void exitToGameOver(int score) {
        // 关闭线程和音效
        gameSurface.stopThread();
        soundManager.stopAllSounds();
        soundManager.stopBackgroundMusic();

        // 跳转并传分数和用户名
        Intent intent = new Intent(this, GameOverActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameSurface.stopThread();
        soundManager.stopAllSounds();
        soundManager.stopBackgroundMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        soundManager.startBackgroundMusic();
        gameSurface.startThread();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameSurface.stopThread();
        soundManager.stopAllSounds();
        soundManager.release();
    }
}