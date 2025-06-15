package com.example.fishes.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fishes.R;
import com.example.fishes.model.UserDao;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;
    private UserDao userDao;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userDao = new UserDao(this);
        preferences = getSharedPreferences("game_prefs", MODE_PRIVATE);

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.registerButton);
        Button guestButton = findViewById(R.id.guestButton);

        // 检查是否已登录
        if (preferences.contains("username")) {
            startMainActivity(preferences.getString("username", ""));
            finish();
            return;
        }

        loginButton.setOnClickListener(v -> handleLogin());
        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
        guestButton.setOnClickListener(v -> {
            startMainActivity("游客");
            finish();
        });
    }

    private void handleLogin() {
        String username = String.valueOf(usernameInput.getText());
        String password = String.valueOf(passwordInput.getText());

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请填写用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        UserDao.User user = userDao.login(username, password);
        if (user != null) {
            // 保存登录状态
            preferences.edit()
                    .putString("username", username)
                    .apply();
            
            startMainActivity(username);
            finish();
        } else {
            Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
        }
    }

    private void startMainActivity(String username) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
}
