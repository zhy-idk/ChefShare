package com.example.practice1;

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

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ProfilePostsFragment extends Fragment {
    RecyclerView rvUserPosts;
    PostsAdapter adapter;

    public ProfilePostsFragment() {
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
        return inflater.inflate(R.layout.fragment_profile_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvUserPosts = view.findViewById(R.id.rvUserPosts);


        rvUserPosts.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private void fetchPosts(){
        String author = "1234567";
        Query userPostsQuery = FirebaseDatabase.getInstance().getReference("posts").orderByChild("author").equalTo(author);

        FirebaseRecyclerOptions<PostsModel> options = new FirebaseRecyclerOptions.Builder<PostsModel>()
                .setQuery(userPostsQuery, PostsModel.class)
                .build();

        adapter = new PostsAdapter(options);
        rvUserPosts.setAdapter(adapter);
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