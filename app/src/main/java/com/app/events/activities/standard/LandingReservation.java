package com.app.events.activities.standard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.assist.AssistStructure;
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
import com.app.events.activities.admin.ViewBusiness;
import com.app.events.activities.business.Followers;
import com.app.events.activities.business.Notifications;
import com.app.events.activities.business.ReservationReport;
import com.app.events.activities.business.ViewEvents;
import com.app.events.activities.commons.Profile;
import com.app.events.activities.commons.Signin;
import com.app.events.activities.commons.SplashAccountActivity;
import com.app.events.adapters.admin.ViewBusinessAdapter;
import com.app.events.adapters.business.ViewEventsAdapter;
import com.app.events.adapters.business.ViewEventsAdapter3;
import com.app.events.utils.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LandingReservation extends AppCompatActivity {
    public Helper helper;
    public ProgressDialog pgdialog;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private JSONArray allList,searchList;
    private String data_response_type;
    private TextView business_heading_title;
    private ViewEventsAdapter3 adapter;
    private ViewBusinessAdapter businessAdapter;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_reservation);
        helper = new Helper(this);
        pgdialog = new ProgressDialog(this);
        pgdialog.setMessage(getString(R.string.loading));
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name)+" - Upcoming Event");
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recycler_events);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        business_heading_title = findViewById(R.id.business_heading_title);
        loadEvents();
    }
    void initDefaultDummyData(){
        String jsonArr = "[{id:12,event_name:'Partying the show',event_type:Networking,event_kikoff:'2021-06-30 12:30',event_close:'2021-06-30 17:30',brief_description:'Network with startup founders',reservation_allowed:30,available_seat:18,banners:''" +
                "},{id:12,event_name:'Cyber security forums for engineers',event_type:'Skillup and data management',event_kikoff:'2021-06-30 12:30',event_close:'2021-06-30 17:30',brief_description:'Cyber hacking intro to cyber enthusiast in the field',reservation_allowed:30,available_seat:20,banners:''"+//\" +\n" +
                "},{id:12,event_name:'Bruce melody album lunch',event_type:'Entertainment',event_kikoff:'2021-07-10 12:30',event_close:'2021-07-10 17:30',brief_description:'Melody will be performing in front of executive leaders ',reservation_allowed:30,available_seat:10,banners:''}]";//\" +\n" +
//                "},{id:12,event_name:'President inauguration',event_type:'Inauguration for the elected president',event_kikoff:'2021-07-28 12:30',event_close:'2021-07-28 17:30',brief_description:'Will be broadcasted on TV Rwanda Broadcasting agency',banners:''}]";
        try {
            JSONArray arr = new JSONArray(jsonArr);
            allList = arr;
            searchList = arr;
            adapter = new ViewEventsAdapter3(getApplicationContext(),searchList);
            recyclerView.setAdapter(adapter);
        }catch (JSONException ex){
            Log.d("jsonerr"," Json data error: "+ex.getMessage());
        }

        }
    void loadEvents(){
        final String url = helper.host+"/events/active";
        Log.d("URL",url);
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
                        Log.d("Logresp",response);
                        try{
                            JSONObject object = new JSONObject(response);
                            data_response_type = object.getString("type");
                            JSONArray arr = object.getJSONArray("data");
                            allList = arr;
                            searchList = arr;
                            if(data_response_type.equals("events")) {
                                adapter = new ViewEventsAdapter3(getApplicationContext(), searchList);
                                recyclerView.setAdapter(adapter);
                                business_heading_title.setVisibility(View.GONE);
                            }else if(data_response_type.equals("businesses")){
                                business_heading_title.setVisibility(View.VISIBLE);
                                businessAdapter = new ViewBusinessAdapter(getApplicationContext(), searchList);
                                recyclerView.setAdapter(businessAdapter);

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
                        helper.showToast("Something went wrong "+error.toString());
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
    public void onResume(){
        super.onResume();
        loadEvents();
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

        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
             /*   if(list.contains(query)){
                    adapter.getFilter().filter(query);
                }else{
                    Toast.makeText(MainActivity.this, "No Match found",Toast.LENGTH_LONG).show();
                }*/
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchInEvents(newText);
                return false;
            }
        });


        return true;
    }
    void searchInEvents(String keyword){
        searchList = new JSONArray();

        for(int i = 0;i<allList.length();i++){
            try{
                JSONObject obj = allList.getJSONObject(i);
                if(data_response_type.equals("events")) {
                    if (obj.getString("event_name").toLowerCase().contains(keyword.toLowerCase()) || obj.getString("event_type").toLowerCase().contains(keyword.toLowerCase())) {
                        searchList.put(obj);
                    }
                }else if(data_response_type.equals("businesses")){
                    if (obj.getString("name").toLowerCase().contains(keyword.toLowerCase()) || obj.getString("event_type").toLowerCase().contains(keyword.toLowerCase())) {
                        searchList.put(obj);
                    }
                }
            }catch (JSONException ex){

            }
        }
        if(searchList.length()>0){
//            Log.d("SearchQy",keyword+"++++\n"+searchList.toString());
            if(data_response_type.equals("events")) {
                adapter = new ViewEventsAdapter3(getApplicationContext(), searchList);
                recyclerView.setAdapter(adapter);
            }else if(data_response_type.equals("businesses")){
                businessAdapter = new ViewBusinessAdapter(getApplicationContext(), searchList);
                recyclerView.setAdapter(businessAdapter);
            }
//            adapter.notifyDataSetChanged();
//            recyclerView.refreshDrawableState();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (helper.getDataValue("user_type")){
            case "Standard":
                if (id == R.id.my_reservation) {
                    Intent intent1 = new Intent(this,ViewMyReservations.class);
                    this.startActivity(intent1);
                    return true;
                }

                if (id == R.id.followings) {
                    Intent intent1 = new Intent(this,Followings.class);
                    this.startActivity(intent1);
                    return true;
                }
                if (id == R.id.business) {
                    Intent intent1 = new Intent(this,EventOriganizers.class);
                    this.startActivity(intent1);
                    return true;
                }

                if (id == R.id.watch_later) {
                    Intent intent1 = new Intent(this,SavedWatchLater.class);
                    this.startActivity(intent1);
                    return true;
                }
                if (id == R.id.locate_businesses) {
                    Intent intent1 = new Intent(this,LocateBusinesses.class);
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

                if (id == R.id.report) {
                    startActivity(new Intent(LandingReservation.this, ReservationReport.class));
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
        if (id == R.id.home) {
            Intent intent1 = new Intent(this, LandingReservation.class);
            this.startActivity(intent1);
            return true;
        }
        if (id == R.id.profile) {
            Intent intent1 = new Intent(this, Profile.class);
            this.startActivity(intent1);
            return true;
        }
        if(id == R.id.logout){
            helper.logout();
            finish();
            startActivity(new Intent(LandingReservation.this, Signin.class));
        }
        if(id == R.id.signin){
            finish();
            startActivity(new Intent(LandingReservation.this, SplashAccountActivity.class));
        }

        if (id == R.id.locate_businesses) {
            Intent intent1 = new Intent(this,LocateBusinesses.class);
            this.startActivity(intent1);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


}