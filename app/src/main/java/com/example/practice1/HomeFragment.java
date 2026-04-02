package com.example.practice1;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class HomeFragment extends Fragment {
    RecyclerView rvPosts;

    PostsModel model;
    Button btnNewPost;
    PostsAdapter adapter;


    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnNewPost = view.findViewById(R.id.btnNewPost);
        rvPosts = view.findViewById(R.id.rvPosts);
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        Log.d("onViewCreated", "onViewCreated");

        //rvPosts.setNestedScrollingEnabled(true);

        btnNewPost.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PostActivity.class);
            startActivity(intent);
        });

    }

    public void fetchPosts(){
        Log.d("fetchPosts", "fetchPosts");
        Query query = FirebaseDatabase.getInstance().getReference("posts");

        FirebaseRecyclerOptions<PostsModel> options = new FirebaseRecyclerOptions.Builder<PostsModel>()
                .setQuery(query, PostsModel.class)
                .build();

        adapter = new PostsAdapter(options);
        rvPosts.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchPosts();
        Log.d("onStart", "onStart");
    }


    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
        Log.d("onStop", "onStop");
    }

}