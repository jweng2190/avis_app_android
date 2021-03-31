package org.avisandover.security;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.avisandover.R;
import org.avisandover.UserHomeActivity;

public class LoginActivity extends Activity implements View.OnClickListener {

    private Button ok;
    private EditText editTextUsername, editTextPassword;
    private CheckBox saveLoginCheckBox;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private UserAuth userAuth;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;

        userAuth = new UserAuth(this);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);

        ok = findViewById(R.id.buttonLogin);
        ok.setOnClickListener(this);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        saveLoginCheckBox = findViewById(R.id.saveLoginCheckBox);
        loginPrefsEditor = loginPreferences.edit();

        String username = loginPreferences.getString("username", null);
        editTextUsername.setText(username);

        SpannableString ss = new SpannableString("No account yet? Create a new account here.");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(LoginActivity.this, AccountCreateActivity.class));
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, 37, 41, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView textView = findViewById(R.id.createAccountLink);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(Color.TRANSPARENT);

        SpannableString ss2 = new SpannableString("Forget your password? Click here to reset the password.");
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                String username = editTextUsername.getText().toString().trim().toLowerCase();
                Intent intent = new Intent(mContext, PasswordResetActivity.class);
                intent.putExtra("prefilled_username", username);
                startActivity(intent);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss2.setSpan(clickableSpan2, 28, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView textView2 = findViewById(R.id.forgetPasswordLink);
        textView2.setText(ss2);
        textView2.setMovementMethod(LinkMovementMethod.getInstance());
        textView2.setHighlightColor(Color.TRANSPARENT);
    }

    public void onClick(View view) {
        saveLoginCheckBox = findViewById(R.id.saveLoginCheckBox);
        if(saveLoginCheckBox.isChecked()) {
            loginPrefsEditor.putBoolean("remember_me", true);
        } else {
            loginPrefsEditor.putBoolean("remember_me", false);
        }
        loginPrefsEditor.apply();

        String username, password;
        if (view == ok) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editTextUsername.getWindowToken(), 0);

            username = editTextUsername.getText().toString().toLowerCase();
            password = editTextPassword.getText().toString();

            userAuth.loginUser(username, password);
            int loginState = userAuth.getLoginState();
            if(loginState == 0) {
                loginPrefsEditor.putBoolean("userLoggedIn", true);
                loginPrefsEditor.putInt("user_id", userAuth.getUserId());
                loginPrefsEditor.putString("username", username);
                loginPrefsEditor.putString("password", password);
                loginPrefsEditor.apply();
                Intent intent = new Intent(this, UserHomeActivity.class);
                startActivity(intent);
            } else if(loginState == 1) {
                loginPrefsEditor.putBoolean("userLoggedIn", true);
                loginPrefsEditor.putInt("user_id", userAuth.getUserId());
                loginPrefsEditor.putString("username", username);
                loginPrefsEditor.putString("password", password);
                loginPrefsEditor.apply();
                Intent intent = new Intent(this, ForceChangePasswordActivity.class);
                startActivity(intent);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                AlertDialog dialog = builder.setMessage("Login Failed!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
                dialog.show();
            }
        }
    }

}
