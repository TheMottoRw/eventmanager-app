package com.app.events.activities.admin;

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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.events.MainActivity;
import com.app.events.R;
import com.app.events.activities.commons.Signin;
import com.app.events.activities.standard.Signup;
import com.app.events.adapters.admin.ViewBusinessAdapter;
import com.app.events.adapters.business.ViewEventsAdapter;
import com.app.events.utils.DummyData;
import com.app.events.utils.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ViewBusiness extends AppCompatActivity {
    public Helper helper;
    public ProgressDialog pgdialog;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_business);
        helper = new Helper(this);
        pgdialog = new ProgressDialog(this);
        pgdialog.setMessage(getString(R.string.loading));
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name)+" - Businesses");
        setSupportActionBar(toolbar);   recyclerView = findViewById(R.id.recycler_business);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        loadBusiness();
    }

    void initDefaultDummyData() {

        String jsonArr = DummyData.businesses;
        try {
            JSONArray arr = new JSONArray(jsonArr);
            ViewBusinessAdapter adapter = new ViewBusinessAdapter(getApplicationContext(), arr);
            recyclerView.setAdapter(adapter);
        } catch (JSONException ex) {
            Log.d("jsonerr", " Json data error: " + ex.getMessage());
        }
    }

    void loadBusiness() {
        final String url = helper.host + "/business/load";
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
                            ViewBusinessAdapter adapter = new ViewBusinessAdapter(getApplicationContext(), arr);
                            recyclerView.setAdapter(adapter);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            helper.logout();
            finish();
            startActivity(new Intent(ViewBusiness.this, Signin.class));
        }


        return super.onOptionsItemSelected(item);
    }
}