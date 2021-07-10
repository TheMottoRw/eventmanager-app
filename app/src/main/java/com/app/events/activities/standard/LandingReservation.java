package com.app.events.activities.standard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.events.R;
import com.app.events.activities.admin.ViewBusiness;
import com.app.events.activities.business.ViewEvents;
import com.app.events.activities.commons.Signin;
import com.app.events.adapters.business.ViewEventsAdapter;
import com.app.events.utils.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LandingReservation extends AppCompatActivity {
    public Helper helper;
    public ProgressDialog pgdialog;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_reservation);
        helper = new Helper(this);
        pgdialog = new ProgressDialog(this);
        pgdialog.setMessage(getString(R.string.loading));
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name)+" - Event");
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recycler_events);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        loadEvents();
    }
    void initDefaultDummyData(){
        String jsonArr = "[{id:12,event_name:'Partying the show',event_type:Networking,event_kikoff:'2021-06-30 12:30',event_close:'2021-06-30 17:30',brief_description:'Network with startup founders',reservation_allowed:30,available_seat:18,banners:''" +
                "},{id:12,event_name:'Cyber security forums for engineers',event_type:'Skillup and data management',event_kikoff:'2021-06-30 12:30',event_close:'2021-06-30 17:30',brief_description:'Cyber hacking intro to cyber enthusiast in the field',reservation_allowed:30,available_seat:20,banners:''"+//\" +\n" +
                "},{id:12,event_name:'Bruce melody album lunch',event_type:'Entertainment',event_kikoff:'2021-07-10 12:30',event_close:'2021-07-10 17:30',brief_description:'Melody will be performing in front of executive leaders ',reservation_allowed:30,available_seat:10,banners:''}]";//\" +\n" +
//                "},{id:12,event_name:'President inauguration',event_type:'Inauguration for the elected president',event_kikoff:'2021-07-28 12:30',event_close:'2021-07-28 17:30',brief_description:'Will be broadcasted on TV Rwanda Broadcasting agency',banners:''}]";
        try {
            JSONArray arr = new JSONArray(jsonArr);
            ViewEventsAdapter adapter = new ViewEventsAdapter(getApplicationContext(),arr);
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
                            JSONArray arr = object.getJSONArray("data");
                            ViewEventsAdapter adapter = new ViewEventsAdapter(getApplicationContext(),arr);
                            recyclerView.setAdapter(adapter);
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
        };;

// add it to the RequestQueue
        queue.add(getRequest);
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
                    Intent intent1 = new Intent(this,ViewMyReservations.class);
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
                    Intent intent1 = new Intent(this, ViewBusiness.class);
                    this.startActivity(intent1);
                    return true;
                }
                break;

        }
        if(id == R.id.logout){
            helper.logout();
            finish();
            startActivity(new Intent(LandingReservation.this, Signin.class));
        }
        if(id == R.id.signin){
            finish();
            startActivity(new Intent(LandingReservation.this, Signin.class));
        }


        return super.onOptionsItemSelected(item);
    }


}