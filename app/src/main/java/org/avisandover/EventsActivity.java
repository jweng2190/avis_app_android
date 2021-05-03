package org.avisandover;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
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

import org.avisandover.adapter.UserDataAdapter;
import org.avisandover.network.RequestQueueSingleton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EventsActivity extends BaseMenuUnloggedinActivity {
    private RecyclerView recyclerViewUpcoming;
    private RecyclerView recyclerViewPast;

    private RecyclerView.LayoutManager layoutManagerUpcoming;
    private RecyclerView.LayoutManager layoutManagerPast;

    private RecyclerView.Adapter adapterUpcoming;
    private RecyclerView.Adapter adapterPast;
    private Context mContext;
    private static final String TAG = "EventsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = findViewById(R.id.contentPane);
        View view = View.inflate(this, R.layout.layout_events, null);
        frameLayout.addView(view);
        final ProgressBar progressBar = findViewById(R.id.progress_bar);

        String baseUrl = ((AVISApplication) getApplication()).getBaseUrl();
        String eventsUrl = baseUrl + "/events/all";
        mContext = this;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, eventsUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject result) {
                        progressBar.setVisibility(View.GONE);


                        if(result.has("upcoming")) {
                            View tv = findViewById(R.id.textview_upcoming);
                            recyclerViewUpcoming = findViewById(R.id.recycler_upcoming);
                            tv.setVisibility(View.VISIBLE);
                            recyclerViewUpcoming.setVisibility(View.VISIBLE);
                            View separator = findViewById(R.id.separator);
                            separator.setVisibility(View.VISIBLE);
                            recyclerViewUpcoming.setHasFixedSize(true);
                            layoutManagerUpcoming = new LinearLayoutManager(mContext);
                            recyclerViewUpcoming.setLayoutManager(layoutManagerUpcoming);
                            try {
                                JSONArray myUpcomingEvents = result.getJSONArray("upcoming");
                                adapterUpcoming = new UserDataAdapter(myUpcomingEvents, mContext, 1);
                                recyclerViewUpcoming.setAdapter(adapterUpcoming);
                            } catch (JSONException e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }

                        if(result.has("past")) {
                            View tv = findViewById(R.id.textview_past);
                            recyclerViewPast = findViewById(R.id.recycler_past);
                            tv.setVisibility(View.VISIBLE);
                            recyclerViewPast.setVisibility(View.VISIBLE);
                            recyclerViewPast.setHasFixedSize(true);
                            layoutManagerPast = new LinearLayoutManager(mContext);
                            recyclerViewPast.setLayoutManager(layoutManagerPast);
                            try {
                                JSONArray pastEvents = result.getJSONArray("past");
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
                        } else {
                            Toast.makeText(mContext, "Error: server error!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            RequestQueueSingleton.getInstance(mContext).addToRequestQueue(request);
        }

}
