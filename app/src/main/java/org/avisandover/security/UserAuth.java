package org.avisandover.security;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.avisandover.AVISApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UserAuth {
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Future<String> future;
    private Context mContext;
    private SharedPreferences loginPreferences;
    private int userId;
    private String username;
    private String password;

    private String baseUrl;
    private static final String TAG = "UserAuth";

    public UserAuth(Context context) {
        mContext = context;
        baseUrl =  ((AVISApplication) ((Activity) context).getApplication()).getBaseUrl();
        loginPreferences = mContext.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        if(isLoggedIn()) {
            userId = loginPreferences.getInt("user_id", -1);
            username = loginPreferences.getString("username", null);
            password = loginPreferences.getString("password", null);
        }
    }

    public void loginUser(final String username, final String password) {
        future = executor.submit(new Callable<String>() {
            @Override
            public String call() {
                String response = null;
                HttpURLConnection conn = null;
                try {
                    //to do
                    URL url = new URL(baseUrl + "/login");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    OutputStream out = new BufferedOutputStream(conn.getOutputStream());
                    String reqBody = "username=" + username + "&password=" + password;
                    byte[] reqBodyBytes = reqBody.getBytes();
                    out.write(reqBodyBytes);
                    out.flush();
                    out.close();

                    int statusCode = conn.getResponseCode();
                    if(statusCode == 200) {
                        /*InputStream in = new BufferedInputStream(conn.getInputStream());
                        byte[] inBytes = new byte[20];
                        StringBuilder sb = new StringBuilder();
                        String s;
                        while (in.read(inBytes) != -1) {
                            s = new String(inBytes);
                            sb.append(s);
                        }
                        response = sb.toString();*/
                        try (BufferedReader br = new BufferedReader(
                                new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                            StringBuilder sb = new StringBuilder();
                            String line;
                            while ((line = br.readLine()) != null) {
                                sb.append(line.trim());
                            }
                            response = sb.toString();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(conn != null) {
                        conn.disconnect();
                    }
                }
                Log.d(TAG, response);

                return response;
            }
        });
    }

    public int getLoginState() {
        JSONObject json;
        int loginState = -1;

        try {
            String resp = future.get();
            json = new JSONObject(resp);
            if(json.getBoolean("success") && !json.getBoolean("reset_flag")) {
                loginState = 0;
            } else if(json.getBoolean("success") && json.getBoolean("reset_flag")) {
                loginState = 1;
            } else {
                loginState = 2;
            }
            userId = json.getInt("user_id");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return loginState;
    }

    public boolean isLoggedIn() {
        boolean isLoggedIn = loginPreferences.getBoolean("userLoggedIn", false);
        return isLoggedIn;
    }

    public int getUserId() {
        return userId;
    }
}
