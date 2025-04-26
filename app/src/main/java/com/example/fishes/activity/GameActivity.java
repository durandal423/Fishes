package com.example.fishes.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fishes.R;
import com.example.fishes.manager.SoundManager;
import com.example.fishes.view.GameSurface;
import com.example.fishes.view.JoystickView;

public class GameActivity extends AppCompatActivity {
    private GameSurface gameSurface ;
    private JoystickView joystickView;

    private Button btnBoost, btnQuit;
    private SoundManager soundManager;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        joystickView = findViewById(R.id.joystick);
        gameSurface = findViewById(R.id.game_surface);

        gameSurface.setJoystick(joystickView);

        btnBoost = findViewById(R.id.btn_boost);
        btnQuit = findViewById(R.id.btn_quit);

        soundManager = new SoundManager(this);
        soundManager.startBackgroundMusic();

        btnBoost.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                gameSurface.getPlayer().setAccelerating(true);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                gameSurface.getPlayer().setAccelerating(false);
            }
            return true;
        });

        btnQuit.setOnClickListener(view -> {
            int score = gameSurface.getScore(); // 获取当前分数
            // 停止背景音乐
            soundManager.stopBackgroundMusic();
            // 跳转到游戏结束界面，传递分数
            Intent intent = new Intent(GameActivity.this, GameOverActivity.class);
            intent.putExtra("score", score);
            startActivity(intent);
            finish();
        });

        gameSurface.setGameOverListener(finalScore -> {
            Intent intent = new Intent(GameActivity.this, GameOverActivity.class);
            intent.putExtra("score", finalScore);
            startActivity(intent);
            finish(); // 结束当前GameActivity
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        soundManager.stopBackgroundMusic();
        gameSurface.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        soundManager.startBackgroundMusic();
        gameSurface.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundManager.release();
    }
}
