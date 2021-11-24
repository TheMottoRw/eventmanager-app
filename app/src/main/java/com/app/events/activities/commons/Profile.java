package com.app.events.activities.commons;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.events.R;
import com.app.events.activities.admin.Navigation;
import com.app.events.activities.business.Followers;
import com.app.events.activities.business.Notifications;
import com.app.events.activities.business.ViewEvents;
import com.app.events.activities.standard.EventOriganizers;
import com.app.events.activities.standard.Followings;
import com.app.events.activities.standard.LandingReservation;
import com.app.events.activities.standard.SavedWatchLater;
import com.app.events.activities.standard.Signup;
import com.app.events.activities.standard.ViewMyReservations;
import com.app.events.adapters.admin.ViewBusinessAdapter;
import com.app.events.adapters.business.ViewEventsAdapter;
import com.app.events.utils.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Profile extends Activity {

    public Helper helper;
    public ProgressDialog pgdialog;
    Toolbar toolbar;
    private TextView tvUser,tvUserType,tvPhone,tvEmail,tvAddress,tvFollowsLbl,tvFollows,tvEvents;
    private ImageView addressIcon;
    private Button btnLogout;

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
        tvAddress = findViewById(R.id.userAddress);
        tvFollowsLbl = findViewById(R.id.tvFollowsLbl);
        tvEvents = findViewById(R.id.eventsValue);
        tvFollows = findViewById(R.id.followersValue);
        addressIcon = findViewById(R.id.userAddressLabel);
        btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helper.logout();
                startActivity(new Intent(Profile.this, Signin.class));
                finish();
            }
        });
    }
    void loadProfile(){
        String category = helper.getDataValue("user_type");
        Log.d("ProCategory",category);
        if(category.equals("Business")) {
            findViewById(R.id.userEmailLabel).setVisibility(View.GONE);
            tvEmail.setVisibility(View.GONE);
            tvFollowsLbl.setText("Followers");
        }else if(category.equals("Standard")){
            addressIcon.setVisibility(View.GONE);
            tvAddress.setVisibility(View.GONE);
            tvFollowsLbl.setText("Following");
        }
        tvUser.setText(helper.getDataValue("name"));
        tvUserType.setText(helper.getDataValue("user_type"));
        tvPhone.setText(helper.getDataValue("phone"));
        tvEmail.setText(helper.getDataValue("email"));
        tvAddress.setText(helper.getDataValue("address"));

        loadUserProfileStat();
    }
    void loadUserProfileStat(){
        final String url = helper.host+"/user/profile?category="+helper.getDataValue("user_type")+"&id="+helper.getDataValue("id");
        pgdialog.show();
        Log.d("URL",url);
//        tvLoggingIn.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
// prepare the Request
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // display response
                        pgdialog.dismiss();
                        Log.d("Logresp",response);
                        try{
                            JSONObject object = new JSONObject(response);
                            if(object.getString("status").equals("ok")) {
                                if(tvUserType.getText().toString().equals("Business")){
                                    tvFollows.setText(object.getJSONObject("data").getString("followers"));
                                }else if(tvUserType.getText().toString().equals("Standard")){
                                    tvFollows.setText(object.getJSONObject("data").getString("following"));
                                }
                                tvEvents.setText(object.getJSONObject("data").getString("events"));

                            }
                        }catch (JSONException ex){
                            helper.showToast("Json error "+ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pgdialog.dismiss();
                        helper.showToast("Something went wrong");
                        Log.e("jsonerr","JSON Error "+(error!=null?error.getMessage():""));
                    }
                }
        ) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cate", "load");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", helper.getDataValue("appid"));//put your token here
                return headers;
            }
        };

// add it to the RequestQueue
        queue.add(getRequest);
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