package com.example.backend.dto;

public class LoginResponse {
    private boolean success;
    private String message;
    private Integer id;
    private String username;

    public LoginResponse() {
    }

    public LoginResponse(boolean success, String message, Integer id, String username) {
        this.success = success;
        this.message = message;
        this.id = id;
        this.username = username;
    }

    public static LoginResponseBuilder builder() {
        return new LoginResponseBuilder();
    }

    public static class LoginResponseBuilder {
        private boolean success;
        private String message;
        private Integer id;
        private String username;

        public LoginResponseBuilder success(boolean success) {
            this.success = success;
            return this;
        }

        public LoginResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public LoginResponseBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public LoginResponseBuilder username(String username) {
            this.username = username;
            return this;
        }

        public LoginResponse build() {
            return new LoginResponse(success, message, id, username);
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
