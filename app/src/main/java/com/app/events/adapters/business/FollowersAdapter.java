package com.app.events.adapters.business;


import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class FollowersAdapter extends RecyclerView.Adapter<com.app.events.adapters.business.FollowersAdapter.MyViewHolder> {
    public LinearLayout v;
    public Context ctx;
    public Helper helper;
    private JSONArray mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public FollowersAdapter(Context context, JSONArray myDataset) {
        mDataset = myDataset;
        ctx = context;
        helper = new Helper(ctx);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public com.app.events.adapters.business.FollowersAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                                                    int viewType) {
        // create a new view
        v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_followers, parent, false);
        com.app.events.adapters.business.FollowersAdapter.MyViewHolder vh = new com.app.events.adapters.business.FollowersAdapter.MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final com.app.events.adapters.business.FollowersAdapter.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        try {
            JSONObject currentObj = mDataset.getJSONObject(position);
            //Toast.makeText(ctx,currentObj.getString("cat_name")+"-"+currentObj.getString("cat_id"),Toast.LENGTH_SHORT).show();
            holder.followId.setText(currentObj.getString("id"));
            holder.followedBy.setText(currentObj.getString("follower_name"));
            holder.followerPhone.setText(currentObj.getString("follower_phone"));
            SimpleDateFormat sda = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            SimpleDateFormat sdf = new SimpleDateFormat("E,MMM dd,yyyy hh:mm a");
            holder.followedOn.setText("Followed on "+sdf.format(sda.parse(currentObj.getString("created_at").replace("T"," "))));

        } catch (JSONException ex) {
            Log.d("jsonerr","Error "+ex.getMessage());
        } catch (ParseException e) {
            e.printStackTrace();
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
        public TextView followerPhone,followedBy,followedOn,followId;
        public ImageView imgCategoryIcon;
        public LinearLayout lnlayout;

        public MyViewHolder(LinearLayout lny) {
            super(lny);
            lnlayout = lny.findViewById(R.id.singleBusinessHolder);

            followId = lny.findViewById(R.id.followId);
            followedBy = lny.findViewById(R.id.followerName);
            followerPhone = lny.findViewById(R.id.followerPhone);
            followedOn = lny.findViewById(R.id.followedOn);
        }
    }
}