package com.example.practice1;


import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.color.MaterialColors;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.BreakIterator;
import java.util.Objects;

public class PostsAdapter extends FirebaseRecyclerAdapter<PostsModel, PostsAdapter.ViewHolder> {

    //private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference dbRef;

    public PostsAdapter(@NonNull FirebaseRecyclerOptions<PostsModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull PostsModel model) {
        holder.txtTitle.setText(model.getTitle());

        String postId = getRef(position).getKey();
        String authorId = model.getAuthor();

        dbRef = FirebaseDatabase.getInstance().getReference("posts")
                .child(postId);

        DatabaseReference authorRef = FirebaseDatabase.getInstance().getReference("users")
                .child(authorId);

        authorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String authorName = String.valueOf(snapshot.child("name").getValue());
                    holder.txtAuthor.setText(authorName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //String uid = mAuth.getCurrentUser().getUid();
        String uid = "qwertyuiop";

        DatabaseReference likesRef = dbRef.child("likes");

        DatabaseReference bodyRef = dbRef.child("body");

        bodyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String body = String.valueOf(snapshot.getValue());

                if (body.isBlank()){
                    Log.d("onDataChange", "body is empty");
                    holder.ivImage.setVisibility(View.GONE);
                } else {
                    Log.d("onDataChange", "body is not empty");
                    holder.ivImage.setVisibility(View.VISIBLE);
                    Glide.with(holder.itemView.getContext()).load(body).into(holder.ivImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Load like count and check if user already liked
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String likeCount = String.valueOf(snapshot.getChildrenCount());
                holder.btnLike.setText(likeCount);
                int color = MaterialColors.getColor(holder.itemView, com.google.android.material.R.attr.colorSurface, Color.BLACK);


                // Check if current user liked the post
                if (snapshot.hasChild(uid)) {
                    holder.btnLike.setText(likeCount); // Liked
                    holder.btnLike.setIconTint(ColorStateList.valueOf(holder.itemView.getResources().getColor(R.color.blue)));
                } else {
                    holder.btnLike.setText(likeCount); // Not liked
                    holder.btnLike.setIconTint(ColorStateList.valueOf(color));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Like / Unlike toggle
        holder.btnLike.setOnClickListener(v -> {
            likesRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Already liked, so unlike
                        likesRef.child(uid).removeValue();
                    } else {
                        // Not liked, so like
                        likesRef.child(uid).setValue(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        });

        holder.itemView.setOnClickListener(view -> {

        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.posts_item, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtAuthor, txtTitle;
        MaterialButton btnLike;

        ImageView ivImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAuthor = itemView.findViewById(R.id.txtAuthor);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            ivImage = itemView.findViewById(R.id.ivImage);
            btnLike = itemView.findViewById(R.id.btnLike);
        }
    }
}