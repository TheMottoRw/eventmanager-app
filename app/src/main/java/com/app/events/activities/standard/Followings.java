package com.app.events.activities.standard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.events.R;
import com.app.events.activities.admin.Navigation;
import com.app.events.activities.business.ViewEvents;
import com.app.events.activities.commons.Profile;
import com.app.events.activities.commons.Signin;
import com.app.events.adapters.admin.ViewBusinessAdapter;
import com.app.events.adapters.business.ViewEventsAdapter;
import com.app.events.adapters.standard.FollowingsAdapter;
import com.app.events.utils.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Followings extends AppCompatActivity {
    private Helper helper;
    private ProgressDialog pgdialog;
    private RecyclerView recyclerviewFollowings;
    private LinearLayoutManager layoutManager;
    private TextView headingTitle;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followings);

        helper = new Helper(this);
        pgdialog = new ProgressDialog(this);
        pgdialog.setMessage(getString(R.string.loading));

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name) + " - Followers");
        setSupportActionBar(toolbar);

        headingTitle = findViewById(R.id.heading_title);
        recyclerviewFollowings = findViewById(R.id.recyclerviewFollowings);
        layoutManager = new LinearLayoutManager(this);
        recyclerviewFollowings.setLayoutManager(layoutManager);

        loadFollowings();
    }

    void loadFollowings() {
        final String url = helper.host + "/follow/load?user_id=" + helper.getDataValue("id");
        Log.d("URL", url);
        pgdialog.show();
//        tvLoggingIn.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
// prepare the Request
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // display response
                        pgdialog.dismiss();
                        Log.d("Logresp", response);
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray arr = object.getJSONArray("data");
                            FollowingsAdapter adapter = new FollowingsAdapter(getApplicationContext(), arr);
                            if (arr.length() > 0) headingTitle.setVisibility(View.GONE);
                            else headingTitle.setVisibility(View.VISIBLE);
                            recyclerviewFollowings.setAdapter(adapter);
                        } catch (JSONException ex) {
                            helper.showToast("Json error " + ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pgdialog.dismiss();
                        helper.showToast("Something went wrong");
                        Log.e("jsonerr", "JSON Error " + (error != null ? error.getMessage() : ""));
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
        ;

// add it to the RequestQueue
        queue.add(getRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFollowings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        if (helper.hasSession()) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.standard, menu);
        } else {
            getMenuInflater().inflate(R.menu.signin, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Toast.makeText(getApplicationContext(), "Clicked standard user", Toast.LENGTH_LONG).show();

        switch (helper.getDataValue("user_type")) {
            case "Standard":

                if (id == R.id.home) {
                    Intent intent1 = new Intent(this, LandingReservation.class);
                    this.startActivity(intent1);
                    return true;
                }
                if (id == R.id.my_reservation) {
                    Intent intent1 = new Intent(this, ViewMyReservations.class);
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
                break;
            case "Admin":
                if (id == R.id.business) {
                    Intent intent1 = new Intent(this, Navigation.class);
                    this.startActivity(intent1);
                    return true;
                }
                break;

        }
        if (id == R.id.profile) {
            Intent intent1 = new Intent(this, Profile.class);
            this.startActivity(intent1);
            return true;
        }
        if (id == R.id.logout) {
            helper.logout();
            finish();
            startActivity(new Intent(Followings.this, Signin.class));
        }
        if (id == R.id.signin) {
            finish();
            startActivity(new Intent(Followings.this, Signin.class));
        }


        return super.onOptionsItemSelected(item);
    }

}