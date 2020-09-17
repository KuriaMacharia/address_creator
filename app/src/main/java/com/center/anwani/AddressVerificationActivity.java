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
import android.os.AsyncTask;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.center.agiza.MyView;
import com.center.anwani.Helper.CheckNetWorkStatus;
import com.center.anwani.Helper.HttpJsonParser;
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
import com.google.common.hash.Hashing;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.nio.charset.StandardCharsets;
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

public class AddressVerificationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback {

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

    private final String COLLECTION_KEY = "address";

    public static final String starttime1 = "starttimeKey";
    public static final String endtime1 = "endtimeKey";
    public static final String regionstatus1 = "regionstatusKey";

    public static final String clat1 = "clatKey";
    public static final String clong1 = "clongKey";
    public static final String radius1 = "radiusKey";

    private static final String KEY_SUCCESS = "success";
    private static final String KEY_ADDRESS_ID = "id";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_ADDRESS_NAME = "name";
    private static final String KEY_ROAD = "road";
    private static final String KEY_REGION = "region";
    private static final String KEY_COUNTY = "county";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_ADDRESS_TYPE = "address_type";
    private static final String KEY_ADDRESS_CATEGORY = "category";

    private static final String BASE_URL = "http://www.anwani.net/seya/";

    String startTime, endTime, regionStatus, Reason, cLat, cLong, Radius, theId, longCheck, myDistance, propertyCategory;
    int locCount, capCount;

    private FirebaseAnalytics mFirebaseAnalytics;

    private ArrayList<Address> listAddress, searchList;
    private AddressAdapter addressAdapter;

    ConstraintLayout findmeContraint, constraintCaptured;
    TextView coordinatestxt, resultsTxt, failureTxt, categoryTxt;
    FirebaseFirestore db;
    ImageView searchImg;
    EditText searchEdt;

    private Location mylocation;

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
    ArrayList<Double> listDistance;
    ArrayList<String> listAllDistance, listReDistance;
    List<String> list1;
    List<String> list2;
    List<String> list3;
    ArrayList<String> list4;
    ArrayList<Double> lisLat, resultLat, latitudeList;
    ArrayList<Double> lisLon, resultLong, longitudeList;
    ArrayAdapter<String> leftList;

    List<String> latAddresses;
    List<String> longAddresses;
    Double diffMax, latitude;
    int mCounter, indexL, success;
    String fullAddress, Latitude, Longitude, absoluteLat, reLatitude, reLongitude, searchLat, searchLong, addressId,
            nameAddress, verificationStatus, activeStatus, addressType, latCount, longCount, commonCount;
    Double angleRadius, distanceRadius, maxLat, minLat, maxLong, minLong, curLat1, bsLong, curLat, curLong, endLat, endLong;
    ArrayList<Address> longFilterList, latFilterList;
    ArrayList<Address> commonList;
    ArrayList<Address> filteredList, newList;
    ArrayList<HashMap<String, String>> distanceListReNe, duplicateList, coordinatesListRe;
    private ProgressDialog pDialog, startDialog, FstartDialog, UstartDialog;
    ListView neighborList, latAdList, longAdList;
    ArrayList<HashMap<String, String>> distanceListRe;

    SharedPreferences.Editor editor;
    SharedPreferences pref;
    Button verifyBtn, missingAddressBtn;

