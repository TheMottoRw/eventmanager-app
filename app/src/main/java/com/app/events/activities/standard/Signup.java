package com.app.events.activities.standard;


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
import com.app.events.utils.Helper;
import com.app.events.utils.SwAlertHelper;
import com.app.events.utils.Validator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Signup extends Activity {
    private EditText edtNames,edtEmail,edtPhone,edtPassword,edtConfirmPassword;
    private Button btnSignup;
    private ProgressDialog pgdialog;
    private Helper helper;
    private SwAlertHelper swHelper;
    private Validator validator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        helper = new Helper(getApplicationContext());
        swHelper = new SwAlertHelper(this);
        validator = new Validator();
        pgdialog = new ProgressDialog(this);
        pgdialog.setMessage(getString(R.string.senddata));

        initDefault();
    }

    public void initDefault(){
        edtNames = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignup = findViewById(R.id.btnSignup);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateSignup();
            }
        });
    }

    void validateSignup(){
        String phone = edtPhone.getText().toString().trim(),
                names = edtNames.getText().toString().trim(),
                email = edtEmail.getText().toString().trim(),
                password = edtPassword.getText().toString(),
                confirmPassword = edtConfirmPassword.getText().toString();
        if(names.isEmpty() || phone.isEmpty() || email.isEmpty() || password.trim().isEmpty() || confirmPassword.isEmpty() ){
            swHelper.failed(getString(R.string.all_field_required));
        }else{
            if(!validator.email(email)){
                swHelper.failed(getString(R.string.invalid_email));
            }else if(!validator.phone(phone)){
                swHelper.failed(getString(R.string.invalid_phone));
            }else if(!password.equals(confirmPassword)){
                swHelper.failed(getString(R.string.passwordnotmatch));
            }else{
                signup();
            }
        }
    }
    public void signup() {
        final String url = helper.host+"/users/";
        pgdialog.show();
//        tvLoggingIn.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
// prepare the Request
        StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // display response
                        edtNames.setText("");edtEmail.setText("");edtPhone.setText("");  edtPassword.setText(""); edtConfirmPassword.setText("");
                        pgdialog.dismiss();
                        Log.d("Logresp",response);
                        try{
                            JSONObject object = new JSONObject(response);
                            if(object.getString("status").equals("ok")) {
                                JSONObject obj = object.getJSONObject("data");
                                if(obj.has("user_type")) {
                                    helper.setSession(obj.getString("id"), obj.getString("phone"), obj.getString("email"), obj.getString("user_type"), obj.getString("created_at"));
                                    helper.showToast(object.getString("message"));
                                    if (obj.getString("user_type").equals("Standard")) {
                                        startActivity(new Intent(getApplicationContext(), LandingReservation.class));
                                    }else if (obj.getString("user_type").equals("Admin")) {
                                        startActivity(new Intent(getApplicationContext(), ViewBusiness.class));
                                    }
                                }
                            } else  helper.showToast("Something went wrong");
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
                params.put("name", edtNames.getText().toString().trim());
                params.put("email",edtEmail.getText().toString().trim());
                params.put("phone", edtPhone.getText().toString().trim());
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