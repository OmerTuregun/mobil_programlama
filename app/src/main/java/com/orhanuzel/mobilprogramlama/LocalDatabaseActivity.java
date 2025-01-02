package com.orhanuzel.mobilprogramlama;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.orhanuzel.mobilprogramlama.databinding.ActivityLocalDatabaseBinding;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class LocalDatabaseActivity extends AppCompatActivity {

    private String content;
    private String name;
    private int year;
    private float rate;
    private int categoryId;
    private int directorId;
    private String category;
    private String director;
    private int favoriteState;

    ArrayList<Movie> movieArrayList = new ArrayList<Movie>();

    private ActivityLocalDatabaseBinding binding;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_local_database);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding=ActivityLocalDatabaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());// burayı sakın unutma
        ListView listViewForMovies=binding.listViewForMovies;
       // TextView contentTextView = binding.contentTextView;
       // TextView favoriteStateTextView = binding.favoriteStateTextView;

        try {
            SQLiteDatabase database=this.openOrCreateDatabase("Database",MODE_PRIVATE,null);
            Log.d("database","database basariyla acildi");

            database.execSQL("CREATE TABLE IF NOT EXISTS Categories(categoryId INTEGER PRIMARY KEY AUTOINCREMENT,categoryName VARCHAR)");
            Log.d("database","Categories tablosu basariyla olusturuldu");

            database.execSQL("CREATE TABLE IF NOT EXISTS Directors(directorId INTEGER PRIMARY KEY AUTOINCREMENT,directorName VARCHAR)");
            Log.d("database","Directors tablosu basariyla olusturuldu");

            database.execSQL("CREATE TABLE IF NOT EXISTS Movies(moviesId INTEGER PRIMARY KEY AUTOINCREMENT,movieName VARCHAR,movieYear INTEGER," +
                    "content VARCHAR,favoriteState INTEGER,rate REAL,movieCategoryId INTEGER,movieDirectorId INTEGER," +
                    "FOREIGN KEY(movieCategoryId) REFERENCES Categories(categoryId)," +
                    "FOREIGN KEY(movieDirectorId) REFERENCES Directors(directorId))");//Attention to Foreign Keys

            Log.d("database","Movies tablosu basariyla olusturuldu");

    //        database.execSQL("INSERT INTO Categories(categoryName) VALUES('Korku')");//tek tırnak olmasına dikkat !
    //        database.execSQL("INSERT INTO Directors(directorName) VALUES(' Stanley Kubrick')");
    //        database.execSQL("INSERT INTO Movies(movieName,movieYear,content,favoriteState,rate,movieCategoryId,movieDirectorId) " +
    //                "VALUES('The Shining',1980,'gerilim ve korku diyince akla gelen o efsane film',true,9.5,2,2)");

//            database.execSQL("CREATE TABLE IF NOT EXISTS Notes(content VARCHAR,favoriteState BOOLEAN)");

            //  database.execSQL("INSERT INTO Notes (content,favoriteState) VALUES ('My Arkadash Here :)',true)");

            Cursor cursor=database.rawQuery("SELECT * FROM Movies",null);///Notes vardı eskiden
            int contentIndex=cursor.getColumnIndex("content");
            int nameIndex=cursor.getColumnIndex("movieName");
            int yearIndex=cursor.getColumnIndex("movieYear");
            int rateIndex=cursor.getColumnIndex("rate");
            int favoriteStateIndex=cursor.getColumnIndex("favoriteState");
            int categoryIdIndex=cursor.getColumnIndex("movieCategoryId");
            int directorIdIndex=cursor.getColumnIndex("movieDirectorId");

            //  int favoriteStateIndex=cursor.getColumnIndex("favoriteState");

            if(cursor.getCount()>0) {
                while (cursor.moveToNext()) {
                    Movie movie =new Movie();
                    movie.name=cursor.getString(nameIndex);
                    movie.year=cursor.getInt(yearIndex);
                    movie.content=cursor.getString(contentIndex);
                    movie.rate=cursor.getFloat(rateIndex);
                    movie.favoriteState=cursor.getInt(favoriteStateIndex);
                    categoryId=cursor.getInt(categoryIdIndex);
                    directorId=cursor.getInt(directorIdIndex);

                    // Categories tablosundan categoryId'ye karşılık gelen kategori adını al
                   category = "Kategori Bulunamadı";
                    Cursor categoryCursor = database.rawQuery("SELECT categoryName FROM Categories WHERE categoryId = ?",
                            new String[]{String.valueOf(categoryId)});
                    if (categoryCursor.moveToFirst()) {//cevap dönüyorsa
                        category = categoryCursor.getString(categoryCursor.getColumnIndex("categoryName"));
                    }
                    categoryCursor.close();

                    // Alınan kategori adını Movie nesnesine ekliyoruzz
                    movie.category = category;
                    director="yönetmen ismi bulunamadı";
                    Cursor directorCursor=database.rawQuery("SELECT  directorName FROM Directors WHERE directorId = ? ",
                            new String[]{String.valueOf(directorId)});

                    if(directorCursor.moveToFirst()){
                        director=directorCursor.getString(directorCursor.getColumnIndex("directorName"));
                    }
                    directorCursor.close();
                    movie.director=director;

                    movieArrayList.add(movie);
                    //content = cursor.getString(contentIndex);
                    System.out.println(content);
                    System.out.println("cursor bos donmuyor");
                }
            }
            else{
                System.out.println("cursor bos donuyor");
            }

//            while(cursor.moveToNext()){
//                content=cursor.getString(contentIndex);
//                favoriteState=cursor.getInt(favoriteStateIndex);
//
//             // contentTextView.setText(cursor.getString(contentIndex));
//             // favoriteStateTextView.setText(String.valueOf(cursor.getInt(favoriteStateIndex)));
//                System.out.println(cursor.getString(contentIndex));
//                System.out.println(cursor.getInt(favoriteStateIndex));
//
//            }

            cursor.close();
            database.close();

        }
        catch (Exception e){
            e.printStackTrace();
            Log.e("Database Error", "Hata oluştu: " + e.getMessage());

            System.out.println("hatalarla karsialasildi") ;
        }
        System.out.println("son "+content);
        System.out.println("son "+favoriteState);
        //contentTextView.setText(content);
        //favoriteStateTextView.setText(String.valueOf(favoriteState));


        ArrayAdapter movieArrayAdapter=new ArrayAdapter(this, android.R.layout.simple_list_item_1,
                movieArrayList.stream().map(movie -> movie.name).collect(Collectors.toList()));
        listViewForMovies.setAdapter(movieArrayAdapter);
        System.out.println("yillar"+movieArrayList.get(0).year+" "+movieArrayList.get(1).year);

        listViewForMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // activity movie  details kısmına geçiş aşaması
                Intent intent=new Intent(LocalDatabaseActivity.this,MovieDetailsInListView.class);
                intent.putExtra("movie",movieArrayList.get(position));
                startActivity(intent);
            }
        });

    }

    public void addMovie(View view){
        Toast.makeText(LocalDatabaseActivity.this,"film ekleme işlevi henüz eklenmemiştir",Toast.LENGTH_LONG).show();
    }




}