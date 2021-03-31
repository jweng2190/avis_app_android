package org.avisandover.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.avisandover.GeneralHomeActivity;
import org.avisandover.R;
import org.avisandover.AVISApplication;
import org.avisandover.network.RequestQueueSingleton;

public class SettingActivity extends Activity {

    private SharedPreferences loginPreferences;
    private String baseUrl;
    private static final String TAG = "SettingActivity";
    private Activity activity = this;
    private SharedPreferences settingPreferences;
    private SharedPreferences.Editor settingPrefsEditor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        baseUrl = ((AVISApplication) getApplication()).getBaseUrl();
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        settingPreferences = getSharedPreferences("settingPrefs", MODE_PRIVATE);
        settingPrefsEditor = settingPreferences.edit();

        TextView textView = findViewById(R.id.logout_text);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(activity).setTitle("Confirm")
                        .setMessage("Are you sure to log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String logoutUrl = baseUrl + "/logout";
                                StringRequest request = new StringRequest(Request.Method.GET, logoutUrl,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                SharedPreferences.Editor editor = loginPreferences.edit();
                                                if(loginPreferences.getBoolean("remember_me", true)) {
                                                    editor.remove("userLoggedIn");
                                                    editor.remove("user_id");
                                                    editor.remove("password");
                                                } else {
                                                    editor.clear();
                                                }
                                                editor.apply();
                                                Intent intent = new Intent(activity, GeneralHomeActivity.class);
                                                startActivity(intent);
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Log.e(TAG, error.getMessage());
                                            }
                                        });
                                RequestQueueSingleton.getInstance(activity).addToRequestQueue(request);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });

        Switch s1 = findViewById(R.id.switch1);
        s1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d(TAG, "Push notification enabled");
                } else {
                    Log.d(TAG, "Push notification disabled");
                }
            }
        });

        Switch s2 = findViewById(R.id.switch2);
        s2.setChecked(settingPreferences.getBoolean("upcomingEventsNotify", true));
        s2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    settingPrefsEditor.putBoolean("upcomingEventsNotify", true);
                } else {
                    settingPrefsEditor.putBoolean("upcomingEventsNotify", false);
                }
                settingPrefsEditor.apply();
            }
        });
    }
}
