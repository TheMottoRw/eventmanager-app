package com.app.events.fragments;

import android.app.Activity;
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
import com.app.events.MainActivity;
import com.app.events.R;
import com.app.events.activities.admin.Navigation;
import com.app.events.adapters.business.ViewEventsAdapter;
import com.app.events.utils.DummyData;
import com.app.events.utils.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PendingEvents extends Fragment {
    public Helper helper;
    public ProgressDialog pgdialog;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private Context ctx;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pending_events, container, false);
        ctx = getActivity().getWindow().getContext();
        helper = new Helper(ctx);
        pgdialog = new ProgressDialog(ctx);
        pgdialog.setMessage(ctx.getString(R.string.loading));
      
        recyclerView = view.findViewById(R.id.recycler_events);
        layoutManager = new LinearLayoutManager(ctx);
        recyclerView.setLayoutManager(layoutManager);
        loadEvents();

        return view;
    }
    void initDefaultDummyData(){
        String jsonArr = DummyData.events;
        try {
            JSONArray arr = new JSONArray(jsonArr);
            ViewEventsAdapter adapter = new ViewEventsAdapter(ctx,arr);
            recyclerView.setAdapter(adapter);
        }catch (JSONException ex){
            Log.d("jsonerr"," Json data error: "+ex.getMessage());
        }
    }

    void loadEvents(){
        final String url = helper.host+"/events/bystatus/pending";
        pgdialog.show();
        Log.d("URL",url);
//        tvLoggingIn.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(ctx);
// prepare the Request
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // display response
                        pgdialog.dismiss();
                        Log.d("Logresp",response);
                        try{
                            JSONObject object = new JSONObject(response);
                            if(object.getString("status").equals("ok")) {
                                JSONArray arr = object.getJSONArray("data");
                                ViewEventsAdapter adapter = new ViewEventsAdapter(getActivity(), arr);
                                recyclerView.setAdapter(adapter);
                            }
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
//    @Override
//    public void onAttach(Activity activity){
//        super.onAttach(activity);
//        ctx = getActivity();
//    }
}