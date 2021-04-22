package org.avisandover.reservation;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ReservationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView myWebView = new WebView(this);
        setContentView(myWebView);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        int reservationPosId = getIntent().getIntExtra("reservationPosId", -1);
        switch (ReservationListActivity.reservations[reservationPosId]) {
            case "Andover Highlands":
                break;
            case "Baker's Meadow":
                myWebView.loadUrl("file:///android_asset/bakers_meadow.html");
                break;
            case "Deer Jump":
                myWebView.loadUrl("file:///android_asset/deerjump.html");
                break;
        }


    }
}
