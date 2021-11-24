package com.app.events.activities.commons;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.app.events.R;
import com.app.events.activities.business.BusinessSignup;
import com.app.events.activities.standard.Signup;

public class SplashAccountActivity extends AppCompatActivity {
    public Button btnLogin,btnSignupStandard,btnSignupBusiness;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_account);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignupStandard = findViewById(R.id.btnSignupStandard);
        btnSignupBusiness = findViewById(R.id.btnSignupBusiness);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashAccountActivity.this,Signin.class));
            }
        });
        btnSignupBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashAccountActivity.this, BusinessSignup.class));
            }
        });
        btnSignupStandard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashAccountActivity.this, Signup.class));
            }
        });
    }
}