package com.app.events.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.events.R;
import com.app.events.adapters.admin.ViewBusinessAdapter;
import com.app.events.utils.DummyData;
import com.app.events.utils.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class BusinessView extends Fragment {
    public Helper helper;
    public ProgressDialog pgdialog;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Context ctx;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_business_view, container, false);
        ctx = view.getContext();
        helper = new Helper(ctx);
        pgdialog = new ProgressDialog(ctx);
        pgdialog.setMessage(ctx.getString(R.string.loading));

        recyclerView = view.findViewById(R.id.recycler_business);
        layoutManager = new LinearLayoutManager(ctx);
        recyclerView.setLayoutManager(layoutManager);
        loadBusiness();
        return view;
    }
    void initDefaultDummyData() {

        String jsonArr = DummyData.businesses;
        try {
            JSONArray arr = new JSONArray(jsonArr);
            ViewBusinessAdapter adapter = new ViewBusinessAdapter(ctx, arr);
            recyclerView.setAdapter(adapter);
        } catch (JSONException ex) {
            Log.d("jsonerr", " Json data error: " + ex.getMessage());
        }
    }

    void loadBusiness() {
        final String url = helper.host + "/business/load";
        pgdialog.show();
//        tvLoggingIn.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(ctx);
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
                            ViewBusinessAdapter adapter = new ViewBusinessAdapter(ctx, arr);
                            recyclerView.setAdapter(adapter);
                        } catch (JSONException ex) {
                            helper.showToast("Json error " + ex.getMessage());
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