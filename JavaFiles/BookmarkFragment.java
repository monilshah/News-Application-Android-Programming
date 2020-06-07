package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Map;

public class BookmarkFragment extends Fragment implements BookmarkAdapter.OnItemClickListener {

    public static final String EXTRA_URL = "imageUrl";
    public static final String EXTRA_Title = "title";
    public static final String EXTRA_Section = "section";
    public static final String EXTRA_date = "date";
    public static final String EXTRA_abstract = "abstract";
    public static final String EXTRA_ARTICLEID = "artcileID";
    public static final String EXTRA_WebURL = "webURL";
    public String absData="";
    private RequestQueue mRequestQueue;


    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";
    private RecyclerView mRecyclerView;
    View view;
    public ArrayList<HomeItem> mHomeList;
    private BookmarkAdapter mBookmarkAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_bookmark, container, false);

        boolean var = loadData();

        if(var == true){
            mRecyclerView = view.findViewById(R.id.recycler_view);
            mRecyclerView.setHasFixedSize(true);

            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));

            mBookmarkAdapter = new BookmarkAdapter(getActivity(), mHomeList);
            mRecyclerView.setAdapter(mBookmarkAdapter);
            mBookmarkAdapter.setOnItemClickListener(BookmarkFragment.this);
        }
        else{
            TextView textView = view.findViewById(R.id.textBookmark);
            textView.setText("No Bookmarked Articles");
        }
        return view;
    }

    @Override
    public void onResume() {
        if(mBookmarkAdapter!=null) {
            loadData();
            if(mHomeList.isEmpty()){
                TextView textView = view.findViewById(R.id.textBookmark);
                textView.setText("No Bookmarked Articles");
            }

            mBookmarkAdapter.swap(mHomeList);
        }
        super.onResume();
    }

    public boolean loadData() {

        boolean flag = false;
        mHomeList = new ArrayList<>();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("",Context.MODE_PRIVATE);

        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
           String textToDis = sharedPreferences.getString(entry.getKey(), "default value");

           if(textToDis.compareToIgnoreCase("default value")!=0){
               String[] dataFetched = textToDis.split("@split@");
               String imageUrl = dataFetched[0];
               String title = dataFetched[1];
               String section = dataFetched[2];
               String time = dataFetched[3];

               String articleID = dataFetched[4];
               String webURL = dataFetched[5];

               mHomeList.add(new HomeItem(imageUrl, title, section, time, entry.getKey(), webURL));
               flag=true;
           }
        }

     return flag;
    }

@Override
    public void onItemClick(int position) {

        final Intent detailIntent = new Intent(this.getActivity(), DetailHomeActivity.class);
        final HomeItem clickedItem = mHomeList.get(position);

        String idToDetailed = clickedItem.getmArticleId();
        mRequestQueue = Volley.newRequestQueue(getActivity());
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

                                int counter=0;
                                while (counter< body.length()) {
                                    JSONObject bodyj = body.getJSONObject(counter);
                                    absData = bodyj.getString("bodyHtml");
                                    counter++;
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
