package com.app.events.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class AddEvent extends AppCompatActivity {
    private EditText edtEventName,edtEventType,edtEventBriefDescription,edtReservationAllowed,edtFullAgenda;
    private Button btnKickOff,btnKickOn,btnBanners;
    private ProgressDialog pgdialog;
    private Helper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        helper = new Helper(getApplicationContext());
        pgdialog = new ProgressDialog(this);
    }
    public void initDefault(){
        edtEventName = findViewById(R.id.edtEventName);
        edtEventType = findViewById(R.id.edtEventType);
        edtEventBriefDescription = findViewById(R.id.edtEventBriefDescription);
        edtReservationAllowed = findViewById(R.id.edtReservationAllowed);
        edtFullAgenda = findViewById(R.id.edtFullAgenda);
        btnKickOff = findViewById(R.id.btnKickOff);
        btnKickOn = findViewById(R.id.btnKickOn);
        btnBanners = findViewById(R.id.btnBanners);

    }
    public void save() {
        final String url = helper.host+"/events/";
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
                params.put("event_name", edtEventName.getText().toString().trim());
                params.put("event_type",edtEventType.getText().toString().trim());
                params.put("brief_description", edtEventBriefDescription.getText().toString().trim());
                params.put("full_description", edtFullAgenda.getText().toString().trim());
                params.put("images", edtEventName.getText().toString().trim());
                params.put("event_kikoff", btnKickOff.getText().toString().trim());
                params.put("event_close", btnKickOn.getText().toString().trim());
                params.put("reservation_allowed", edtReservationAllowed.getText().toString().trim());
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