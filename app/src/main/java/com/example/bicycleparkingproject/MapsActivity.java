package com.example.bicycleparkingproject;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.bicycleparkingproject.databinding.ActivityMaps2Binding;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
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



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMaps2Binding binding;

    FusedLocationProviderClient fusedLocationProviderClient;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference bikeRackRef = db.collection("BikeRacks");
    private DocumentReference rackRef = db.document("BikeRacks/First Bike Rack");
    private List<BikeRack> bikeRacks = new ArrayList<>();
    private Button buttonTestLoad;
    private Button btn;
    private final Handler HANDLER = new Handler();
    private static final int DELAY_TIME = 3000;

    private static final String TAG = "MapsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMaps2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng chicago = new LatLng(41.881832, -87.623177);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        float zoomLevel = 15.0f;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(chicago, zoomLevel));
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
                    //Log.d(TAG, "Successfully loaded bike rack");
                }
            }
        });
        HANDLER.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                //Toast.makeText(MapsActivity.this, "First bike rack location: " + bikeRacks.get(0).getLocation(), Toast.LENGTH_SHORT).show();
                //addTestMarkers();
                //Toast.makeText(MapsActivity.this, "Size: " + bikeRacks.size(), Toast.LENGTH_SHORT).show();
                //addOneMarker();
                addMarkers();
            }
        }, DELAY_TIME);

    }

    private void addTestMarkers() {
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(41.8789, -87.6359))
                .title("Willis Tower")
                .snippet("Snippet 1"));
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(41.8826, -87.6226))
                .title("Millenium Park")
                .snippet("Snippet 2"));

    }
    private void addMarkers() {
        for (BikeRack rack : bikeRacks) {
            if (rack == null) continue;
            Coordinates coordinates = parseCoordinatesFromString(rack.getLocation());
            //assert coordinates != null;
            if (coordinates == null) continue;
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(coordinates.getLatitude(), coordinates.getLongitude()))
                    .title(rack.getAddress()));
            Log.d(TAG, "Added marker");
        }
    }
    private Coordinates parseCoordinatesFromString(String location) {
        String cleanedLocation = location.replaceAll("[^0-9.;-]", "");
        String[] parts = cleanedLocation.split(";");
        if (parts.length == 2) {
            try {
                double latitude = Double.parseDouble(parts[0]);
                double longitude = Double.parseDouble(parts[1]);
                return new Coordinates(latitude, longitude);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return null; // Handle invalid format
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
                    Toast.makeText(MapsActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}