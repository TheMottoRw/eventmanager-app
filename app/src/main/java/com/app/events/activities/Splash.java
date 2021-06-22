package com.app.events.activities;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.app.events.MainActivity;
import com.app.events.R;
import com.app.events.utils.Helper;

public class Splash extends AppCompatActivity {
    private Helper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        helper = new Helper(this);
        if(!helper.hasSession()){
            finish();
            startActivity(new Intent(getApplicationContext(),Signin.class));
        } else {
            if(helper.getDataValue("user_type").equals("Admin")){
                startActivity(new Intent(this, MainActivity.class));
            }else if(helper.getDataValue("user_type").equals("Standard")){
                startActivity(new Intent(this,Signin.class));
            }
        }
    }
}