package com.example.practice1;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PostActivity extends AppCompatActivity {
    Button btnPost, btnBack, btnImageSelect;
    TextInputEditText etPost;
    ImageView ivPreview;
    View voverlay;
    LinearLayout progressLayout;
    TextView tvProgress;

    DatabaseReference postsRef;
    Uri selectedUri;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        postsRef = FirebaseDatabase.getInstance().getReference("posts");

        btnPost = findViewById(R.id.btnPost);
        btnBack = findViewById(R.id.btnBack);
        btnImageSelect = findViewById(R.id.btnImageSelect);
        etPost = findViewById(R.id.etPost);

        ivPreview = findViewById(R.id.ivPreview);

        voverlay = findViewById(R.id.voverlay);
        progressLayout = findViewById(R.id.progressLayout);
        tvProgress = findViewById(R.id.tvProgress);


        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                        Glide.with(this).load(uri).into(ivPreview);
                        selectedUri = uri;
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        btnImageSelect.setOnClickListener(view -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder().build());
        });


        btnPost.setOnClickListener(view -> {
            newPost();
        });

        btnBack.setOnClickListener(v -> {
            finish();
        });


    }

    /*
    // we can do it like this just initiate a String url above and call getUrl in post button listener

    private void getUrl(){
        String filename = "images/" + System.currentTimeMillis() + ".jpg";
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filename);
        if (selectedUri == null){
            url = "";
            newPost();
            return;
        }

        ref.putFile(selectedUri)
                .addOnSuccessListener(task -> {
                    task.getStorage().getDownloadUrl()
                            .addOnCompleteListener(urlTask -> {
                                url = urlTask.toString();
                                newPost();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.d("PostActivity", "Upload failed: " + e.getMessage());
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
                    url = "";
                    newPost();
                });
    }

    private void newPost() {
        String title = String.valueOf(etPost.getText());
        String author = "1234567";
        PostsModel model = new PostsModel(title, url, author);
        DatabaseReference postPushRef = postsRef.push();

        postPushRef.setValue(model).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Log.d("PostActivity", "Post created");
                Toast.makeText(this, "Post created", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Log.d("PostActivity", "Post creation failed");
                Toast.makeText(this, "Post creation failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    */

    private void newPost() {
        voverlay.setVisibility(View.VISIBLE);
        progressLayout.setVisibility(View.VISIBLE);
        getUrl(url -> {
            String title = String.valueOf(etPost.getText());
            String author = "1234567";
            PostsModel model = new PostsModel(title, url, author);
            DatabaseReference postPushRef = postsRef.push();


            postPushRef.setValue(model).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("PostActivity", "Post created");
                    Toast.makeText(this, "Post created", Toast.LENGTH_SHORT).show();
                    voverlay.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    finish();
                } else {
                    Log.d("PostActivity", "Post creation failed");
                    Toast.makeText(this, "Post creation failed", Toast.LENGTH_SHORT).show();
                    voverlay.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                }
            });

        });
    }

    private void getUrl(OnUrlReadyCallback callback) {
        String author = "1234567";
        DatabaseReference userPhotos = FirebaseDatabase.getInstance().getReference("users").child(author).child("photos");
        if (selectedUri == null) {
            callback.onReady(""); // no image, proceed immediately
            return;
        }

        String filename = "images/" + System.currentTimeMillis() + ".jpg";
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filename);

        ref.putFile(selectedUri)
                .addOnProgressListener(taskSnapshot -> {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    tvProgress.setText("Uploading image: " + (int) progress + "%");
                })
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                callback.onReady(uri.toString()); // ✅ URL is ready, proceed
                                userPhotos.push().child("link").setValue(uri.toString());
                            })
                            .addOnFailureListener(e -> {
                                Log.d("PostActivity", "Failed to get URL: " + e.getMessage());
                                Toast.makeText(this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.d("PostActivity", "Upload failed: " + e.getMessage());
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
                });
    }

    // Define the callback interface
    interface OnUrlReadyCallback {
        void onReady(String url);
    }

}