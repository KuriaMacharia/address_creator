package com.center.anwani;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.center.anwani.Helper.CheckNetWorkStatus;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;

public class StreetSearchActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String name1 = "nameKey";
    public static final String supervisor1 = "supervisorKey";
    public static final String phoneNumber1 = "phoneKey";
    public static final String Email1 = "emailKey";
    public static final String myid1 = "myidKey";

    private final String COLLECTION_KEY = "address";

    public static final String starttime1 = "starttimeKey";
    public static final String endtime1 = "endtimeKey";
    public static final String regionstatus1 = "regionstatusKey";

    public static final String clat1 = "clatKey";
    public static final String clong1 = "clongKey";
    public static final String radius1 = "radiusKey";

    String startTime, endTime, regionStatus, Reason, cLat, cLong, Radius, latCheck, longCheck;
    int locCount;

    private FirebaseAnalytics mFirebaseAnalytics;

    private ArrayList<Street> listAddress, searchList;
    private AddressAdapter addressAdapter;

    ConstraintLayout findmeContraint, constraintCaptured;
    TextView coordinatestxt, resultsTxt, failureTxt;
    FirebaseFirestore db;
    ImageView searchImg;
    EditText searchEdt;

    private Location mylocation;
    ImageView homeImg;

    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    private static final int PERMISSION_REQUEST_CODE = 200;
    LocationRequest locationRequest;
    Resources.Theme myTheme;
    Context context;
    LocationManager locationManager;
    boolean GpsStatus = false;
    FirebaseApp app;

    Double lat1, lat2, lon1, lon2, reLat1, reLat2, reLong1, reLong2;
    List<String> listDistance;
    ArrayList<String> listAllDistance, listReDistance;
    List<String> list1;
    List<String> list2;
    List<String> list3;
    ArrayList<String> list4;
    ArrayList<Double> lisLat, resultLat, latitudeList;
    ArrayList<Double> lisLon, resultLong, longitudeList;
    ArrayAdapter<String> leftList;
    Double diffMax, latitude;
    int mCounter, indexL;
    String fullAddress, Latitude, Longitude, absoluteLat, reLatitude, reLongitude, searchLat, searchLong, addressId,
            nameAddress, countyId, regionId, streetId, County, Region, Street, entryRoad, exitRoad, verificationStatus,
            activeStatus;
    Double angleRadius, distanceRadius, maxLat, minLat, maxLong, minLong, curLat1, bsLong, curLat, curLong, endLat, endLong;
    List<Street> longFilterList, latFilterList;
    List<Street> commonList;
    ArrayList<Street> filteredList, newList;
    ArrayList<HashMap<String, String>> distanceListRe, distanceListReNe, duplicateList;
    private ProgressDialog pDialog, startDialog, FstartDialog;
    URL lin;

    SharedPreferences.Editor editor;
    SharedPreferences pref;
    int themeName;
    TextView entryRoadTxt, exitRoadTxt, verificationTxt, activeTxt;
    Button editStreetBtn, createAddressBtn, verifyBtn;
    String Phone, Email, Name, Supervisor, myid;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //themeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_street_search);

        db = FirebaseFirestore.getInstance();
        googleApiClient=null;

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

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(StreetSearchActivity.this);
        InitialCheck();

        startDialog = new ProgressDialog(StreetSearchActivity.this, R.style.mydialog);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        context = getApplicationContext();
        //setUpGClient();
        CheckGpsStatus();
        listAddress = new ArrayList<Street>();
        searchList=new ArrayList<Street>();
        commonList = new ArrayList<Street>();
        filteredList = new ArrayList<Street>();
        newList = new ArrayList<Street>();
        longFilterList = new ArrayList<>();
        latFilterList = new ArrayList<>();
        distanceListRe = new ArrayList<>();
        distanceListReNe=new ArrayList<>();
        duplicateList=new ArrayList<>();

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

        listAddress = new ArrayList<Street>();
        coordinatestxt=(TextView) findViewById(R.id.txt_coordinates_address);
        findmeContraint=(ConstraintLayout) findViewById(R.id.constraint_check_address);
        constraintCaptured=(ConstraintLayout) findViewById(R.id.constraint_address_captured);
        resultsTxt=(TextView) findViewById(R.id.txt_captured_address);
        failureTxt=(TextView) findViewById(R.id.txt_failed_to_capture);
        searchEdt=(EditText) findViewById(R.id.edt_search_address);
        searchImg=(ImageView) findViewById(R.id.img_search);

        entryRoadTxt=(TextView) findViewById(R.id.txt_entry_road_street_search);
        exitRoadTxt=(TextView) findViewById(R.id.txt_exit_road_street_search);
        createAddressBtn=(Button) findViewById(R.id.btn_create_address_street_search);
        verificationTxt=(TextView) findViewById(R.id.txt_street_verification_status);
        activeTxt=(TextView) findViewById(R.id.txt_street_active_status);
        verifyBtn=(Button) findViewById(R.id.btn_verify_street);

        Bundle loca=getIntent().getExtras();
        if (loca != null) {

            County=String.valueOf(loca.getCharSequence("county"));
            Region=String.valueOf(loca.getCharSequence("region"));
            countyId=String.valueOf(loca.getCharSequence("countyid"));

            getRegionId();
        }

        findmeContraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (googleApiClient==null) {
                    setUpGClient();
                }

                if(distanceListRe.size()>0){
                    distanceListRe.clear();
                }

                CheckGpsStatus();
                if (GpsStatus) {

                    if (CheckNetWorkStatus.isNetworkAvailable(getApplicationContext())) {
                        googleApiClient.connect();
                        StartScan();
                        pDialog = new ProgressDialog(StreetSearchActivity.this, R.style.mydialog);
                        pDialog.setMessage("Initiating Scan. Please wait...");
                        pDialog.setIndeterminate(false);
                        pDialog.setCancelable(false);
                        pDialog.show();
                        failureTxt.setVisibility(View.GONE);
                        constraintCaptured.setVisibility(View.GONE);

                        if (commonList.size()>0){
                            commonList.clear();
                        }

                    } else {
                        Toast.makeText(StreetSearchActivity.this,"Unable to connect to internet",Toast.LENGTH_LONG).show();
                    }


                } else {
                    Toast.makeText(StreetSearchActivity.this, "Please Enable GPS First", Toast.LENGTH_LONG).show();
                }

            }
        });

        createAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(StreetSearchActivity.this, CreateActivity.class);
                Bundle location = new Bundle();
                location.putString("county", County);
                location.putString("region", Region);
                location.putString("road", resultsTxt.getText().toString());

                i.putExtras(location);
                startActivity(i);
            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("counties").document(countyId).collection("regions")
                        .document(regionId).collection("roads")
                        .whereGreaterThanOrEqualTo("entrylongitude", reLongitude)
                        .whereLessThanOrEqualTo("entrylongitude", reLatitude)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Street miss = document.toObject(Street.class);
                                streetId=document.getId();
                                UpdateStreet();
                            }

                        } else {
                            Toast.makeText(StreetSearchActivity.this, "Error fetching document", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    private synchronized void setUpGClient() {
        if (CheckNetWorkStatus.isNetworkAvailable(getApplicationContext())) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, 0, this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            getMyLocation();
        } else {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (CheckNetWorkStatus.isNetworkAvailable(getApplicationContext())) {
            if (googleApiClient!=null){
                if (googleApiClient.isConnected()) {
                    LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, (com.google.android.gms.location.LocationListener) this);
                    googleApiClient.disconnect();
                }
            } else {

            }

        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //You can display a message here
    }

    private void getMyLocation(){
        if(googleApiClient!=null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(StreetSearchActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation =                     LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
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
                                            .checkSelfPermission(StreetSearchActivity.this,
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
                                        status.startResolutionForResult(StreetSearchActivity.this,
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getMyLocation();
                        googleApiClient.connect();
                        break;
                    case Activity.RESULT_CANCELED:

                        //finish();
                        break;
                }
                break;
        }
    }

    private void checkPermissions(){
        int permissionLocation = ContextCompat.checkSelfPermission(StreetSearchActivity.this,
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean callAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && callAccepted){

                    }
                    //Toast.makeText(HomeActivity.this, "", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(HomeActivity.this, "Permission Denied, You cannot access location data and call.", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                            showMessageOKCancel("You need to allow access to both the permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{ACCESS_FINE_LOCATION, CALL_PHONE},
                                                        PERMISSION_REQUEST_CODE);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }
                break;
        }
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(StreetSearchActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void CheckGpsStatus() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onLocationChanged(final Location location) {
        mylocation = location;
        if (mylocation != null) {
            latitude = mylocation.getLatitude();
            coordinatestxt.setText(Double.toString(latitude));
            startDialog.dismiss();
        }
    }

    private void StartScan(){

        coordinatestxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //pDialog.setMessage("Capturing your location. Please keep the Phone still...");
                mCounter++;

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

                    if (listAllDistance.size() > 22) {
                        listDistance = listAllDistance.subList(2, 20);

                        Set<String> set = new HashSet<>(listDistance);
                        listDistance.clear();
                        listDistance.addAll(set);
                        listDistance.remove("0.0");
                        Collections.sort(listDistance);

                        if (listDistance.size() > 1) {
                            diffMax = Double.valueOf(listDistance.get(listDistance.size() - 1));

                            //if (diffMax >= 0.7) {listAllDistance.clear();}

                            if(diffMax>50){
                                listAllDistance.clear();
                                pDialog.setMessage("Capturing your location. Please keep the Phone still... (10%)");
                            }else if(diffMax>25 && diffMax <51){
                                listAllDistance.clear();
                                pDialog.setMessage("Capturing your location. Please keep the Phone still... (30%)");
                            }else if(diffMax>5 && diffMax <26){
                                listAllDistance.clear();
                                pDialog.setMessage("Capturing your location. Please keep the Phone still... (50%)");
                            }else if(diffMax>1 && diffMax <6){
                                listAllDistance.clear();
                                pDialog.setMessage("Capturing your location. Please keep the Phone still... (70%)");
                            }
                            else if (diffMax < 1.1) {

                                googleApiClient.disconnect();
                                Latitude = String.valueOf(latitudeList.get(latitudeList.size() - 2));
                                Longitude = String.valueOf(longitudeList.get(longitudeList.size() - 2));
                                CalculateDomain();
                                pDialog.setMessage("Location sucessfully captured. Fetching addresses...");
                                pDialog.setCancelable(true);
                                listAllDistance.clear();
                                locCount++;
                                if(locCount==1){
                                    LocationCheck();
                                }

                            } else {

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

    private void CalculateDomain(){
        absoluteLat= String.valueOf(Double.parseDouble(Latitude) + 100);

        curLat=Double.parseDouble(absoluteLat);
        curLat1=Double.parseDouble(Latitude);
        curLong=Double.parseDouble(Longitude);

        angleRadius= 0.003/ ( 111 * Math.cos(curLat1));

        minLat = curLat - angleRadius;
        maxLat = curLat + angleRadius;
        minLong = curLong - angleRadius;
        maxLong = curLong + angleRadius;

        ScanAddress();
    }

    private void ScanAddress() {

        db.collection("counties").document(countyId).collection("regions")
                .document(regionId).collection("roads")
                .whereGreaterThanOrEqualTo("entrylongitude", String.valueOf(minLong))
                .whereLessThanOrEqualTo("entrylongitude", String.valueOf(maxLong))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Street miss = document.toObject(Street.class);
                        longFilterList.add(miss);
                        if (longFilterList.size() > 0) {
                            pDialog.setMessage("Configuring addresses...");
                            for (int j = 0; j < longFilterList.size(); j++) {
                                if (Double.valueOf(longFilterList.get(j).getEntrylatitude()) > minLat
                                        && Double.valueOf(longFilterList.get(j).getEntrylatitude()) < maxLat) {

                                    commonList.add(longFilterList.get(j));

                                    int cc = 0;
                                    while (cc < longFilterList.size() - 1) {
                                        cc++;
                                        if (cc == longFilterList.size() - 1) {
                                            FilterList();
                                        }
                                    }
                                }
                            }
                            FilterList();
                        } else {
                            failureTxt.setVisibility(View.VISIBLE);
                            failureTxt.setText("Address not found. Try again and standing still on the outside of the gate.");
                            pDialog.dismiss();
                        }

                    }

                } else {
                    Toast.makeText(StreetSearchActivity.this, "Error fetching document", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void FilterList(){

        if(commonList.size()==1) {
            nameAddress=commonList.get(0).getRoad();
            resultsTxt.setText(commonList.get(0).getRoad());
            reLatitude=commonList.get(0).getEntrylatitude();
            reLongitude=commonList.get(0).getEntrylongitude();
            entryRoadTxt.setText(commonList.get(0).getFromroad());
            exitRoadTxt.setText(commonList.get(0).getExitroad());
            verificationTxt.setText(commonList.get(0).getVerificationstatus());
            activeTxt.setText(commonList.get(0).getActivestatus());

                    if(verificationTxt.getText().toString().contentEquals("Not Verified")||
                            commonList.get(0).getVerificationstatus().isEmpty()){
                            if(activeTxt.getText().toString().contentEquals("Active")||
                                    commonList.get(0).getActivestatus().isEmpty()){
                                            createAddressBtn.setVisibility(View.GONE);
                                            verifyBtn.setVisibility(View.VISIBLE);
                            }else {
                                createAddressBtn.setVisibility(View.GONE);
                                verifyBtn.setVisibility(View.GONE);
                            }

                    }else{
                        createAddressBtn.setVisibility(View.VISIBLE);
                        verifyBtn.setVisibility(View.GONE);
                    }

            constraintCaptured.setVisibility(View.VISIBLE);

            pDialog.dismiss();

        }else if(commonList.size()>1){

            for (int a = 0; a < commonList.size(); a++) {

                reLat1 = Double.valueOf(commonList.get(a).getEntrylatitude())-100;
                reLong1 = Double.valueOf(commonList.get(a).getEntrylongitude());

                Location loc1 = new Location("");
                loc1.setLatitude(curLat1);
                loc1.setLongitude(curLong);

                Location loc2 = new Location("");
                loc2.setLatitude(reLat1);
                loc2.setLongitude(reLong1);

                float distanceInMeters = loc1.distanceTo(loc2);
                listReDistance.add(String.valueOf(distanceInMeters));

                HashMap<String, String> map = new HashMap<>();
                map.put("fulladdress", commonList.get(a).getRoad());
                map.put("addressname", commonList.get(a).getRoad());
                map.put("entryroad", commonList.get(a).getFromroad());
                map.put("exitroad", commonList.get(a).getExitroad());
                map.put("verificationstatus", commonList.get(a).getVerificationstatus());
                map.put("activestatus", commonList.get(a).getActivestatus());

                map.put("latitude", String.valueOf(Double.valueOf(commonList.get(a).getEntrylatitude())-100));
                map.put("longitude", String.valueOf(Double.valueOf(commonList.get(a).getEntrylongitude())));
                map.put("distance", String.valueOf(listReDistance.get(a)));
                distanceListRe.add(map);
                SortList();

            }

        }else{
            //failureTxt.setVisibility(View.VISIBLE);
            //failureTxt.setText("Address not found. Try again and standing still on the outside of the gate.");
            pDialog.dismiss();
            Toast.makeText(StreetSearchActivity.this,"Address not found. Try again and standing still on the outside of the gate.",Toast.LENGTH_LONG).show();

        }
    }

    public void SortList(){

        class MapComparator implements Comparator<Map<String, String>> {
            private final String key;

            public MapComparator(String key){
                this.key = key;
            }

            public int compare(Map<String, String> first,
                               Map<String, String> second){
                // TODO: Null checking, both for maps and values
                String firstValue = first.get(key);
                String secondValue = second.get(key);
                return firstValue.compareTo(secondValue);
            }
        }
        Collections.sort(distanceListRe, new MapComparator("distance"));


        String reFulladdress = distanceListRe.get(0).get("fulladdress");
        String reAddressName = distanceListRe.get(0).get("addressname");
        entryRoad = distanceListRe.get(0).get("entryroad");
        exitRoad = distanceListRe.get(0).get("exitroad");
        reLatitude = distanceListRe.get(0).get("latitude");
        reLongitude = distanceListRe.get(0).get("longitude");
        verificationStatus = distanceListRe.get(0).get("verificationstatus");
        reLongitude = distanceListRe.get(0).get("activestatus");


        HashSet hs = new HashSet();
        hs.addAll(commonList); // demoArrayList= name of arrayList from which u want to remove duplicates
        commonList.clear();
        commonList.addAll(hs);

            if(verificationStatus.contentEquals("Not Verified")||
                    verificationStatus.isEmpty()){
                if(activeStatus.contentEquals("Active")||
                        activeStatus.isEmpty()){
                    createAddressBtn.setVisibility(View.GONE);
                    verifyBtn.setVisibility(View.VISIBLE);
                }else {
                    createAddressBtn.setVisibility(View.GONE);
                    verifyBtn.setVisibility(View.GONE);
                }

            }else{
                createAddressBtn.setVisibility(View.VISIBLE);
                verifyBtn.setVisibility(View.GONE);
            }

        resultsTxt.setText(reFulladdress);
        constraintCaptured.setVisibility(View.VISIBLE);
        entryRoadTxt.setText(entryRoad);
        exitRoadTxt.setText(exitRoad);


        pDialog.dismiss();
    }

    private void CaptureAddress(){
        db.collection(COLLECTION_KEY).whereEqualTo("fulladdress", resultsTxt.getText().toString())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        Address miss = document.toObject(Address.class);
                        addressId=document.getId();
                    }

                } else {
                    failureTxt.setVisibility(View.VISIBLE);
                    failureTxt.setText("Address not found!");
                    pDialog.dismiss();
                }
            }
        });
    }


    private void getRegionId() {
        db.collection("counties").document(countyId).collection("regions")
                .whereEqualTo("region", Region)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                regionId=document.getId();
                            }
                        }
                    }
                });
    }
    private void UpdateStreet(){
        FstartDialog = new ProgressDialog(StreetSearchActivity.this, R.style.mydialog);
        FstartDialog.setMessage("Verifying. Please wait...");
        FstartDialog.setIndeterminate(false);
        FstartDialog.setCancelable(false);
        FstartDialog.show();

        Date currentDate = new Date();
        Map<String, Object> addressName = new HashMap<>();
        addressName.put("verificationstatus", "Verified");
        addressName.put("verifiedby", myid);
        addressName.put("verificationdate", currentDate);


        db.collection("counties").document(countyId).collection("regions")
                .document(regionId).collection("roads").document(streetId)
                .set(addressName, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Bundle params = new Bundle();
                        params.putString("user_id", myid);
                        mFirebaseAnalytics.logEvent("street_update", params);

                        Toast.makeText(StreetSearchActivity.this,"Street Verification Updated Successfully",Toast.LENGTH_LONG).show();
                        FstartDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(StreetSearchActivity.this,"Verification Update Failed",Toast.LENGTH_LONG).show();
                        FstartDialog.dismiss();
                    }
                });
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
                    Intent i = new Intent(StreetSearchActivity.this, MainActivity.class);
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
                Intent i = new Intent(StreetSearchActivity.this, MainActivity.class);
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

            Bundle params = new Bundle();
            params.putString("user_id", myid);
            mFirebaseAnalytics.logEvent("location_violation", params);

            Date currentDate = new Date();
            Map<String, Object> user = new HashMap<>();
            user.put("officer", myid);
            user.put("supervisor", Supervisor);
            user.put("county", County);
            user.put("region", Region);
            user.put("latitude", absoluteLat);
            user.put("longitude", Longitude);
            user.put("timecreated", currentDate);

            // Add a new document with a generated ID

            db.collection("location_violation")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Reason=("Not within assigned region");
                            sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                            editor = sharedpreferences.edit();
                            editor.clear();
                            editor.commit();
                            Intent i = new Intent(StreetSearchActivity.this, MainActivity.class);
                            Bundle location = new Bundle();
                            location.putString("reason", Reason);
                            i.putExtras(location);
                            startActivity(i);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        }

    }
}
