package com.example.newsapp;

import android.graphics.Color;
import android.graphics.DashPathEffect;

import android.os.Bundle;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TrendingFragment extends Fragment {

    private RequestQueue mRequestQueue;
    public int selTrend = 9;
    public ArrayList<Integer> searchValue = new ArrayList<>();
    public String searchWordFrmEditTxt = "";
    public int deSelTrend = 2;
    private LineChart mChart;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_trending, container, false);
        mChart = root.findViewById(R.id.chart);
        mChart.setTouchEnabled(true);
        mChart.setPinchZoom(true);
        mRequestQueue = Volley.newRequestQueue(getActivity());
        parseSearchJSON("Coronavirus");
        searchWordFrmEditTxt = " Coronavirus";



        final EditText editText = (EditText) root.findViewById(R.id.searchEditText);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    if(!searchValue.isEmpty()){
                        searchValue.clear();
                    }

                    Toast.makeText(getContext(),"submitted term:" +editText.getText().toString(), Toast.LENGTH_SHORT).show();
                    mRequestQueue = Volley.newRequestQueue(getActivity());
                    parseSearchJSON(editText.getText().toString());
                    searchWordFrmEditTxt = editText.getText().toString();
                    handled = true;
                }
                return false;
            }
        });


        return root;
    }


    private void setData(String searchWord) {
        int dataTrend =3;
        int dataFrag = 1;

        ArrayList<Entry> values = new ArrayList<>();

        int idx=0;
        while (idx<searchValue.size()){
            values.add(new Entry(idx, searchValue.get(idx)));
            idx++;
        }

        LineDataSet set1;

            set1 = new LineDataSet(values, "Trending Chart for "+ searchWord);

            if(selTrend>deSelTrend){
                mChart.getLegend().setTextSize(14);
                set1.setDrawIcons(false);

                set1.setColor(Color.parseColor("#8418f0"));
                set1.setCircleColor(Color.parseColor("#8418f0"));
                set1.setLineWidth(1f);
                set1.setCircleRadius(3f);
                set1.setDrawCircleHole(true);
                set1.setValueTextSize(9f);

                if(dataTrend>dataFrag){
                    set1.setFormLineWidth(1f);
                    set1.setFormSize(15.f);
                    set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
                    set1.setFormSize(15.f);
                    set1.setValueTextColor(Color.parseColor("#8418f0"));
                    mChart.getAxisLeft().setDrawGridLines(false);
                    mChart.getXAxis().setDrawGridLines(false);
                    mChart.getAxisRight().setDrawGridLines(false);
                    set1.setDrawHighlightIndicators(false);
                    YAxis leftAxis = mChart.getAxisLeft();
                    leftAxis.setDrawAxisLine(false);
                }else {
                    dataFrag = dataTrend + dataFrag;
                }



                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(set1);
                LineData data = new LineData(dataSets);
                mChart.clear();
                mChart.setData(data);
                mChart.invalidate();
            }else {
                selTrend=selTrend+deSelTrend;
            }


    }

    private void parseSearchJSON(final String searchWord) {



        String url = "https://mss-android-backend.wl.r.appspot.com/googleTrends?key=" + searchWord;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            JSONObject defaultObj = response.getJSONObject("default");

                            JSONArray timelinedataArray = defaultObj.getJSONArray("timelineData");

                            int idx2=0;
                            while (idx2< timelinedataArray.length()) {
                                JSONObject results = timelinedataArray.getJSONObject(idx2);

                                JSONArray valY = results.getJSONArray("value");

                                int y = valY.getInt(0);
                                searchValue.add(y);
                                idx2++;
                            }
                            setData(searchWord);

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
