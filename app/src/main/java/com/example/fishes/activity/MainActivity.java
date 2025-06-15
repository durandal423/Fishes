package com.example.fishes.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fishes.R;

public class MainActivity extends AppCompatActivity {
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 获取传入的用户名
        username = getIntent().getStringExtra("username");
        
        TextView title = findViewById(R.id.title);
        TextView welcomeText = findViewById(R.id.welcome_text);
        Button btnStart = findViewById(R.id.btn_start);
        Button btnExit = findViewById(R.id.btn_exit);
        Button btnLeaderboard = findViewById(R.id.btn_leaderboard);
        Button btnLogout = findViewById(R.id.btn_logout);

        // 显示欢迎文本
        welcomeText.setText("欢迎，" + username + "！");

        DisplayMetrics dm = getResources().getDisplayMetrics();
        float screenWidthPx = dm.widthPixels;
        float scaledDensity = dm.scaledDensity;

        float titleSizeSp = pxToSp(screenWidthPx * 0.08f, scaledDensity);      // 标题：8%
        float buttonSizeSp = pxToSp(screenWidthPx * 0.05f, scaledDensity);     // 按钮：5%
        float welcomeSizeSp = pxToSp(screenWidthPx * 0.04f, scaledDensity);    // 欢迎文本：4%

//        title.setTextSize(titleSizeSp);
//        welcomeText.setTextSize(welcomeSizeSp);
//        btnStart.setTextSize(buttonSizeSp);
//        btnExit.setTextSize(buttonSizeSp);
//        btnLeaderboard.setTextSize(buttonSizeSp);
//        btnLogout.setTextSize(buttonSizeSp);

        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        btnExit.setOnClickListener(v -> finish());

        btnLeaderboard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LeaderboardActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            // 清除登录状态
            SharedPreferences preferences = getSharedPreferences("game_prefs", MODE_PRIVATE);
            preferences.edit().clear().apply();
            
            // 返回登录页面，但不清除其他任务
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // 只结束当前Activity
        });
    }

    private float pxToSp(float px, float scaledDensity) {
        return px / scaledDensity;
    }
}