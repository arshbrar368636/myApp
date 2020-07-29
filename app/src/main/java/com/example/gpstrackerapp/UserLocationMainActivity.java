package com.example.gpstrackerapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Looper;
import android.preference.PreferenceManager;
import android.telephony.CellLocation;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserLocationMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private AppBarConfiguration mAppBarConfiguration;
    FusedLocationProviderClient mFusedLocationClient;
    GoogleMap mMap;
    FirebaseAuth auth;
    GoogleApiClient client;
    LocationRequest request;
    LatLng latLng;
    DatabaseReference databaseReference;
    DatabaseReference reference;
    FirebaseUser user;
    DatabaseReference driversDatabaseReference;
    DatabaseReference DriversReference;
    DatabaseReference DriversLocationReference;
    String current_user_name, current_user_email;
    String current_user_imageUrl;
    Button CustomerCallButton;
    TextView t1_current_name, t2_current_email;
    ImageView i1;
    SearchView searchView, mSearchView;
    String customerId;
    int radius = 5;
    Boolean driverFound = false, requestType = false;
    String driverFoundId;
    Marker DriverMarker;
    Button SettingsBtn;

    Button availableDriversBtn;

    private ValueEventListener DriversLocationRefListener;

    TextView txtname, txtPhone, txtCarName, driverOrigin, driverDestination, availableDriverName;
    CircleImageView profilePicture;
    RelativeLayout relativeLayout;

    Location mLastLocation;

    Button b1;
    ArrayList<Double> modelListLong = new ArrayList<>();
    ArrayList<Double> modelListLat = new ArrayList<>();

    ArrayList<String> array = new ArrayList<>();
    ListView mListView;

    DatabaseReference mUserDatabase;

    RecyclerView mResultList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location_main);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers");


//        mSearchField = (EditText) findViewById(R.id.search_field);
//        mSearchBtn = (ImageButton) findViewById(R.id.search_btn);

        mResultList = (RecyclerView) findViewById(R.id.result_list);
       mResultList.setHasFixedSize(true);
       mResultList.setLayoutManager(new LinearLayoutManager(this));

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // mListView = findViewById(R.id.listView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

//        driverOrigin=findViewById(R.id.available_origin_driver);
//        driverDestination=findViewById(R.id.available_destination_driver);

        b1 = (Button) findViewById(R.id.search_button);
       // availableDriversBtn = findViewById(R.id.findDriversBtn);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        availableDriversBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "9999999", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getApplicationContext(), AvailableDrivers.class);
//                startActivity(intent);
//                //getAvailableDriversInformation();
//            }
//        });

        searchView = findViewById(R.id.SearchBar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                String location = searchView.getQuery().toString();
                //firebaseUserSearch(location);
                List<Address> addressList = null;
                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(UserLocationMainActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                }
                return false;
            }


            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


        View header = navigationView.getHeaderView(0);
        t1_current_name = header.findViewById(R.id.title_text);
        t2_current_email = header.findViewById(R.id.email_text);
        i1 = header.findViewById(R.id.imageView1);

        CustomerCallButton = (Button) findViewById(R.id.call_cab_btn);

        txtname = (TextView) findViewById(R.id.driver_name_show);
        txtPhone = (TextView) findViewById(R.id.driver_phone_show);
        txtCarName = (TextView) findViewById(R.id.driver_carName_show);
        relativeLayout = (RelativeLayout) findViewById(R.id.rel1);
        profilePicture = (CircleImageView) findViewById(R.id.profile_image_drivers_show);


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                current_user_name = dataSnapshot.child(user.getUid()).child("name").getValue(String.class);
                current_user_email = dataSnapshot.child(user.getUid()).child("email").getValue(String.class);
                current_user_imageUrl = dataSnapshot.child(user.getUid()).child("imageUrl").getValue(String.class);
                t1_current_name.setText(current_user_name);
                t2_current_email.setText(current_user_email);


                Picasso.get().load(current_user_imageUrl).into(i1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send,R.id.nav_joinCircle)
                .setDrawerLayout(drawer)
                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        client.connect();


