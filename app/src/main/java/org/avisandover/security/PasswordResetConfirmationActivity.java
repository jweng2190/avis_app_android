package org.avisandover.security;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import org.avisandover.R;

public class PasswordResetConfirmationActivity extends Activity {
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset_confirmation);
        mContext = this;

        String confirmationMessage = getResources().getString(R.string.password_reset_confirmation);
        SpannableString ss = new SpannableString(confirmationMessage);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(mContext, LoginActivity.class));
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        int startPos, endPos;
        endPos = confirmationMessage.length() - 1;
        startPos = endPos - 4;
        ss.setSpan(clickableSpan, startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView tv = findViewById(R.id.textview_confirmation);
        tv.setText(ss);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tv.setHighlightColor(Color.TRANSPARENT);
    }
}