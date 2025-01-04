package com.orhanuzel.mobilprogramlama;

import java.io.Serializable;

public class Movie implements Serializable {//for transformation use serializable

    public int id;
    public String name;
    public int year;
    public String content;
    public int favoriteState;
    public float rate;
    public String category;//in the "Movies" database called moviesCategoryId
    public String director;

}
