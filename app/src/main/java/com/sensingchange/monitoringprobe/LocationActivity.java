package com.sensingchange.monitoringprobe;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sensingchange.monitoringprobe.model.AirInformation;
import com.sensingchange.monitoringprobe.remote.InformationService;
import com.sensingchange.monitoringprobe.remote.RetrofitClient;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    AirInformation geolocation_data;
    TextView info;
    TextView txt_timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        info = findViewById(R.id.info);
        txt_timestamp = findViewById(R.id.txt_timestamp);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void back(View v){
        Intent intent = new Intent(LocationActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap map) {
//        if (ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    MY_PERMISSIONS_REQUEST_LOCATION);
//        }

        try{
            if (ContextCompat.checkSelfPermission(this, // request permission when it is not granted.
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d("Location", "permission:ACCESS_FINE_LOCATION: NOT granted!");

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // Resquest permisssion to user and the thread wait for the user's response!

                } else {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);

                    // MY_PERMISSIONS_REQUEST_LOCATION is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Get user current location
        LocationManager lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        Location location = getLastKnownLocation(lm);

        double user_latitude = location.getLatitude();
        double user_longitude = location.getLongitude();

        Marker map_marker = map.addMarker(new MarkerOptions().position(new LatLng(user_latitude, user_longitude)).title("Probe"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(user_latitude, user_longitude), 13));

//        Get probe location
        getGeolocationData(map_marker, map);

    }

    private Location getLastKnownLocation(LocationManager mLocationManager) {
        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;

        try{
            if (ContextCompat.checkSelfPermission(this, // request permission when it is not granted.
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d("Location", "permission:ACCESS_FINE_LOCATION: NOT granted!");

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // Resquest permisssion to user and the thread wait for the user's response!

                } else {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);

                    // MY_PERMISSIONS_REQUEST_LOCATION is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    private void getGeolocationData(final Marker marker, final GoogleMap map){
        InformationService service = RetrofitClient.getClient().create(InformationService.class);

        /*Call the method with parameter in the interface to get the user login*/
        Call<AirInformation> call = service.auth("application/json");

        call.enqueue(new Callback<AirInformation>() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onResponse(Call<AirInformation> call, Response<AirInformation> response) {
                Integer status = response.code();
                if (status.equals(200)) {
                    if (ActivityCompat.checkSelfPermission(LocationActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(LocationActivity.this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(LocationActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_LOCATION);
                    }

                    geolocation_data = response.body();

                    double probe_latitude =  geolocation_data.getGeolocation().getLatitude();
                    double probe_longitude = geolocation_data.getGeolocation().getLongitude();
                    String timestamp = geolocation_data.getTimestamp();

                    if (probe_latitude != 0.0 && probe_longitude != 0.0) {
                        marker.setPosition(new LatLng(probe_latitude, probe_longitude));
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(probe_latitude, probe_longitude), 13));
                        map.setMyLocationEnabled(true);
                        txt_timestamp.setText(timestamp);
                    }
                    else {
                        info.setText(R.string.invalid_geolocation);
                    }

                } else {
                    Toast.makeText(LocationActivity.this, "Server error. Please go back end try again.", Toast.LENGTH_SHORT).show();
                }
            }

            public void onFailure(Call<AirInformation> call, Throwable t) {
                Context context = LocationActivity.this;
//                Developer version
//
//                StringBuilder stringBuilder = new StringBuilder();
//                stringBuilder.append("Error: ");
//                stringBuilder.append(t.toString());
//                Toast.makeText(context, stringBuilder.toString(), Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "Server error. Please go back end try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}