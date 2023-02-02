package com.example.bicycleparkingproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
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
    private final CollectionReference noteBookRef = db.collection("BikeRacks");

    private ListView listView;

    private List<BikeRack> bikeRacks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            readBikeRackData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //Toast.makeText(this, "" + bikeRacks.get(2), Toast.LENGTH_LONG).show();
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

}