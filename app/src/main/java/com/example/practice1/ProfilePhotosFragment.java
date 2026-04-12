package com.example.practice1;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class ProfilePhotosFragment extends Fragment {
    RecyclerView rvUserPhotos;
    PostImageAdapter adapter;


    public ProfilePhotosFragment() {
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
        return inflater.inflate(R.layout.fragment_profile_photos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvUserPhotos = view.findViewById(R.id.rvUserPhotos);

        rvUserPhotos.setLayoutManager(new GridLayoutManager(getContext(), 3));
    }

    private void fetchPosts(){
        String author = "1234567";
        Query userPostsQuery = FirebaseDatabase.getInstance().getReference("users").child(author).child("photos");

        FirebaseRecyclerOptions<PostImageModel> options = new FirebaseRecyclerOptions.Builder<PostImageModel>()
                .setQuery(userPostsQuery, PostImageModel.class)
                .build();

        adapter = new PostImageAdapter(options);
        rvUserPhotos.setAdapter(adapter);
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