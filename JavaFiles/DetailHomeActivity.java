package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import static com.example.newsapp.HomeFragment.EXTRA_ARTICLEID;
import static com.example.newsapp.HomeFragment.EXTRA_Section;
import static com.example.newsapp.HomeFragment.EXTRA_Title;
import static com.example.newsapp.HomeFragment.EXTRA_URL;
import static com.example.newsapp.HomeFragment.EXTRA_WebURL;
import static com.example.newsapp.HomeFragment.EXTRA_abstract;
import static com.example.newsapp.HomeFragment.EXTRA_date;

public class DetailHomeActivity extends AppCompatActivity {
    String imageUrl;
     String title ;
     String section;
     String date;
    String abstractData;
     String articleID ;
     String webURL;
     boolean flag=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_home);

        Intent intent = getIntent();
        imageUrl = intent.getStringExtra(EXTRA_URL);
         title = intent.getStringExtra(EXTRA_Title);
         section = intent.getStringExtra(EXTRA_Section);
         date = intent.getStringExtra(EXTRA_date);
         abstractData = intent.getStringExtra(EXTRA_abstract);
         articleID = intent.getStringExtra(EXTRA_ARTICLEID);
         webURL = intent.getStringExtra(EXTRA_WebURL);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        String value = actionBar.getTitle().toString();
        actionBar.setTitle(title);


        FrameLayout frame = findViewById(R.id.DetailFrame);
        value = actionBar.getTitle().toString();
        frame.setVisibility(View.VISIBLE);

        NestedScrollView scrollView = findViewById(R.id.detailScroll);
        scrollView.setVisibility(View.GONE);

        ImageView imageView = findViewById(R.id.image_view_detail);
        TextView textViewTitle = findViewById(R.id.text_view_title_detail);
        TextView textViewSection = findViewById(R.id.text_view_section_detail);
        TextView textViewDate = findViewById(R.id.text_view_date_detail);
        TextView textViewAbstract = findViewById(R.id.text_view_abstract_detail);

        Picasso.with(this).load(imageUrl).fit().centerInside().into(imageView);
        textViewTitle.setText(title);
        textViewSection.setText(section);

        String newDate = date.substring(0,10);
        String[] dateContents = newDate.split("-");
        StringBuffer sb = new StringBuffer();
        sb.append(dateContents[2]);
        switch (Integer.parseInt(dateContents[1])){
            case 1: sb.append(" Jan");break;
            case 2: sb.append(" Feb");break;
            case 3: sb.append(" Mar");break;
            case 4: sb.append(" Apr");break;
            case 6: sb.append(" Jun");break;
            case 7: sb.append(" Jul");break;
            case 8: sb.append(" Aug");break;
            case 9: sb.append(" Sept");break;
            case 10: sb.append(" Oct");break;
            case 11: sb.append(" Nov");break;
            case 12: sb.append(" Dec");break;
            default:sb.append(" May");
        }
        sb.append(" "+dateContents[0]);
        textViewDate.setText(sb.toString());
        textViewAbstract.setText(Html.fromHtml(Html.fromHtml(abstractData).toString()));
            frame.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);


        final SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                if (articleID.compareToIgnoreCase(entry.getKey()) == 0) {
                    flag = true;
                    break;
                }

        }

        TextView textViewURL = (TextView)findViewById(R.id.FullPageURL);
        textViewURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = webURL;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try{
                    intent.setData(Uri.parse(url));
                }catch (Exception e){
                    intent.setData(Uri.parse("https://www.theguardian.com/us"));
                }

                startActivity(intent);
            }
        });



    }
    public String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int data1 =1;
        int randData = 2;
        getMenuInflater().inflate(R.menu.detailed_menu,menu);
        randData = data1+randData;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.bookmark:
                final SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("", Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = sharedPreferences.edit();

                Map<String, ?> allEntries = sharedPreferences.getAll();
                if(!flag) {
                    String data = imageUrl + "@split@" + title + "@split@" + section + "@split@" + date + "@split@" + articleID + "@split@"+ webURL;
                    editor.putString(articleID, data);
                    editor.commit();
                    Toast.makeText(this, '"'+title +'"'+" was added to bookmarks", Toast.LENGTH_SHORT).show();
                    flag=true;
                } else{
                        editor.remove(articleID);
                        editor.commit();
                    Toast.makeText(this, '"'+title +'"'+" was removed from bookmarks", Toast.LENGTH_SHORT).show();
                        flag = false;
                }
                invalidateOptionsMenu();
                return true;
            case R.id.twitter:
                String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s&url=%s&hashtags=CSCI571NewsSearch",
                        urlEncode("Check out this Link:\n"),
                        urlEncode(webURL));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                return true;
                }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem bookmark = menu.findItem(R.id.bookmark);
        if(flag) bookmark.setIcon(R.drawable.ic_bookmark_fill);
        else bookmark.setIcon(R.drawable.ic_bookmark_border);
        return super.onPrepareOptionsMenu(menu);
    }
}
