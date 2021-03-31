package org.avisandover.event;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.avisandover.R;
import org.avisandover.UserHomeActivity;
import org.avisandover.network.RequestQueueSingleton;
import org.avisandover.notification.NotificationWorker;
import org.avisandover.template.BaseMenuLoggedinActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventActivity extends BaseMenuLoggedinActivity {

    private TextView mTextView;
    private Context mContext;
    private static String TAG = "EventActivity";
    private SharedPreferences eventNotificationPref;
    private SharedPreferences.Editor prefEditor;
    private SharedPreferences settingPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        eventNotificationPref = mContext.getSharedPreferences("notifyPrefs", Context.MODE_PRIVATE);
        prefEditor = eventNotificationPref.edit();
        settingPref = mContext.getSharedPreferences("settingPrefs", MODE_PRIVATE);
        final boolean notificationSwitch = settingPref.getBoolean("upcomingEventsNotify", true);
        JSONObject event = null;
        if (getIntent().hasExtra("Event Details")) {
            try {
                event = new JSONObject(getIntent().getStringExtra("Event Details"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        final int eventId = event.optInt("event_id");
        //Spannable eventDetails = new SpannableString();
        StringBuilder sb = new StringBuilder();
        sb.append("Event Name: ");
        sb.append(event.optString("event_name")).append("\n");
        sb.append("Date: ");
        final String dateStr = event.optString("date_time");
        String dateStrFormatted = null;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date eventDate;
        try {
            eventDate = df.parse(dateStr);
            df.applyPattern("MMM d, YYYY HH:mm");
            dateStrFormatted = df.format(eventDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sb.append(dateStrFormatted).append("\n");
        sb.append("Details: ");
        sb.append(event.optString("event_description")).append("\n");
        String location, contactEmail, contactPhone;

            location = event.optString("location");
            contactEmail = event.optString("contact_email");
            contactPhone = event.optString("contact_phone");



        if(!location.equals("null")) {
            sb.append("Location: " + location).append("\n");
        }
        if(!contactEmail.equals("null") || !contactPhone.equals("null")) {
            sb.append("Contact: ");
            if(!contactEmail.equals("null")) {
                sb.append(contactEmail).append(", ");
            }
            if(!contactPhone.equals("null")) {
                sb.append(contactPhone).append("\n");
            }
        }
        String website = event.optString("website");
        if(!website.equals("null")) {
            sb.append("Meeting Link: ").append(website).append("\n");
            String meetingId = event.optString("meeting_id");
            String passcode = event.optString("passcode");
            if(!meetingId.equals("null")) {
                sb.append("Meeting ID: ").append(meetingId).append("\n");
            }
            if(!passcode.equals("null")) {
                sb.append("Passcode: ").append(passcode).append("\n");
            }
        }

        String cost = event.optString("cost");
        sb.append("Cost: ");
        if(cost.equals("0")) {
            sb.append("Free");
        } else {
            sb.append("$" + cost);
        }

        String eventContent = sb.toString();
        Pattern pattern = Pattern.compile("^([A-Za-z] *)+:", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(eventContent);
        SpannableStringBuilder spannable = new SpannableStringBuilder(eventContent);
        while(matcher.find()) {
            StyleSpan span = new StyleSpan(Typeface.BOLD_ITALIC);
            spannable.setSpan(span, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            // spannable.setSpan(new ForegroundColorSpan(Color.RED), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        Pattern pattern2 = Pattern.compile("^Meeting Link: .*$", Pattern.MULTILINE);
        Matcher matcher2 = pattern2.matcher(eventContent);
        if(matcher2.find()) {
            int offset = 14;
            int startIndex = matcher2.start() + offset;
            int endIndex = matcher2.end();
            final String meetingLink = eventContent.substring(startIndex, endIndex);
            ClickableSpan span = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    //start Zoom app or whatever other apps
                    String capitalized = meetingLink.toUpperCase();
                    PackageManager pm = getPackageManager();
                    Intent intent;
                    if(capitalized.contains("ZOOM")) {
                        intent = pm.getLaunchIntentForPackage("us.zoom.videomeetings");
                        if(intent == null) {
                            intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=us.zoom.videomeetings"));
                        }
                        startActivity(intent);
                    } else if(capitalized.contains("GOOGLE MEET")) {
                        intent = pm.getLaunchIntentForPackage("com.google.android.apps.meetings");
                        if(intent == null) {
                            intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.meetings"));
                        }
                        startActivity(intent);
                    }
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(getResources().getColor(R.color.MediumBlue));
                    ds.setUnderlineText(false);
                }
            };

            spannable.setSpan(span, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        Intent intent = getIntent();
        final int eventType = intent.getIntExtra("Event Type", 0);
        TextView tv;
        if (eventType == 1 || eventType == 2) {
            LinearLayout linearLayout = (LinearLayout) View.inflate(this, R.layout.layout_event, null);
            FrameLayout frameLayout = findViewById(R.id.contentPane);
            frameLayout.addView(linearLayout);
            tv = findViewById(R.id.textview_event);
            tv.setText(spannable);
        } else {
            LinearLayout linearLayout = (LinearLayout) View.inflate(this, R.layout.layout_past_event, null);
            FrameLayout frameLayout = findViewById(R.id.contentPane);
            frameLayout.addView(linearLayout);
            tv = findViewById(R.id.textview_past_event);
            tv.setText(spannable);
        }
        tv.setMovementMethod(LinkMovementMethod.getInstance());

        Button okButton = findViewById(R.id.button_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (eventType == 1 || eventType == 2) {
            Button unregisterButton = findViewById(R.id.button_unregister);
            String url = null;
            String confirmMsg = null;
            Class<?> clazz = null;
            int intentFlag = 0;
            if (eventType == 1) {
                unregisterButton.setText("Unregister");
                url = baseUrl + "/unsubscribe/user/event";
                confirmMsg = "Do you really want to unregister this event?";
                clazz = MyEventActivity.class;
                intentFlag = Intent.FLAG_ACTIVITY_CLEAR_TOP;
            } else if (eventType == 2) {
                unregisterButton.setText("Register");
                url = baseUrl + "/subscribe/user/event";
                confirmMsg = "Do you really want to register this event?";
                clazz = UserHomeActivity.class;
                intentFlag = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK;
            }
            final String finalUrl = url;
            final String finalMsg = confirmMsg;
            final Class<?> finalClass = clazz;
            final int finalIntentFlag = intentFlag;

            unregisterButton.setOnClickListener(new View.OnClickListener() {

                private WorkManager wm = WorkManager.getInstance(mContext);
                private WorkRequest notificationWorkRequest;

                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(mContext).setTitle("Confirmation")
                            .setMessage(finalMsg)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    JSONObject json = new JSONObject();
                                    try {
                                        json.put("username", username);
                                        json.put("event_id", eventId);


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, finalUrl, json,
                                            new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    Log.i(TAG, response.toString());
                                                    if (eventType == 1) {
                                                        String notificationWorkReqId = eventNotificationPref.getString(String.valueOf(eventId), null);
                                                        if(notificationWorkReqId != null) {
                                                            UUID uuid = UUID.fromString(notificationWorkReqId);
                                                            cancelNotification(uuid);
                                                        }

                                                    } else if (eventType == 2) {
                                                        if(notificationSwitch) {
                                                            scheduleNotification(dateStr);
                                                        }
                                                    }
                                                    Intent intent = new Intent(mContext, finalClass);
                                                    intent.putExtra("event_type", eventType);
                                                    intent.setFlags(finalIntentFlag);
                                                    startActivity(intent);
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Log.e(TAG, error.getMessage());
                                                }
                                            }) {
                                        @Override
                                        public Map<String, String> getHeaders() throws AuthFailureError {
                                            Map<String, String> header = new HashMap<>();
                                            header.put("Content-Type", "application/json");
                                            String pair = username + ":" + password;
                                            header.put("Authorization", "Basic " + Base64.encodeToString(pair.getBytes(), Base64.NO_WRAP));
                                            return header;
                                        }
                                    };
                                    RequestQueueSingleton.getInstance(mContext).addToRequestQueue(request);

                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }

                private void cancelNotification(UUID requestId) {
                    wm.cancelWorkById(requestId);
                }

                private void scheduleNotification(String eventTime) {
                    OneTimeWorkRequest.Builder builder = new OneTimeWorkRequest.Builder(NotificationWorker.class);
                    LocalDateTime eventDateTime = LocalDateTime.parse(eventTime);
                    LocalDateTime currentTime = LocalDateTime.now();
                    String eventHourMinute = eventDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));

                    long delay = currentTime.until(eventDateTime.minusHours(24), ChronoUnit.HOURS);
                    builder.setInitialDelay(delay, TimeUnit.HOURS);
                    Map<String, Object> valueMap = new HashMap<>();
                    valueMap.put("eventTime", eventHourMinute);
                    Data inputData = new Data.Builder().putAll(valueMap).build();
                    builder.setInputData(inputData);

                    notificationWorkRequest = builder.build();
                    UUID workRequestId = notificationWorkRequest.getId();
                    prefEditor.putString(String.valueOf(eventId), workRequestId.toString());
                    prefEditor.apply();
                    wm.enqueue(notificationWorkRequest);
                }

            });
        }
    }
}