package org.avisandover.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.avisandover.R;
import org.avisandover.event.EventActivity;
import org.avisandover.event.EventBrief;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserDataAdapter extends RecyclerView.Adapter<UserDataAdapter.EventViewHolder> {
    private JSONArray mDataset;
    private Context mContext;
    private int eventType;
    private static final String TAG = "UserDataAdapter";

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public EventViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public UserDataAdapter(JSONArray myDataset, Context context, int eventType) {
        mDataset = myDataset;
        mContext = context;
        this.eventType = eventType;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent,
                                              int viewType) {
        // create a new view
        TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.user_event_view, parent, false);
        GradientDrawable drawable = (GradientDrawable) v.getBackground();
        if(eventType == 1) {
            //v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.Orange));
            drawable.setColor(ContextCompat.getColor(mContext, R.color.SpringGreen));
        } else if(eventType == 2) {
            //v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.SpringGreen));
            drawable.setColor(ContextCompat.getColor(mContext, R.color.Orange));
        } else {
            //v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.LightCoral));
            drawable.setColor(ContextCompat.getColor(mContext, R.color.LightCoral));
        }
        EventViewHolder vh = new EventViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        EventBrief brief = new EventBrief();
        JSONObject current = null;
        try {
            current = mDataset.getJSONObject(position);
            String dateStr = current.optString("date_time");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date eventDate = df.parse(dateStr);
            brief.setEventDate(eventDate);
            brief.setEventName(current.optString("event_name"));
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
        final JSONObject eventJson = current;

        holder.textView.setText(brief.toString());
        holder.textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Log.i(TAG, "Detailed event info");
                if(eventJson != null) {
                    Intent intent = new Intent(mContext, EventActivity.class);
                    intent.putExtra("Event Details", eventJson.toString());
                    intent.putExtra("Event Type", eventType);
                    mContext.startActivity(intent);
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length();
    }

}
