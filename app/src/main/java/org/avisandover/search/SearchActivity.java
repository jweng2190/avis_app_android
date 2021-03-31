package org.avisandover.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.textfield.TextInputLayout;
import org.avisandover.R;
import org.avisandover.template.BaseMenuLoggedinActivity;

public class SearchActivity extends BaseMenuLoggedinActivity {
    private static final String TAG = "SearchActivity";
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        FrameLayout frameLayout = findViewById(R.id.contentPane);
        View view = View.inflate(this, R.layout.layout_search, null);
        frameLayout.addView(view);
        final TextInputLayout layout = findViewById(R.id.search_text_field);
        layout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SearchResultActivity.class);
                intent.putExtra("search_keywords", layout.getEditText().getText().toString().trim());
                startActivity(intent);
            }
        });
    }
}
