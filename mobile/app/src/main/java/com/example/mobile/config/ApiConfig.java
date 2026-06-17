package com.example.mobile.config;

public class ApiConfig {
    // 10.0.2.2 is the IP address to access the development host computer from the Android Emulator
    public static final String BASE_URL = "http://10.0.2.2:8080";

    /**
     * Helper to construct full URL for an API endpoint
     * @param endpoint the relative path (e.g. "/api/san")
     * @return the full URL (e.g. "http://10.0.2.2:8080/api/san")
     */
    public static String getApiUrl(String endpoint) {
        if (endpoint == null) return BASE_URL;
        if (endpoint.startsWith("/")) {
            return BASE_URL + endpoint;
        }
        return BASE_URL + "/" + endpoint;
    }
}
