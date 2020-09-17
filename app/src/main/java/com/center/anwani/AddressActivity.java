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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
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

public class AddressActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String name1 = "nameKey";
    public static final String supervisor1 = "supervisorKey";
    public static final String phoneNumber1 = "phoneKey";
    public static final String Email1 = "emailKey";
    public static final String myid1 = "myidKey";
    public static final String county1 = "countyKey";
    public static final String region1 = "regionKey";

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

    private ArrayList<Address> listAddress, searchList;
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
            nameAddress, verificationStatus, activeStatus, resultAddress;
    Double angleRadius, distanceRadius, maxLat, minLat, maxLong, minLong, curLat1, bsLong, curLat, curLong, endLat, endLong;
    List<Address> longFilterList, latFilterList;
    List<Address> commonList;
    ArrayList<StartCheck> startList;
    ArrayList<Address> filteredList, newList;
    ArrayList<HashMap<String, String>> distanceListRe, distanceListReNe, duplicateList;
    private ProgressDialog pDialog, startDialog, FstartDialog, UstartDialog;
    URL lin;

    SharedPreferences.Editor editor;
    SharedPreferences pref;
    Button verifyBtn, addressTypeCancelBtn, addressTypeSaveBtn;

    ConstraintLayout addNameCons, nameCons, editNameCons, addressNameCons, editOptionsCons, saveCons, cancelCons, updateLocationCons,
                    addressTypeSaveCons;
    EditText addressNameEdt;
    TextView addressNameTxt;
    String Phone, Email, Name, Supervisor, myid, myCounty, myRegion;
    SharedPreferences sharedpreferences;
    RadioGroup propertyGroup, institutionGroup, residenceGroup;
    RadioButton residenceBtn, institutionBtn;
    TextView propertyTypeTxt, changeTypeTxt;
    ScrollView addressScroll;
    private final String log_findme = "addressfindme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //themeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_address);

        db = FirebaseFirestore.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(AddressActivity.this);

        sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        Email = sharedpreferences.getString(Email1,"");
        Phone = sharedpreferences.getString(phoneNumber1,"");
        Name = sharedpreferences.getString(name1,"");
        Supervisor = sharedpreferences.getString(supervisor1,"");
        myid = sharedpreferences.getString(myid1,"");
        myCounty = sharedpreferences.getString(county1,"");
        myRegion = sharedpreferences.getString(region1,"");

        endTime = sharedpreferences.getString(endtime1,"");
        startTime = sharedpreferences.getString(starttime1,"");
        regionStatus = sharedpreferences.getString(regionstatus1,"");

        cLat = sharedpreferences.getString(clat1,"");
        cLong = sharedpreferences.getString(clong1,"");
        Radius = sharedpreferences.getString(radius1,"");

        UstartDialog = new ProgressDialog(AddressActivity.this, R.style.mydialog);
        startDialog = new ProgressDialog(AddressActivity.this, R.style.mydialog);
        FstartDialog = new ProgressDialog(AddressActivity.this, R.style.mydialog);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        context = getApplicationContext();
        //setUpGClient();
        CheckGpsStatus();
        InitialCheck();
        startList=new ArrayList<StartCheck>();
        listAddress = new ArrayList<Address>();
        searchList=new ArrayList<Address>();
        commonList = new ArrayList<Address>();
        filteredList = new ArrayList<Address>();
        newList = new ArrayList<Address>();
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

        propertyGroup=(RadioGroup) findViewById(R.id.group_property_type_create);
        institutionGroup=(RadioGroup) findViewById(R.id.group_institution_create);
        residenceGroup=(RadioGroup) findViewById(R.id.group_residence_create);

        institutionBtn=(RadioButton) findViewById(R.id.radio_institution_create);
        residenceBtn=(RadioButton) findViewById(R.id.radio_residential_create);
        addressScroll=(ScrollView) findViewById(R.id.scroll_address_type);
        propertyTypeTxt=(TextView) findViewById(R.id.txt_address_type_create);
        changeTypeTxt=(TextView) findViewById(R.id.txt_change_property_type_create);
        addressTypeSaveCons=(ConstraintLayout) findViewById(R.id.cons_address_type_save);
        addressTypeSaveBtn=(Button) findViewById(R.id.btn_save_address_type_edit);
        addressTypeCancelBtn=(Button) findViewById(R.id.btn_cancel_address_type_edit);

        listAddress = new ArrayList<Address>();
        coordinatestxt=(TextView) findViewById(R.id.txt_coordinates_address);
        findmeContraint=(ConstraintLayout) findViewById(R.id.constraint_check_address);
        constraintCaptured=(ConstraintLayout) findViewById(R.id.constraint_address_captured);
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

        searchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (googleApiClient==null) {
                    setUpGClient();
                }
                if(searchList.size()>0){
                    searchList.clear();
                }

                if (CheckNetWorkStatus.isNetworkAvailable(getApplicationContext())) {
                    pDialog = new ProgressDialog(AddressActivity.this, R.style.mydialog);
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
                    Toast.makeText(AddressActivity.this,"Unable to connect to internet",Toast.LENGTH_LONG).show();
                }
            }
        });

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

                            Bundle params = new Bundle();
                            params.putString("user_id", myid);
                            mFirebaseAnalytics.logEvent("address_check_ad_creator", params);

                            googleApiClient.connect();
                            StartScan();
                            pDialog = new ProgressDialog(AddressActivity.this, R.style.mydialog);
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
                            Toast.makeText(AddressActivity.this,"Unable to connect to internet",Toast.LENGTH_LONG).show();
                        }


                    } else {
                        Toast.makeText(AddressActivity.this, "Please Enable GPS First", Toast.LENGTH_LONG).show();
                    }

            }
        });

        changeTypeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addressScroll.setVisibility(View.VISIBLE);
            }
        });

        propertyGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.radio_residential_create:
                        if (residenceBtn.isChecked()) {

                            if (residenceGroup.getVisibility() == View.GONE) {
                                residenceGroup.setVisibility(View.VISIBLE);
                                institutionGroup.setVisibility(View.GONE);
                                institutionGroup.clearCheck();
                                residenceGroup.clearCheck();
                            } else {
                                residenceGroup.setVisibility(View.GONE);
                            }
                        }
                        break;

                    case R.id.radio_institution_create:
                        if (institutionBtn.isChecked()) {
                            if (institutionGroup.getVisibility() == View.GONE) {
                                institutionGroup.setVisibility(View.VISIBLE);
                                residenceGroup.setVisibility(View.GONE);
                                institutionGroup.clearCheck();
                                residenceGroup.clearCheck();
                            } else {
                                institutionGroup.setVisibility(View.GONE);
                            }
                        }
                        break;

                    case R.id.radio_government_create:
                        institutionGroup.clearCheck();
                        residenceGroup.clearCheck();
                        propertyTypeTxt.setText("Government Department");
                        institutionGroup.setVisibility(View.GONE);
                        institutionGroup.clearCheck();
                        addressScroll.setVisibility(View.GONE);
                        addressTypeSaveCons.setVisibility(View.VISIBLE);
                        break;

                    case R.id.radio_religious_institution_create:
                        institutionGroup.clearCheck();
                        residenceGroup.clearCheck();
                        propertyTypeTxt.setText("Religious Institution");
                        institutionGroup.setVisibility(View.GONE);
                        institutionGroup.clearCheck();
                        addressScroll.setVisibility(View.GONE);
                        addressTypeSaveCons.setVisibility(View.VISIBLE);
                        break;

                    case R.id.radio_business_create:
                        institutionGroup.clearCheck();
                        residenceGroup.clearCheck();
                        propertyTypeTxt.setText("Business/ Office Premises");
                        institutionGroup.setVisibility(View.GONE);
                        institutionGroup.clearCheck();
                        addressScroll.setVisibility(View.GONE);
                        addressTypeSaveCons.setVisibility(View.VISIBLE);
                        break;

                    case R.id.radio_other_create:
                        institutionGroup.clearCheck();
                        residenceGroup.clearCheck();
                        propertyTypeTxt.setText("Other Address Type");
                        institutionGroup.setVisibility(View.GONE);
                        institutionGroup.clearCheck();
                        addressScroll.setVisibility(View.GONE);
                        addressTypeSaveCons.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        residenceGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {

                    case R.id.radion_single_residence_create:
                        propertyTypeTxt.setText("Single Residence");
                        addressScroll.setVisibility(View.GONE);
                        residenceGroup.setVisibility(View.GONE);
                        addressTypeSaveCons.setVisibility(View.VISIBLE);
                        break;

                    case R.id.radio_apartment_create:
                        propertyTypeTxt.setText("Apartment Building");
                        addressScroll.setVisibility(View.GONE);
                        residenceGroup.setVisibility(View.GONE);
                        addressTypeSaveCons.setVisibility(View.VISIBLE);
                        break;

                    case R.id.radio_community_create:
                        propertyTypeTxt.setText("Community Setup");
                        addressScroll.setVisibility(View.GONE);
                        residenceGroup.setVisibility(View.GONE);
                        addressTypeSaveCons.setVisibility(View.VISIBLE);
                        break;

                    case R.id.radio_informal_settlement_create:
                        propertyTypeTxt.setText("Informal Settlement");
                        addressScroll.setVisibility(View.GONE);
                        residenceGroup.setVisibility(View.GONE);
                        addressTypeSaveCons.setVisibility(View.VISIBLE);
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
                        addressScroll.setVisibility(View.GONE);
                        residenceGroup.setVisibility(View.GONE);
                        addressTypeSaveCons.setVisibility(View.VISIBLE);
                        break;

                    case R.id.radio_primary_create:
                        propertyTypeTxt.setText("Primary School");
                        addressScroll.setVisibility(View.GONE);
                        residenceGroup.setVisibility(View.GONE);
                        addressTypeSaveCons.setVisibility(View.VISIBLE);
                        break;

                    case R.id.radio_secondary_create:
                        propertyTypeTxt.setText("Secondary School");
                        addressScroll.setVisibility(View.GONE);
                        residenceGroup.setVisibility(View.GONE);
                        addressTypeSaveCons.setVisibility(View.VISIBLE);
                        break;

                    case R.id.radio_college_institution:
                        propertyTypeTxt.setText("College or University");
                        addressScroll.setVisibility(View.GONE);
                        residenceGroup.setVisibility(View.GONE);
                        addressTypeSaveCons.setVisibility(View.VISIBLE);
                        break;
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
                FstartDialog.setMessage("Updating. Please wait...");
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
                Intent i = new Intent(AddressActivity.this, EditActivity.class);
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
                            }
                        } else {
                            Toast.makeText(AddressActivity.this, "Error fetching document", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        addressTypeSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UstartDialog.setMessage("Updating Address Type. Please wait...");
                UstartDialog.setIndeterminate(false);
                UstartDialog.setCancelable(true);
                UstartDialog.show();

                db.collection(COLLECTION_KEY)
                        .whereEqualTo("fulladdress", resultsTxt.getText().toString())
                        //.whereEqualTo("longitude", reLongitude)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Address miss = document.toObject(Address.class);
                                addressId=document.getId();
                                UpdateAddressType();
                            }
                        } else {
                            Toast.makeText(AddressActivity.this, "Error fetching document", Toast.LENGTH_LONG).show();
                            UstartDialog.dismiss();
                        }
                    }
                });
            }
        });

        addressTypeCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addressScroll.setVisibility(View.GONE);
                addressTypeSaveCons.setVisibility(View.GONE);
            }
        });

    }

    private synchronized void setUpGClient() {
        if (CheckNetWorkStatus.isNetworkAvailable(getApplicationContext())) {
            googleApiClient = new GoogleApiClient.Builder(AddressActivity.this)
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
                int permissionLocation = ContextCompat.checkSelfPermission(AddressActivity.this,
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
                                            .checkSelfPermission(AddressActivity.this,
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
                                        status.startResolutionForResult(AddressActivity.this,
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
        int permissionLocation = ContextCompat.checkSelfPermission(AddressActivity.this,
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
        new AlertDialog.Builder(AddressActivity.this)
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
                                LocationCheck();

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

        angleRadius= 0.002/ ( 111 * Math.cos(curLat1));

        minLat = curLat - angleRadius;
        maxLat = curLat + angleRadius;
        minLong = curLong - angleRadius;
        maxLong = curLong + angleRadius;

        ScanAddress();
    }

    private void ScanAddress() {

        db.collection(COLLECTION_KEY)
                .whereGreaterThanOrEqualTo("longitude", String.valueOf(minLong)).whereLessThanOrEqualTo("longitude", String.valueOf(maxLong))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Address miss = document.toObject(Address.class);
                        longFilterList.add(miss);
                        if (longFilterList.size() > 0) {
                            pDialog.setMessage("Configuring addresses...");
                            for (int j = 0; j < longFilterList.size(); j++) {
                                if (Double.valueOf(longFilterList.get(j).getLatitude()) > minLat
                                        && Double.valueOf(longFilterList.get(j).getLatitude()) < maxLat) {

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
                    Toast.makeText(AddressActivity.this, "Error fetching document", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void FilterList(){

        if(commonList.size()==1) {
            nameAddress=commonList.get(0).getAddressname();
            resultAddress=commonList.get(0).getFulladdress();
            resultsTxt.setText(commonList.get(0).getFulladdress());
            reLatitude=commonList.get(0).getLatitude();
            reLongitude=commonList.get(0).getLongitude();
            verificationStatus= commonList.get(0).getVerificationstatus();
            activeStatus=commonList.get(0).getActivestatus();
            propertyTypeTxt.setText(commonList.get(0).getAddresstype());

                /*if(verificationStatus.contentEquals("Not Verified")||
                        verificationStatus.isEmpty()){
                    if(activeStatus.contentEquals("Active")||
                            activeStatus.isEmpty()){
                                verifyBtn.setVisibility(View.VISIBLE);
                    }else {
                        verifyBtn.setVisibility(View.GONE);
                    }
                }else{
                    verifyBtn.setVisibility(View.GONE);
                }*/

            constraintCaptured.setVisibility(View.VISIBLE);

            if(nameAddress.contentEquals("No Name")){
                //addressNameTxt.setText(commonList.get(0).getAddressname());
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

        }else if(commonList.size()>1){

            for (int a = 0; a < commonList.size(); a++) {

                reLat1 = Double.valueOf(commonList.get(a).getLatitude())-100;
                reLong1 = Double.valueOf(commonList.get(a).getLongitude());

                Location loc1 = new Location("");
                loc1.setLatitude(curLat1);
                loc1.setLongitude(curLong);

                Location loc2 = new Location("");
                loc2.setLatitude(reLat1);
                loc2.setLongitude(reLong1);

                float distanceInMeters = loc1.distanceTo(loc2);
                listReDistance.add(String.valueOf(distanceInMeters));

                HashMap<String, String> map = new HashMap<>();
                map.put("fulladdress", commonList.get(a).getFulladdress());
                map.put("addressname", commonList.get(a).getAddressname());
                map.put("verificationstatus", commonList.get(a).getVerificationstatus());
                map.put("activestatus", commonList.get(a).getActivestatus());
                map.put("addresstype", commonList.get(a).getAddresstype());

                map.put("latitude", String.valueOf(Double.valueOf(commonList.get(a).getLatitude())-100));
                map.put("longitude", String.valueOf(Double.valueOf(commonList.get(a).getLongitude())));
                map.put("distance", String.valueOf(listReDistance.get(a)));
                distanceListRe.add(map);
                SortList();

            }

        }else{
            //failureTxt.setVisibility(View.VISIBLE);
            //failureTxt.setText("Address not found. Try again and standing still on the outside of the gate.");
            pDialog.dismiss();
            Toast.makeText(AddressActivity.this,"Address not found. Try again and standing still on the outside of the gate.",Toast.LENGTH_LONG).show();

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
        resultAddress= distanceListRe.get(0).get("fulladdress");
        reLatitude = String.valueOf(Double.valueOf(distanceListRe.get(0).get("latitude")) +100);
        reLongitude = distanceListRe.get(0).get("longitude");
        verificationStatus = distanceListRe.get(0).get("verificationstatus");
        reLongitude = distanceListRe.get(0).get("activestatus");
        String addressType = distanceListRe.get(0).get("addresstype");


        HashSet hs = new HashSet();
        hs.addAll(commonList); // demoArrayList= name of arrayList from which u want to remove duplicates
        commonList.clear();
        commonList.addAll(hs);



        resultsTxt.setText(reFulladdress);
        constraintCaptured.setVisibility(View.VISIBLE);
        addressNameTxt.setText(reAddressName);
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
                        Toast.makeText(AddressActivity.this,"Address Name Updated Successfully",Toast.LENGTH_LONG).show();
                        nameCons.setVisibility(View.VISIBLE);
                        addNameCons.setVisibility(View.GONE);
                        addressNameCons.setVisibility(View.VISIBLE);
                        editOptionsCons.setVisibility(View.GONE);
                        addressNameTxt.setText(addressNameEdt.getText().toString());
                        FstartDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddressActivity.this,"Address Update Failed",Toast.LENGTH_LONG).show();
                        nameCons.setVisibility(View.VISIBLE);
                        addNameCons.setVisibility(View.GONE);
                        addressNameCons.setVisibility(View.GONE);
                        editOptionsCons.setVisibility(View.VISIBLE);
                        FstartDialog.dismiss();
                    }
                });
    }

    private void UpdateAddressVerified(){
        UstartDialog = new ProgressDialog(AddressActivity.this, R.style.mydialog);
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
                        Toast.makeText(AddressActivity.this,"Address Verification Updated Successfully",Toast.LENGTH_LONG).show();
                        nameCons.setVisibility(View.VISIBLE);
                        addNameCons.setVisibility(View.GONE);
                        addressNameCons.setVisibility(View.VISIBLE);
                        editOptionsCons.setVisibility(View.GONE);
                        verifyBtn.setVisibility(View.GONE);
                        UstartDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddressActivity.this,"Address Verification Update Failed",Toast.LENGTH_LONG).show();
                        nameCons.setVisibility(View.VISIBLE);
                        addNameCons.setVisibility(View.GONE);
                        addressNameCons.setVisibility(View.GONE);
                        editOptionsCons.setVisibility(View.VISIBLE);
                        verifyBtn.setVisibility(View.VISIBLE);
                        UstartDialog.dismiss();
                    }
                });
    }

    private void UpdateAddressType(){

        Map<String, Object> addressTypeM = new HashMap<>();
        addressTypeM.put("addresstype", propertyTypeTxt.getText().toString());


        db.collection(COLLECTION_KEY).document(addressId)
                .set(addressTypeM, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Bundle params = new Bundle();
                        params.putString("user_id", myid);
                        mFirebaseAnalytics.logEvent("address_type_update", params);

                        Toast.makeText(AddressActivity.this,"Address Verification Updated Successfully",Toast.LENGTH_LONG).show();
                        addressScroll.setVisibility(View.GONE);
                        addressTypeSaveCons.setVisibility(View.GONE);
                        UstartDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddressActivity.this,"Address Verification Update Failed",Toast.LENGTH_LONG).show();
                        UstartDialog.dismiss();
                    }
                });
    }

    private void InitialCheck(){
        db.collection("checker")
                .whereEqualTo("county", myCounty).whereEqualTo("region", myRegion)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {

                        StartCheck miss = document.toObject(StartCheck.class);
                        startList.add(miss);
                        startTime=startList.get(0).getStarttime();
                        endTime=startList.get(0).getEndtime();
                        regionStatus=startList.get(0).getRegionstatus();

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
                                    Intent i = new Intent(AddressActivity.this, MainActivity.class);
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
                                Intent i = new Intent(AddressActivity.this, MainActivity.class);
                                Bundle location = new Bundle();
                                location.putString("reason", Reason);
                                i.putExtras(location);
                                startActivity(i);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });
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
                            Intent i = new Intent(AddressActivity.this, MainActivity.class);
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
