package com.app.events.activities.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.events.R;
import com.app.events.adapters.business.ViewEventsAdapter;
import com.app.events.utils.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ConfirmEventAction extends AppCompatActivity {
    public Helper helper;
    public ProgressDialog pgdialog;
    private Button btnKickOffDate,btnKickOffTime,btnKickOnDate,btnKickOnTime,btnReschedule;
    int mYear,mMonth,mDay,mHour,mMinute;
    private LinearLayout lnyRescheduleLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_event_action);
        helper = new Helper(this);
        pgdialog = new ProgressDialog(this);
        pgdialog.setMessage(getString(R.string.senddata));
        lnyRescheduleLayout = findViewById(R.id.lnyRescheduleLayout);
        btnKickOffDate = findViewById(R.id.btnKickOffDate);
        btnKickOffTime = findViewById(R.id.btnKickOffTime);
        btnKickOnDate = findViewById(R.id.btnCloseDate);
        btnKickOnTime = findViewById(R.id.btnCloseTime);
        btnReschedule = findViewById(R.id.btnReschedule);

        if(getIntent().getStringExtra("action").equals("reschedule")){
            lnyRescheduleLayout.setVisibility(View.VISIBLE);
        }else{
            alert();
        }

        btnReschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                save(); //without image upload
//                triggerSaveWithImageUpload(); // with image uploading
                rescheduleEvent();
            }

        });

        btnKickOffDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(btnKickOffDate);
            }

        });
        btnKickOffTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(btnKickOffTime);
            }
        });
        btnKickOnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(btnKickOnDate);
            }
        });
        btnKickOnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(btnKickOnTime);
            }
        });

    }
    void showDatePicker(Button btn){
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        btn.setText(year + "-" + ((monthOfYear + 1)<10?"0"+(monthOfYear + 1):(monthOfYear + 1)) + "-" + (dayOfMonth<10?"0"+dayOfMonth:dayOfMonth));

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
    void showTimePicker(Button btn){
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        btn.setText((hourOfDay<10?"0"+hourOfDay:hourOfDay) + ":" + (minute<10?"0"+minute:minute));
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }
    void alert(){
        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);
        pDialog.setTitleText(getString(R.string.takeaction));
        if(getIntent().getStringExtra("action").equals("approve")){
            pDialog.setContentText("Confirm to approve publication of the event "+getIntent().getStringExtra("event_name"));
        }else if(getIntent().getStringExtra("action").equals("reject")){
            pDialog.setContentText("Confirm to reject publication of the event "+getIntent().getStringExtra("event_name"));
        }else if(getIntent().getStringExtra("action").equals("cancel")){
            pDialog.setContentText("Confirm to cancel this event "+getIntent().getStringExtra("event_name"));
        }
        pDialog.setConfirmText("Confirm").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {//send request to approve/reject event
                sweetAlertDialog.dismiss();
                changeEventStatus();
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

    void changeEventStatus(){
        final String url = helper.host+"/events/status/"+getIntent().getStringExtra("id");
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
                params.put("status", getIntent().getStringExtra("status"));
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

    void rescheduleEvent(){
        final String url = helper.host+"/events/reschedule/"+getIntent().getStringExtra("id");
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
                params.put("event_kikoff", btnKickOffDate.getText().toString()+" "+btnKickOffTime.getText().toString());
                params.put("event_close", btnKickOnDate.getText().toString()+" "+btnKickOnTime.getText());
                Log.d("PassedData",params.toString());
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