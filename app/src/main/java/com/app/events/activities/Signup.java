package com.app.events.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.events.MainActivity;
import com.app.events.R;
import com.app.events.utils.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {
    private EditText edtBusinessName,edtBusinessType,edtBusinessTin,edtPhone,edtAddress,edtPassword;
    private Button btnSignup;
    private ProgressDialog pgdialog;
    private Helper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        helper = new Helper(getApplicationContext());
        pgdialog = new ProgressDialog(this);
        pgdialog.setMessage(getString(R.string.senddata));

        initDefault();
    }

    public void initDefault(){
        edtBusinessName = findViewById(R.id.edtBusinessName);
        edtBusinessType = findViewById(R.id.edtBusinessType);
        edtBusinessTin = findViewById(R.id.edtBusinessTin);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignup = findViewById(R.id.btnSignup);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup();
            }
        });
    }


    public void signup() {
        final String url = helper.host+"/business/";
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
                                JSONObject obj = object.getJSONObject("data");
                                if(obj.has("user_type")) {
                                    if (obj.getString("user_type").equals("Admin")) {
                                        helper.setSession(obj.getString("id"), obj.getString("phone"), obj.getString("email"), obj.getString("user_type"), obj.getString("created_at"));
//                                tvLoggingIn.setVisibility(View.GONE);
                                        helper.showToast("Login success");
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    } else if (obj.getString("user_type").equals("Police")) {
                                        helper.setSession(obj.getString("id"), obj.getString("phone"), obj.getString("email"), obj.getString("user_type"), obj.getString("created_at"));
//                                tvLoggingIn.setVisibility(View.GONE);
                                        helper.showToast("Login success");
//                                        startActivity(new Intent(getApplicationContext(), PoliceAllocation.class));
                                    } else helper.showToast("Wrong username or password");
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
                params.put("cate", "register");
                params.put("name", edtBusinessName.getText().toString().trim());
                params.put("business_type",edtBusinessType.getText().toString().trim());
                params.put("tin", edtBusinessTin.getText().toString().trim());
                params.put("phone", edtPhone.getText().toString().trim());
                params.put("address", edtAddress.getText().toString().trim());
                params.put("password", edtPassword.getText().toString().trim());
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