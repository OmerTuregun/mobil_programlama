package com.orhanuzel.mobilprogramlama;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.orhanuzel.mobilprogramlama.databinding.ActivitySharedPreferencesBinding;


public class SharedPreferencesActivity extends AppCompatActivity {
private String isim;
private ActivitySharedPreferencesBinding binding;
private EditText eMailAdressEditText;
private EditText phoneNumberEditText;
private EditText nameEditText;
private Button cancelButton;
private Button saveButton;
private Button settingsButton;
private String name;
private String email;
private String phoneNumber;

SharedPreferences sharedPreferences;//atamasını on create de yapmak lazım

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shared_preferences);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding = ActivitySharedPreferencesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        eMailAdressEditText=binding.editTextTextEmailAddress;
        phoneNumberEditText=binding.editTextPhone;
        nameEditText=binding.editTextText;

        settingsButton=binding.buttonSettings;
        cancelButton=binding.buttonForCancel;
        saveButton=binding.buttonForSave;

        sharedPreferences=getSharedPreferences("personalInformations",MODE_PRIVATE);

        name=sharedPreferences.getString("name","");
        nameEditText.setText(name);

        email=sharedPreferences.getString("email","");
        eMailAdressEditText.setText(email);

        phoneNumber=sharedPreferences.getString("phoneNumber","");
        phoneNumberEditText.setText(phoneNumber);



        saveButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
        nameEditText.setEnabled(false);
        eMailAdressEditText.setEnabled(false);
        phoneNumberEditText.setEnabled(false);

        //nameEditText.setCursorVisible(false);



    }

    public void settingToPersonalInformationFunc(View view){
        settingsButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.VISIBLE);

        //  editing access for user informations
        nameEditText.setEnabled(true);
        eMailAdressEditText.setEnabled(true);
        phoneNumberEditText.setEnabled(true);
        //


    }

    public void saveToUserInformationFunct(View view){
        nameEditText.setEnabled(false);
        eMailAdressEditText.setEnabled(false);
        phoneNumberEditText.setEnabled(false);


        saveButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
        settingsButton.setVisibility(View.VISIBLE);

      String newName =nameEditText.getText().toString();
      sharedPreferences.edit().putString("name",newName).apply();

      String newEmail=eMailAdressEditText.getText().toString();
      sharedPreferences.edit().putString("email",newEmail).apply();

      String newPhoneNumber=phoneNumberEditText.getText().toString();
      sharedPreferences.edit().putString("phoneNumber",newPhoneNumber).apply();



    }

    public void cancelToUserInformationEditingFunct(View view){
        nameEditText.setEnabled(false);
        eMailAdressEditText.setEnabled(false);
        phoneNumberEditText.setEnabled(false);

        saveButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
        settingsButton.setVisibility(View.VISIBLE);
       String oldName= sharedPreferences.getString("name","");
        nameEditText.setText(oldName);

        String oldEmail=sharedPreferences.getString("email","");
        eMailAdressEditText.setText(oldEmail);

        String oldPhoneNumber=sharedPreferences.getString("phoneNumber","");
        phoneNumberEditText.setText(oldPhoneNumber);



    }


}