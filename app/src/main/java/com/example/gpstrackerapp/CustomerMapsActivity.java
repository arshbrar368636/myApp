package com.example.gpstrackerapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class CustomerMapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    GoogleApiClient client;
    LocationRequest request;
    LatLng latLng;
    FirebaseAuth auth;
    FirebaseUser currentUser;

    Location mLastLocation;
    private float rideDistance;

    DatabaseReference AssignedCustomerRef,AssignedCustomerPickupRef;
    String driverId,customerId="";

    Button DriverLogoutButton;
    Button DriverSettingsButton;
    Button DriverHistoryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

         auth= FirebaseAuth.getInstance();
         currentUser= auth.getCurrentUser();

         driverId = auth.getCurrentUser().getUid();

         DriverSettingsButton= (Button)findViewById(R.id.driver_settings);
         DriverSettingsButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent= new Intent(CustomerMapsActivity.this,SettingsActivity.class);
                 intent.putExtra("type","Drivers");
                 startActivity(intent);
             }
         });


         DriverLogoutButton=(Button)findViewById(R.id.driver_logout);
         DriverLogoutButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
              auth.signOut();
                 LogoutDriver();

             }
         });

        // DriverHistoryButton=(Button)findViewById(R.id.driver_history) ;
//         DriverHistoryButton.setOnClickListener(new View.OnClickListener() {
//             @Override
//             public void onClick(View v) {
//                 Intent intent= new Intent(CustomerMapsActivity.this,HistoryActivity.class);
//                 intent.putExtra("customerOrDriver","Drivers");
//                 startActivity(intent);
//             }
//         });
//

        getAssignedCustomerRequest();
    }

    private void LogoutDriver() {
        Intent intent= new Intent(CustomerMapsActivity.this,DriverRegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void getAssignedCustomerRequest() {
         AssignedCustomerRef =FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("CustomerRideId");
         AssignedCustomerRef.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if(dataSnapshot.exists())
                 {
                     customerId = dataSnapshot.getValue().toString();
                     getAssignedCustomerPickupLocation();
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
    }

    private void getAssignedCustomerPickupLocation() {
        AssignedCustomerPickupRef= FirebaseDatabase.getInstance().getReference().child("Customers Request")
                .child(customerId).child("l");
        AssignedCustomerPickupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    List<Object> customerLocationMap = (List<Object>) dataSnapshot.getValue();
                    double LocationLat = 0;
                    double LocationLng = 0;


                    if (customerLocationMap.get(0) != null)
                    {
                        LocationLat= Double.parseDouble(customerLocationMap.get(0).toString());
                    }
                    if (customerLocationMap.get(1) != null)
                    {
                        LocationLng= Double.parseDouble(customerLocationMap.get(1).toString());
                    }
                    LatLng DriverLatLang = new LatLng(LocationLat,LocationLng);
                    mMap.addMarker(new MarkerOptions().position(DriverLatLang).title("Pickup Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.location)));
                    recordRide();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void recordRide() {
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("history");
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId).child("history");
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("history");
        String requestId=historyRef.push().getKey();
        driverRef.child(requestId).setValue(true);
        customerRef.child(requestId).setValue(true);

        HashMap map= new HashMap();
        map.put("driver",driverId);
        map.put("customer",customerId);
        map.put("timestamp",getCurrentTimeStamp());
        map.put("distance",rideDistance);
        historyRef.child(requestId).updateChildren(map);
    }

    private Long getCurrentTimeStamp() {
        Long timestamp = System.currentTimeMillis()/1000;

        return timestamp;


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

        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        client.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = new LocationRequest().create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(3000);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            Toast.makeText(getApplicationContext(), "Could not get location", Toast.LENGTH_SHORT).show();
        }
        else {
                if(getApplicationContext() != null)
                {
                    if(!customerId.equals(""))
                    {
                        rideDistance +=mLastLocation.distanceTo(location);

                    }
                    mLastLocation=location;
                latLng = new LatLng(location.getLatitude(), location.getLongitude());

                MarkerOptions options = new MarkerOptions();
                options.position(latLng);
                options.title("Driver").icon(BitmapDescriptorFactory.fromResource(R.drawable.car));
                mMap.addMarker(options);

                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(latLng, 12);
                mMap.animateCamera(yourLocation);

                String userId= FirebaseAuth.getInstance().getCurrentUser().getUid();

                DatabaseReference DriversAvailabilityRef= FirebaseDatabase.getInstance().getReference().child("Drivers available");
                GeoFire geoFire= new GeoFire(DriversAvailabilityRef);

                geoFire.setLocation(userId,new GeoLocation(location.getLatitude(),location.getLongitude()));

//                DatabaseReference DriversWorkingRef= FirebaseDatabase.getInstance().getReference().child("Drivers Working");
//                GeoFire geoFire1= new GeoFire(DriversWorkingRef);

//                switch (customerId)
//                {
//                    case "":
//                        geoFire1.removeLocation(userId);
//                        geoFire.setLocation(userId,new GeoLocation(location.getLatitude(),location.getLongitude()));
//                        break;
//                    default :
//                        geoFire.removeLocation(userId);
//                        geoFire1.setLocation(userId,new GeoLocation(location.getLatitude(),location.getLongitude()));
//                        break;
//
//                }
            }

        }
    }

}