    ConstraintLayout addNameCons, nameCons, editNameCons, addressNameCons, editOptionsCons, saveCons, cancelCons, updateLocationCons,
                        neighborsCons, mapCons;
    EditText addressNameEdt;
    TextView addressNameTxt, verificationTxt, propertyTypeTxt;
    SharedPreferences sharedpreferences;
    String Phone, Email, Name, Supervisor, myid, myCounty, myRegion, myRole, myStatus, theRoad, theCounty, theRegion;
    TextWatcher textwatcher;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_verification);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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

        endTime = sharedpreferences.getString(endtime1,"");
        startTime = sharedpreferences.getString(starttime1,"");
        regionStatus = sharedpreferences.getString(regionstatus1,"");

        cLat = sharedpreferences.getString(clat1,"");
        cLong = sharedpreferences.getString(clong1,"");
        Radius = sharedpreferences.getString(radius1,"");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(AddressVerificationActivity.this);
        InitialCheck();
        UstartDialog = new ProgressDialog(AddressVerificationActivity.this, R.style.mydialog);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map3);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        context = getApplicationContext();
        setUpGClient();
        CheckGpsStatus();
        listAddress = new ArrayList<Address>();
        searchList=new ArrayList<Address>();
        commonList = new ArrayList<Address>();
        filteredList = new ArrayList<Address>();
        newList = new ArrayList<Address>();
        longFilterList = new ArrayList<Address>();
        latFilterList = new ArrayList<Address>();
        distanceListRe = new ArrayList<>();
        distanceListReNe=new ArrayList<>();
        duplicateList=new ArrayList<>();
        coordinatesListRe=new ArrayList<>();

        listDistance = new ArrayList<Double>();
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
        latAddresses =new ArrayList<>();
        longAddresses =new ArrayList<>();

        listAddress = new ArrayList<Address>();
        coordinatestxt=(TextView) findViewById(R.id.txt_coordinates_address);
        findmeContraint=(ConstraintLayout) findViewById(R.id.constraint_check_address);
        constraintCaptured=(ConstraintLayout) findViewById(R.id.constraint_address_captured);
        neighborsCons=(ConstraintLayout) findViewById(R.id.constraint_map_verify);
        mapCons=(ConstraintLayout) findViewById(R.id.constraint_map_verification);
        resultsTxt=(TextView) findViewById(R.id.txt_captured_address);
        failureTxt=(TextView) findViewById(R.id.txt_failed_to_capture);
        searchEdt=(EditText) findViewById(R.id.edt_search_address);
        searchImg=(ImageView) findViewById(R.id.img_search);

        addNameCons=(ConstraintLayout) findViewById(R.id.constraint_add_name_address);
        nameCons=(ConstraintLayout) findViewById(R.id.constraint_name);
        editNameCons=(ConstraintLayout) findViewById(R.id.constraint_edit_name_option);
        addressNameCons=(ConstraintLayout) findViewById(R.id.constraint_address_name);
        editOptionsCons=(ConstraintLayout) findViewById(R.id.constraint_edit_name_address);
        saveCons=(ConstraintLayout) findViewById(R.id.constraint_save_name_option);
        cancelCons=(ConstraintLayout) findViewById(R.id.constraint_cancel_change_option);
        addressNameEdt=(EditText) findViewById(R.id.edt_address_name_address);
        addressNameTxt=(TextView) findViewById(R.id.txt_address_name_address);
        updateLocationCons=(ConstraintLayout) findViewById(R.id.constraint_update_location_address);
        verifyBtn=(Button) findViewById(R.id.btn_verify_address);
        missingAddressBtn=(Button) findViewById(R.id.btn_mark_missing_address);
        verificationTxt=(TextView) findViewById(R.id.txt_address_verification_status);
        propertyTypeTxt=(TextView) findViewById(R.id.txt_property_type);
        categoryTxt=(TextView) findViewById(R.id.txt_address_category_item);
        neighborList=(ListView) findViewById(R.id.list_neighbors_verification);
        latAdList=(ListView) findViewById(R.id.list_lat_addresses);
        longAdList=(ListView) findViewById(R.id.list_long_address);

        missingAddressBtn.setVisibility(View.GONE);
        pDialog = new ProgressDialog(AddressVerificationActivity.this, R.style.mydialog);

        pDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                coordinatestxt.removeTextChangedListener(textwatcher);
                lisLat.clear();
                lisLon.clear();
                listDistance.clear();
                coordinatesListRe.clear();
                listAllDistance.clear();
                commonList.clear();
                distanceListRe.clear();
            }
        });

        searchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(searchList.size()>0){
                    searchList.clear();
                }

                if (CheckNetWorkStatus.isNetworkAvailable(getApplicationContext())) {
                    pDialog.setMessage("Searching. Please wait...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();

                    if(Character.isDigit(searchEdt.getText().toString().charAt(0))
                            &&Character.isDigit(searchEdt.getText().toString().charAt(0))){
                        db.collection(COLLECTION_KEY).whereEqualTo("fulladdress", searchEdt.getText().toString())
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for(QueryDocumentSnapshot document : task.getResult()) {
                                        Address miss = document.toObject(Address.class);
                                        searchList.add(miss);
                                        searchLat=searchList.get(0).getLatitude();
                                        searchLong=searchList.get(0).getLongitude();

                                        if(searchList.get(0).getAddressname().contentEquals("No Name")){
                                            constraintCaptured.setVisibility(View.VISIBLE);
                                            addressNameTxt.setText(searchList.get(0).getAddressname());
                                            resultsTxt.setText(searchList.get(0).getFulladdress());
                                            nameCons.setVisibility(View.GONE);
                                            addNameCons.setVisibility(View.VISIBLE);
                                            addressNameCons.setVisibility(View.GONE);
                                            editOptionsCons.setVisibility(View.GONE);
                                        }else{
                                            constraintCaptured.setVisibility(View.VISIBLE);
                                            addressNameTxt.setText(searchList.get(0).getAddressname());
                                            resultsTxt.setText(searchList.get(0).getFulladdress());
                                            nameCons.setVisibility(View.VISIBLE);
                                            addNameCons.setVisibility(View.GONE);
                                            addressNameCons.setVisibility(View.VISIBLE);
                                            editOptionsCons.setVisibility(View.GONE);
                                        }

                                        findmeContraint.setVisibility(View.GONE);
                                        failureTxt.setVisibility(View.GONE);
                                        pDialog.dismiss();
                                    }

                                } else {
                                    failureTxt.setVisibility(View.VISIBLE);
                                    failureTxt.setText("Address not found!");
                                    pDialog.dismiss();
                                }
                            }
                        });
                    }else{
                        db.collection(COLLECTION_KEY).whereEqualTo("addressname", searchEdt.getText().toString())
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for(QueryDocumentSnapshot document : task.getResult()) {
                                        Address miss = document.toObject(Address.class);
                                        searchList.add(miss);
                                        searchLat=searchList.get(0).getLatitude();
                                        searchLong=searchList.get(0).getLongitude();

                                        if(searchList.get(0).getAddressname().contentEquals("No Name")){
                                            constraintCaptured.setVisibility(View.VISIBLE);
                                            addressNameTxt.setText(searchList.get(0).getAddressname());
                                            resultsTxt.setText(searchList.get(0).getFulladdress());
                                            nameCons.setVisibility(View.GONE);
                                            addNameCons.setVisibility(View.VISIBLE);
                                            addressNameCons.setVisibility(View.GONE);
                                            editOptionsCons.setVisibility(View.GONE);
                                        }else{
                                            constraintCaptured.setVisibility(View.VISIBLE);
                                            addressNameTxt.setText(searchList.get(0).getAddressname());
                                            resultsTxt.setText(searchList.get(0).getFulladdress());
                                            nameCons.setVisibility(View.VISIBLE);
                                            addNameCons.setVisibility(View.GONE);
                                            addressNameCons.setVisibility(View.VISIBLE);
                                            editOptionsCons.setVisibility(View.GONE);
                                        }

                                        findmeContraint.setVisibility(View.GONE);
                                        failureTxt.setVisibility(View.GONE);
                                        pDialog.dismiss();
                                    }

                                } else {
                                    constraintCaptured.setVisibility(View.GONE);
                                    failureTxt.setVisibility(View.VISIBLE);
                                    failureTxt.setText("Address not found!");
                                    pDialog.dismiss();
                                }
                            }
                        });
                    }

                } else {
                    Toast.makeText(AddressVerificationActivity.this,"Unable to connect to internet",Toast.LENGTH_LONG).show();
                }
            }
        });

        findmeContraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (googleApiClient==null) {
                    setUpGClient();
                }
                googleApiClient.connect();
                getMyLocation();

                coordinatesListRe.clear();
                coordinatestxt.addTextChangedListener(textwatcher);

                if(distanceListRe.size()>0){
                    distanceListRe.clear();
                }
                CheckGpsStatus();
                if (GpsStatus) {
                    if (CheckNetWorkStatus.isNetworkAvailable(getApplicationContext())) {
                        googleApiClient.connect();
                        //StartScan();
                        //pDialog = new ProgressDialog(AddressVerificationActivity.this, R.style.mydialog);
                        pDialog.setMessage("Initiating Scan. Please wait...");
                        pDialog.setIndeterminate(false);
                        pDialog.setCancelable(false);
                        pDialog.show();
                        constraintCaptured.setVisibility(View.GONE);
                        neighborsCons.setVisibility(View.GONE);
                        mapCons.setVisibility(View.GONE);

                        if (commonList.size()>0){
                            commonList.clear();
                        }

                    } else {
                        Toast.makeText(AddressVerificationActivity.this,"Unable to connect to internet",Toast.LENGTH_LONG).show();
                    }


                } else {
                    Toast.makeText(AddressVerificationActivity.this, "Please Enable GPS First", Toast.LENGTH_LONG).show();
                }
            }
        });

        addNameCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameCons.setVisibility(View.VISIBLE);
                addNameCons.setVisibility(View.GONE);
                addressNameCons.setVisibility(View.GONE);
                editOptionsCons.setVisibility(View.VISIBLE);
            }
        });

        editNameCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addressNameEdt.setText(addressNameTxt.getText().toString());
                nameCons.setVisibility(View.VISIBLE);
                addNameCons.setVisibility(View.GONE);
                addressNameCons.setVisibility(View.GONE);
                editOptionsCons.setVisibility(View.VISIBLE);
            }
        });

        saveCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FstartDialog = new ProgressDialog(AddressVerificationActivity.this);
                FstartDialog.setMessage("Preparing platform. Please wait...");
                FstartDialog.setIndeterminate(false);
                FstartDialog.setCancelable(false);
                FstartDialog.show();

                CaptureAddress();
            }
        });

        cancelCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameCons.setVisibility(View.VISIBLE);
                addNameCons.setVisibility(View.GONE);
                addressNameCons.setVisibility(View.VISIBLE);
                editOptionsCons.setVisibility(View.GONE);
            }
        });

        updateLocationCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AddressVerificationActivity.this, EditActivity.class);
                Bundle location = new Bundle();
                location.putString("address", resultsTxt.getText().toString());
                location.putString("addressid", addressId);

                i.putExtras(location);
                startActivity(i);
            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNetWorkStatus.isNetworkAvailable(getApplicationContext())) {
                        db.collection(COLLECTION_KEY)
                                .whereEqualTo("latitude", reLatitude).whereEqualTo("longitude", reLongitude)
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Address miss = document.toObject(Address.class);
                                        addressId=document.getId();
                                        UpdateAddressVerified();
                                        new AddAddress().execute();

                                    }
                                } else {
                                    Toast.makeText(AddressVerificationActivity.this, "Error fetching document", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                } else {
                    Toast.makeText(AddressVerificationActivity.this,"Unable to connect to internet",Toast.LENGTH_LONG).show();
                }
            }
        });

        missingAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date currentDate = new Date();
                Map<String, Object> user = new HashMap<>();

                user.put("officer", myid);
                user.put("supervisor", Supervisor);
                user.put("county", myCounty);
                user.put("region", myRegion);
                user.put("fulladdress", "Missing Address");
                user.put("latitude", Latitude);
                user.put("longitude", Longitude);
                user.put("creationdate", currentDate);
                user.put("activestatus", "Not Active");

                db.collection("address")
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {

                                Bundle params = new Bundle();
                                params.putString("user_id", myid);
                                mFirebaseAnalytics.logEvent("mark_address_missing", params);

                                Toast.makeText(AddressVerificationActivity.this, "Successful", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(AddressVerificationActivity.this, AddressVerificationActivity.class));

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddressVerificationActivity.this, "Error adding document", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        textwatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                lisLat.add((mylocation.getLatitude()));
                lisLon.add(mylocation.getLongitude());
                int a=lisLat.size();

                if(lisLat.size()>2) {

                    lat1 = lisLat.get(a-1);
                    lon1 = lisLon.get(a-1);
                    lat2 = lisLat.get(a - 2);
                    lon2 = lisLon.get(a - 2);

                    Location loc1 = new Location("");
                    loc1.setLatitude(lat1);
                    loc1.setLongitude(lon1);

                    Location loc2 = new Location("");
                    loc2.setLatitude(lat2);
                    loc2.setLongitude(lon2);

                    float distanceInMeters = loc1.distanceTo(loc2);

                    pDialog.setMessage("Locating: Please Wait...");

//Add the coordinates storage list
                    HashMap<String, String> map = new HashMap<>();
                    map.put("latitude", String.valueOf(Double.valueOf(lat1)));
                    map.put("longitude", String.valueOf(Double.valueOf(lon1)));
                    map.put("distance", String.valueOf(distanceInMeters));
                    coordinatesListRe.add(map);

//Compute distance list and process accuracy

                    listDistance.add(Double.parseDouble(String.valueOf(distanceInMeters)));
                    //ss.notifyDataSetChanged();

                    int j=listDistance.size();

                    if(listDistance.size()>15) {
                        if (listDistance.get(j - 1) < 0.03
                                && listDistance.get(j - 2) < 0.03
                                && listDistance.get(j - 3) < 0.03
                                && listDistance.get(j - 4) < 0.03
                                && listDistance.get(j - 5) < 0.03) {

                            coordinatestxt.removeTextChangedListener(textwatcher);
                            lisLat.clear();
                            lisLon.clear();
                            listDistance.clear();
//Coordinates to be set as address point

                            Latitude = coordinatesListRe.get(coordinatesListRe.size()-2).get("latitude");
                            Longitude = coordinatesListRe.get(coordinatesListRe.size()-2).get("longitude");

                            absoluteLat = String.valueOf(Double.parseDouble(coordinatesListRe.get(coordinatesListRe.size()-2).get("latitude"))+ 100);
                            myDistance= coordinatesListRe.get(coordinatesListRe.size()-2).get("distance");
                            coordinatesListRe.clear();

                            CalculateDomain();
                            pDialog.setMessage("Location sucessfully captured. Fetching addresses...");
                            listAllDistance.clear();
                            pDialog.setCancelable(true);
                            locCount++;
                            if(locCount==1){
                                LocationCheck();
                            }

                        }
                    }


                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

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
            //googleApiClient.connect();
        } else {

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(-1.289238, 36.820442);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Start"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    private void SetMarker(){

        mMap.clear();
        LatLng building = new LatLng(Double.parseDouble(Latitude), Double.parseDouble(Longitude));
        mMap.addMarker(new MarkerOptions().position(building).title(myDistance));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(building));
        Log.e("PlaceLL", myDistance);

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
                int permissionLocation = ContextCompat.checkSelfPermission(AddressVerificationActivity.this,
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
                                            .checkSelfPermission(AddressVerificationActivity.this,
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
                                        status.startResolutionForResult(AddressVerificationActivity.this,
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
        int permissionLocation = ContextCompat.checkSelfPermission(AddressVerificationActivity.this,
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
                } else {
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
        new AlertDialog.Builder(AddressVerificationActivity.this)
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
        }
    }

    private void CalculateDomain(){

        curLat=Double.parseDouble(absoluteLat);
        curLat1=Double.parseDouble(Latitude);
        curLong=Double.parseDouble(Longitude);

        angleRadius= 0.003/ ( 111 * Math.cos(curLat1));
        //angleRadius= 0.1/ ( 111 * Math.cos(curLat1));

        minLat = curLat - angleRadius;
        maxLat = curLat + angleRadius;
        minLong = curLong - angleRadius;
        maxLong = curLong + angleRadius;

        ScanAddress();
    }

    private void ScanAddress() {

        db.collection(COLLECTION_KEY)
                .whereGreaterThanOrEqualTo("longitude", String.valueOf(minLong))
                .whereLessThanOrEqualTo("longitude", String.valueOf(maxLong))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    pDialog.setMessage("Fetching Data. If this takes more than 10 secs, cancel and retry!");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            Address miss = document.toObject(Address.class);
                            commonList.add(miss);
                            //failureTxt.setText(String.valueOf(commonList.size()));

                            if (commonList.size() > 1) {
                                for (int a = 0; a < commonList.size(); a++) {
                                    if (Double.parseDouble(commonList.get(a).getLatitude()) > minLat
                                            && Double.parseDouble(commonList.get(a).getLatitude()) < maxLat) {

                                        HashMap<String, String> map = new HashMap<>();
                                        map.put("fulladdress", commonList.get(a).getFulladdress());
                                        map.put("addressname", commonList.get(a).getAddressname());
                                        map.put("verificationstatus", commonList.get(a).getVerificationstatus());
                                        map.put("activestatus", commonList.get(a).getActivestatus());
                                        map.put("addresstype", commonList.get(a).getAddresstype());
                                        map.put("category", commonList.get(a).getCategory());

                                        map.put("road", commonList.get(a).getRoad());
                                        map.put("county", commonList.get(a).getCounty());
                                        map.put("region", commonList.get(a).getRegion());

                                        map.put("latitude", String.valueOf(Double.valueOf(commonList.get(a).getLatitude()) - 100));
                                        map.put("longitude", String.valueOf(Double.valueOf(commonList.get(a).getLongitude())));
                                        distanceListRe.add(map);

                                        failureTxt.setText(String.valueOf(distanceListRe.size()) + "; " +
                                                String.valueOf(commonList.size()));
                                        FilterList();
                                    }
                                }
                            }else if(commonList.size()==1) {
                                if (Double.parseDouble(commonList.get(0).getLatitude()) > minLat
                                        && Double.parseDouble(commonList.get(0).getLatitude()) < maxLat) {
                                    FilterList();
                                }

                            }else {
                                mapCons.setVisibility(View.VISIBLE);
                                SetMarker();
                                pDialog.dismiss();
                            }
                        }else{
                            pDialog.dismiss();
                            Toast.makeText(AddressVerificationActivity.this,
                                    "Error fetching filtered document", Toast.LENGTH_LONG).show();
                        }
                    }

                } else {
                    pDialog.dismiss();
                    Toast.makeText(AddressVerificationActivity.this,
                            "Error fetching filtered document", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void FilterList(){

        if(distanceListRe.size()==1) {
            nameAddress=commonList.get(0).getAddressname();
            resultsTxt.setText(commonList.get(0).getFulladdress());
            reLatitude=commonList.get(0).getLatitude();
            reLongitude=commonList.get(0).getLongitude();
            propertyCategory=commonList.get(0).getCategory();
            addressType=commonList.get(0).getAddresstype();
            theRoad=commonList.get(0).getRoad();
            theRegion=commonList.get(0).getRegion();
            theCounty=commonList.get(0).getCounty();
            verificationStatus= commonList.get(0).getVerificationstatus();
            activeStatus=commonList.get(0).getActivestatus();
            verificationTxt.setText(verificationStatus);
            propertyTypeTxt.setText(addressType);

            if(verificationStatus.contentEquals("Not Verified")||
                    verificationStatus.isEmpty()){
                if(activeStatus.contentEquals("Active")||
                        activeStatus.isEmpty()){
                    verifyBtn.setVisibility(View.VISIBLE);
                }else {
                    verifyBtn.setVisibility(View.GONE);
                }
            }else{
                verifyBtn.setVisibility(View.GONE);
            }
            constraintCaptured.setVisibility(View.VISIBLE);

            if(nameAddress.contentEquals("No Name")){
                nameCons.setVisibility(View.GONE);
                addNameCons.setVisibility(View.VISIBLE);
                addressNameCons.setVisibility(View.GONE);
                editOptionsCons.setVisibility(View.GONE);

            }else{
                addressNameTxt.setText(commonList.get(0).getAddressname());
                nameCons.setVisibility(View.VISIBLE);
                addNameCons.setVisibility(View.GONE);
                addressNameCons.setVisibility(View.VISIBLE);
                editOptionsCons.setVisibility(View.GONE);
            }

            pDialog.dismiss();

        }else if(distanceListRe.size()>1){
            SortList();
            neighborsCons.setVisibility(View.VISIBLE);
        }else{
            mapCons.setVisibility(View.VISIBLE);
            SetMarker();
            pDialog.dismiss();
        }
    }

    public void SortList(){

        String reFulladdress = distanceListRe.get(0).get("fulladdress");
        String reAddressName = distanceListRe.get(0).get("addressname");
        reLatitude = String.valueOf(Double.valueOf(distanceListRe.get(0).get("latitude")) +100);
        reLongitude = distanceListRe.get(0).get("longitude");
        verificationStatus = distanceListRe.get(0).get("verificationstatus");
        activeStatus = distanceListRe.get(0).get("activestatus");
        addressType = distanceListRe.get(0).get("addresstype");
        propertyCategory = distanceListRe.get(0).get("category");
        theRoad=distanceListRe.get(0).get("road");
        theRegion = distanceListRe.get(0).get("region");
        theCounty = distanceListRe.get(0).get("county");

        HashSet hs = new HashSet();
        hs.addAll(distanceListRe); // demoArrayList= name of arrayList from which u want to remove duplicates
        distanceListRe.clear();
        distanceListRe.addAll(hs);

        ListAdapter adapter = new SimpleAdapter(
                AddressVerificationActivity.this, distanceListRe, R.layout.address_item_verified,
                new String[]{"fulladdress", "latitude", "longitude", "addressname", "addresstype", "verificationstatus", "category"},
                new int[]{R.id.txt_address_number, R.id.txt_latitude_item, R.id.txt_longitude_item,
                        R.id.txt_address_name_item, R.id.txt_address_type_item, R.id.txt_verification_status_item,
                        R.id.txt_address_category_item});

        neighborList.setAdapter(adapter);

        neighborList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String latMap=((TextView) view.findViewById(R.id.txt_latitude_item)).getText().toString();
                final String longMap=((TextView) view.findViewById(R.id.txt_longitude_item)).getText().toString();
                final String addressNumber=((TextView) view.findViewById(R.id.txt_address_number)).getText().toString();
                final String theName=((TextView) view.findViewById(R.id.txt_address_name_item)).getText().toString();
                final String theType=((TextView) view.findViewById(R.id.txt_address_type_item)).getText().toString();
                final String theVerification=((TextView) view.findViewById(R.id.txt_verification_status_item)).getText().toString();
                final String theCategory=((TextView) view.findViewById(R.id.txt_address_category_item)).getText().toString();

                resultsTxt.setText(addressNumber);
                reLatitude=latMap;
                reLongitude=longMap;
                addressNameTxt.setText(theName);
                propertyTypeTxt.setText(theType);
                verificationTxt.setText(theVerification);
                categoryTxt.setText(theCategory);

                if(theVerification.contentEquals("Not Verified")||
                        theVerification.isEmpty()){
                        verifyBtn.setVisibility(View.VISIBLE);
                }else{
                    verifyBtn.setVisibility(View.GONE);
                }

            }
        });

        if(verificationStatus.contentEquals("Not Verified")||
                verificationStatus.isEmpty()){
            if(activeStatus.contentEquals("Active")||
                    activeStatus.isEmpty()){
                verifyBtn.setVisibility(View.VISIBLE);
            }else {
                verifyBtn.setVisibility(View.GONE);
            }
        }else{
            verifyBtn.setVisibility(View.GONE);
        }

        resultsTxt.setText(reFulladdress);
        constraintCaptured.setVisibility(View.VISIBLE);
        addressNameTxt.setText(reAddressName);
        verificationTxt.setText(verificationStatus);
        propertyTypeTxt.setText(addressType);

        if(reAddressName.contentEquals("No Name")){
            nameCons.setVisibility(View.GONE);
            addNameCons.setVisibility(View.VISIBLE);
            addressNameCons.setVisibility(View.GONE);
            editOptionsCons.setVisibility(View.GONE);

        }else{
            nameCons.setVisibility(View.VISIBLE);
            addNameCons.setVisibility(View.GONE);
            addressNameCons.setVisibility(View.VISIBLE);
            editOptionsCons.setVisibility(View.GONE);
        }

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
                        UpdateAddress();
                    }

                } else {
                    failureTxt.setVisibility(View.VISIBLE);
                    failureTxt.setText("Address not found!");
                    pDialog.dismiss();
                }
            }
        });
    }

    private void UpdateAddress(){
        Map<String, Object> addressName = new HashMap<>();
        addressName.put("addressname", addressNameEdt.getText().toString());


        db.collection(COLLECTION_KEY).document(addressId)
                .set(addressName, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Bundle params = new Bundle();
                        params.putString("user_id", myid);
                        mFirebaseAnalytics.logEvent("address_name_update", params);

                        Toast.makeText(AddressVerificationActivity.this,"Address Name Updated Successfully",Toast.LENGTH_LONG).show();
                        nameCons.setVisibility(View.VISIBLE);
                        addNameCons.setVisibility(View.GONE);
                        addressNameCons.setVisibility(View.VISIBLE);
                        editOptionsCons.setVisibility(View.GONE);
                        FstartDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddressVerificationActivity.this,"Address Update Failed",Toast.LENGTH_LONG).show();
                        nameCons.setVisibility(View.VISIBLE);
                        addNameCons.setVisibility(View.GONE);
                        addressNameCons.setVisibility(View.GONE);
                        editOptionsCons.setVisibility(View.VISIBLE);
                        FstartDialog.dismiss();
                    }
                });
    }

    private void UpdateAddressVerified(){
        UstartDialog.setMessage("Verifying. Please wait...");
        UstartDialog.setIndeterminate(false);
        UstartDialog.setCancelable(false);
        UstartDialog.show();

        Date currentDate = new Date();
        Map<String, Object> addressName = new HashMap<>();
        addressName.put("verificationstatus", "Verified");
        addressName.put("verifiedby", myid);
        addressName.put("verificationdate", currentDate);


        db.collection(COLLECTION_KEY).document(addressId)
                .set(addressName, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Bundle params = new Bundle();
                        params.putString("user_id", myid);
                        mFirebaseAnalytics.logEvent("address_verification", params);


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

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
                    Intent i = new Intent(AddressVerificationActivity.this, MainActivity.class);
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
                Intent i = new Intent(AddressVerificationActivity.this, MainActivity.class);
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
            user.put("county", myCounty);
            user.put("region", myRegion);
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
                            Intent i = new Intent(AddressVerificationActivity.this, MainActivity.class);
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

    private class AddAddress extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();

            httpParams.put(KEY_ADDRESS, resultsTxt.getText().toString());
            httpParams.put(KEY_ADDRESS_NAME, addressNameTxt.getText().toString());
            httpParams.put(KEY_ROAD, theRoad);
            httpParams.put(KEY_REGION, theRegion);
            httpParams.put(KEY_COUNTY, theCounty);
            httpParams.put(KEY_LATITUDE, String.format("%.8f", Double.parseDouble(reLatitude)-100));
            httpParams.put(KEY_LONGITUDE, reLongitude);
            httpParams.put(KEY_ADDRESS_TYPE, addressType);
            httpParams.put(KEY_ADDRESS_CATEGORY, propertyCategory);

            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL + "address.php", "POST", httpParams);
            if(success==1)
                try {
                    success = jsonObject.getInt(KEY_SUCCESS);

                }catch (JSONException e) {
                    e.printStackTrace();
                }
            return null;
        }

        protected void onPostExecute(String result) {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    if (success == 0) {
                        Toast.makeText(AddressVerificationActivity.this,"Address Verification Successful",Toast.LENGTH_LONG).show();

                        constraintCaptured.setVisibility(View.GONE);
                        verifyBtn.setVisibility(View.GONE);
                        neighborsCons.setVisibility(View.GONE);
                        UstartDialog.dismiss();

                    } else {
                        Toast.makeText(AddressVerificationActivity.this,"Address Verification Failed",Toast.LENGTH_LONG).show();
                        verifyBtn.setVisibility(View.VISIBLE);
                        UstartDialog.dismiss();
                    }
                }
            });
        }
    }
}
