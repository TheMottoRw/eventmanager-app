package com.app.events.activities.business;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.events.MainActivity;
import com.app.events.R;
import com.app.events.activities.admin.ViewBusiness;
import com.app.events.activities.commons.Signin;
import com.app.events.activities.standard.LandingReservation;
import com.app.events.utils.Helper;
import com.app.events.utils.SwAlertHelper;
import com.app.events.utils.Validator;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BusinessSignup extends Activity {
    private static final int REQUEST_LOCATION = 1;
    private EditText edtBusinessName,edtBusinessType,edtBusinessTin,edtPhone,edtEmail,edtAddress,edtPassword,edtConfirmPassword;
    private Button btnSignup;
    private Spinner spnBusinessType;
    private ProgressDialog pgdialog;
    private Helper helper;
    private SwAlertHelper swHelper;
    private Validator validator;
    private Button btnLocation;
    private TextView showLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    public String latitude = "",longitude = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_signup);
        helper = new Helper(getApplicationContext());
        swHelper = new SwAlertHelper(this);
        validator = new Validator();
        pgdialog = new ProgressDialog(this);
        pgdialog.setMessage(getString(R.string.senddata));

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initDefault();
    }

    public void initDefault(){
        edtBusinessName = findViewById(R.id.edtBusinessName);
        spnBusinessType = findViewById(R.id.spnBusinessType);
        edtBusinessTin = findViewById(R.id.edtBusinessTin);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtAddress = findViewById(R.id.edtAddress);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        showLocation = findViewById(R.id.location);
        btnLocation = findViewById(R.id.btnLocation);
        btnSignup = findViewById(R.id.btnSignup);

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastLocation();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateSignup();
            }
        });
    }

    void validateSignup(){
        String phone = edtPhone.getText().toString().trim(),
                names = edtBusinessName.getText().toString().trim(),
                type = spnBusinessType.getSelectedItem().toString().trim(),
                tin = edtBusinessTin.getText().toString().trim(),
                address = edtAddress.getText().toString().trim(),
                password = edtPassword.getText().toString(),
                confirmPassword = edtConfirmPassword.getText().toString();
        if(names.isEmpty() || type.isEmpty() || tin.isEmpty() ||  phone.isEmpty() || address.isEmpty() ||  password.trim().isEmpty() || confirmPassword.isEmpty() ){
            swHelper.failed(getString(R.string.all_field_required));
        }else{
            if(!validator.tin(tin)){
                swHelper.failed(getString(R.string.invalid_tin));
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
                        edtBusinessName.setText("");edtBusinessTin.setText("");  edtPhone.setText(""); edtAddress.setText("");
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
                                        helper.setSession(obj.getString("id"),obj.getString("name"),  obj.getString("contact_number"), obj.getString("tin"), obj.getString("user_type"), obj.getString("address"),obj.getString("created_at"));
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
                params.put("business_type",spnBusinessType.getSelectedItem().toString().trim());
                params.put("tin", edtBusinessTin.getText().toString().trim());
                params.put("phone", edtPhone.getText().toString().trim());
                params.put("email", edtEmail.getText().toString().trim());
                params.put("address", edtAddress.getText().toString().trim());
                params.put("gps_location", latitude+","+longitude);
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
        int socketTimeout = 60000;//60 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        getRequest.setRetryPolicy(policy);
// add it to the RequestQueue
        queue.add(getRequest);
    }

    //Location  services

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            latitude = String.valueOf(location.getLatitude());
                            longitude = String.valueOf(location.getLongitude());
                            helper.showToast("Location found");
                            showLocation.setText("Latitude "+location.getLatitude() + " Longitude "+ location.getLongitude() + "");
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitude = String.valueOf(mLastLocation.getLatitude());
            longitude = String.valueOf(mLastLocation.getLongitude());
            helper.showToast("Location found");
            showLocation.setText("Latitude:" + mLastLocation.getLatitude() + ", Longitude:" + mLastLocation.getLongitude() + "");
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

}