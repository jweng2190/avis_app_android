package org.avisandover.reservation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import org.avisandover.R;
import org.avisandover.BaseMenuUnloggedinActivity;

public class ReservationListActivity extends BaseMenuUnloggedinActivity {
    private Context mContext;
    public static String[] reservations = new String[] {"Andover Highlands", "Baker's Meadow", "Behrakis", "Richard J. Bonier",
            "Burns", "Collins", "Deer Jump", "John Gardener", "Goldsmith Woodlands", "Ann Rawlins Greene", "Hammond",
            "Indian Ridge", "Peggy Keck", "Lowell & Lawrence Railroad", "Lupine", "Purdon", "Harold R. Rafton",
            "Sakowich", "Sanborn", "Shawsheen River", "Shawsheen Road", "Sherman", "Skug River", "Nat Smith", "Spalding",
            "Stanley", "Sunset Rock", "Amy Gordon Taft", "Turtle Mound-McCarthy", "Vale", "West Parish Meadow", "Wilkinson"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = findViewById(R.id.contentPane);
        View view = View.inflate(this, R.layout.layout_reservation_list, null);
        frameLayout.addView(view);
        mContext = this;

        ListView listView = findViewById(R.id.reservation_list);
        ArrayAdapter<String> resDataAdapter = new ArrayAdapter<>(this, R.layout.view_reservation, reservations);
        listView.setAdapter(resDataAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0 || position == 2 || position == 3 || position == 7 || position == 13 || position == 26) {
                    return;
                }
                Intent intent = new Intent(mContext, ReservationActivity.class);
                intent.putExtra("reservationPosId", position);
                startActivity(intent);
            }
        });
    }
}
