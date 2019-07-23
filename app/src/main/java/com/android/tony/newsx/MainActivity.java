package com.android.tony.newsx;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.mainProgressBar);
        checkPermission();




    }

    void checkPermission()
    {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
        else
            setProg(0);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 101 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            setProg(0);
        else
        {
            Snackbar.make(findViewById(R.id.mainParentCL),"Location is required",Snackbar.LENGTH_INDEFINITE).setActionTextColor(getResources().getColor(R.color.colortext)).setAction("Ok", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkPermission();
                }
            }).show();
        }
    }

    public void setProg(final int i) {
        progressBar.setProgress(i);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setProg(i + 25);
                if (i == 100) {

                    if (FirebaseAuth.getInstance().getCurrentUser() != null && FirebaseAuth.getInstance().getCurrentUser().isEmailVerified())
                        startActivity(new Intent(MainActivity.this, NewsActivity.class));
                    else
                        startActivity(new Intent(MainActivity.this, UserManagerActivity.class));
                    MainActivity.this.finish();
                }
            }
        }, 1000);
    }
}
