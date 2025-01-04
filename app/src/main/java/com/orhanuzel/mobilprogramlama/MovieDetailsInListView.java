package com.orhanuzel.mobilprogramlama;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.orhanuzel.mobilprogramlama.databinding.ActivityMovieDetailsInListViewBinding;

public class MovieDetailsInListView extends AppCompatActivity {

    private ActivityMovieDetailsInListViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie_details_in_list_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding=ActivityMovieDetailsInListViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent=getIntent();
        Movie movie=(Movie)intent.getSerializableExtra("movie");
        binding.textViewName.setText(movie.name);
        binding.textViewYear.setText(String.valueOf(movie.year));
        binding.textViewContent.setText(movie.content);
        binding.textViewRate.setText(String.valueOf(movie.rate));
        binding.textViewCategory.setText(movie.category);
        binding.textViewDirector.setText(movie.director);

    }
}