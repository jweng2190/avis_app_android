package org.avisandover.reservation;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
                if (webResourceRequest.getUrl().getScheme().equals("file")) {
                    //WebSettings settings = webView.getSettings();
                    //settings.setJavaScriptEnabled(true);
                    webView.loadUrl(webResourceRequest.getUrl().toString());
                } else {
                    // If the URI is not pointing to a local file, open with an ACTION_VIEW Intent
                    webView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, webResourceRequest.getUrl()));
                }
                return true;
            }
        });
        int reservationPosId = getIntent().getIntExtra("reservationPosId", -1);
        switch (ReservationListActivity.reservations[reservationPosId]) {
            case "Andover Highlands":
                break;
            case "Baker's Meadow":
                myWebView.loadUrl("file:///android_asset/bakers_meadow.html");
                break;
            case "Behrakis":
                break;
            case "Richard J. Bonier":
                break;
            case "Burns":
                myWebView.loadUrl("file:///android_asset/burns.html");
                break;
            case "Collins":
                myWebView.loadUrl("file:///android_asset/collins.html");
                break;
            case "Deer Jump":
                myWebView.loadUrl("file:///android_asset/deerjump.html");
                break;
            case "John Gardener":
                break;
            case "Goldsmith Woodlands":
                myWebView.loadUrl("file:///android_asset/goldsmith_woodlands.html");
                break;
            case "Ann Rawlins Greene":
                myWebView.loadUrl("file:///android_asset/ann_rawlins_greene.html");
                break;
            case "Hammond":
                myWebView.loadUrl("file:///android_asset/hammond.html");
                break;
            case "Indian Ridge":
                myWebView.loadUrl("file:///android_asset/indian_ridge.html");
                break;
            case "Peggy Keck":
                myWebView.loadUrl("file:///android_asset/peggy_keck.html");
                break;
            case "Lowell & Lawrence Railroad":
                break;
            case "Lupine":
                myWebView.loadUrl("file:///android_asset/lupine.html");
                break;
            case "Purdon":
                myWebView.loadUrl("file:///android_asset/purdon.html");
                break;
            case "Harold R. Rafton":
                myWebView.loadUrl("file:///android_asset/harold_r_rafton.html");
                break;
            case "Sakowich":
                myWebView.loadUrl("file:///android_asset/sakowich.html");
                break;
            case "Sanborn":
                myWebView.loadUrl("file:///android_asset/sanborn.html");
                break;
            case "Shawsheen River":
                myWebView.loadUrl("file:///android_asset/shawsheen_river.html");
                break;
            case "Shawsheen Road":
                myWebView.loadUrl("file:///android_asset/shawsheen_road.html");
                break;
            case "Sherman":
                myWebView.loadUrl("file:///android_asset/sherman.html");
                break;
            case "Skug River":
                myWebView.loadUrl("file:///android_asset/skug_river.html");
                break;
            case "Nat Smith":
                myWebView.loadUrl("file:///android_asset/nat_smith.html");
                break;
            case "Spalding":
                myWebView.loadUrl("file:///android_asset/spalding.html");
                break;
            case "Stanley":
                myWebView.loadUrl("file:///android_asset/stanley.html");
                break;
            case "Sunset Rock":
                break;
            case "Amy Gordon Taft":
                myWebView.loadUrl("file:///android_asset/amy_gordon_taft.html");
                break;
            case "Turtle Mound-McCarthy":
                myWebView.loadUrl("file:///android_asset/turtle_mound.html");
                break;
            case "Vale":
                myWebView.loadUrl("file:///android_asset/vale.html");
                break;
            case "West Parish Meadow":
                myWebView.loadUrl("file:///android_asset/west_parish_meadow.html");
                break;
            case "Wilkinson":
                myWebView.loadUrl("file:///android_asset/wilkinson.html");
                break;
        }


    }
}
