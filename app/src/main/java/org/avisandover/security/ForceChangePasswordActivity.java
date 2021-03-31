package org.avisandover.security;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.avisandover.R;
import org.avisandover.AVISApplication;
import org.avisandover.UserHomeActivity;
import org.avisandover.network.RequestQueueSingleton;

import java.util.HashMap;
import java.util.Map;

public class ForceChangePasswordActivity extends Activity implements View.OnClickListener {

    private EditText etNewPassword;
    private EditText etConfirmedNewPassword;
    private static final String TAG = "ForceChangePasswordActivity";
    private Context mContext;
    private String username;
    private String tempPassword;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_force_change_password);
        mContext = this;

        Button okButton = findViewById(R.id.button_ok);
        okButton.setOnClickListener(this);

        etNewPassword = findViewById(R.id.new_password);
        etConfirmedNewPassword = findViewById(R.id.confirmed_new_password);

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        username = loginPreferences.getString("username", null);
        tempPassword = loginPreferences.getString("password", null);
    }

    @Override
    public void onClick(View v) {
        final String newPassword = etNewPassword.getText().toString();
        String confirmedNewPassword = etConfirmedNewPassword.getText().toString();

        if(newPassword.equals(confirmedNewPassword)) {
            if(!AccountCreateActivity.checkPasswordComplexity(newPassword)) {
                new AlertDialog.Builder(mContext)
                        .setTitle("Error")
                        .setMessage("Password must be at least 3 characters long!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                return;
            }
            String baseUrl = ((AVISApplication) getApplication()).getBaseUrl();
            StringRequest request = new StringRequest(Request.Method.POST, baseUrl+"/passwd_change",
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            if(response.equals("change done")) {
                                loginPrefsEditor.putString("password", newPassword);
                                loginPrefsEditor.apply();
                                Intent intent = new Intent(mContext, UserHomeActivity.class);
                                startActivity(intent);
                            } else {
                                new AlertDialog.Builder(mContext)
                                        .setTitle("Error")
                                        .setMessage("Password change failed! Please retry.")
                                        .setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
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
                public Map<String, String> getParams() {
                    Map<String, String> paramMap = new HashMap<>();
                    paramMap.put("password", newPassword);
                    return paramMap;
                }
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header = new HashMap<>();
                    String pair = username + ":" + tempPassword;
                    header.put("Authorization", "Basic " + Base64.encodeToString(pair.getBytes(), Base64.NO_WRAP));
                    return header;
                }
            };

            RequestQueueSingleton.getInstance(mContext).addToRequestQueue(request);

        } else {
            new AlertDialog.Builder(mContext)
                    .setTitle("Error")
                    .setMessage("Passwords do not match!")
                    .setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
}