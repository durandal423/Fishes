package com.example.fishes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fishes.R;

public class GameOverActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        int score = getIntent().getIntExtra("score", 0);

        TextView txtScore = findViewById(R.id.txt_score);
        txtScore.setText("得分：" + score);

        Button btnRestart = findViewById(R.id.btn_restart);
        Button btnExit = findViewById(R.id.btn_main_menu);

        btnRestart.setOnClickListener(view -> {
            // 重新开始游戏
            Intent intent = new Intent(GameOverActivity.this, GameActivity.class);
            startActivity(intent);
            finish();
        });

        btnExit.setOnClickListener(view -> {
            // 退出到主菜单
            Intent intent = new Intent(GameOverActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
