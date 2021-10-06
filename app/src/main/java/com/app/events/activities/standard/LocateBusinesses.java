package com.app.events.activities.standard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.events.R;
import com.app.events.adapters.admin.ViewBusinessAdapter;
import com.app.events.utils.Helper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LocateBusinesses  extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    private Helper helper;
    private ProgressDialog pgdialog;
    private GoogleMap mMap;

    // below are the latitude and longitude
    // of 4 different locations.
    LatLng sydney = new LatLng(-1.9499194,30.1240155);
    LatLng TamWorth = new LatLng(-1.9498327,30.1238119);
    LatLng NewCastle = new LatLng(-1.9583264,30.1105498);
    LatLng Npd = new LatLng(-1.9748903,30.1125671);

    // creating array list for adding all our locations.
    private ArrayList<LatLng> locationArrayList;
    private ArrayList <String> locationNameArrayList;
    public SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate_businesses);

        helper = new Helper(this);
        pgdialog = new ProgressDialog(this);
        pgdialog.setMessage(getString(R.string.loading));

//        // in below line we are initializing our array list.
        locationArrayList = new ArrayList<>();
        locationNameArrayList = new ArrayList<>();
        loadBusiness();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);



//        locationNameArrayList.add("Kimironko market");
//        locationNameArrayList.add("Rwanda Education Board");
//        locationNameArrayList.add("Ndoli Supermarke");
//        locationNameArrayList.add("NPD Main Headquarter");
//
//        // on below line we are adding our
//        // locations in our array list.
//        locationArrayList.add(sydney);
//        locationArrayList.add(TamWorth);
//        locationArrayList.add(NewCastle);
//        locationArrayList.add(Npd);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("LatLong","LatLogintude "+locationArrayList.size());
        // inside on map ready method
        // we will be displaying all our markers.
        // for adding markers we are running for loop and
        // inside that we are drawing marker on our map.0
        for (int i = 0; i < locationArrayList.size(); i++) {

            // below line is use to add marker to each location of our array list.
            mMap.addMarker(new MarkerOptions().position(locationArrayList.get(i)).title(locationNameArrayList.get(i))).showInfoWindow();

            // below lin is use to zoom our camera on map.
            mMap.animateCamera(CameraUpdateFactory.zoomTo(21.0f));

            // below line is use to move our camera to the specific location.
            mMap.moveCamera(CameraUpdateFactory.newLatLng(locationArrayList.get(i)));
        }
        mMap.setOnMapLongClickListener(this);

    }
    @Override
    public void onMapLongClick(LatLng point) {
        if(mMap != null){
            mMap.addMarker(new MarkerOptions()
                    .position(point)
                    .title("You are here")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
    }

    void loadBusiness() {
        final String url = helper.host + "/business/load";
        pgdialog.show();
//        tvLoggingIn.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
// prepare the Request
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // display response
                        pgdialog.dismiss();
                        Log.d("Logresp", response);
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray arr = object.getJSONArray("data");
                            for(int i=0;i<arr.length();i++){
                                JSONObject obj = arr.getJSONObject(i);
                                Log.d("BusinessLocation",obj.getString("name")+" == "+obj.getString("gps_location"));
                                if(!obj.getString("gps_location").equals("")){
                                    String[] locationArr = obj.getString("gps_location").split(",");
                                    float lat =Float.parseFloat(locationArr[0]),
                                            longitude = Float.parseFloat(locationArr[1]);
                                    locationNameArrayList.add(obj.getString("name"));
                                    locationArrayList.add(new LatLng(lat,longitude));
                                }
                            }
                            mapFragment.getMapAsync(LocateBusinesses.this);

                        } catch (JSONException ex) {
                            Log.d("jsonerr",ex.getMessage());
//                            helper.showToast("Json error " + ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pgdialog.dismiss();
                        helper.showToast("Something went wrong");
                        Log.e("jsonerr", "JSON Error " + (error != null ? error.getMessage() : ""));
                    }
                }
        ) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cate", "load");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", helper.getDataValue("appid"));//put your token here
                return headers;
            }
        };
        ;

// add it to the RequestQueue
        queue.add(getRequest);
    }

}