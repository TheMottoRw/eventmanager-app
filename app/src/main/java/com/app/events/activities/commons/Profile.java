package com.app.events.activities.commons;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.app.events.R;
import com.app.events.activities.admin.Navigation;
import com.app.events.activities.business.Followers;
import com.app.events.activities.business.Notifications;
import com.app.events.activities.business.ViewEvents;
import com.app.events.activities.standard.EventOriganizers;
import com.app.events.activities.standard.Followings;
import com.app.events.activities.standard.LandingReservation;
import com.app.events.activities.standard.SavedWatchLater;
import com.app.events.activities.standard.ViewMyReservations;
import com.app.events.adapters.admin.ViewBusinessAdapter;
import com.app.events.adapters.business.ViewEventsAdapter;
import com.app.events.utils.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Profile extends Activity {

    public Helper helper;
    public ProgressDialog pgdialog;
    Toolbar toolbar;
    private TextView tvUser,tvUserType,tvPhone,tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        helper = new Helper(this);
        pgdialog = new ProgressDialog(this);
        pgdialog.setMessage(getString(R.string.loading));
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name)+" - Profile");
        initUi();
        loadProfile();
    }

    void initUi(){
        tvUser = findViewById(R.id.user);
        tvUserType = findViewById(R.id.userType);
        tvPhone = findViewById(R.id.userPhone);
        tvEmail = findViewById(R.id.userEmail);
    }
    void loadProfile(){
        tvUser.setText(helper.getDataValue("name"));
        tvUserType.setText(helper.getDataValue("user_type"));
        tvPhone.setText(helper.getDataValue("phone"));
        tvEmail.setText(helper.getDataValue("email"));
    }
    @Override
    public void onResume(){
        super.onResume();
        loadProfile();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        if(helper.hasSession()) {
            MenuInflater inflater = getMenuInflater();
            if(helper.getDataValue("user_type").equals("Standard")) {
                inflater.inflate(R.menu.standard, menu);
            }else if(helper.getDataValue("user_type").equals("Business")){
                inflater.inflate(R.menu.business,menu);
            }else if(helper.getDataValue("user_type").equals("Admin")){
                inflater.inflate(R.menu.admin,menu);
            }
        }else{
            getMenuInflater().inflate(R.menu.signin,menu);
        }

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (helper.getDataValue("user_type")){
            case "Standard":
                if (id == R.id.my_reservation) {
                    Intent intent1 = new Intent(this, ViewMyReservations.class);
                    this.startActivity(intent1);
                    return true;
                }

                if (id == R.id.followings) {
                    Intent intent1 = new Intent(this, Followings.class);
                    this.startActivity(intent1);
                    return true;
                }
                if (id == R.id.business) {
                    Intent intent1 = new Intent(this, EventOriganizers.class);
                    this.startActivity(intent1);
                    return true;
                }

                if (id == R.id.watch_later) {
                    Intent intent1 = new Intent(this, SavedWatchLater.class);
                    this.startActivity(intent1);
                    return true;
                }
                break;
            case "Business":
                if (id == R.id.events) {
                    Intent intent1 = new Intent(this, ViewEvents.class);
                    this.startActivity(intent1);
                    return true;
                }

                if (id == R.id.followers) {
                    Intent intent1 = new Intent(this, Followers.class);
                    this.startActivity(intent1);
                    return true;
                }

                if (id == R.id.notifications) {
                    Intent intent1 = new Intent(this, Notifications.class);
                    this.startActivity(intent1);
                    return true;
                }
                break;
            case "Admin":
                if (id == R.id.business) {
                    Intent intent1 = new Intent(this, Navigation.class);
                    this.startActivity(intent1);
                    return true;
                }
                break;

        }
        if(id == R.id.logout){
            helper.logout();
            finish();
            startActivity(new Intent(Profile.this, Signin.class));
        }

        return super.onOptionsItemSelected(item);
    }

}