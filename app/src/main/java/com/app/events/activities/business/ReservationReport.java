package com.app.events.activities.business;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.events.R;
import com.app.events.activities.commons.Profile;
import com.app.events.activities.commons.Signin;
import com.app.events.activities.standard.EventOriganizers;
import com.app.events.activities.standard.LandingReservation;
import com.app.events.adapters.business.ViewEventsAdapter;
import com.app.events.adapters.business.ViewReservationsAdapter;
import com.app.events.utils.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservationReport extends AppCompatActivity  implements AdapterView.OnItemSelectedListener {
    public Helper helper;
    public ProgressDialog pgdialog;
    String[] list;
    List<String> lists;
    private JSONArray array;
    private Spinner spnEvent;
    private Button btnDownload;
    private WebView webView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_report);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name)+" - Report");
        setSupportActionBar(toolbar);
        helper = new Helper(this);
        pgdialog = new ProgressDialog(this);
        pgdialog.setMessage(getString(R.string.loading));
        spnEvent = findViewById(R.id.spnEvents);
        btnDownload = findViewById(R.id.btnDownload);
        webView = findViewById(R.id.webView);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String eventId = "0";
                try{
                    JSONObject obj = array.getJSONObject(spnEvent.getSelectedItemPosition());
                    eventId = obj.getString("id");
                    Log.d("LoadUrl",helper.host+"/reservation/report?business_id="+helper.getDataValue("id")+"&event_id="+eventId);
//                    webView.loadUrl(helper.host+"/reservation/report?business_id="+helper.getDataValue("id")+"&event_id="+eventId);
                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(helper.host+"/reservation/report?business_id="+helper.getDataValue("id")+"&event_id="+eventId));
                    startActivity(intent);

                }catch(JSONException ex){
                    Log.d("jsonerr",ex.getMessage());
                }
            }
        });
        loadEvents();
    }

    void loadEvents(){
        final String url = helper.host+"/events/load?business_id="+helper.getDataValue("id");
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
                                array = object.getJSONArray("data");
                                Log.d("RepLen","Len "+array.length());
                                if(array.length()>0) {
                                    lists = new ArrayList<String>();
                                    for (int i = 0; i < array.length(); i++) {
                                        String eventName = array.getJSONObject(i).getString("event_name");
                                        if(!eventName.isEmpty() && eventName!=null && !eventName.trim().equalsIgnoreCase("null")) {
                                            lists.add(eventName);

                                        }
                                    }
                                    list = new String[lists.size()];
                                    // Assign list with null removed to String[]
                                    for(int n=0;n<lists.size();n++){
                                        list[n] = lists.get(n);
                                    }
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ReservationReport.this, android.R.layout.simple_spinner_item, list);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spnEvent.setAdapter(adapter);
                                }else{

                                }
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
        };;

// add it to the RequestQueue
        queue.add(getRequest);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
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
        if (id == R.id.home) {
            startActivity(new Intent(ReservationReport.this, LandingReservation.class));
        }
        if (id == R.id.followers) {
            startActivity(new Intent(ReservationReport.this, Followers.class));
        }
        if (id == R.id.notifications) {
            startActivity(new Intent(ReservationReport.this, Notifications.class));
        }
        if (id == R.id.profile) {
            startActivity(new Intent(ReservationReport.this, Profile.class));
        }
        if (id == R.id.events) {
            startActivity(new Intent(ReservationReport.this, ViewEvents.class));
        }
        if (id == R.id.logout) {
            helper.logout();
            finish();
            startActivity(new Intent(ReservationReport.this, Signin.class));
        }


        return super.onOptionsItemSelected(item);
    }
}