package com.example.fishes.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fishes.R;
import com.example.fishes.model.LeaderboardDao;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        ListView listView = findViewById(R.id.list_leaderboard);
        LeaderboardDao dao = new LeaderboardDao(this);
        List<LeaderboardDao.PlayerScore> scores = dao.getLeaderboard();
        List<String> displayList = new ArrayList<>();
        int rank = 1;
        for (LeaderboardDao.PlayerScore ps : scores) {
            displayList.add(rank + ". " + ps.playerName + " - " + ps.score);
            rank++;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        listView.setAdapter(adapter);
    }
} 