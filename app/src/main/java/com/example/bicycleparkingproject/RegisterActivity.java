package com.example.bicycleparkingproject;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword, editTextFirstName, editTextLastName, editTextPasswordConfirm;
    Button btnRegister, btnLogin;
//    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    CollectionReference reference;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.etEmailRegister);
        editTextPassword = findViewById(R.id.etPasswordRegister);
        editTextFirstName = findViewById(R.id.etFirstNameRegister);
        editTextLastName = findViewById(R.id.etLastNameRegister);
        editTextPasswordConfirm = findViewById(R.id.etPasswordConfirmRegister);
        btnLogin = findViewById(R.id.btnGoToLogin);
        btnRegister = findViewById(R.id.btnRegister);
        db = FirebaseFirestore.getInstance();
        reference = db.collection("UserInformation");

//        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                progressBar.setVisibility(View.VISIBLE);
                String email, password, confirmPassword, firstName, lastName;
                email = String.valueOf(editTextEmail.getText()).trim();
                password = String.valueOf(editTextPassword.getText()).trim();
                confirmPassword = String.valueOf(editTextPasswordConfirm.getText()).trim();
                firstName = String.valueOf(editTextFirstName.getText()).trim();
                lastName = String.valueOf(editTextLastName.getText()).trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(RegisterActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Enter Confirmed Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(firstName)) {
                    Toast.makeText(RegisterActivity.this, "Enter First Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(lastName)) {
                    Toast.makeText(RegisterActivity.this, "Enter Last Name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Passwords Do Not Match", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
//                                    progressBar.setVisibility(View.GONE);
                                    String uid = user.getUid();
                                    addData(uid, firstName, lastName, reference);
                                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

        });

    }
    private void addData(String uid, String firstName, String lastName, CollectionReference reference) {
        Log.d(TAG, "reached addData");
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Map<String, String> info = new HashMap<>();
        info.put("uid", uid);
        info.put("First Name", firstName);
        info.put("Last Name", lastName);

        reference.document(uid)
                .set(info)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }
}
