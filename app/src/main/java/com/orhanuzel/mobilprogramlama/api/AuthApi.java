package com.orhanuzel.mobilprogramlama.api;

import com.orhanuzel.mobilprogramlama.api.AuthRequest;
import com.orhanuzel.mobilprogramlama.api.AuthResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("users/login")
    Call<AuthResponse> login(@Body AuthRequest request);
}
