package org.avisandover;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.avisandover.security.UserAuth;

public class MainActivity extends AppCompatActivity {

    private UserAuth auth;
    public static final String CHANNEL_ID = "sc_connect";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AVISApplication) getApplication()).setBaseUrl("http://72.167.43.203");
        createNotificationChannel();
        auth = new UserAuth(this);
        Intent intent;
        if(auth.isLoggedIn()) {
            intent = new Intent(this, UserHomeActivity.class);
        } else {
            intent = new Intent(this, GeneralHomeActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}