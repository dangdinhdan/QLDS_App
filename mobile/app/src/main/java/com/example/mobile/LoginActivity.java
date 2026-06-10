package com.example.mobile;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobile.viewmodel.LoginViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel viewModel;

    private EditText editUsername;
    private EditText editPassword;
    private ImageView imagePasswordToggle;
    private MaterialCheckBox checkboxRemember;
    private MaterialButton buttonLogin;
    private ProgressBar progressLoading;

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Instantiate ViewModel
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        initViews();
        setupPasswordToggle();
        setupActions();
        observeViewModel();
    }

    private void initViews() {
        editUsername = findViewById(R.id.edit_username);
        editPassword = findViewById(R.id.edit_password);
        imagePasswordToggle = findViewById(R.id.image_password_toggle);
        checkboxRemember = findViewById(R.id.checkbox_remember);
        buttonLogin = findViewById(R.id.button_login);
        progressLoading = findViewById(R.id.progress_loading);
        
        // Initial state of eye toggle (semi-transparent)
        imagePasswordToggle.setAlpha(0.6f);
    }

    private void setupPasswordToggle() {
        imagePasswordToggle.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            
            // Capture typeface to prevent resetting to monospaced
            Typeface typeface = editPassword.getTypeface();
            
            if (isPasswordVisible) {
                editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                imagePasswordToggle.setAlpha(1.0f);
            } else {
                editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                imagePasswordToggle.setAlpha(0.6f);
            }
            
            editPassword.setTypeface(typeface);
            
            // Keep the cursor positioned at the end of the text
            editPassword.setSelection(editPassword.getText().length());
        });
    }

    private void setupActions() {
        buttonLogin.setOnClickListener(v -> {
            String username = editUsername.getText().toString();
            String password = editPassword.getText().toString();
            boolean rememberMe = checkboxRemember.isChecked();

            viewModel.login(username, password, rememberMe);
        });
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, loading -> {
            if (loading != null) {
                if (loading) {
                    progressLoading.setVisibility(View.VISIBLE);
                    buttonLogin.setEnabled(false);
                    editUsername.setEnabled(false);
                    editPassword.setEnabled(false);
                    checkboxRemember.setEnabled(false);
                } else {
                    progressLoading.setVisibility(View.GONE);
                    buttonLogin.setEnabled(true);
                    editUsername.setEnabled(true);
                    editPassword.setEnabled(true);
                    checkboxRemember.setEnabled(true);
                }
            }
        });

        viewModel.getErrorMessage().observe(this, errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getLoginSuccess().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_LONG).show();
                android.content.Intent intent = new android.content.Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
