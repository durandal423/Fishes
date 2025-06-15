package com.example.fishes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fishes.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView title = findViewById(R.id.title);
        Button btnStart = findViewById(R.id.btn_start);
        Button btnExit = findViewById(R.id.btn_exit);
        Button btnLeaderboard = findViewById(R.id.btn_leaderboard);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        float screenWidthPx = dm.widthPixels;
        float scaledDensity = dm.scaledDensity;

        float titleSizeSp = pxToSp(screenWidthPx * 0.08f, scaledDensity);      // 标题：8%
        float buttonSizeSp = pxToSp(screenWidthPx * 0.05f, scaledDensity);     // 按钮：5%

        title.setTextSize(titleSizeSp);
        btnStart.setTextSize(buttonSizeSp);
        btnExit.setTextSize(buttonSizeSp);
        btnLeaderboard.setTextSize(buttonSizeSp);

        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
        });

        btnExit.setOnClickListener(v -> finish());

        btnLeaderboard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LeaderboardActivity.class);
            startActivity(intent);
        });
    }

    private float pxToSp(float px, float scaledDensity) {
        return px / scaledDensity;
    }
}