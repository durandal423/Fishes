package com.example.fishes.activity;

import android.content.Intent;
import android.os.Bundle;
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

        TextView txtScore = findViewById(R.id.txt_score);
        txtScore.setText("得分：" + score);

        // 查询当前分数排名
        List<LeaderboardDao.PlayerScore> scores = dao.getLeaderboard();
        int rank = 1;
        for (LeaderboardDao.PlayerScore ps : scores) {
            if (score > ps.score) {
                break;
            }
            rank++;
        }
        TextView txtRank = findViewById(R.id.txt_rank);
        txtRank.setText("当前排名：第" + rank + "名");

        EditText editPlayerName = findViewById(R.id.edit_player_name);
        Button btnSubmitScore = findViewById(R.id.btn_submit_score);
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
