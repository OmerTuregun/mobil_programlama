package com.orhanuzel.mobilprogramlama;



import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.orhanuzel.mobilprogramlama.databinding.ActivityCloudServiceAiactivityBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
// HuggingFaceApi.java


// CloudServiceAIActivity.java
public class CloudServiceAIActivity extends AppCompatActivity {
    private ActivityCloudServiceAiactivityBinding binding;
    private TextView sentimentResultTextView;
    private static final String BASE_URL = "https://api-inference.huggingface.co/";
    private HuggingFaceApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCloudServiceAiactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sentimentResultTextView = binding.textViewResponse;


        String text = "bugün kendimi çok kötü hissediyorum";

        //analyzeSentiment(text);
    }

    private void setupRetrofit() {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(HuggingFaceApi.class);
    }

    private void analyzeSentiment(String text) {
        String jsonRequest = String.format("{\"inputs\": \"%s\"}", text.replace("\"", "\\\""));

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                jsonRequest
        );

        api.analyzeSentiment(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseString = response.body().string();
                        Log.d("API Raw Response", responseString);
                        parseSentimentResponse(responseString);
                    } catch (IOException e) {
                        handleError("Error reading response: " + e.getMessage());
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "No error body";
                        Log.e("API Error", "Code: " + response.code() +
                                ", Error Body: " + errorBody);
                        handleError("Server error: " + response.code());
                    } catch (IOException e) {
                        handleError("Error: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("API Call Failed", t.getMessage(), t);
                handleError("Network error: " + t.getMessage());
            }
        });
    }

    private void parseSentimentResponse(String responseString) {
        try {
            JSONArray jsonArray = new JSONArray(responseString);
            JSONArray innerArray = jsonArray.getJSONArray(0);

            // En yüksek skorlu duyguyu bul
            JSONObject bestResult = null;
            double bestScore = -1;

            for (int i = 0; i < innerArray.length(); i++) {
                JSONObject result = innerArray.getJSONObject(i);
                double score = result.getDouble("score");

                if (score > bestScore) {
                    bestScore = score;
                    bestResult = result;
                }
            }

            if (bestResult != null) {
                String sentiment = bestResult.getString("label");
                double confidence = bestResult.getDouble("score");

                // Duygu skorunu yorumla (1-5 yıldız)
                String interpretation;
                switch (sentiment) {
                    case "1 star":
                        interpretation = "Berbat";
                        break;
                    case "2 stars":
                        interpretation = "Negatif";
                        break;
                    case "3 stars":
                        interpretation = "Nötr";
                        break;
                    case "4 stars":
                        interpretation = "Pozitif";
                        break;
                    case "5 stars":
                        interpretation = "Mükemmel";
                        break;
                    default:
                        interpretation = "Belirsiz";
                }

                final String displayText = String.format("Duygu Analizi: %s\nGüven Skoru: %.2f%%",
                        interpretation, confidence * 100);

                runOnUiThread(() -> {
                    sentimentResultTextView.setText(displayText);
                    Log.d("API Success", displayText);
                });
            }

        } catch (JSONException e) {
            Log.e("JSON Parse Error", "Response: " + responseString);
            Log.e("JSON Parse Error", "Error: " + e.getMessage());
            handleError("Error parsing response. Check logs for details.");
        }
    }

    private void handleError(String errorMessage) {
        Log.e("SentimentAnalysis", errorMessage);
        runOnUiThread(() -> sentimentResultTextView.setText(errorMessage));
    }

    public void goToResult(View view){
        setupRetrofit();
        String textInput=binding.editTextInput.getText().toString();
        analyzeSentiment(textInput);
    }

}
//
//        String input = binding.editTextInput.getText().toString();
//        AzureCognitiveService service = new AzureCognitiveService();
//        TextView textViewResponse = binding.textViewResponse;
//
//
//        service.setOnResultListener(new OnResultListener() {
//            @Override
//            public void onResult(String sentiment) {
//                // UI'yi güncelle
//                runOnUiThread(() -> {
//                    textViewResponse.setText("Sonuç: " + sentiment);
//                });
//            }
//        });
//        service.analyzeSentiment(input);
//    }



//}