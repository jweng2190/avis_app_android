package org.avisandover.security;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.avisandover.R;
import org.avisandover.AVISApplication;
import org.avisandover.network.RequestQueueSingleton;

import java.util.HashMap;
import java.util.Map;

public class PasswordResetActivity extends Activity implements View.OnClickListener {

    private Button okButton;
    private EditText editTextUsername;
    private Context mContext;
    private static final String TAG = "PasswordResetActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        mContext = this;

        String prefilledUsername = getIntent().getStringExtra("prefilled_username");
        editTextUsername = findViewById(R.id.username);
        editTextUsername.setText(prefilledUsername);

        okButton = findViewById(R.id.button_ok);
        okButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        final String username = editTextUsername.getText().toString().trim().toLowerCase();
        //Log.d(TAG, "username=" + username);

        String baseUrl = ((AVISApplication) getApplication()).getBaseUrl();
        StringRequest request = new StringRequest(Request.Method.POST, baseUrl+"/passwd_reset",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if(response.equals("reset done")) {
                            Intent intent = new Intent(mContext, PasswordResetConfirmationActivity.class);
                            startActivity(intent);
                        } else {
                            new AlertDialog.Builder(mContext)
                                    .setTitle("Error")
                                    .setMessage("Username does not exist!")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
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
                paramMap.put("username", username);
                return paramMap;
            }
        };

        RequestQueueSingleton.getInstance(mContext).addToRequestQueue(request);

    }
}
