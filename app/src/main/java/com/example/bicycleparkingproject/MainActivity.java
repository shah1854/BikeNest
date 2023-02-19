package com.example.bicycleparkingproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference bikeRackRef = db.collection("BikeRacks");
    private DocumentReference rackRef = db.document("BikeRacks/First Bike Rack");
    private List<BikeRack> bikeRacks = new ArrayList<>();
    private Button buttonTestLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadBikeRackData();
        /*buttonTestLoad = findViewById(R.id.button_test_load);
        buttonTestLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, bikeRacks.get(0).getLocation(), Toast.LENGTH_SHORT).show();
            }
        });
         */
        /* parse bike_racks.csv into an array list and save data in a firestore collection
        btn = findViewById(R.id.btn2);
        try {
            readBikeRackData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBikeRack(v);
            }
        });
         */
    }

    private void readBikeRackData() throws IOException {
        InputStream is = getResources().openRawResource(R.raw.bike_racks);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8)
        );
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                // split by comma
                String[] tokens = line.split(",");
                if (tokens[0].length() == 0 || tokens[1].length() == 0 || tokens[2].length() == 0) {
                    continue;
                }
                // read the data
                BikeRack rack = new BikeRack();
                rack.setId(tokens[0]);
                rack.setAddress(tokens[1]);
                rack.setLocation(tokens[2]);
                bikeRacks.add(rack);
                //Log.d(TAG, "Just created bike rack");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void saveBikeRack(View v) {
        for (BikeRack b : bikeRacks) {
            String id = b.getId();
            String location = b.getLocation();
            String address = b.getAddress();
            BikeRack bikeRack = new BikeRack(id, location, address);
            bikeRackRef.add(bikeRack).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    //Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Added to DB");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void loadBikeRackData() {
        bikeRackRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // retrieve every document from a collection using for-each loop:
                        // don't have to check if QueryDocumentSnapshot exists
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            BikeRack rack = documentSnapshot.toObject(BikeRack.class);
                            rack.setId(documentSnapshot.getId());   // gets firestore generated id for document
                            // can add this rack to an ArrayList
                            String id = rack.getId();
                            String address = rack.getAddress();
                            String location = rack.getLocation();
                            bikeRacks.add(rack);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error loading note!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });
    }
}