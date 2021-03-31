package org.avisandover.search;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.avisandover.R;
import org.avisandover.AVISApplication;
import org.avisandover.adapter.UserDataAdapter;
import org.avisandover.network.RequestQueueSingleton;
import org.avisandover.template.BaseMenuLoggedinActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SearchResultActivity extends BaseMenuLoggedinActivity {

    private RecyclerView recyclerViewMyUpcoming;
    private RecyclerView recyclerViewOtherUpcoming;
    private RecyclerView recyclerViewPast;

    private RecyclerView.LayoutManager layoutManagerMyUpcoming;
    private RecyclerView.LayoutManager layoutManagerOtherUpcoming;
    private RecyclerView.LayoutManager layoutManagerPast;

    private RecyclerView.Adapter adapterMyUpcoming;
    private RecyclerView.Adapter adapterOtherUpcoming;
    private RecyclerView.Adapter adapterPast;

    private SharedPreferences loginPreferences;
    private static final String TAG = "SearchResultActivity";
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        final FrameLayout frameLayout = findViewById(R.id.contentPane);
        View view = View.inflate(this, R.layout.layout_search_result, null);
        frameLayout.addView(view);
        final ProgressBar progressBar = findViewById(R.id.progress_bar);

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        final String username = loginPreferences.getString("username", null);
        final String password = loginPreferences.getString("password", null);

        Intent intent = getIntent();
        final String searchKeywords = intent.getStringExtra("search_keywords");
        String baseUrl = ((AVISApplication) getApplication()).getBaseUrl();
        String searchUrl = baseUrl + "/events?q=" + searchKeywords;
        final TextView title = findViewById(R.id.search_result_title);
        title.setText("Events related to your search \"" + searchKeywords + "\"");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, searchUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject searchResult) {
                        progressBar.setVisibility(View.GONE);
                        if(searchResult.has("result_found")) {
                            title.setText("No matching events found related to your search \"" + searchKeywords + "\"!");
                            return;
                        }
                        if(searchResult.has("my_upcoming")) {
                            View tv = findViewById(R.id.textview_my_upcoming);
                            recyclerViewMyUpcoming = findViewById(R.id.recycler_my_upcoming);
                            tv.setVisibility(View.VISIBLE);
                            recyclerViewMyUpcoming.setVisibility(View.VISIBLE);
                            recyclerViewMyUpcoming.setHasFixedSize(true);
                            layoutManagerMyUpcoming = new LinearLayoutManager(mContext);
                            recyclerViewMyUpcoming.setLayoutManager(layoutManagerMyUpcoming);
                            try {
                                JSONArray myUpcomingEvents = searchResult.getJSONArray("my_upcoming");
                                adapterMyUpcoming = new UserDataAdapter(myUpcomingEvents, mContext, 1);
                                recyclerViewMyUpcoming.setAdapter(adapterMyUpcoming);
                            } catch (JSONException e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                        if(searchResult.has("other_upcoming")) {
                            View tv = findViewById(R.id.textview_other_upcoming);
                            recyclerViewOtherUpcoming = findViewById(R.id.recycler_other_upcoming);
                            tv.setVisibility(View.VISIBLE);
                            recyclerViewOtherUpcoming.setVisibility(View.VISIBLE);
                            recyclerViewOtherUpcoming.setHasFixedSize(true);
                            layoutManagerOtherUpcoming = new LinearLayoutManager(mContext);
                            recyclerViewOtherUpcoming.setLayoutManager(layoutManagerOtherUpcoming);
                            try {
                                JSONArray otherUpcomingEvents = searchResult.getJSONArray("other_upcoming");
                                adapterOtherUpcoming = new UserDataAdapter(otherUpcomingEvents, mContext, 2);
                                recyclerViewOtherUpcoming.setAdapter(adapterOtherUpcoming);
                            } catch (JSONException e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                        if(searchResult.has("past")) {
                            View tv = findViewById(R.id.textview_past);
                            recyclerViewPast = findViewById(R.id.recycler_past);
                            tv.setVisibility(View.VISIBLE);
                            recyclerViewPast.setVisibility(View.VISIBLE);
                            recyclerViewPast.setHasFixedSize(true);
                            layoutManagerPast = new LinearLayoutManager(mContext);
                            recyclerViewPast.setLayoutManager(layoutManagerPast);
                            try {
                                JSONArray pastEvents = searchResult.getJSONArray("past");
                                adapterPast = new UserDataAdapter(pastEvents, mContext, 3);
                                recyclerViewPast.setAdapter(adapterPast);
                            } catch (JSONException e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                    }

                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.getMessage());
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(mContext, "Error: network timeout!",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(mContext, "Error: authentication failed!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<>();
                String pair = username + ":" + password;
                header.put("Authorization", "Basic " + Base64.encodeToString(pair.getBytes(), Base64.NO_WRAP));
                return header;
            }
        };

        RequestQueueSingleton.getInstance(mContext).addToRequestQueue(request);
    }
}
