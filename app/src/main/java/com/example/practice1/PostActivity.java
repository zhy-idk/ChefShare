package com.example.practice1;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
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

    DatabaseReference postsRef;
    Uri selectedUri;

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

        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                        Glide.with(this).load(uri).into(ivPreview);
                        //ivPreview.setImageURI(uri);
                        selectedUri = uri;
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        btnImageSelect.setOnClickListener(view -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder().build());
        });


        btnPost.setOnClickListener(view -> {
            StorageReference ref = FirebaseStorage.getInstance().getReference().child("images");

            ref.putFile(selectedUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    ref.getDownloadUrl().addOnCompleteListener(urlTask -> {
                        if (urlTask.isSuccessful()) {
                            String imageUrl = urlTask.getResult().toString(); // ✅ actual URL
                            newPost(imageUrl);
                            finish();
                        }
                    });
                } else {
                    Log.d("PostActivity", "Image upload failed");
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnBack.setOnClickListener(v -> {
            finish();
        });


    }

    private void newPost(String imageUrl) {
        String title = String.valueOf(etPost.getText());
        String author = "1234567";
        PostsModel model = new PostsModel(title, imageUrl, author);
        DatabaseReference postPushRef = postsRef.push();

        postPushRef.setValue(model).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Log.d("PostActivity", "Post created");
                Toast.makeText(this, "Post created", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("PostActivity", "Post creation failed");
                Toast.makeText(this, "Post creation failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}