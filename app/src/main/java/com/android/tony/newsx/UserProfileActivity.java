package com.android.tony.newsx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    TextView userName, userEmail, userLocation, userLogout;
    CircleImageView circleImageView;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userName = findViewById(R.id.userNameTextView);
        userEmail = findViewById(R.id.userEmailTextView);
        userLocation = findViewById(R.id.userLocationTextView);
        userLogout = findViewById(R.id.userLogoutTextView);
        circleImageView = findViewById(R.id.userProfileImageView);
        ImageView userBack;

        firebaseAuth = FirebaseAuth.getInstance();

        Picasso.get().load(firebaseAuth.getCurrentUser().getPhotoUrl()).into(circleImageView);
        userName.setText(firebaseAuth.getCurrentUser().getDisplayName());
        userEmail.setText(firebaseAuth.getCurrentUser().getEmail());
        userLocation.setText(getApplicationContext().getResources().getConfiguration().locale.getDisplayCountry());
        userBack = findViewById(R.id.userBackImageView);
        userBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserProfileActivity.this.finish();
            }
        });

        userLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(UserProfileActivity.this, MainActivity.class));
                UserProfileActivity.this.finish();
            }
        });
    }
}
