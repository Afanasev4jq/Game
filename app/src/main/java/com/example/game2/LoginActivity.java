package com.example.game2;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private UserDBHelper userDBHelper;
    private EditText etUsername;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);

        userDBHelper = new UserDBHelper(this);

        Button btnRegister = findViewById(R.id.btn_login);
        Button btnLoginExisting = findViewById(R.id.btn_login_existing);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });

        btnLoginExisting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (userDBHelper.isUserRegistered(username) && userDBHelper.loginUser(username, password)) {
                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    startMainActivity(username);
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid credentials. Please register or check your credentials.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private static final int REQUEST_SELECT_SKIN = 1;

    private void registerNewUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (userDBHelper.isUserRegistered(username)) {
            Toast.makeText(this, "User already exists. Try logging in.", Toast.LENGTH_SHORT).show();
        } else {
            if (userDBHelper.registerUser(username, password)) {
                Toast.makeText(this, "Registration successful. Please select your character skin.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, CharacterSelectionActivity.class);
                startActivityForResult(intent, REQUEST_SELECT_SKIN);
            } else {
                Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void startMainActivity(String username) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }
}

class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}




