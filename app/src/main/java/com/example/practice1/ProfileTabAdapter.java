package com.example.practice1;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.Objects;

public class ProfileTabAdapter extends FragmentStateAdapter {

    // ✅ Pass the Fragment, not the Activity
    public ProfileTabAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new ProfilePhotosFragment();
        }
        return new ProfilePostsFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
