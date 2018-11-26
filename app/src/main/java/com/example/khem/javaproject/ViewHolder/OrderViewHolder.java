package com.example.khem.javaproject.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.khem.javaproject.Interface.ItemClickListener;
import com.example.khem.javaproject.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView food_name;
    public ImageView food_image;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
