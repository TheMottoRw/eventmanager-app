package com.app.events.activities.business;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.widget.Button;
import android.widget.EditText;
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
import com.app.events.activities.commons.Signin;
import com.app.events.activities.standard.LandingReservation;
import com.app.events.adapters.business.FollowersAdapter;
import com.app.events.utils.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Notifications extends AppCompatActivity {
    private Helper helper;
    private ProgressDialog pgdialog;
    private RecyclerView recyclerviewFollowings;
    private LinearLayoutManager layoutManager;
    private TextView headingTitle;
    private Toolbar toolbar;
    private EditText edtMessage;
    private Button btnSend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        helper = new Helper(this);
        pgdialog = new ProgressDialog(this);
        pgdialog.setMessage(getString(R.string.loading));
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name)+" - Notifications");
        setSupportActionBar(toolbar);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNotifications();
            }
        });
    }
    void  sendNotifications(){
        final String url = helper.host+"/events/notifications";
        Log.d("URL",url);
        pgdialog.show();
//        tvLoggingIn.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
// prepare the Request
        StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // display response
                        pgdialog.dismiss();
                        Log.d("Logresp",response);
                        try{
                            JSONObject object = new JSONObject(response);
                          if(object.getString("status").equals("ok")){
                              edtMessage.setText("");
                              Toast.makeText(getApplicationContext(),"Your message broadcasted successful",Toast.LENGTH_LONG).show();
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
                        edtMessage.setText("");
                        helper.showToast("Notification broadcasted to your followers successful");
                        Log.e("jsonerr","JSON Error "+(error!=null?error.getMessage():""));
                    }
                }
        ) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cate", "load");
                params.put("business_id", helper.getDataValue("id"));
                params.put("message", edtMessage.getText().toString().trim());
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
            inflater.inflate(R.menu.business,menu);
        }else{
            getMenuInflater().inflate(R.menu.signin,menu);
        }


        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (helper.getDataValue("user_type")){

            case "Business":
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
                if (id == R.id.report) {
                    startActivity(new Intent(Notifications.this, ReservationReport.class));
                }
                if (id == R.id.logout) {
                    helper.logout();
                    finish();
                    startActivity(new Intent(Notifications.this, Signin.class));
                }

                break;

        }
        if(id == R.id.logout){
            helper.logout();
            finish();
            startActivity(new Intent(Notifications.this, Signin.class));
        }
        return super.onOptionsItemSelected(item);
    }
}