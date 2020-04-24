package com.example.wahyuajisantoso.animalshop.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wahyuajisantoso.animalshop.Interface.ItemClickListener;
import com.example.wahyuajisantoso.animalshop.R;

public class AnimaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView animal_name;
    public ImageView animal_image;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public AnimaViewHolder(View itemView) {
        super(itemView);

        animal_name = (TextView)itemView.findViewById(R.id.animal_name);
        animal_image = (ImageView) itemView.findViewById(R.id.animal_image);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);

    }
}