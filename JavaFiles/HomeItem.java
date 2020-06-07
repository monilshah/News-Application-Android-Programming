package com.example.newsapp;

import android.app.Activity;

import android.content.Context;

import android.os.Bundle;

import android.view.View;

import android.widget.ImageView;

import android.widget.Toast;


public class HomeItem extends Activity {
    View view;
    private String mImageUrl;
    private String mTitle;
    private String mSection;
    private String mTime;
    private String mArticleId;
    private String mwebURL;

    public boolean getBookmarkFlag() {
        return bookmarkFlag;
    }

    public void setBookmarkFlag(boolean bookmarkFlag) {
        this.bookmarkFlag = bookmarkFlag;
    }

    private boolean bookmarkFlag = false;

    final Context context = this;



    public HomeItem(String imageUrl, String title, String section,String time, String articleId, String webURL) {
        mImageUrl = imageUrl;
        mTitle = title;
        mSection = section;
        mTime = time;
        mArticleId = articleId;
        mwebURL = webURL;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmSection() {
        return mSection;
    }

    public String getmTime() {
        return mTime;
    }

    public String getmArticleId() {
        return mArticleId;
    }

    public String getmwebURL() { return mwebURL; }


    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_item);


        ImageView bookmark = (ImageView) findViewById(R.id.bookmark);

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast1 = Toast.makeText(context,"bookmark button clicked", Toast.LENGTH_SHORT);
                toast1.show();

            }
        });
    }


}
