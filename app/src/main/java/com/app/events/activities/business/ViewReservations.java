package com.app.events.activities.business;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.events.R;
import com.app.events.activities.commons.Signin;
import com.app.events.activities.standard.LandingReservation;
import com.app.events.adapters.business.ViewEventsAdapter;
import com.app.events.adapters.business.ViewReservationsAdapter;
import com.app.events.utils.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ViewReservations extends AppCompatActivity {

    public Helper helper;
    public ProgressDialog pgdialog;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TextView tvEventName,tvKickoffDate;
    private Toolbar toolbar;
    private RelativeLayout rtvReservationHeader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reservations);
        helper = new Helper(this);
        pgdialog = new ProgressDialog(this);
        pgdialog.setMessage(getString(R.string.loading));
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.app_name) + " - Reservations");

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        recyclerView = findViewById(R.id.recycler_reservations);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        tvEventName = findViewById(R.id.eventName);
        tvKickoffDate = findViewById(R.id.eventStart);
        rtvReservationHeader = findViewById(R.id.rtvReservationHeader);
        loadEventsReservation();
    }
    void loadEventsReservation(){
        final String url = helper.host+"/reservation/load?event_id="+getIntent().getStringExtra("event_id");
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
                                JSONArray arr = object.getJSONArray("data");
                                if(arr.length()>0) {
                                    SimpleDateFormat sda = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                    SimpleDateFormat sdf = new SimpleDateFormat("E,MMM dd . hh:mm a");

                                    tvEventName.setText(arr.getJSONObject(0).getString("event_name"));
                                    tvKickoffDate.setText(sdf.format(sda.parse(arr.getJSONObject(0).getString("event_kikoff"))));
                                }else{
                                    rtvReservationHeader.setVisibility(View.GONE);
                                }
                                ViewReservationsAdapter adapter = new ViewReservationsAdapter(getApplicationContext(), arr);
                                recyclerView.setAdapter(adapter);
                            }
                        }catch (JSONException ex){
                            helper.showToast("Json error "+ex.getMessage());
                        } catch (ParseException e) {
                            e.printStackTrace();
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
        };;

// add it to the RequestQueue
        queue.add(getRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.business, menu);

        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        searchViewItem.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
        }
        if (id == R.id.home) {
            Intent intent1 = new Intent(this, LandingReservation.class);
            this.startActivity(intent1);
            return true;
        }
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
        if (id == R.id.report) {
            startActivity(new Intent(ViewReservations.this, ReservationReport.class));
        }
        if (id == R.id.logout) {
            helper.logout();
            finish();
            startActivity(new Intent(ViewReservations.this, Signin.class));
        }


        return super.onOptionsItemSelected(item);
    }

}