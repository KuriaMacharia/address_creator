package com.center.anwani;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.center.anwani.Helper.CheckNetWorkStatus;
import com.center.anwani.Helper.HttpJsonParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private final String COLLECTION_KEY = "address";

    public static final String name1 = "nameKey";
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String supervisor1 = "supervisorKey";
    public static final String phoneNumber1 = "phoneKey";
    public static final String Email1 = "emailKey";
    public static final String myid1 = "myidKey";
    public static final String county1 = "countyKey";
    public static final String region1 = "regionKey";
    public static final String role1 = "roleKey";
    public static final String status1 = "statusKey";

    private static final String KEY_SUCCESS = "success";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";

    private static final String KEY_ENTRY_ROAD = "road";
    private static final String KEY_REGION = "region";
    private static final String KEY_COUNTY = "county";

    private static final String BASE_URL = "http://www.anwani.net/seya/";

    private GoogleMap mMap;
    Button cancelBtn, updateBtn;
    TextView coordinatesTxt, addressTxt;
    String upLat, upLong, fullAddress, Latitude, Longitude, absoluteLatitude, addressId, Type, regionId, roadId;
    FirebaseFirestore db;
    Address miss;
    SharedPreferences sharedpreferences;
    String Phone, Email, Name, Supervisor, myid, myCounty, myRegion, myRole, myStatus, countyId, theRoad, theCounty, theRegion;
    ImageView terrainImg, flatImg, satelliteImg;
    int success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        db = FirebaseFirestore.getInstance();

        sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        Email = sharedpreferences.getString(Email1,"");
        Phone = sharedpreferences.getString(phoneNumber1,"");
        Name = sharedpreferences.getString(name1,"");
        myid = sharedpreferences.getString(myid1,"");
        Supervisor = sharedpreferences.getString(supervisor1,"");
        myCounty = sharedpreferences.getString(county1,"");
        myRegion = sharedpreferences.getString(region1,"");
        myRole = sharedpreferences.getString(role1,"");
        myStatus = sharedpreferences.getString(status1,"");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        cancelBtn=(Button) findViewById(R.id.btn_cancel_edit_map);
        updateBtn=(Button) findViewById(R.id.btn_update_edit_map);
        coordinatesTxt=(TextView) findViewById(R.id.txt_coordiantes_edit_map);
        addressTxt=(TextView) findViewById(R.id.txt_address_edit_map);
        terrainImg = (ImageView) findViewById(R.id.img_terrain_map);
        flatImg =(ImageView) findViewById(R.id.img_flat_map);
        satelliteImg = (ImageView) findViewById(R.id.img_sattelite_map);

        Bundle loca=getIntent().getExtras();
        if (loca != null) {
            fullAddress = String.valueOf(loca.getCharSequence("address"));
            Latitude = String.valueOf(loca.getCharSequence("latitude"));
            Longitude = String.valueOf(loca.getCharSequence("longitude"));
            countyId=String.valueOf(loca.getCharSequence("countyid"));
            regionId=String.valueOf(loca.getCharSequence("regionid"));
            Type=String.valueOf(loca.getCharSequence("type"));
        }
        addressTxt.setText(fullAddress);
        coordinatesTxt.setText(Latitude + ":   " + Longitude);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent i = new Intent(MapsActivity.this, ViewActivity.class);
                Bundle location = new Bundle();
                location.putString("county", myCounty);
                location.putString("region", myRegion);
                location.putString("countyid", countyId);

                i.putExtras(location);
                startActivity(i);*/
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Type.contentEquals("Street")){
                    CaptureStreet();
                    new UpdateSqlStreet().execute();
                }else{
                    CaptureAddress();
                    new UpdateSqlAddress().execute();
                }

            }
        });

        terrainImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        });

        flatImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });

        satelliteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(Double.parseDouble(Latitude), Double.parseDouble(Longitude));
        mMap.addMarker(new MarkerOptions().position(sydney).title(fullAddress).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                Log.e("PlaceLL", String.valueOf(marker.getPosition().latitude) + ":  " +
                        String.valueOf(marker.getPosition().longitude));

                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude)));
            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Log.e("PlaceLL", String.valueOf(marker.getPosition().latitude) + ":  " +
                        String.valueOf(marker.getPosition().longitude));

                upLat= String.format("%.8f", marker.getPosition().latitude);
                upLong= String.format("%.8f", marker.getPosition().longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude)));
                coordinatesTxt.setText(upLat + ":   " + upLong);
            }
        });
    }

    private void CaptureAddress(){
        db.collection(COLLECTION_KEY).whereEqualTo("fulladdress", fullAddress)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        Address miss = document.toObject(Address.class);
                        addressId=document.getId();
                        UpdateLocation();
                    }

                } else {

                }
            }
        });
    }

    private void UpdateLocation(){
        Map<String, Object> coordinates = new HashMap<>();
        coordinates.put("latitude", String.valueOf(Double.parseDouble(upLat) + 100));
        coordinates.put("longitude", upLong);

        db.collection(COLLECTION_KEY).document(addressId)
                .set(coordinates, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Intent i = new Intent(MapsActivity.this, ViewActivity.class);
                        Bundle location = new Bundle();
                        location.putString("county", myCounty);
                        location.putString("region", myRegion);
                        location.putString("countyid", countyId);

                        i.putExtras(location);
                        startActivity(i);

                        Toast.makeText(MapsActivity.this,"Address Name Updated Successfully",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MapsActivity.this,"Address Update Failed",Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void CaptureStreet() {
        db.collection("counties").document(countyId).collection("regions")
                .document(regionId).collection("roads").whereEqualTo("road", fullAddress)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        roadId=document.getId();
                        UpdateStreet();
                    }
                } else {
                    Toast.makeText(MapsActivity.this, "Error adding document", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void UpdateStreet() {
        Map<String, Object> coordinates = new HashMap<>();
        coordinates.put("entrylatitude", String.valueOf(Double.parseDouble(upLat) + 100));
        coordinates.put("entrylongitude", upLong);

        db.collection("counties").document(countyId).collection("regions")
                .document(regionId).collection("roads").document(roadId)
                .set(coordinates, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Intent i = new Intent(MapsActivity.this, ViewActivity.class);
                        Bundle location = new Bundle();
                        location.putString("county", myCounty);
                        location.putString("region", myRegion);
                        location.putString("countyid", countyId);

                        i.putExtras(location);
                        startActivity(i);

                        Toast.makeText(MapsActivity.this,"Address Name Updated Successfully",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MapsActivity.this,"Address Update Failed",Toast.LENGTH_LONG).show();
                    }
                });
    }

    private class UpdateSqlAddress extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();
            httpParams.put(KEY_ADDRESS, fullAddress);
            httpParams.put(KEY_LATITUDE, upLat);
            httpParams.put(KEY_LONGITUDE, upLong);

            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL + "update_address.php", "POST", httpParams);
            if(success==1)
                try {
                    success = jsonObject.getInt(KEY_SUCCESS);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            return null;
        }

        protected void onPostExecute(String result) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (success == 0) {

                    } else {

                    }
                }
            });
        }
    }

    private class UpdateSqlStreet extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();

            httpParams.put(KEY_ENTRY_ROAD, fullAddress);
            httpParams.put(KEY_COUNTY, myCounty);
            httpParams.put(KEY_REGION, myRegion);
            httpParams.put(KEY_LATITUDE, upLat);
            httpParams.put(KEY_LONGITUDE, upLong);

            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL + "update_street.php", "POST", httpParams);
            if(success==1)
                try {
                    success = jsonObject.getInt(KEY_SUCCESS);

                }catch (JSONException e) {
                    e.printStackTrace();
                }
            return null;
        }

        protected void onPostExecute(String result) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (success == 0) {

                    } else {

                    }
                }
            });
        }
    }
}
