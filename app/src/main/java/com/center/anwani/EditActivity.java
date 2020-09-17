package com.center.anwani;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EditActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final String COLLECTION_KEY = "address";
    public static final String name1 = "nameKey";
    public static final String supervisor1 = "supervisorKey";
    public static final String phoneNumber1 = "phoneKey";
    public static final String Email1 = "emailKey";
    public static final String myid1 = "myidKey";

    private static final String TAG = "CreateActivity";

    private final String NUMBER = "number";

    public static final String starttime1 = "starttimeKey";
    public static final String endtime1 = "endtimeKey";
    public static final String regionstatus1 = "regionstatusKey";

    public static final String clat1 = "clatKey";
    public static final String clong1 = "clongKey";
    public static final String radius1 = "radiusKey";

    String startTime, endTime, regionStatus, Reason, cLat, cLong, Radius, latCheck, longCheck;
    int locCount;
    SharedPreferences.Editor editor;

    private FirebaseAnalytics mFirebaseAnalytics;

    Button leftBtn, rightBtn, updateLocationBtn, cancelUpdateBtn;
    TextView countyTxt, regionTxt, roadTxt, addressTxt, coordinatesTxt, roadListTxt, counterTxt, changeTypeTxt, addNameBtn,
            addressLocationTxt;
    MapView myMap;
    int mCounterL, mCounterR, addressNumber, changeCounter, previousCounterL, previousCounterR, red, yel, green, beg;
    String fullAddress, Latitude, Longitude, absoluteLat, addressId;
    EditText enterNumberTxt, addressNameTxt;

    Context context;
    String Holder;
    Criteria criteria;
    FirebaseFirestore db;
    private ArrayList<Address> listAddress;
    Address miss;
    ListView addressList, addressListR;
    int counter;
    View counterIndicator;
    List<Integer> counterList;
    RadioGroup propertyGroup, institutionGroup;
    ImageView homeImg;
    TextView propertyTypeTxt;

    private Location mylocation;
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    LocationRequest locationRequest;
    ConstraintLayout createCons, addressCapatureSaveCons, locationCompleteCons, sideSelectCons, capturingLocationCons;
    Animation animation;
    Animation animSequence;
    private AnimatorSet mSetRightOut, mSetLeftIn, xRotate;
    ImageView gpsSearchImg, gpsOffimg, locationCapturedImg, listImg, backImg;

    Double lat1, lat2, lon1, lon2, reLat1, reLat2, reLong1, reLong2;
    List<String> listDistance;
    ArrayList<String> listAllDistance, listReDistance, list,listR;
    List<String> list1;
    List<String> list2;
    List<String> list3;
    ArrayList<String> list4;
    ArrayList<Double> lisLat, resultLat, latitudeList;
    ArrayList<Double> lisLon, resultLong, longitudeList;
    ArrayAdapter<String> leftList;
    Double diffMax;
    int mCounter, indexL;

    String Phone, Email, Name, Supervisor, myid;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        db = FirebaseFirestore.getInstance();

        sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        Email = sharedpreferences.getString(Email1,"");
        Phone = sharedpreferences.getString(phoneNumber1,"");
        Name = sharedpreferences.getString(name1,"");
        Supervisor = sharedpreferences.getString(supervisor1,"");
        myid = sharedpreferences.getString(myid1,"");

        endTime = sharedpreferences.getString(endtime1,"");
        startTime = sharedpreferences.getString(starttime1,"");
        regionStatus = sharedpreferences.getString(regionstatus1,"");
        cLat = sharedpreferences.getString(clat1,"");
        cLong = sharedpreferences.getString(clong1,"");
        Radius = sharedpreferences.getString(radius1,"");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(EditActivity.this);
        InitialCheck();

        listAddress = new ArrayList<Address>();
        counterList = new ArrayList<>();

        listDistance = new ArrayList<String>();
        listAllDistance = new ArrayList<String>();
        listReDistance = new ArrayList<String>();
        lisLat =new ArrayList<Double>();
        lisLon =new ArrayList<Double>();
        resultLat =new ArrayList<Double>();
        resultLong =new ArrayList<Double>();
        latitudeList =new ArrayList<Double>();
        longitudeList =new ArrayList<Double>();

        list1 = new ArrayList<>();
        list2 =new ArrayList<>();
        list3 =new ArrayList<>();
        list4 =new ArrayList<>();

        list = new ArrayList<String>();
        listR = new ArrayList<String>();

        listAddress = new ArrayList<Address>();

        mCounterL=0;
        mCounterR=0;

        createCons=(ConstraintLayout) findViewById(R.id.cons_create_address);
        addressCapatureSaveCons=(ConstraintLayout) findViewById(R.id.cons_address_completion);
        locationCompleteCons=(ConstraintLayout) findViewById(R.id.cons_locating_complete);
        sideSelectCons=(ConstraintLayout) findViewById(R.id.cons_sides_selection);
        capturingLocationCons=(ConstraintLayout) findViewById(R.id.cons_capturing_location);
        addressNameTxt=(EditText) findViewById(R.id.edt_enter_address_name);

        createCons.setVisibility(View.VISIBLE);
        sideSelectCons.setVisibility(View.VISIBLE);
        capturingLocationCons.setVisibility(View.GONE);
        addressCapatureSaveCons.setVisibility(View.GONE);

        propertyGroup=(RadioGroup) findViewById(R.id.group_property_type_create);
        institutionGroup=(RadioGroup) findViewById(R.id.group_institution_create);
        propertyTypeTxt=(TextView) findViewById(R.id.txt_address_type_create);
        changeTypeTxt=(TextView) findViewById(R.id.txt_change_property_type_create);

        countyTxt=(TextView) findViewById(R.id.txt_county_create);
        regionTxt=(TextView) findViewById(R.id.txt_region_create);
        roadTxt=(TextView) findViewById(R.id.txt_road_create);
        addressTxt=(TextView) findViewById(R.id.txt_address_create);
        coordinatesTxt=(TextView) findViewById(R.id.txt_captured_location);
        roadListTxt=(TextView) findViewById(R.id.txt_road_create_list);
        counterTxt=(TextView) findViewById(R.id.txt_counter_create);
        backImg=(ImageView) findViewById(R.id.img_back);

        gpsSearchImg = (ImageView) findViewById(R.id.img_location_searching);
        locationCapturedImg = (ImageView) findViewById(R.id.img_location_captured);
        updateLocationBtn=(Button) findViewById(R.id.btn_update_location);
        cancelUpdateBtn=(Button) findViewById(R.id.btn_cancel_update_location);
        addressLocationTxt=(TextView) findViewById(R.id.txt_address_location);

        animSequence = AnimationUtils.loadAnimation(EditActivity.this,R.anim.rotate );
        gpsSearchImg.startAnimation(animSequence);

        mSetRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(EditActivity.this, R.animator.out_animation);
        mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(EditActivity.this, R.animator.in_animation);
        xRotate = (AnimatorSet) AnimatorInflater.loadAnimator(EditActivity.this, R.animator.horizontal_rotate);

        xRotate.setTarget(listImg);
        xRotate.start();

        context = getApplicationContext();
        setUpGClient();


        Bundle loca=getIntent().getExtras();
        if (loca != null) {
            addressLocationTxt.setText(loca.getCharSequence("address"));
            fullAddress = String.valueOf(loca.getCharSequence("address"));
        }

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditActivity.this, HomeActivity.class));
            }
        });

        changeTypeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                propertyGroup.setVisibility(View.VISIBLE);
                createCons.setVisibility(View.GONE);
            }
        });

        propertyGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.radio_residential_create:
                        propertyTypeTxt.setText("Residential Address");
                        institutionGroup.setVisibility(View.GONE);
                        institutionGroup.clearCheck();
                        propertyGroup.setVisibility(View.GONE);
                        createCons.setVisibility(View.VISIBLE);
                        break;

                    case R.id.radio_institution_create:
                        if(institutionGroup.getVisibility()==View.GONE){
                            institutionGroup.setVisibility(View.VISIBLE);
                        }else{
                            institutionGroup.setVisibility(View.GONE);
                        }
                        break;

                    case R.id.radio_government_create:
                        propertyTypeTxt.setText("Government Department");
                        institutionGroup.setVisibility(View.GONE);
                        institutionGroup.clearCheck();
                        propertyGroup.setVisibility(View.GONE);
                        createCons.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        institutionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {

                    case R.id.radio_pre_school_create:
                        propertyTypeTxt.setText("Preschool");
                        propertyGroup.setVisibility(View.GONE);
                        createCons.setVisibility(View.VISIBLE);
                        break;

                    case R.id.radio_primary_create:
                        propertyTypeTxt.setText("Primary School");
                        propertyGroup.setVisibility(View.GONE);
                        createCons.setVisibility(View.VISIBLE);
                        break;

                    case R.id.radio_secondary_create:
                        propertyTypeTxt.setText("Secondary School");
                        propertyGroup.setVisibility(View.GONE);
                        createCons.setVisibility(View.VISIBLE);
                        break;

                    case R.id.radio_college_institution:
                        propertyTypeTxt.setText("College or University");
                        propertyGroup.setVisibility(View.GONE);
                        createCons.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        sideSelectCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleApiClient.connect();
                getMyLocation();

                capturingLocationCons.setVisibility(View.VISIBLE);
                sideSelectCons.setVisibility(View.GONE);
                addressCapatureSaveCons.setVisibility(View.GONE);

            }
        });

        updateLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CaptureAddress();
            }
        });

        cancelUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sideSelectCons.setVisibility(View.VISIBLE);
                capturingLocationCons.setVisibility(View.GONE);
                addressCapatureSaveCons.setVisibility(View.GONE);
            }
        });

    }

    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        getMyLocation();

    }

    private void CaptureAddress(){
        db.collection(COLLECTION_KEY).whereEqualTo("fulladdress", addressLocationTxt.getText().toString())
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
        coordinates.put("latitude", absoluteLat);
        coordinates.put("longitude", Longitude);

        db.collection(COLLECTION_KEY).document(addressId)
                .set(coordinates, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Bundle params = new Bundle();
                        params.putString("user_id", myid);
                        mFirebaseAnalytics.logEvent("address_location_update", params);

                        Toast.makeText(EditActivity.this,"Address Name Updated Successfully",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditActivity.this,"Address Update Failed",Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onLocationChanged(Location location) {

        mylocation = location;
        if (mylocation != null) {
            Double latitude = mylocation.getLatitude();

            coordinatesTxt.setText(Double.toString(latitude));

            coordinatesTxt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    mCounter ++;

                    lisLat.add((mylocation.getLatitude() * -1));
                    lisLon.add(mylocation.getLongitude());

                    latitudeList.add(mylocation.getLatitude());
                    longitudeList.add(mylocation.getLongitude());

                    for (int a = 1; a < lisLat.size(); a++) {

                        lat1 = lisLat.get(a);
                        lon1 = lisLon.get(a);
                        lat2 = lisLat.get(a - 1);
                        lon2 = lisLon.get(a - 1);

                        Location loc1 = new Location("");
                        loc1.setLatitude(lat1);
                        loc1.setLongitude(lon1);

                        Location loc2 = new Location("");
                        loc2.setLatitude(lat2);
                        loc2.setLongitude(lon2);

                        float distanceInMeters = loc1.distanceTo(loc2);

                        listAllDistance.add(String.valueOf(distanceInMeters));

                        if(listAllDistance.size()>22){
                            listDistance=listAllDistance.subList(2,20);

                            Set<String> set = new HashSet<>(listDistance);
                            listDistance.clear();
                            listDistance.addAll(set);
                            listDistance.remove("0.0");
                            Collections.sort(listDistance);

                            if(listDistance.size()>1){
                                diffMax= Double.valueOf(listDistance.get(listDistance.size()-1));

                                if(diffMax>=0.7){
                                    listAllDistance.clear();

                                }else if(diffMax<0.7){
                                    listAllDistance.clear();
                                    googleApiClient.disconnect();
                                    Latitude= String.valueOf(latitudeList.get(latitudeList.size()-1));
                                    Longitude= String.valueOf(longitudeList.get(longitudeList.size()-1));
                                    absoluteLat = String.valueOf(latitudeList.get(latitudeList.size()-1)+ 100);

                                    capturingLocationCons.setVisibility(View.GONE);
                                    sideSelectCons.setVisibility(View.GONE);
                                    addressCapatureSaveCons.setVisibility(View.VISIBLE);
                                    LocationCheck();

                                }else{

                                }
                            }
                        }
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkPermissions();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getMyLocation();
                        break;
                    case Activity.RESULT_CANCELED:

                        break;
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(EditActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }
    }


    private void getMyLocation(){
        if(googleApiClient!=null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(EditActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    locationRequest = new LocationRequest();
                    locationRequest.setInterval(20);
                    locationRequest.setFastestInterval(20);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(EditActivity.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(EditActivity.this,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }

    private void checkPermissions(){
        int permissionLocation = ContextCompat.checkSelfPermission(EditActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }else{
            getMyLocation();
        }
    }

    private void InitialCheck(){
        Date currentTime = Calendar.getInstance().getTime();
        DateFormat sdf = new SimpleDateFormat("HH:mm");
        String ct=sdf.format(currentTime);
        try {
            Date sttime = sdf.parse(startTime);
            Date entime = sdf.parse(endTime);
            Date curTime=sdf.parse(ct);

            if(curTime.after(sttime)
                    && curTime.before(entime)) {
                if(regionStatus.contentEquals("Ongoing")){


                }else{
                    Reason=("Platform offline in your region");
                    sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                    editor = sharedpreferences.edit();
                    editor.clear();
                    editor.commit();
                    Intent i = new Intent(EditActivity.this, MainActivity.class);
                    Bundle location = new Bundle();
                    location.putString("reason", Reason);
                    i.putExtras(location);
                    startActivity(i);
                }
            }else {
                Reason=("Platform offline at this time");
                sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();
                Intent i = new Intent(EditActivity.this, MainActivity.class);
                Bundle location = new Bundle();
                location.putString("reason", Reason);
                i.putExtras(location);
                startActivity(i);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void LocationCheck(){
        Location center = new Location("");
        center.setLatitude(Double.parseDouble(cLat));
        center.setLongitude(Double.parseDouble(cLong));

        Location loc2 = new Location("");
        loc2.setLatitude(Double.parseDouble(Latitude));
        loc2.setLongitude(Double.parseDouble(Longitude));

        float distanceInMeters = center.distanceTo(loc2);
        Double theRadius=Double.parseDouble(Radius);

        if(distanceInMeters<theRadius) {

        }else {
            Reason=("Not within assigned region");
            sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            editor = sharedpreferences.edit();
            editor.clear();
            editor.commit();
            Intent i = new Intent(EditActivity.this, MainActivity.class);
            Bundle location = new Bundle();
            location.putString("reason", Reason);
            i.putExtras(location);
            startActivity(i);
        }

    }

}

