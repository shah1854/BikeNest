package com.example.bicycleparkingproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String KEY_NICKNAME = "title";
    private static final String KEY_LATITUDE = "description";
    private static final String KEY_LONGITUDE = "description";
    private static final String KEY_TYPE = "description";
    private static final String KEY_SAFETY_SCORE = "description";
    private static final String KEY_PICTURE = "description";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference bikeRackRef = db.collection("BikeRacks");
    private DocumentReference rackRef = db.document("BikeRacks/First Bike Rack");
    private List<BikeRack> bikeRacks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /* parse bike_rack.csv data into array list
        try {
            readBikeRackData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
                if (tokens[0].length() == 0 || tokens[2].length() == 0) {
                    continue;
                }
                // read the data
                BikeRack rack = new BikeRack();
                rack.setId(tokens[0]);
                rack.setLocation(tokens[2]);
                bikeRacks.add(rack);
                Log.d(TAG, "Just created bike rack");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void saveBikeRack(View v) {
        for (BikeRack b : bikeRacks) {
            String id = b.getId();
            String location = b.getLocation();
            BikeRack bikeRack = new BikeRack(id, location);
            bikeRackRef.add(bikeRack).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}