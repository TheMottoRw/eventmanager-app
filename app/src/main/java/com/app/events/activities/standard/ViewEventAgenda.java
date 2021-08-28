package com.app.events.activities.standard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.app.events.activities.admin.ViewBusiness;
import com.app.events.activities.business.AddEvent;
import com.app.events.activities.business.ViewEvents;
import com.app.events.activities.commons.Signin;
import com.app.events.adapters.business.ViewEventsAdapter;
import com.app.events.utils.Helper;
import com.app.events.utils.SwAlertHelper;
import com.bumptech.glide.Glide;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ViewEventAgenda extends AppCompatActivity {
    public TextView eventName,eventType,eventKickOff,eventKickOn,eventBriefDetails,fullDescriptionAgenda,eventPreparedBy,availableSeat;
    public Button arrowBack,btnAddToWatchLater,btnSendReview;
    public ImageView imgBanners;
    public Button btnReserve;
    public Helper helper;
    public SwAlertHelper swHelper;
    public String event_id,business_id;
    public ProgressDialog pgdialog;
    private Toolbar toolbar;
    public EditText edtReview;
    public float rate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event_agenda);
        helper = new Helper(this);
        swHelper = new SwAlertHelper(ViewEventAgenda.this);
        pgdialog = new ProgressDialog(this);
        pgdialog.setMessage(getString(R.string.loading));
        initDefault();
    }
    void initDefault(){
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name) + " - Event view" );
        setSupportActionBar(toolbar);

        imgBanners = findViewById(R.id.imgBanners);
        arrowBack = findViewById(R.id.arrowBack);
        eventName = findViewById(R.id.eventName);
        eventType = findViewById(R.id.eventType);
        eventKickOff = findViewById(R.id.eventKickOff);
        eventKickOn = findViewById(R.id.eventKickOn);
        eventBriefDetails = findViewById(R.id.eventBriefDetails);
        fullDescriptionAgenda = findViewById(R.id.eventFullDescriptionAgenda);
        eventPreparedBy = findViewById(R.id.eventPreparedBy);
        availableSeat = findViewById(R.id.eventAvailableSeat);
        edtReview = findViewById(R.id.edtReview);
        btnReserve = findViewById(R.id.btnReserve);
        btnAddToWatchLater = findViewById(R.id.btnAddToWatchLater);
        btnSendReview = findViewById(R.id.btnSendReview);
        btnSendReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendReview();
            }
        });
        btnAddToWatchLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert();
            }
        });
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        String object = getIntent().getStringExtra("data");
        try{
            JSONObject obj = new JSONObject(object);

            event_id = obj.getString("id");
            business_id = obj.getString("business_id");

            eventName.setText(obj.getString("event_name"));
            eventType.setText(obj.getString("event_type"));
            eventBriefDetails.setText(obj.getString("brief_description"));
            fullDescriptionAgenda.setText(obj.getString("full_description"));
            eventKickOff.setText(obj.getString("event_kikoff"));
            eventKickOn.setText(obj.getString("event_close"));
            eventPreparedBy.setText(obj.getString("business_name"));
            if(!obj.has("reserved_seat")) {
                availableSeat.setText("Only "+obj.getString("available_seat")+" seat remaining");
            }else{
                if(obj.getInt("reserved_seat") > 0) {
                    availableSeat.setText(obj.getString("reserved_seat") + " people are going");
                }else{
                    availableSeat.setText("Only "+obj.getString("available_seat")+" seat remaining");
                }
            }
            if(!obj.getString("images").isEmpty()){
                String[] images = obj.getString("images").split(",");
                Glide.with(getApplicationContext()).load(images[0])
                        .error(getDrawable(R.drawable.logo)).centerCrop().into(imgBanners);
            }else{
                Glide.with(getApplicationContext()).load(getDrawable(R.drawable.logo))
                        .error(getDrawable(R.drawable.logo)).centerCrop().into(imgBanners);
            }

        }catch (JSONException ex){
            Log.d("jsonerr","Error is "+ex.getMessage());
        }


        btnReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(helper.hasSession()){
                    //send reservation request
                reserveSeat();
                }else{
                    //login for reservation
                    Intent intent = new Intent(ViewEventAgenda.this, Signin.class);
                    intent.putExtra("action","reservation");
                    intent.putExtra("event_id",event_id);
                    intent.putExtra("business_id",business_id);
                    startActivity(intent);
                }
            }
        });

        initStar();
    }
    public void initStar(){
        ScaleRatingBar ratingBar = new ScaleRatingBar(this);
        ratingBar.setNumStars(5);
        ratingBar.setMinimumStars(1);
        ratingBar.setRating(3);
        ratingBar.setStarPadding(10);
        ratingBar.setStepSize(0.5f);
        ratingBar.setStarWidth(105);
        ratingBar.setStarHeight(105);
        ratingBar.setIsIndicator(false);
        ratingBar.setClickable(true);
        ratingBar.setScrollable(true);
        ratingBar.setClearRatingEnabled(true);
        ratingBar.setEmptyDrawableRes(R.drawable.ic_baseline_star_24);
        ratingBar.setFilledDrawableRes(R.drawable.ic_baseline_star_border_24);

        ratingBar.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(BaseRatingBar ratingBar, float rating, boolean fromUser) {
                rate = rating;
                Log.e("RateChange", "onRatingChange: " + rating);

            }
        });
    }
    void alert(){
        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);
        pDialog.setTitleText(getString(R.string.takeaction));
            pDialog.setContentText("Confirm adding this event  to your list "+eventName.getText().toString());

        pDialog.setConfirmText("Confirm").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {//send request to approve/reject event
                sweetAlertDialog.dismiss();
                saveForLater();
            }
        });
        pDialog.setCancelText("Cancel").setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
                finish();
            }
        });
        pDialog.show();
    }
    void saveForLater(){
        final String url = helper.host+"/saveforlater/";
        pgdialog.show();
        Log.d("URL",url);
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
                            helper.showToast(object.getString("message"));
                            finish();
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
                params.put("user_id", helper.getDataValue("id"));
                params.put("event_id", event_id);
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
    void sendReview(){
        final String url = helper.host+"/review/";
        pgdialog.show();
        Log.d("URL",url);
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
                            helper.showToast(object.getString("message"));
                            finish();
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
                params.put("user_id", helper.getDataValue("id"));
                params.put("event_id", event_id);
                params.put("rate", String.valueOf(rate));
                params.put("review", edtReview.getText().toString());
                params.put("image", "");
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

    public void reserveSeat() {
        final String url = helper.host+"/reservation/";
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
                                        swHelper.success(object.getString("message"));
                                    }else{
                                        swHelper.failed(object.getString("message"));
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
                params.put("event_id", event_id);
                params.put("business_id", business_id);
                params.put("user_id", helper.getDataValue("id"));
                Log.d("dataParams",params.toString());
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
}