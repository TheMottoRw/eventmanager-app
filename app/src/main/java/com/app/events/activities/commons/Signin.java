package com.app.events.activities.commons;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.events.MainActivity;
import com.app.events.R;
import com.app.events.activities.admin.Navigation;
import com.app.events.activities.admin.ViewBusiness;
import com.app.events.activities.business.AddEvent;
import com.app.events.activities.business.BusinessSignup;
import com.app.events.activities.business.ViewEvents;
import com.app.events.activities.standard.Signup;
import com.app.events.activities.standard.ViewMyReservations;
import com.app.events.utils.Helper;
import com.app.events.utils.SwAlertHelper;
import com.app.events.utils.Validator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Signin extends Activity {
    private EditText edtPhone,edtPassword,edtHost;
    private Button btnLogin,btnSet;
    private ProgressDialog pgdialog;
    private ImageView imgLogo;
    private LinearLayout lnyHost;
    private TextView signupBusiness,signupStandard;
    private Helper helper;
    private SwAlertHelper swHelper;
    private Validator validator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signin);
        helper = new Helper(getApplicationContext());
        swHelper = new SwAlertHelper(this);
        validator = new Validator();
        lnyHost = findViewById(R.id.lnyHost);
        edtPhone = findViewById(R.id.edtPhone);
        edtHost = findViewById(R.id.edtHost);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSet = findViewById(R.id.btnSet);
        signupBusiness = findViewById(R.id.tvSignupBusiness);
        signupStandard = findViewById(R.id.tvSignupStandard);
        imgLogo = findViewById(R.id.imgLogo);
        pgdialog = new ProgressDialog(this);
        pgdialog.setMessage(getString(R.string.trylogin));
        signupStandard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Signin.this, Signup.class));
            }
        });
        signupBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Signin.this, BusinessSignup.class));
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateLogin();
//                login(edtPhone.getText().toString().trim(),edtPassword.getText().toString());
//                startActivity(new Intent(Signin.this, ViewEvents.class));
            }
        });

        imgLogo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                edtHost.setText(helper.host);
                lnyHost.setVisibility(View.VISIBLE);
                return false;
            }
        });
        edtHost.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                helper.setHost(edtHost.getText().toString().trim());
                lnyHost.setVerticalGravity(View.GONE);
                return true;
            }
        });
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helper.setHost(edtHost.getText().toString().trim());
                lnyHost.setVisibility(View.GONE);
            }
        });
    }
    void validateLogin(){
        String phone = edtPhone.getText().toString().trim(),
                password = edtPassword.getText().toString();
        if(phone.isEmpty() || password.trim().isEmpty()){
            swHelper.failed(getString(R.string.all_field_required));
        }else{
            if(!validator.phone(phone)){
                swHelper.failed(getString(R.string.invalid_phone));
            }else{
                login(phone,password);
            }
        }
    }
    public void login(final String phone,final String password) {
        final String url = helper.host+"/user/login";
        pgdialog.show();
//        tvLoggingIn.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
// prepare the Request
        StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // display response
                        edtPhone.setText("");edtPassword.setText("");
                        pgdialog.dismiss();
                        Log.d("Logresp",response);
                        try{
                            JSONObject object = new JSONObject(response);
                            if(object.getString("status").equals("ok")) {

                                JSONArray arr = object.getJSONArray("data");
                                if(arr.length()>0){
                                    JSONObject obj = arr.getJSONObject(0);
                                if(obj.has("user_type")) {
                                    if (obj.getString("user_type").equals("Admin")) {
                                        helper.setSession(obj.getString("id"), obj.getString("name"), obj.getString("phone"), obj.getString("email"), obj.getString("user_type"),obj.getString("address"), obj.getString("created_at"));
//                                tvLoggingIn.setVisibility(View.GONE);
                                        helper.showToast("Login success");
                                        startActivity(new Intent(getApplicationContext(), Navigation.class));
                                    } else if (obj.getString("user_type").equals("Business")) {
                                        helper.setSession(obj.getString("id"), obj.getString("name"), obj.getString("contact_number"), obj.getString("email"), obj.getString("user_type"),obj.getString("address"), obj.getString("created_at"));
//                                tvLoggingIn.setVisibility(View.GONE);
                                        helper.showToast("Login success");
                                        startActivity(new Intent(getApplicationContext(), ViewEvents.class));
                                    } else if (obj.getString("user_type").equals("Standard")) {
                                        helper.setSession(obj.getString("id"),obj.getString("name"),  obj.getString("phone"), obj.getString("email"), obj.getString("user_type"), "",obj.getString("created_at"));
//                                tvLoggingIn.setVisibility(View.GONE);
                                        helper.showToast("Login success");
                                        if (getIntent().hasExtra("action") && getIntent().getStringExtra("action").equals("reservation")) {
                                            Toast.makeText(getApplicationContext(),"You are now signed in,Click reserve a seat by clicking RESERVE",Toast.LENGTH_LONG).show();
                                            finish();
                                        } else {
                                            startActivity(new Intent(getApplicationContext(), ViewMyReservations.class));
                                        }
                                    } else helper.showToast("Wrong username or password");
                                }
                                } else helper.showToast("Access denied");
                            } else  helper.showToast("Wrong username or password");
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
                params.put("cate", "login");
                params.put("phone", phone);
                params.put("password", password);
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