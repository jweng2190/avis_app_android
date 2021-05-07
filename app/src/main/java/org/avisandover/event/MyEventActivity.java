package org.avisandover.event;

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
import com.android.volley.toolbox.JsonArrayRequest;
import org.avisandover.R;
import org.avisandover.AVISApplication;
import org.avisandover.adapter.UserDataAdapter;
import org.avisandover.network.RequestQueueSingleton;
import org.avisandover.template.BaseMenuLoggedinActivity;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class MyEventActivity extends BaseMenuLoggedinActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private SharedPreferences loginPreferences;
    private static final String TAG = "MyEventActivity";
    private String baseUrl;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        FrameLayout frameLayout = findViewById(R.id.contentPane);
        View view = View.inflate(this, R.layout.layout_myevent, null);
        frameLayout.addView(view);
        TextView textView = findViewById(R.id.textview_first);
        recyclerView = findViewById(R.id.recycler_list_events);
        final ProgressBar progressBar = findViewById(R.id.progress_bar);

        baseUrl = ((AVISApplication) getApplication()).getBaseUrl();
        if(baseUrl == null) {
            baseUrl = "http://ec2-13-58-197-138.us-east-2.compute.amazonaws.com";
        }
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);

        int userId = loginPreferences.getInt("user_id", -1);
        final String username = loginPreferences.getString("username", null);
        final String password = loginPreferences.getString("password", null);

        Intent intent = getIntent();
        final int eventType = intent.getIntExtra("event_type", 0);
        String eventsUrl;
        if(eventType == 1) {
            eventsUrl = baseUrl + "/upcoming_events/user/" + userId;
            textView.setText(R.string.text_my_events);
        } else if(eventType == 2) {
            eventsUrl = baseUrl + "/other_events/user/" + userId;
            textView.setText(R.string.text_other_events);
        } else {
            eventsUrl = baseUrl + "/past_events/user/" + userId;
            textView.setText(R.string.text_past_events);
        }
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, eventsUrl, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray myEvents) {
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        recyclerView.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(mContext);
                        recyclerView.setLayoutManager(layoutManager);
                        mAdapter = new UserDataAdapter(myEvents, mContext, eventType);
                        recyclerView.setAdapter(mAdapter);
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
