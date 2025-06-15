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
        String username = getIntent().getStringExtra("username");

        // 1. 绑定控件
        TextView tvGameOver  = findViewById(R.id.tv_game_over);
        TextView tvScore     = findViewById(R.id.tv_score);
        TextView tvRank      = findViewById(R.id.tv_rank);
        EditText etPlayer    = findViewById(R.id.et_player_name);
        Button btnSubmit     = findViewById(R.id.btn_submit_score);
        Button btnRestart    = findViewById(R.id.btn_restart);
        Button btnMainMenu   = findViewById(R.id.btn_main_menu);

        // 2. 查询排名并设置文本
        tvScore.setText("得分：" + score);
        List<LeaderboardDao.PlayerScore> scores = dao.getLeaderboard();
        int rank = 1;
        for (LeaderboardDao.PlayerScore ps : scores) {
            if (score > ps.score) break;
            rank++;
        }
        tvRank.setText("当前排名：第" + rank + "名");

        // 3. 自动填充用户名
        if (username != null && !username.equals("游客")) {
            etPlayer.setText(username);
        }

        // 4. 获取屏幕参数
        DisplayMetrics dm       = getResources().getDisplayMetrics();
        float screenWidthPx     = dm.widthPixels;
        float scaledDensity     = dm.scaledDensity;

        // —— 可滚动布局下可以稍微放大比例，如果还是过大请再调小 ——
        float titlePercent      = 0.06f;  // 标题文字占屏宽 6%
        float labelPercent      = 0.04f;  // 其他文字占屏宽 4%
        float editWidthPercent  = 0.50f;  // 输入框宽度占屏宽 50%
        float buttonWidthPercent= 0.45f;  // 按钮宽度占屏宽 45%

        // 5. 计算并设置字体大小
        float titleSp  = screenWidthPx * titlePercent / scaledDensity;
        float labelSp  = screenWidthPx * labelPercent / scaledDensity;

        tvGameOver.setTextSize(titleSp);
        tvScore   .setTextSize(labelSp);
        tvRank    .setTextSize(labelSp);
        etPlayer  .setTextSize(labelSp);
        btnSubmit .setTextSize(labelSp);
        btnRestart.setTextSize(labelSp);
        btnMainMenu.setTextSize(labelSp);

        // 6. 计算并设置宽度（px）
        int editW   = (int)(screenWidthPx * editWidthPercent);
        int btnW    = (int)(screenWidthPx * buttonWidthPercent);

        setViewWidth(etPlayer,  editW);
        setViewWidth(btnSubmit, btnW);
        setViewWidth(btnRestart,btnW);
        setViewWidth(btnMainMenu,btnW);

        // 7. 按钮点击逻辑
        btnSubmit.setOnClickListener(v -> {
            String name = etPlayer.getText().toString().trim();
            if (name.isEmpty()) {
                etPlayer.setError("请输入名字");
                return;
            }
            dao.insertScore(name, score);
            btnSubmit.setEnabled(false);
            etPlayer  .setEnabled(false);
            btnSubmit .setText("已提交");
        });

        btnRestart.setOnClickListener(v -> {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
            finish();
        });

        btnMainMenu.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
            finish();
        });
    }

    /** 动态修改控件宽度 */
    private void setViewWidth(android.view.View view, int widthPx) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp != null) {
            lp.width = widthPx;
            view.setLayoutParams(lp);
        }
    }
}
