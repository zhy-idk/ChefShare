package com.example.practice1;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class PostImageAdapter extends FirebaseRecyclerAdapter<PostImageModel, PostImageAdapter.ViewHolder> {


    public PostImageAdapter(@NonNull FirebaseRecyclerOptions<PostImageModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull PostImageModel model) {
        Log.d("onBindViewHolder", model.getLink());
        try {
            Glide.with(holder.itemView.getContext())
                    .load(model.getLink())
                    .into(holder.ivPostUserImage);
        } catch (Exception e){
            Log.d("onBindViewHolder", "Exception: " + e.getMessage());
        }
        //Glide.with(holder.itemView.getContext()).load(model.getLink()).into(holder.ivPostUserImage);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photos_item, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPostUserImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPostUserImage = itemView.findViewById(R.id.ivPostUserImage);
        }
    }
}
