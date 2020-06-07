package com.example.newsapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TabFragment extends Fragment implements HomeAdapter.OnItemClickListener{

    int position;
    private TextView textView;
    public View view;

    public static final String EXTRA_URL = "imageUrl";
    public static final String EXTRA_Title = "title";
    public static final String EXTRA_Section = "section";
    public static final String EXTRA_date = "date";
    public static final String EXTRA_abstract = "abstract";
    public static final String EXTRA_ARTICLEIDTab = "artcileID";
    public String absData="";


    private RecyclerView mRecyclerView;
    private HomeAdapter mHomeAdapter;
    private ArrayList<HomeItem> mHomeList;
    private RequestQueue mRequestQueue;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static Fragment getInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        TabFragment tabFragment = new TabFragment();
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("pos");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_tab, container, false);
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

        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.HeadlinesFrame);

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
String url ="";

        if(position==0)
            url = "https://mss-android-backend.wl.r.appspot.com/guardianWorldTab";

        else if(position==1)
            url = "https://mss-android-backend.wl.r.appspot.com/guardianBusinessTab";

        else if(position==2)
            url = "https://mss-android-backend.wl.r.appspot.com/guardianPoliticsTab";

        else if(position==3)
            url = "https://mss-android-backend.wl.r.appspot.com/guardianSportTab";

        else if(position==4)
            url = "https://mss-android-backend.wl.r.appspot.com/guardianTechTab";

        else if(position==5)
            url = "https://mss-android-backend.wl.r.appspot.com/guardianScienceTab";



        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {

                            JSONObject data = response.getJSONObject("data");

                            JSONObject jsdg = data.getJSONObject("response");
                            JSONArray jsonArray = jsdg.getJSONArray("results");

                            int idx=0;
                            while (idx< 10) {
                                JSONObject results = jsonArray.getJSONObject(idx);

                                String sectionName = results.getString("sectionName");
                                String title = results.getString("webTitle");
                                String date = results.getString("webPublicationDate");
                                String articleId = results.getString("id");
                                String webURL = results.getString("webUrl");

                                String imageUrl = "";

                                try {

                                    JSONObject blocks = results.getJSONObject("blocks");
                                    JSONObject main = blocks.getJSONObject("main");
                                    JSONArray elements = main.getJSONArray("elements");
                                    JSONObject elements0 = elements.getJSONObject(0);
                                    JSONArray assets = elements0.getJSONArray("assets");
                                    JSONObject assetslast = assets.getJSONObject(0);
                                    imageUrl = assetslast.getString("file");
                                } catch (JSONException e) {
                                    imageUrl = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png";
                                }

                                mHomeList.add(new HomeItem(imageUrl, title, sectionName, date, articleId, webURL));
                                idx++;

                            }


                            mHomeAdapter = new HomeAdapter(getActivity(), mHomeList);
                            mRecyclerView.setAdapter(mHomeAdapter);
                            mHomeAdapter.setOnItemClickListener(TabFragment.this);
                            FrameLayout frame = view.findViewById(R.id.HeadlinesFrame);
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




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
                                detailIntent.putExtra(EXTRA_ARTICLEIDTab,clickedItem.getmArticleId());
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

    @Override
    public void onResume() {
        if(mHomeAdapter!=null) mHomeAdapter.refreshList();
        super.onResume();
    }

}
