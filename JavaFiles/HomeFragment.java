package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements HomeAdapter.OnItemClickListener {


    public static final String EXTRA_URL = "imageUrl";
    public static final String EXTRA_Title = "title";
    public static final String EXTRA_Section = "section";
    public static final String EXTRA_date = "date";
    public static final String EXTRA_abstract = "abstract";
    public static final String EXTRA_ARTICLEID = "artcileID";
    public static final String EXTRA_WebURL = "webURL";
    public String absData="";
    public String[] weatherValue={"",""};
    public View view;

    private Context context;
    private RecyclerView mRecyclerView;
    private HomeAdapter mHomeAdapter;
    private ArrayList<HomeItem> mHomeList;
    private RequestQueue mRequestQueue;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

         view = inflater.inflate(R.layout.fragment_home, container, false);

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
                            mRequestQueue = Volley.newRequestQueue(getActivity());
                            parseJSON();
                        }
                    }
                }, 1000);
            }
        });

         context = getActivity().getApplicationContext();
        mRequestQueue = Volley.newRequestQueue(getActivity());
         parseWeatherJSON();


        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.homeFrame);

        frameLayout.setVisibility(view.VISIBLE);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setVisibility(view.GONE);

        mHomeList = new ArrayList<>();

        mRequestQueue = Volley.newRequestQueue(getActivity());
        parseJSON();


    return view;
    }

    private void parseJSON() {

        String url = "https://mss-android-backend.wl.r.appspot.com/guardianHomeTab";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            JSONObject data = response.getJSONObject("data");

                            JSONObject jsdg = data.getJSONObject("response");
                            JSONArray jsonArray = jsdg.getJSONArray("results");
                            int idx=0;

                            while (idx < 10) {
                                JSONObject results = jsonArray.getJSONObject(idx);
                                String sectionName = results.getString("sectionName");
                                String title = results.getString("webTitle");
                                String date = results.getString("webPublicationDate");
                                String articleId = results.getString("id");
                                String webURL = results.getString("webUrl");

                                String imageUrl = "";

                                try {
                                    JSONObject img = results.getJSONObject("fields");
                                    imageUrl = img.getString("thumbnail");
                                } catch (JSONException e) {
                                    imageUrl = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png";
                                    e.printStackTrace();
                                }
                                mHomeList.add(new HomeItem(imageUrl, title, sectionName, date, articleId, webURL));
                                idx++;

                            }

                            mHomeAdapter = new HomeAdapter(getActivity(), mHomeList);
                            mRecyclerView.setAdapter(mHomeAdapter);
                            mHomeAdapter.setOnItemClickListener(HomeFragment.this);
                            mHomeAdapter.notifyDataSetChanged();

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
    public void onResume() {
        if(mHomeAdapter!=null) mHomeAdapter.refreshList();
        super.onResume();
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
//                            Log.i("i&i2", String.valueOf(response1));
                            JSONObject content = response1.getJSONObject("content");


                            try {
                                JSONObject blocks = content.getJSONObject("blocks");
                                JSONArray body = blocks.getJSONArray("body");
//                                Log.i("body content", body.toString());

                                int idx2=0;
                                while (idx2< body.length()) {
                                    JSONObject bodyj = body.getJSONObject(idx2);
                                    absData = bodyj.getString("bodyHtml");
                                    idx2++;
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


    private String[] parseWeatherJSON() {


        String url = "https://api.openweathermap.org/data/2.5/weather?q=los+angeles,usa&units=metric&appid=33947dcfeb7cb18ba8aa5360bf9abbec";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        //Log.i("weather response", response.toString());

                        try {

                            JSONArray weatherArray = response.getJSONArray("weather");
//                            Log.i("weatherArray", weatherArray.toString());

                            JSONObject mainArray = weatherArray.getJSONObject(0);
//                            Log.i("mainArray", mainArray.toString());

                            weatherValue[0] = mainArray.getString("main");
//                            Log.i("weatherValue", weatherValue);


                            JSONObject mainObjTemp = response.getJSONObject("main");
                            int mainTemp = mainObjTemp.getInt("temp");
                            String value =""+mainTemp+" "+"\u2103";
                           // Log.i("Temp", value);
                            weatherValue[1]=value;

                            setimage(weatherValue);




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

        return weatherValue;
    }


    public void setimage(String[] val){

        ImageView ivBasicImage = (ImageView) view.findViewById(R.id.weatherImage);
        TextView weather = (TextView) view.findViewById(R.id.weather);
        TextView temp = (TextView) view.findViewById(R.id.temperature);

        if(val[0].compareTo("Clear")==0){
            Picasso.with(context).load(R.drawable.clear_weather).into(ivBasicImage);
        }else if(val[0].compareTo("Clouds")==0){
            Picasso.with(context).load(R.drawable.cloudy_weather).into(ivBasicImage);
        }else if(val[0].compareTo("Snow")==0){
            Picasso.with(context).load(R.drawable.snowy_weather).into(ivBasicImage);
        }else if(val[0].compareTo("Rain")==0 ||  val[0].compareTo("Drizzle")==0){
            Picasso.with(context).load(R.drawable.rainy_weather).into(ivBasicImage);
        }else if(val[0].compareTo("Thunderstorm")==0){
            Picasso.with(context).load(R.drawable.thunder_weather).into(ivBasicImage);
        }
        else
            Picasso.with(context).load(R.drawable.sunny_weather).into(ivBasicImage);

        weather.setText(val[0]);
        //Log.i("temp passed:", val[1]);
        temp.setText(val[1]);
    }
}
