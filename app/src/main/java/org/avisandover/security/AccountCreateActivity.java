package org.avisandover.security;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.avisandover.R;
import org.avisandover.AVISApplication;
import org.avisandover.network.RequestQueueSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AccountCreateActivity extends Activity implements View.OnClickListener {

    private Button button;
    private String baseUrl;
    private static final String TAG = "AccountCreateActivity";
    private String textUsername;
    private String textPassword;
    private String textReenterPassword;
    private String textName;
    private String textEmail;
    private ColorStateList defaultColor;
    private boolean isComplete = true;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountcreate);
        baseUrl = ((AVISApplication) getApplication()).getBaseUrl();

        TextView tv = findViewById(R.id.textNewAccountUsername);
        textUsername = tv.getText().toString();
        defaultColor = tv.getTextColors();
        tv = findViewById(R.id.textNewAccountPassword);
        textPassword = tv.getText().toString();
        tv = findViewById(R.id.textReenterNewAccountPassword);
        textReenterPassword = tv.getText().toString();
        tv = findViewById(R.id.textNewAccountName);
        textName = tv.getText().toString();
        tv = findViewById(R.id.textEmail);
        textEmail = tv.getText().toString();

        button = findViewById(R.id.buttonRegister);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.buttonRegister) {
            if(isComplete == false) {
                resetAllTextView();
            }
            String url = baseUrl + "/create";
            EditText editText = findViewById(R.id.newAccountUsername);
            String username = editText.getText().toString().trim().toLowerCase();
            editText = findViewById(R.id.newAccountPassword);
            String password = editText.getText().toString();
            editText = findViewById(R.id.reenterAccountPassword);
            String reenteredPassword = editText.getText().toString();
            editText = findViewById(R.id.newAccountName);
            String realname = editText.getText().toString().trim();
            editText = findViewById(R.id.newAccountEmail);
            String email = editText.getText().toString().trim();

            //Do some checks for entered fields
            TextView tv;
            if(username.equals("")) {
                tv = findViewById(R.id.textNewAccountUsername);
                tv.setText(textUsername + " (*required)");
                tv.setTextColor(Color.RED);
                isComplete = false;
            }
            if(password.equals("")) {
                tv = findViewById(R.id.textNewAccountPassword);
                tv.setText(textPassword + " (*required)");
                tv.setTextColor((Color.RED));
                isComplete = false;
            }
            if(reenteredPassword.equals("")) {
                tv = findViewById(R.id.textReenterNewAccountPassword);
                tv.setText(textReenterPassword + " (*required)");
                tv.setTextColor(Color.RED);
                isComplete = false;
            }
            if(email.equals("")) {
                tv = findViewById(R.id.textEmail);
                tv.setText(textEmail + " (*required)");
                tv.setTextColor(Color.RED);
                isComplete = false;
            }
            if(!password.equals("") && !password.trim().equals(password)) {
                tv = findViewById(R.id.textNewAccountPassword);
                tv.setText(textPassword + " (Password cannot contain leading or trailing zeros!)");
                tv.setTextColor(Color.RED);
                isComplete = false;
            }
            if(!reenteredPassword.equals("") && !reenteredPassword.trim().equals(reenteredPassword)) {
                tv = findViewById(R.id.textReenterNewAccountPassword);
                tv.setText(textReenterPassword + " (Password cannot contain leading or trailing zeros!)");
                tv.setTextColor(Color.RED);
                isComplete = false;
            }
            if(!password.equals(reenteredPassword)) {
                tv = findViewById(R.id.textReenterNewAccountPassword);
                tv.setText(textReenterPassword + " (Reenetered password does NOT match!)");
                tv.setTextColor(Color.RED);
                isComplete = false;
            } else {
                if(!checkPasswordComplexity(password)) {
                    tv = findViewById(R.id.textNewAccountPassword);
                    tv.setText(textPassword + " (Password must be at least 3 characters long!)");
                    tv.setTextColor(Color.RED);
                    isComplete = false;
                }
            }

            if(isComplete == false) {
                return;
            }

            JSONObject json = new JSONObject();
            try {
                json.put("username", username);
                json.put("password", password);
                json.put("realname", realname);
                json.put("email", email);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i(TAG, response.toString());
                            finish();
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
                    return header;
                }
            };
            RequestQueueSingleton.getInstance(this).addToRequestQueue(request);

        }
    }

    private void resetAllTextView() {
        TextView tv = findViewById(R.id.textNewAccountName);
        tv.setText(textUsername);
        tv.setTextColor(defaultColor);
        tv = findViewById(R.id.textNewAccountPassword);
        tv.setText(textPassword);
        tv.setTextColor(defaultColor);
        tv = findViewById(R.id.textReenterNewAccountPassword);
        tv.setText(textReenterPassword);
        tv.setTextColor(defaultColor);
        tv = findViewById(R.id.textNewAccountName);
        tv.setText(textName);
        tv.setTextColor(defaultColor);
        tv = findViewById(R.id.textEmail);
        tv.setText(textEmail);
        tv.setTextColor(defaultColor);

        isComplete = true;
    }

    public static boolean checkPasswordComplexity(String password) {
        int length = password.length();
        return length >= 3 ? true : false;
    }
}
