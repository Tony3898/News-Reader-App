package com.android.tony.newsx;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserManagerActivity extends AppCompatActivity {

    TextView userAccountTextView;
    ImageView googleImageView, facebookImageView, addImageView;
    EditText nameEditText, emailEditText, passwordEditText;
    Button signButton;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    CircleImageView circleImageView;
    Uri uriImage;
    ProgressBar progressBar;
    int RC_SIGN_IN = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manager);

        progressBar = findViewById(R.id.userManagementProgressBar);
        userAccountTextView = findViewById(R.id.userTextView);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signButton = findViewById(R.id.signbtn);
        googleImageView = findViewById(R.id.googleImageView);
        facebookImageView = findViewById(R.id.facebookImageView);
        circleImageView = findViewById(R.id.userManagerImageView);
        addImageView = findViewById(R.id.addImageView);
        circleImageView.setClickable(false);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 101);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        storageReference = FirebaseStorage.getInstance().getReference().child("Users");

        userAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userAccountTextView.getText().equals("Create Account")) {
                    signButton.setText(R.string.sign_up);
                    addImageView.setVisibility(View.VISIBLE);
                    circleImageView.setClickable(true);
                    nameEditText.setVisibility(View.VISIBLE);
                    userAccountTextView.setText(R.string.already_have_an_account);
                    emailEditText.setText("");
                    passwordEditText.setText("");
                    nameEditText.setText("");
                } else {
                    addImageView.setVisibility(View.GONE);
                    emailEditText.setText("");
                    passwordEditText.setText("");
                    nameEditText.setText("");
                    circleImageView.setClickable(false);
                    signButton.setText(R.string.sign_in);
                    nameEditText.setVisibility(View.GONE);
                    userAccountTextView.setText(R.string.create_account);
                }
            }
        });

        signButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                if (signButton.getText().toString().equals("Sign in")) {
                    String email = emailEditText.getText().toString().trim();
                    String password = passwordEditText.getText().toString().trim();

                    if (email.isEmpty() || password.isEmpty()) {
                        Snackbar.make(findViewById(R.id.parentcl), "All Fields are required", Snackbar.LENGTH_SHORT).show();
                        passwordEditText.setText("");
                    } else {
                        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                                        startActivity(new Intent(UserManagerActivity.this, NewsActivity.class));
                                        UserManagerActivity.this.finish();
                                    } else {
                                        Snackbar.make(findViewById(R.id.parentcl), "Verify your email to login", Snackbar.LENGTH_LONG).show();
                                        firebaseAuth.signOut();
                                        emailEditText.setText("");
                                        passwordEditText.setText("");
                                    }
                                } else {
                                    Snackbar.make(findViewById(R.id.parentcl), task.getException().getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    final String name = nameEditText.getText().toString().trim();
                    final String email = emailEditText.getText().toString().trim();
                    String password = passwordEditText.getText().toString().trim();
                    if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "All Fields are required", Toast.LENGTH_SHORT).show();
                        passwordEditText.setText("");
                    } else {
                        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    databaseReference.child(firebaseAuth.getCurrentUser().getUid()).setValue(new Users(name, email)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            storageReference.child("Images").child("Profile Picture").putFile(uriImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        storageReference.child("Images").child("Profile Picture").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Uri> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    Log.i("userimage", task.getResult() + "");
                                                                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                                                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                                                            .setDisplayName(name)
                                                                                            .setPhotoUri(task.getResult())
                                                                                            .build();

                                                                                    user.updateProfile(profileUpdates)
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    if (task.isSuccessful()) {
                                                                                                        Toast.makeText(getApplicationContext(), "Verify your email to login", Toast.LENGTH_LONG).show();
                                                                                                        firebaseAuth.signOut();
                                                                                                        startActivity(new Intent(UserManagerActivity.this, MainActivity.class));
                                                                                                        UserManagerActivity.this.finish();
                                                                                                    }
                                                                                                }
                                                                                            });
                                                                                } else {
                                                                                    Snackbar.make(findViewById(R.id.parentcl), task.getException().getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });

                                                        } else {
                                                            Snackbar.make(findViewById(R.id.parentcl), task.getException().getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
                                                            databaseReference.setValue(null);
                                                            firebaseAuth.getCurrentUser().delete();
                                                        }
                                                    }
                                                });
                                            } else {
                                                Snackbar.make(findViewById(R.id.parentcl), task.getException().getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
                                                firebaseAuth.getCurrentUser().delete();
                                            }
                                        }
                                    });
                                } else {
                                    Snackbar.make(findViewById(R.id.parentcl), task.getException().getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
                                    passwordEditText.setText("");
                                }
                            }
                        });
                    }

                }
            }
        });

        googleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(UserManagerActivity.this, gso);
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Snackbar.make(findViewById(R.id.parentcl), e.getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
            }
        } else if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            uriImage = data.getData();
            try {
                circleImageView.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), uriImage));

            } catch (Exception e) {
                Snackbar.make(findViewById(R.id.parentcl), e.getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).setValue(new Users(user.getDisplayName(), user.getEmail())).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressBar.setVisibility(View.GONE);
                                        startActivity(new Intent(UserManagerActivity.this, NewsActivity.class));
                                        UserManagerActivity.this.finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                        firebaseAuth.getCurrentUser().delete();
                                    }
                                }
                            });

                        } else {
                            progressBar.setVisibility(View.GONE);
                            Snackbar.make(findViewById(R.id.parentcl), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }


                    }
                });
    }
}
