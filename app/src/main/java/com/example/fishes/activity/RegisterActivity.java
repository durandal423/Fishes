package com.example.fishes.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fishes.R;
import com.example.fishes.model.UserDao;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;
    private TextInputEditText confirmPasswordInput;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userDao = new UserDao(this);

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        Button registerButton = findViewById(R.id.registerButton);
        Button backToLoginButton = findViewById(R.id.backToLoginButton);

        registerButton.setOnClickListener(v -> handleRegister());
        backToLoginButton.setOnClickListener(v -> finish());
    }

    private void handleRegister() {
        String username = String.valueOf(usernameInput.getText());
        String password = String.valueOf(passwordInput.getText());
        String confirmPassword = String.valueOf(confirmPasswordInput.getText());

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "请填写所有字段", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userDao.findByUsername(username) != null) {
            Toast.makeText(this, "用户名已存在", Toast.LENGTH_SHORT).show();
            return;
        }

        UserDao.User newUser = new UserDao.User(username, password);
        userDao.insertUser(newUser);
        Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
        finish();
    }
}
