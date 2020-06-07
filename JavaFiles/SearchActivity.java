package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements HomeAdapter.OnItemClickListener {
    public View view;
    public static final String EXTRA_URL = "imageUrl";
    public static final String EXTRA_Title = "title";
    public static final String EXTRA_Section = "section";
    public static final String EXTRA_date = "date";
    public static final String EXTRA_abstract = "abstract";
    public static final String EXTRA_ARTICLEID = "artcileID";
    public static final String EXTRA_WebURL = "webURL";
    public String absData="";

    private Context context;
    private final String KEYWORD = "keyword";
    private RecyclerView mRecyclerView;
    private HomeAdapter mHomeAdapter;
    private ArrayList<HomeItem> mHomeList;
    private RequestQueue mRequestQueue;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AppCompatActivity activity;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_acitivity);
        view = findViewById(R.id.searchresult);
        activity = this;

        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh_items);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            parseJSON();
                        }
                    }
                }, 1000);
            }
        });

        intent = getIntent();
        context = getApplicationContext();
        String keyword = intent.getStringExtra(KEYWORD);
        mRequestQueue = Volley.newRequestQueue(this);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Search results for " + keyword);

        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.homeFrame);

        frameLayout.setVisibility(view.VISIBLE);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setVisibility(view.GONE);

        mHomeList = new ArrayList<>();
        mHomeAdapter = new HomeAdapter(this, mHomeList);
        mRecyclerView.setAdapter(mHomeAdapter);
        mHomeAdapter.setOnItemClickListener(this);
        mHomeAdapter.notifyDataSetChanged();

        parseJSON();

    }

    @Override
    public void onResume() {
        if(mHomeAdapter!=null) mHomeAdapter.refreshList();
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default: return true;
        }

    }

    private void parseJSON() {
        String keyword = intent.getStringExtra(KEYWORD);
        String url = "https://mss-android-backend.wl.r.appspot.com/searchGuardian?key="+keyword;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            JSONObject data = response.getJSONObject("data");

                            JSONObject jsdg = data.getJSONObject("response");


                            JSONArray jsonArray = jsdg.getJSONArray("results");

                            int idx1=0;
                            while (idx1 < 10) {
                                JSONObject results = jsonArray.getJSONObject(idx1);
                                String sectionName = results.getString("sectionName");
                                String title = results.getString("webTitle");
                                String date = results.getString("webPublicationDate");
                                String articleId = results.getString("id");
                                String webURL = results.getString("webUrl");

                                String imageUrl = "";

                                try {
                                    JSONObject blocks = results.getJSONObject("blocks");
                                    JSONObject main = blocks.getJSONObject("main");
                                    JSONArray eleArray = main.getJSONArray("elements");
                                    JSONObject eleO = eleArray.getJSONObject(0);
                                    JSONArray assetsArray = eleO.getJSONArray("assets");
                                    JSONObject assetsO = assetsArray.getJSONObject(0);


                                    imageUrl = assetsO.getString("file");
                                } catch (JSONException e) {
                                    imageUrl = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png";
                                    e.printStackTrace();
                                }


                                mHomeList.add(new HomeItem(imageUrl, title, sectionName, date, articleId, webURL));
                                idx1++;

                            }

                            FrameLayout frame = view.findViewById(R.id.homeFrame);
                            frame.setVisibility(view.GONE);

                            RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
                            recyclerView.setVisibility(view.VISIBLE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequestQueue.add(request);
    }

    @Override
    public void onItemClick(int position) {
        final Intent detailIntent = new Intent(this, DetailHomeActivity.class);
        final HomeItem clickedItem = mHomeList.get(position);

        String idToDetailed = clickedItem.getmArticleId();

        String url = "https://mss-android-backend.wl.r.appspot.com/detailedGuardian?key="+idToDetailed;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {

                            JSONObject data1 = response.getJSONObject("data");
                            JSONObject response1 = data1.getJSONObject("response");

                            JSONObject content = response1.getJSONObject("content");


                            try {
                                JSONObject blocks = content.getJSONObject("blocks");
                                JSONArray body = blocks.getJSONArray("body");
                                int idx=0;
                                while (idx < body.length()) {
                                    JSONObject bodyj = body.getJSONObject(idx);
                                    absData = bodyj.getString("bodyHtml");
                                    idx++;
                                }


                                detailIntent.putExtra(EXTRA_URL, clickedItem.getImageUrl());
                                detailIntent.putExtra(EXTRA_Title, clickedItem.getmTitle());
                                detailIntent.putExtra(EXTRA_date, clickedItem.getmTime());
                                detailIntent.putExtra(EXTRA_Section, clickedItem.getmSection());
                                detailIntent.putExtra(EXTRA_ARTICLEID,clickedItem.getmArticleId());
                                detailIntent.putExtra(EXTRA_WebURL,clickedItem.getmwebURL());
                                detailIntent.putExtra(EXTRA_abstract, absData);

                                startActivity(detailIntent);



                            } catch (JSONException e) {
                            }




                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mRequestQueue.add(request);
    }

}
