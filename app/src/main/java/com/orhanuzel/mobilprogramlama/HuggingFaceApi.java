
package com.orhanuzel.mobilprogramlama;


import static androidx.core.content.ContextCompat.getString;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface HuggingFaceApi {
    // String apiKey = getString(R.string.hf_api_key);
    @Headers({
            "Authorization: Bearer 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", // API anahtarınızı buraya ekleyin
            "Content-Type: application/json"
    })
    @POST("models/nlptown/bert-base-multilingual-uncased-sentiment")
    Call<ResponseBody> analyzeSentiment(@Body RequestBody text);
}
