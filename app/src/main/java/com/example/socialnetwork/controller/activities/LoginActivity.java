package com.example.socialnetwork.controller.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialnetwork.R;
import com.example.socialnetwork.network.ApiRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private Button LoginButton;
    private EditText UserEmail, UserPassword;
    private TextView NeedNewAccountLink;
    private AlertDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        NeedNewAccountLink = findViewById(R.id.register_account_link);
        UserEmail = findViewById(R.id.login_email);
        UserPassword = findViewById(R.id.login_password);
        LoginButton = findViewById(R.id.login_button);

        NeedNewAccountLink.setOnClickListener(view -> SendUserToRegisterActivity());
        LoginButton.setOnClickListener(view -> AllowingUserToLogin());
    }

    @Override
    protected void onStart() {
        super.onStart();

        String currentUserId = getSharedPreferences("user_data", MODE_PRIVATE)
                .getString("user_id", null);

        if (currentUserId != null) {
            SendUserToMainActivity();
        }
    }


    private void AllowingUserToLogin() {
        String email = UserEmail.getText().toString().trim();
        String password = UserPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter email and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoadingDialog();

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        ApiRequest.post("http://farnazboroumand.ir/api/login_user.php", params, new ApiRequest.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                hideLoadingDialog();
                if (response.trim().startsWith("success:")) {
                    String userId = response.trim().substring(8);
                    saveUserId(userId);
                    Toast.makeText(LoginActivity.this, "You are logged in successfully.", Toast.LENGTH_SHORT).show();
                    SendUserToMainActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "Login Failed: " + response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                hideLoadingDialog();
                Toast.makeText(LoginActivity.this, "API Error: " + error, Toast.LENGTH_SHORT).show();
                Log.d("LOG","API Error:"  + error);
            }
        });
    }


    private void saveUserId(String userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_id", userId);
        editor.apply();
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    private void showLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logging In");
        builder.setMessage("Please wait, while we are logging you in...");

        ProgressBar progressBar = new ProgressBar(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);
        layout.addView(progressBar);

        builder.setView(layout);
        builder.setCancelable(false);

        loadingDialog = builder.create();
        loadingDialog.show();
    }

    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}
