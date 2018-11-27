package com.example.khem.javaproject.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.khem.javaproject.Interface.ItemClickListener;
import com.example.khem.javaproject.R;

import java.util.ArrayList;

public class OrderAdater extends RecyclerView.Adapter<OrderAdater.MyViewHolder>{

    private ArrayList<Object> mDataset;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public MyViewHolder(TextView v) {
            super(v);
            mTextView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public OrderAdater (ArrayList<Object> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public OrderAdater.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_text_view , parent, false);
        OrderAdater.MyViewHolder vh = new OrderAdater.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.mTextView.setText(mDataset.get(i).toString());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
