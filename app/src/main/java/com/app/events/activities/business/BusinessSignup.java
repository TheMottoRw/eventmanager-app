package com.app.events.activities.business;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import com.app.events.activities.admin.ViewBusiness;
import com.app.events.activities.commons.Signin;
import com.app.events.activities.standard.LandingReservation;
import com.app.events.utils.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BusinessSignup extends Activity {
    private EditText edtBusinessName,edtBusinessType,edtBusinessTin,edtPhone,edtAddress,edtPassword,edtConfirmPassword;
    private Button btnSignup;
    private ProgressDialog pgdialog;
    private Helper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_signup);
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
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnSignup = findViewById(R.id.btnSignup);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtPassword.getText().toString().trim().equals(edtConfirmPassword.getText().toString().trim())){
                    signup();
                }else{
                    helper.showToast(getString(R.string.passwordnotmatch));
                }
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
                        edtBusinessName.setText("");edtBusinessType.setText("");edtBusinessTin.setText("");  edtPhone.setText(""); edtAddress.setText("");
                        edtPassword.setText(""); edtConfirmPassword.setText("");
                        pgdialog.dismiss();
                        Log.d("Logresp",response);
                        try{
                            JSONObject object = new JSONObject(response);
                            if(object.getString("status").equals("ok")) {
                                JSONObject obj = object.getJSONObject("data");
                                if(obj.has("user_type")) {
                                    helper.showToast(object.getString("message"));
                                    if (obj.getString("user_type").equals("Business")) {
                                        helper.setSession(obj.getString("id"), obj.getString("contact_number"), obj.getString("tin"), obj.getString("user_type"), obj.getString("created_at"));
//                                tvLoggingIn.setVisibility(View.GONE);
                                        startActivity(new Intent(getApplicationContext(), ViewEvents.class));
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