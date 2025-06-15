package com.example.fishes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fishes.R;
import com.example.fishes.model.LeaderboardDao;

import java.util.List;

public class GameOverActivity extends AppCompatActivity {
    private LeaderboardDao dao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        dao = new LeaderboardDao(this);
        int score = getIntent().getIntExtra("score", 0);

        // 获取屏幕宽度和缩放密度
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float screenWidth = dm.widthPixels;
        float scaledDensity = dm.scaledDensity;

        // 动态计算字体大小（以屏宽为基准）
        float largeTextSp = screenWidth * 0.08f / scaledDensity; // 标题文字
        float normalTextSp = screenWidth * 0.05f / scaledDensity; // 一般文字
        int elementWidth = (int) (screenWidth * 0.55f); // 输入框和按钮宽度

        TextView txtScore = findViewById(R.id.tv_score);
        txtScore.setText("得分：" + score);
        txtScore.setTextSize(normalTextSp);
        setViewWidth(txtScore, elementWidth);

        // 查询当前分数排名
        List<LeaderboardDao.PlayerScore> scores = dao.getLeaderboard();
        int rank = 1;
        for (LeaderboardDao.PlayerScore ps : scores) {
            if (score > ps.score) break;
            rank++;
        }

        TextView txtRank = findViewById(R.id.tv_rank);
        txtRank.setText("当前排名：第" + rank + "名");
        txtRank.setTextSize(normalTextSp);
        setViewWidth(txtRank, elementWidth);

        EditText editPlayerName = findViewById(R.id.et_player_name);
        editPlayerName.setTextSize(normalTextSp);
        setViewWidth(editPlayerName, elementWidth);

        Button btnSubmitScore = findViewById(R.id.btn_submit_score);
        btnSubmitScore.setTextSize(normalTextSp);
        setViewWidth(btnSubmitScore, elementWidth);
        btnSubmitScore.setOnClickListener(v -> {
            String playerName = editPlayerName.getText().toString().trim();
            if (playerName.isEmpty()) {
                editPlayerName.setError("请输入名字");
                return;
            }
            dao.insertScore(playerName, score);
            btnSubmitScore.setEnabled(false);
            editPlayerName.setEnabled(false);
            btnSubmitScore.setText("已提交");
        });

        Button btnRestart = findViewById(R.id.btn_restart);
        btnRestart.setTextSize(normalTextSp);
        setViewWidth(btnRestart, elementWidth);
        btnRestart.setOnClickListener(view -> {
            startActivity(new Intent(this, GameActivity.class));
            finish();
        });

        Button btnMainMenu = findViewById(R.id.btn_main_menu);
        btnMainMenu.setTextSize(normalTextSp);
        setViewWidth(btnMainMenu, elementWidth);
        btnMainMenu.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        TextView textGameOver = findViewById(R.id.tv_game_over);
        textGameOver.setTextSize(largeTextSp);
    }

    /** 动态设置控件宽度 */
    private void setViewWidth(android.view.View view, int widthPx) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params != null) {
            params.width = widthPx;
            view.setLayoutParams(params);
        }
    }
}
