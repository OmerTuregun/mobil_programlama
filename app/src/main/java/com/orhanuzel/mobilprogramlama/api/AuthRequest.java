package com.orhanuzel.mobilprogramlama.api;

public class AuthRequest {
    private String email;
    private String password;

    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getter ve Setter
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
