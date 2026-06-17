package com.example.mobile.repository;

import com.example.mobile.config.ApiConfig;
import com.example.mobile.model.UserCredentials;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LoginRepository {
    private static LoginRepository instance;
    private static final String BASE_URL = ApiConfig.getApiUrl("/api/auth/login");

    private LoginRepository() {}

    public static synchronized LoginRepository getInstance() {
        if (instance == null) {
            instance = new LoginRepository();
        }
        return instance;
    }

    public static class AuthResult {
        private final boolean success;
        private final String message;

        public AuthResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }

    public AuthResult authenticate(UserCredentials credentials) {
        if (credentials == null) {
            return new AuthResult(false, "Thông tin đăng nhập không hợp lệ");
        }

        HttpURLConnection conn = null;
        try {
            URL url = new URL(BASE_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            // Construct JSON request body
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("username", credentials.getUsername());
            jsonRequest.put("password", credentials.getPassword());

            // Write request body
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonRequest.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            
            // Read response
            InputStreamReader reader;
            if (responseCode >= 200 && responseCode < 300) {
                reader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8);
            } else {
                reader = new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8);
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(reader)) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            JSONObject jsonResponse = new JSONObject(response.toString());
            boolean success = jsonResponse.optBoolean("success", false);
            String message = jsonResponse.optString("message", "Có lỗi xảy ra");

            return new AuthResult(success, message);

        } catch (Exception e) {
            e.printStackTrace();
            return new AuthResult(false, "Không thể kết nối đến máy chủ: " + e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
