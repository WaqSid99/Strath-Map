package com.example.strathmap;
import com.google.gson.annotations.SerializedName;;
public class Post {

    private int UserId;
    private int id;

    @SerializedName("body")
    private String text;

    public int getUserId() {
        return UserId;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
