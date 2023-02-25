package com.example.bicycleparkingproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.EventListener;

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
    private final Handler HANDLER = new Handler();
    private static final int DELAY_TIME = 5000;

    TextView tv_lat, tv_lon, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /* parse bike_racks.csv into an array list and save data in a firestore collection
        btn = findViewById(R.id.btn2);
        try {
            readBikeRackDataFromCsv();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBikeRackData(v);
            }
        });
         */
        tv_lat = findViewById(R.id.tv_lat); //Latitude
        tv_lon = findViewById(R.id.tv_lon); //Longitude
        tv_address = findViewById(R.id.tv_address); //Address of Location
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
        tv_updates = findViewById(R.id.tv_updates);




    }

    @Override
    protected void onStart() {
        super.onStart();
        bikeRackRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d(TAG, "onEvent: " + error.toString());
                    return;
                }
                assert value != null;
                for (QueryDocumentSnapshot documentSnapshot : value) {
                    BikeRack rack = documentSnapshot.toObject(BikeRack.class);
                    bikeRacks.add(rack);
                    Log.d(TAG, "Successfully loaded bike rack");
                }
            }
        });
        //toastFirstRack();
    }
    private void toastFirstRack() {
        HANDLER.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                Toast.makeText(MainActivity.this, "Back rack size: " + bikeRacks.size(), Toast.LENGTH_SHORT).show();
            }
        }, DELAY_TIME);
    }

    private void readBikeRackDataFromCsv() throws IOException {
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
    public void saveBikeRackData(View v) {
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
}