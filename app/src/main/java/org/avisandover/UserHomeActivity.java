package org.avisandover;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import org.avisandover.R;

import org.avisandover.event.MyEventActivity;
import org.avisandover.template.BaseMenuLoggedinActivity;

public class UserHomeActivity extends BaseMenuLoggedinActivity {
    private static final String TAG = "UserHomeActivity";
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        FrameLayout frameLayout = findViewById(R.id.contentPane);
        View layout = View.inflate(this, R.layout.layout_userhome, null);
        frameLayout.addView(layout);
        View ivMyEvent = findViewById(R.id.ivMyEvent);
        ivMyEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MyEventActivity.class);
                intent.putExtra("event_type", 1);
                startActivity(intent);
            }
        });
        View ivOtherEvent = findViewById(R.id.ivUpcomingEvent);
        ivOtherEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MyEventActivity.class);
                intent.putExtra("event_type", 2);
                startActivity(intent);
            }
        });
        View ivPastEvent = findViewById(R.id.ivPastEvent);
        ivPastEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MyEventActivity.class);
                intent.putExtra("event_type", 3);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
