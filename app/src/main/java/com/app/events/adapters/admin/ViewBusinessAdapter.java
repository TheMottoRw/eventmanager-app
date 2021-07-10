package com.app.events.adapters.admin;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.events.R;
import com.app.events.utils.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ViewBusinessAdapter extends RecyclerView.Adapter<ViewBusinessAdapter.MyViewHolder> {
    public LinearLayout v;
    public Context ctx;
    public Helper helper;
    public JSONObject readStatusSymbol = new JSONObject();
    private JSONArray mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ViewBusinessAdapter(Context context, JSONArray myDataset) {
        mDataset = myDataset;
        ctx = context;
        helper = new Helper(ctx);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewBusinessAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_business_1, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        try {
            JSONObject currentObj = mDataset.getJSONObject(position);
            //Toast.makeText(ctx,currentObj.getString("cat_name")+"-"+currentObj.getString("cat_id"),Toast.LENGTH_SHORT).show();
            holder.businessId.setText(currentObj.getString("id"));
            holder.businessName.setText(currentObj.getString("name"));
            holder.businessType.setText(currentObj.getString("business_type"));
            holder.businessTin.setText(ctx.getString(R.string.business_tin)+": "+ currentObj.getString("tin"));
            holder.businessContact.setText(currentObj.getString("contact_number"));
            holder.businessAddress.setText(currentObj.getString("address"));
            //set image icons


        } catch (JSONException ex) {
            Log.d("jsonerr","Error "+ex.getMessage());
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView businessId,businessName,businessType,businessTin,businessAddress,businessContact;
        public ImageView imgCategoryIcon;
        public LinearLayout lnlayout;

        public MyViewHolder(LinearLayout lny) {
            super(lny);
            lnlayout = lny.findViewById(R.id.singleBusinessHolder);
            businessId = lny.findViewById(R.id.businessId);
            businessName = lny.findViewById(R.id.businessName);
            businessType = lny.findViewById(R.id.businessType);
            businessTin = lny.findViewById(R.id.businessTin);
            businessContact = lny.findViewById(R.id.businessContact);
            businessAddress = lny.findViewById(R.id.businessAddress);
            //tvMsg = lny.findViewById(R.id.tvRecyclerDate);
        }
    }
}