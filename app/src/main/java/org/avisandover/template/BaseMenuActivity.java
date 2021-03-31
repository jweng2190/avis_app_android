package org.avisandover.template;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.avisandover.R;

public class BaseMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_menu);
        Toolbar toolbar = findViewById(R.id.toolbarCommon);
        setSupportActionBar(toolbar);
    }
}