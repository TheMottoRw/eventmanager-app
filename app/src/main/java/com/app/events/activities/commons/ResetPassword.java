package com.app.events.activities.commons;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.events.R;
import com.app.events.activities.admin.Navigation;
import com.app.events.activities.business.ViewEvents;
import com.app.events.activities.standard.ViewMyReservations;
import com.app.events.utils.Helper;
import com.app.events.utils.SwAlertHelper;
import com.app.events.utils.Validator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResetPassword extends AppCompatActivity {
    private boolean addEmail = true,verifyCode = false,resetPassword=false;
    private Button btnCheck,btnVerify,btnReset;
    private EditText edtEmail,edtCode,edtPassword,edtConfirmPassword;
    private LinearLayout lnyResetCheck,lnyResetVerification,lnyResetPassword;
    private ProgressDialog pgdialog;
    private Helper helper;
    private SwAlertHelper swHelper;
    private String user_id="";
    private String reference_type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        helper = new Helper(getApplicationContext());
        swHelper = new SwAlertHelper(this);
        pgdialog = new ProgressDialog(this);
        pgdialog.setMessage(getString(R.string.trylogin));

        edtEmail = findViewById(R.id.edtEmail);
        edtCode = findViewById(R.id.edtCode);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);

        lnyResetPassword = findViewById(R.id.lnyResetPassword);
        lnyResetCheck = findViewById(R.id.lnyResetCheck);
        lnyResetVerification = findViewById(R.id.lnyResetVerification);

        btnCheck = findViewById(R.id.btnCheck);
        btnVerify = findViewById(R.id.btnVerify);
        btnReset = findViewById(R.id.btnReset);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send email verify request
                emailVerification();
            }
        });
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //send code verification request
                verifyCode();
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send new password request
                updatePassword();
            }
        });
    }
    void emailVerification(){
        final String url = helper.host+"/reset/request";
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
                            if(object.getString("status").equals("ok")) {
                                addEmail = false;verifyCode = true;resetPassword = false;
                                lnyResetCheck.setVisibility(View.GONE);
                                lnyResetVerification.setVisibility(View.VISIBLE);
                                lnyResetPassword.setVisibility(View.GONE);
                                user_id = object.getString("id");
                                reference_type = object.getString("type");
                                helper.showToast("Check your email for verification code");
                                lnyResetCheck.setVisibility(View.GONE);
                                lnyResetVerification.setVisibility(View.VISIBLE);
                                lnyResetPassword.setVisibility(View.GONE);

                                helper.showToast(user_id+" and "+reference_type);

                            } else  helper.showToast("Email not exist in our database");
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
                        NetworkResponse response = error.networkResponse;
                        String errorMsg = "";
                        if(response != null && response.data != null){
                            String errorString = new String(response.data);
                            Log.e("log error", errorString);
                        }
                        Log.e("jsonerr","JSON Error "+error.toString()+" == "+response.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cate", "login");
                params.put("email", edtEmail.getText().toString().trim());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", helper.getDataValue("appid"));//put your token here
                return headers;
            }
        };


        getRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));// add it to the RequestQueue
        queue.add(getRequest);
    }
    void verifyCode(){
        final String url = helper.host+"/reset/verify";
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
                            if(object.getString("status").equals("ok")) {
                                addEmail = false;verifyCode = false;resetPassword = true;
                                lnyResetCheck.setVisibility(View.GONE);
                                lnyResetVerification.setVisibility(View.GONE);
                                lnyResetPassword.setVisibility(View.VISIBLE);
                                helper.showToast("Code verified,add new password");
                                lnyResetCheck.setVisibility(View.GONE);
                                lnyResetVerification.setVisibility(View.GONE);
                                lnyResetPassword.setVisibility(View.VISIBLE);

                            } else  helper.showToast("Wrong verification code");
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
                params.put("cate", "verify");
                params.put("code", edtCode.getText().toString().trim());
                params.put("reference_id", user_id);
                params.put("reference_type", reference_type);
                Log.d("URLParams",params.toString());
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
    void updatePassword(){
        final String url = helper.host+"/reset/password/"+user_id;
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
                            if(object.getString("status").equals("ok")) {
                                helper.showToast("Password successuful changed");
                                finish();
                            } else  helper.showToast("Failed to change password");
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
                params.put("cate", "verify");
                params.put("code", edtCode.getText().toString().trim());
                params.put("reference_type", reference_type);
                params.put("password", edtPassword.getText().toString().trim());
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
}