//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Location");
//
//     ValueEventListener listener= databaseReference.addValueEventListener(new ValueEventListener() {
//         @Override
//         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//             Double latitude= dataSnapshot.child("latitude").getValue(Double.class);
//             Double longitude= dataSnapshot.child("longitude").getValue(Double.class);
//
//
//             LatLng location= new LatLng(latitude,longitude);
//
//             mMap.addMarker(new MarkerOptions().position(location).title("Driver"));
//             mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,14f));
//
//         }
//
//         @Override
//         public void onCancelled(@NonNull DatabaseError databaseError) {
//
//         }
//     });
        mFusedLocationClient.requestLocationUpdates(request, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);


    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    mLastLocation = location;

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    //mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                    // if(!getDriversAroundStarted)
                    getDriversAround();
                }
            }
        }
    };

    public void onMapSearch(View view) {
        EditText locationSearch = (EditText) findViewById(R.id.editText);
        String location1 = locationSearch.getText().toString();
        Toast.makeText(getApplicationContext(),location1,Toast.LENGTH_SHORT).show();
        firebaseUserSearch(location1);

        List<Address> addressList = null;


        if (location1 != null && !location1.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location1, 100);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title(location1));
            modelListLat.add(address.getLatitude());
            modelListLong.add(address.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        }

    }

    private void firebaseUserSearch(String location1) {
        Toast.makeText(UserLocationMainActivity.this, "Started Search", Toast.LENGTH_LONG).show();

        Query firebaseSearchQuery = mUserDatabase.orderByChild("destination").startAt(location1).endAt(location1 + "\uf8ff");

        Toast.makeText(getApplicationContext(), "mmmmmmmm", Toast.LENGTH_SHORT).show();
       FirebaseRecyclerAdapter<Users,UsersViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                Users.class,
                R.layout.list_layout,
                UsersViewHolder.class,
                firebaseSearchQuery
       )

       {
           @Override
           protected void populateViewHolder(UsersViewHolder viewHolder, Users users, int i) {
               Toast.makeText(getApplicationContext(),"oooooooo",Toast.LENGTH_SHORT).show();
               viewHolder.setDetails(getApplicationContext(),users.getUserName(),users.getPhone());
           }
       };
        mResultList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }


    public void setDetails(Context ctx, String userName,String userPhone) {

        TextView user_name = (TextView) mView.findViewById(R.id.name_text);
//        TextView user_origin = (TextView) mView.findViewById(R.id.origin_text);
//        TextView user_destination = (TextView) mView.findViewById(R.id.destination_text);
        TextView user_phone=(TextView) mView.findViewById(R.id.origin_text);




        user_name.setText(userName);
        user_phone.setText(userPhone);
//        user_origin.setText(userOrigin);
//        user_destination.setText(userDestination);




    }

}
//   public void findDriver(View view)
//   {
//       getAvailableDriversInformation();
//   }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_location_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_joinCircle) {
            Intent intent = new Intent(UserLocationMainActivity.this, HistoryActivity.class);
            intent.putExtra("customerOrDriver","Drivers");
            startActivity(intent);

        } else if (id == R.id.MyCircle) {
            Intent intent = new Intent(UserLocationMainActivity.this, About.class);
            startActivity(intent);

        } else if (id == R.id.JoinedCircle) {




        } else if (id == R.id.signout) {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(UserLocationMainActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.ShareLocation) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT, "My location is : " + "https://www.google.com/maps/@" + latLng.latitude + "," + latLng.longitude + ",17z");
            startActivity(i.createChooser(i, "Share using: "));

        } else if (id == R.id.InviteMembers) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
    public void onLocationChanged(final Location location) {

        if (location == null) {
            Toast.makeText(getApplicationContext(), "Could not get location", Toast.LENGTH_SHORT).show();
        } else {
            mLastLocation = location;
            latLng = new LatLng(location.getLatitude(), location.getLongitude());


//            MarkerOptions options = new MarkerOptions();
//            options.position(latLng);
//            options.title("Current Location");
//            mMap.addMarker(options);

            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(latLng, 12);
            mMap.animateCamera(yourLocation);
            if (!getDriversAroundStarted)
                getDriversAround();





            Circle circle = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(location.getLatitude(), location.getLongitude()))
                    .radius(5000)
                    .strokeColor(Color.RED)
                    .fillColor(Color.BLUE));
            circle.setRadius(5000.0);


        }
        CustomerCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requestType) {

                } else {
                    requestType = true;

                    customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    reference = FirebaseDatabase.getInstance().getReference().child("Customers Request");

                    GeoFire geoFire = new GeoFire(reference);
                    geoFire.setLocation(customerId, new GeoLocation(location.getLatitude(), location.getLongitude()));

                    latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    mMap.addMarker(new MarkerOptions().position(latLng).title("My Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.location)));

                    CustomerCallButton.setText("Getting your Driver..");
                    getCloserDrivers();
                }


            }
        });


    }

    private void getCloserDrivers() {
        driversDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Drivers available");
        GeoFire geoFire = new GeoFire(driversDatabaseReference);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!driverFound) {
                    driverFound = true;
                    driverFoundId = key;

                    DriversReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundId);

                    HashMap driverMap = new HashMap();
                    driverMap.put("CustomerRideId", customerId);
                    DriversReference.updateChildren(driverMap);

                    GettingDriverLocation();
                    CustomerCallButton.setText("Looking for driver location...");
                }

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!driverFound) {
                    radius = radius + 1;
                    getCloserDrivers();
                }


            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });


    }

    private void GettingDriverLocation() {

        //DriversLocationReference= FirebaseDatabase.getInstance().getReference().child("Drivers Working");
//        if(!getDriversAroundStarted)
//            getDriversAround();

        driversDatabaseReference.child(driverFoundId).child("l")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            List<Object> driverLocationMap = (List<Object>) dataSnapshot.getValue();
                            double LocationLat = 0;
                            double LocationLng = 0;

                            CustomerCallButton.setText("Driver Found");

                            relativeLayout.setVisibility(View.VISIBLE);
                            getAssigneddriverInformation();

                            if (driverLocationMap.get(0) != null) {
                                LocationLat = Double.parseDouble(driverLocationMap.get(0).toString());
                            }
                            if (driverLocationMap.get(1) != null) {
                                LocationLng = Double.parseDouble(driverLocationMap.get(1).toString());
                            }

                            LatLng DriverLatLang = new LatLng(LocationLat, LocationLng);
                            if (DriverMarker != null) {
                                DriverMarker.remove();
                            }

                            Location location1 = new Location("");
                            location1.setLatitude(latLng.latitude);
                            location1.setLongitude(latLng.longitude);

                            Location location2 = new Location("");
                            location2.setLatitude(DriverLatLang.latitude);
                            location2.setLongitude(DriverLatLang.longitude);

                            float Distance = location1.distanceTo(location2);
                            CustomerCallButton.setText("Driver Found " + String.valueOf(Distance));


                            DriverMarker = mMap.addMarker(new MarkerOptions().position(DriverLatLang).title("Your Driver").icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    private void getAssigneddriverInformation() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child("Drivers").child(driverFoundId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    String username = dataSnapshot.child("username").getValue().toString();
                    String phone = dataSnapshot.child("phone").getValue().toString();
                    String car = dataSnapshot.child("car").getValue().toString();

                    txtname.setText(username);
                    txtPhone.setText(phone);
                    txtCarName.setText(car);


                    if (dataSnapshot.hasChild("image")) {
                        String image = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(profilePicture);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    boolean getDriversAroundStarted = false;
    List<Marker> markerList = new ArrayList();

    private void getDriversAround() {
        getDriversAroundStarted = true;
        DatabaseReference driversLocation = FirebaseDatabase.getInstance().getReference().child(("Drivers available"));
        GeoFire geoFire = new GeoFire(driversLocation);
        Toast.makeText(UserLocationMainActivity.this, "11111", Toast.LENGTH_SHORT).show();
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                for (Marker markerIt : markerList) {
                    if (markerIt.getTag().equals(key)) {
                        Toast.makeText(UserLocationMainActivity.this, "2222", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                LatLng driverLocation = new LatLng(location.latitude, location.longitude);
                Marker mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car)).title(key));
                mDriverMarker.setTag(key);
                Toast.makeText(UserLocationMainActivity.this, "3333", Toast.LENGTH_SHORT).show();

                markerList.add(mDriverMarker);
                Toast.makeText(UserLocationMainActivity.this, "44444", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onKeyExited(String key) {
                for (Marker markerIt : markerList) {
                    if (markerIt.getTag().equals(key))
                        markerIt.remove();
                    markerList.remove(markerIt);
                    return;
                }

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for (Marker markerIt : markerList) {
                    if (markerIt.getTag().equals(key)) {
                        markerIt.setPosition(new LatLng(location.latitude, location.longitude));
                    }
                }

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

//    public void getAvailableDriversInformation() {
//        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//
//        DatabaseReference usersdRef = rootRef.child("Users").child("Drivers");
//        final FirebaseUser Users = FirebaseAuth.getInstance().getCurrentUser();
//        ValueEventListener eventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//
//                    String origin = dataSnapshot.child(Users.getUid()).child("origin").getValue(String.class);
//                    String destination = dataSnapshot.child(Users.getUid()).child("destination").getValue(String.class);
//
//
//
//
//                    String username = ds.child("username").getValue(String.class);
//
//                    Log.d("TAG", username);
//
//                    array.add(username);
//
//                }
//                ArrayAdapter<String> adapter = new ArrayAdapter(UserLocationMainActivity.this, android.R.layout.simple_list_item_1, array);
//
//                mListView.setAdapter(adapter);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//        usersdRef.addListenerForSingleValueEvent(eventListener);
//
//
//    }
}
