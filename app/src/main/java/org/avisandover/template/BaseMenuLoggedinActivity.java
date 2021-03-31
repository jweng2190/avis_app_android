package org.avisandover.template;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.avisandover.R;
import org.avisandover.AVISApplication;
import org.avisandover.search.SearchActivity;
import org.avisandover.settings.SettingActivity;

public class BaseMenuLoggedinActivity extends BaseMenuActivity {

    protected String username;
    protected String password;
    protected String baseUrl;
    private SharedPreferences loginPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        username = loginPreferences.getString("username", null);
        password = loginPreferences.getString("password", null);
        baseUrl = ((AVISApplication) getApplication()).getBaseUrl();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_loggedin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;

        switch(id) {
            case R.id.action_settings:
                intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.search_menu:
                intent = new Intent(this, SearchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.favorite_menu:
                // To do
                break;
            case R.id.about_menu:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                AlertDialog alertDialog = builder.create();
                alertDialog.setTitle("About");
                alertDialog.setMessage("AVIS V1");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
