package org.avisandover;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import org.avisandover.R;

import org.avisandover.template.BaseMenuUnloggedinActivity;

public class GeneralHomeActivity extends BaseMenuUnloggedinActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = findViewById(R.id.contentPane);
        View layout = View.inflate(this, R.layout.layout_generalhome, null);
        frameLayout.addView(layout);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
