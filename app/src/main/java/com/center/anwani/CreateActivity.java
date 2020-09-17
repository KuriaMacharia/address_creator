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
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.StringBufferInputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CreateActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback, AdapterView.OnItemSelectedListener {

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

    String[] category = {"---Select---", "Residence", "Finance","Government Office", "Education", "Hospitals", "Religion","Transport","Other"};

    String[] government ={"---Select---","Police","Huduma Center", "Administration Office","Court","Prison","Government Institution", "Other Address Type"};
    String[] finance ={"---Select---","Business Premises", "Mixed Use", "Offices","Hotel","Lodging","Mall","Petrol Station", "Other Address Type"};
    String[] residence = {"---Select---","Single Residence","Apartment Building","Informal Settlement","Community Setup","Farm","Other Address Type"};
    String[] education = {"---Select---","Pre-school","Nursery School","Primary School","Secondary School","Technical School","College","University","Other Address Type"};
    String[] hospitals ={"---Select---","Public Hospitals", "Private Hospitals","Public Clinic","Private Clinic","Laboratory","Other Address Type"};
    String[] religion = {"---Select---","Church","Mosque","Temple","Other Address Type"};
    String[] transport ={"---Select---","Airport","Train Station","Bus Station","Port","Other Address Type"};
    String[] other = {"---Select---","Other Address Type"};
    String[] selCat= { };

    String startTime, endTime, regionStatus, Reason, cLat, cLong, Radius, latCheck, longCheck;
    int locCount;
    SharedPreferences.Editor editor;

    private FirebaseAnalytics mFirebaseAnalytics;

    Button leftBtn, rightBtn, generateBtn, cancelBtn, saveBtn, manualBtn, generateManualBtn, updateBtn, cancelUpdateBtn;
    TextView countyTxt, regionTxt, roadTxt, addressTxt, coordinatesTxt, roadListTxt, counterTxt, changeTypeTxt;
    MapView myMap;
    int mCounterL, mCounterR, addressNumber, changeCounter, previousCounterL, previousCounterR, red, yel, green, beg;
    String fullAddress, Latitude, Longitude, absoluteLat, addressName, upLat, upLong, propertyCategory, propertyType;
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
    RadioGroup propertyGroup, institutionGroup, residenceGroup;
    RadioButton residenceBtn, institutionBtn;
    ScrollView addressScroll;
    ImageView homeImg;
    TextView propertyTypeTxt, mapCoordinatesTxt, gapTxt, lapseTxt, accuracyTxt;

    private Location mylocation;
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    LocationRequest locationRequest;
    ConstraintLayout createCons, addressCapatureSaveCons, locationCompleteCons, saveCons, sideSelectCons, capturingLocationCons,
                    manualCons, mapCons, subcategoryCons;
    Animation animation;
    Animation animSequence;
    private AnimatorSet mSetRightOut, mSetLeftIn, xRotate;
    ImageView gpsSearchImg, gpsOffimg, locationCapturedImg, listImg, backImg, terrainImg, flatImg, satelliteImg;;

    Double lat1, lat2, lon1, lon2, reLat1, reLat2, reLong1, reLong2;
    //List<String> listDistance;
    ArrayList<String> listAllDistance, listReDistance, list,listR;
    List<String> list1;
    List<String> list2;
    List<String> list3;
    ArrayList<String> list4;
    ArrayList<Double> lisLat, resultLat, latitudeList;
    ArrayList<Double> lisLon, resultLong, longitudeList;
    List<Double> listAverageDistance;
    ArrayList<Double> listDistance;
    ArrayList<HashMap<String, String>> distanceListRe;
    ArrayAdapter<String> leftList;
    Double diffMax,selLat, selLon, selDis;
    int mCounter, indexL;

    String Phone, Email, Name, Supervisor, myid;
    SharedPreferences sharedpreferences;
    TextWatcher textwatcher;
    private GoogleMap mMap;
    Spinner categorySpin, subCategorySpin;
    private Handler handler = new Handler();
    private Runnable runnable;
    Date startTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(CreateActivity.this);
        InitialCheck();

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

        listAddress = new ArrayList<Address>();
        counterList = new ArrayList<>();
        distanceListRe = new ArrayList<>();
        startTimer= new Date();

        listDistance = new ArrayList<Double>();
        listAllDistance = new ArrayList<String>();
        listReDistance = new ArrayList<String>();
        lisLat =new ArrayList<Double>();
        lisLon =new ArrayList<Double>();
        resultLat =new ArrayList<Double>();
        resultLong =new ArrayList<Double>();
        latitudeList =new ArrayList<Double>();
        longitudeList =new ArrayList<Double>();
        listAverageDistance =new ArrayList<>();

        list1 = new ArrayList<>();
        list2 =new ArrayList<>();
        list3 =new ArrayList<>();
        list4 =new ArrayList<>();

        list = new ArrayList<String>();
        listR = new ArrayList<String>();

        listAddress = new ArrayList<Address>();

        mCounterL=0;
        mCounterR=0;

        subcategoryCons=(ConstraintLayout) findViewById(R.id.cons_type_spin_create);
        categorySpin=(Spinner) findViewById(R.id.spin_category_create);
        subCategorySpin=(Spinner) findViewById(R.id.spin_type_create);
        createCons=(ConstraintLayout) findViewById(R.id.cons_create_address);
        addressCapatureSaveCons=(ConstraintLayout) findViewById(R.id.cons_address_completion);
        locationCompleteCons=(ConstraintLayout) findViewById(R.id.cons_locating_complete);
        saveCons=(ConstraintLayout) findViewById(R.id.cons_save_address);
        sideSelectCons=(ConstraintLayout) findViewById(R.id.cons_sides_selection);
        capturingLocationCons=(ConstraintLayout) findViewById(R.id.cons_capturing_location);
        manualCons=(ConstraintLayout) findViewById(R.id.cons_enter_manually);
        mapCons=(ConstraintLayout) findViewById(R.id.constraint_map_create);
        enterNumberTxt=(EditText) findViewById(R.id.edt_enter_number_manually);
        manualBtn=(Button) findViewById(R.id.btn_enter_manually);
        generateManualBtn=(Button) findViewById(R.id.btn_generate_manually);
        addressNameTxt=(EditText) findViewById(R.id.edt_enter_address_name);
        terrainImg = (ImageView) findViewById(R.id.img_terrain_map);
        flatImg =(ImageView) findViewById(R.id.img_flat_map);
        satelliteImg = (ImageView) findViewById(R.id.img_sattelite_map);

        createCons.setVisibility(View.VISIBLE);
        sideSelectCons.setVisibility(View.VISIBLE);
        capturingLocationCons.setVisibility(View.GONE);
        locationCompleteCons.setVisibility(View.GONE);
        saveCons.setVisibility(View.GONE);


        propertyGroup=(RadioGroup) findViewById(R.id.group_property_type_create);
        institutionGroup=(RadioGroup) findViewById(R.id.group_institution_create);
        residenceGroup=(RadioGroup) findViewById(R.id.group_residence_create);

        institutionBtn=(RadioButton) findViewById(R.id.radio_institution_create);
        residenceBtn=(RadioButton) findViewById(R.id.radio_residential_create);

        addressScroll=(ScrollView) findViewById(R.id.scroll_address_type);
        propertyTypeTxt=(TextView) findViewById(R.id.txt_address_type_create);
        changeTypeTxt=(TextView) findViewById(R.id.txt_change_property_type_create);

        rightBtn=(Button) findViewById(R.id.btn_right_create);
        leftBtn=(Button) findViewById(R.id.btn_left_create);
        generateBtn=(Button) findViewById(R.id.btn_generate_create);
        cancelBtn=(Button) findViewById(R.id.btn_cancel_create);
        saveBtn=(Button) findViewById(R.id.btn_save_create);
        updateBtn=(Button) findViewById(R.id.btn_update_map_coordinates_create);
        cancelUpdateBtn=(Button) findViewById(R.id.btn_cancel_update_map_coordinates);
        countyTxt=(TextView) findViewById(R.id.txt_county_create);
        regionTxt=(TextView) findViewById(R.id.txt_region_create);
        roadTxt=(TextView) findViewById(R.id.txt_road_create);
        addressTxt=(TextView) findViewById(R.id.txt_address_create);
        coordinatesTxt=(TextView) findViewById(R.id.txt_captured_location);
        roadListTxt=(TextView) findViewById(R.id.txt_road_create_list);
        counterTxt=(TextView) findViewById(R.id.txt_counter_create);
        mapCoordinatesTxt=(TextView) findViewById(R.id.txt_map_coordinates_create);
        backImg=(ImageView) findViewById(R.id.img_back);

        gapTxt=(TextView) findViewById(R.id.txt_gap_create);
        accuracyTxt=(TextView) findViewById(R.id.txt_accuracy_create);
        lapseTxt=(TextView) findViewById(R.id.txt_lapse_create);

        gpsSearchImg = (ImageView) findViewById(R.id.img_location_searching);
        locationCapturedImg = (ImageView) findViewById(R.id.img_location_captured);

        animSequence = AnimationUtils.loadAnimation(CreateActivity.this,R.anim.rotate );
        gpsSearchImg.startAnimation(animSequence);

        mSetRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(CreateActivity.this, R.animator.out_animation);
        mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(CreateActivity.this, R.animator.in_animation);
        xRotate = (AnimatorSet) AnimatorInflater.loadAnimator(CreateActivity.this, R.animator.horizontal_rotate);

        xRotate.setTarget(listImg);
        xRotate.start();

        leftBtn.setEnabled(false);
        rightBtn.setEnabled(false);

        context = getApplicationContext();
        setUpGClient();

        categorySpin.setOnItemSelectedListener(this);
        ArrayAdapter cty = new ArrayAdapter(CreateActivity.this, android.R.layout.simple_spinner_item, category);
        cty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpin.setAdapter(cty);

        Bundle loca=getIntent().getExtras();
        if (loca != null) {
            countyTxt.setText(loca.getCharSequence("county"));
            regionTxt.setText (loca.getCharSequence("region"));
            roadTxt.setText(loca.getCharSequence("road"));
            roadListTxt.setText(loca.getCharSequence("road"));
        }
        CheckPrevious();

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateActivity.this, HomeActivity.class));
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateBtn.setVisibility(View.VISIBLE);
                generateManualBtn.setVisibility(View.VISIBLE);

                updateBtn.setVisibility(View.GONE);
                cancelUpdateBtn.setVisibility(View.GONE);

                Latitude=upLat;
                Longitude=upLong;
                absoluteLat=String.valueOf(Double.parseDouble(upLat)+100);
            }
        });

        cancelUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetMarker();
                generateBtn.setVisibility(View.VISIBLE);
                generateManualBtn.setVisibility(View.VISIBLE);
                updateBtn.setVisibility(View.GONE);
                cancelUpdateBtn.setVisibility(View.GONE);
            }
        });

        changeTypeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addressScroll.setVisibility(View.VISIBLE);
                createCons.setVisibility(View.GONE);
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
                        createCons.setVisibility(View.VISIBLE);

                        break;

                    case R.id.radio_religious_institution_create:
                        institutionGroup.clearCheck();
                        residenceGroup.clearCheck();
                        propertyTypeTxt.setText("Religious Institution");
                        institutionGroup.setVisibility(View.GONE);
                        institutionGroup.clearCheck();
                        addressScroll.setVisibility(View.GONE);
                        createCons.setVisibility(View.VISIBLE);
                        break;

                    case R.id.radio_business_create:
                        institutionGroup.clearCheck();
                        residenceGroup.clearCheck();
                        propertyTypeTxt.setText("Business/ Office Premises");
                        institutionGroup.setVisibility(View.GONE);
                        institutionGroup.clearCheck();
                        addressScroll.setVisibility(View.GONE);
                        createCons.setVisibility(View.VISIBLE);
                        break;

                    case R.id.radio_other_create:
                        institutionGroup.clearCheck();
                        residenceGroup.clearCheck();
                        propertyTypeTxt.setText("Other Address Type");
                        institutionGroup.setVisibility(View.GONE);
                        institutionGroup.clearCheck();
                        addressScroll.setVisibility(View.GONE);
                        createCons.setVisibility(View.VISIBLE);
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
                        createCons.setVisibility(View.VISIBLE);
                        break;

                    case R.id.radio_apartment_create:
                        propertyTypeTxt.setText("Apartment Building");
                        addressScroll.setVisibility(View.GONE);
                        residenceGroup.setVisibility(View.GONE);
                        createCons.setVisibility(View.VISIBLE);
                        break;

                    case R.id.radio_community_create:
                        propertyTypeTxt.setText("Community Setup");
                        addressScroll.setVisibility(View.GONE);
                        residenceGroup.setVisibility(View.GONE);
                        createCons.setVisibility(View.VISIBLE);
                        break;

                    case R.id.radio_informal_settlement_create:
                        propertyTypeTxt.setText("Informal Settlement");
                        addressScroll.setVisibility(View.GONE);
                        residenceGroup.setVisibility(View.GONE);
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
                        addressScroll.setVisibility(View.GONE);
                        residenceGroup.setVisibility(View.GONE);
                        createCons.setVisibility(View.VISIBLE);
                        break;

                    case R.id.radio_primary_create:
                        propertyTypeTxt.setText("Primary School");
                        addressScroll.setVisibility(View.GONE);
                        residenceGroup.setVisibility(View.GONE);
                        createCons.setVisibility(View.VISIBLE);
                        break;

                    case R.id.radio_secondary_create:
                        propertyTypeTxt.setText("Secondary School");
                        addressScroll.setVisibility(View.GONE);
                        residenceGroup.setVisibility(View.GONE);
                        createCons.setVisibility(View.VISIBLE);
                        break;

                    case R.id.radio_college_institution:
                        propertyTypeTxt.setText("College or University");
                        addressScroll.setVisibility(View.GONE);
                        residenceGroup.setVisibility(View.GONE);
                        createCons.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (googleApiClient==null) {
                    setUpGClient();
                }

                googleApiClient.connect();
                getMyLocation();
                StartTimer();

                createCons.setVisibility(View.VISIBLE);
                sideSelectCons.setVisibility(View.GONE);
                capturingLocationCons.setVisibility(View.VISIBLE);
                locationCompleteCons.setVisibility(View.GONE);
                //addressCapatureSaveCons.setVisibility(View.GONE);
                saveCons.setVisibility(View.GONE);

                mCounterL = 2;
                if(previousCounterL == 0){
                    addressNumber= 9 + mCounterL;
                }else{
                    addressNumber = previousCounterL + 2;
                }

                distanceListRe.clear();
                coordinatesTxt.addTextChangedListener(textwatcher);
            }
        });

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (googleApiClient==null) {
                    setUpGClient();
                }
                googleApiClient.connect();
                getMyLocation();
                StartTimer();

                createCons.setVisibility(View.VISIBLE);
                sideSelectCons.setVisibility(View.GONE);
                capturingLocationCons.setVisibility(View.VISIBLE);
                locationCompleteCons.setVisibility(View.GONE);
                //addressCapatureSaveCons.setVisibility(View.GONE);
                saveCons.setVisibility(View.GONE);

                mCounterR = 2;
                if (previousCounterR == 0){
                    addressNumber = 8 + mCounterR;
                }else{
                    addressNumber = previousCounterR + 2;
                }

                distanceListRe.clear();
                coordinatesTxt.addTextChangedListener(textwatcher);
            }
        });
        generateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullAddress = Integer.toString(addressNumber) + " " + roadTxt.getText().toString() + ", " +
                        regionTxt.getText().toString() + ", " + countyTxt.getText().toString();
                addressTxt.setText(fullAddress);

                createCons.setVisibility(View.VISIBLE);
                sideSelectCons.setVisibility(View.GONE);
                capturingLocationCons.setVisibility(View.GONE);
                //addressCapatureSaveCons.setVisibility(View.VISIBLE);
                locationCompleteCons.setVisibility(View.GONE);
                saveCons.setVisibility(View.VISIBLE);

            }
        });

        manualBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manualCons.setVisibility(View.VISIBLE);
            }
        });

        generateManualBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(enterNumberTxt.getText().toString().isEmpty()){
                    Toast.makeText(CreateActivity.this, "Enter Address", Toast.LENGTH_LONG).show();
                }else {

                    if (list.contains(enterNumberTxt.getText().toString())){
                        Toast.makeText(CreateActivity.this, "Address Number Exists", Toast.LENGTH_LONG).show();
                    }else{
                        addressNumber = Integer.parseInt(enterNumberTxt.getText().toString());
                        fullAddress = enterNumberTxt.getText().toString() + " " + roadTxt.getText().toString() + ", " +
                                regionTxt.getText().toString() + ", " + countyTxt.getText().toString();
                        addressTxt.setText(fullAddress);

                        createCons.setVisibility(View.VISIBLE);
                        sideSelectCons.setVisibility(View.GONE);
                        capturingLocationCons.setVisibility(View.GONE);
                        locationCompleteCons.setVisibility(View.GONE);
                        saveCons.setVisibility(View.VISIBLE);
                        manualCons.setVisibility(View.GONE);
                    }
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(propertyTypeTxt.getText().toString().contentEquals("---Select---")||
                   propertyTypeTxt.getText().toString().isEmpty()){
                        Toast.makeText(CreateActivity.this, "Select Property Type", Toast.LENGTH_LONG).show();

                }else{
                    if (!propertyTypeTxt.getText().toString().contentEquals("finance")||
                            !propertyTypeTxt.getText().toString().contentEquals("residence")){

                        if(addressNameTxt.getText().toString().isEmpty()){
                            Toast.makeText(CreateActivity.this, "Add Property Name!", Toast.LENGTH_LONG).show();
                        }else{
                            addressName=addressNameTxt.getText().toString();
                            PostData();

                            counter=0;
                            createCons.setVisibility(View.VISIBLE);
                            sideSelectCons.setVisibility(View.VISIBLE);
                            capturingLocationCons.setVisibility(View.GONE);
                            //addressCapatureSaveCons.setVisibility(View.GONE);
                            locationCompleteCons.setVisibility(View.GONE);
                            saveCons.setVisibility(View.GONE);
                            mapCons.setVisibility(View.GONE);
                        }


                    }else{
                        if(addressNameTxt.getText().toString().isEmpty()){
                            addressName="No Name";
                            PostData();
                        }else{
                            addressName=addressNameTxt.getText().toString();
                            PostData();
                        }

                        counter=0;
                        createCons.setVisibility(View.VISIBLE);
                        sideSelectCons.setVisibility(View.VISIBLE);
                        capturingLocationCons.setVisibility(View.GONE);
                        //addressCapatureSaveCons.setVisibility(View.GONE);
                        locationCompleteCons.setVisibility(View.GONE);
                        saveCons.setVisibility(View.GONE);
                        mapCons.setVisibility(View.GONE);

                    }
                }

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addressTxt.setText("Address");

                createCons.setVisibility(View.VISIBLE);
                sideSelectCons.setVisibility(View.VISIBLE);
                capturingLocationCons.setVisibility(View.GONE);
                //addressCapatureSaveCons.setVisibility(View.GONE);
                locationCompleteCons.setVisibility(View.GONE);
                saveCons.setVisibility(View.GONE);
                mapCons.setVisibility(View.GONE);

                //addressNameTxt.setVisibility(View.GONE);

            }
        });

        textwatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                counter++;

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
                    gapTxt.setText(String.format("%.2f", distanceInMeters));
                    //gapTxt.setText(String.valueOf(distanceInMeters));

                    //String disT=String.format("%.2f", String.valueOf(distanceInMeters));
                    String disT=String.valueOf(distanceInMeters);
                    Double disD=Double.parseDouble(disT);

                    if (disD>9){
                        accuracyTxt.setText(String.valueOf(10));
                    }else if(disD>6&&disD<9){
                        accuracyTxt.setText(String.valueOf(20));
                    } else if(disD>3&& disD<6){
                        accuracyTxt.setText(String.valueOf(25));
                    }else if(disD>1&& disD<3){
                        accuracyTxt.setText(String.valueOf(30));
                    }else if(disD>0.5&& disD<1){
                        accuracyTxt.setText(String.valueOf(40));
                    }else if(disD<0.5){
                        Double acc=100*(1-disD);
                        accuracyTxt.setText(String.format("%.0f", acc));
                    }

//Add the coordinates storage list
                    HashMap<String, String> map = new HashMap<>();
                    map.put("latitude", String.valueOf(Double.valueOf(lat1)));
                    map.put("longitude", String.valueOf(Double.valueOf(lon1)));
                    map.put("distance", String.valueOf(distanceInMeters));
                    distanceListRe.add(map);

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

                            coordinatesTxt.removeTextChangedListener(textwatcher);
                            lisLat.clear();
                            lisLon.clear();
                            listDistance.clear();

//Coordinates to be set as address point

                            Latitude = distanceListRe.get(distanceListRe.size()-2).get("latitude");
                            Longitude = distanceListRe.get(distanceListRe.size()-2).get("longitude");

                            absoluteLat = String.valueOf(Double.parseDouble(distanceListRe.get(distanceListRe.size()-2).get("latitude"))+ 100);
                            mapCoordinatesTxt.setText(Latitude+":   " +Longitude);
                            createCons.setVisibility(View.VISIBLE);
                            sideSelectCons.setVisibility(View.GONE);
                            capturingLocationCons.setVisibility(View.GONE);
                            //addressCapatureSaveCons.setVisibility(View.VISIBLE);
                            locationCompleteCons.setVisibility(View.VISIBLE);
                            saveCons.setVisibility(View.GONE);
                            locCount++;
                            if(locCount==1){
                                LocationCheck();
                            }

//Viewing the point on map
                            selDis = Double.parseDouble(distanceListRe.get(distanceListRe.size()-2).get("distance"));
                            selLat=Double.parseDouble(Latitude);
                            selLon=Double.parseDouble(Longitude);
                            mapCons.setVisibility(View.VISIBLE);
                            SetMarker();
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
        googleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        getMyLocation();
        //googleApiClient.connect();

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
        LatLng building = new LatLng(selLat, selLon);
        mMap.addMarker(new MarkerOptions().position(building).title(String.valueOf(selDis)).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(building));


        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                Log.e("PlaceLL", String.valueOf(marker.getPosition().latitude) + ":  " +
                        String.valueOf(marker.getPosition().longitude));

                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude)));
                generateBtn.setVisibility(View.GONE);
                generateManualBtn.setVisibility(View.GONE);

                updateBtn.setVisibility(View.VISIBLE);
                cancelUpdateBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Log.e("PlaceLL", String.valueOf(marker.getPosition().latitude) + ":  " +
                        String.valueOf(marker.getPosition().longitude));

                //absoluteLat= String.valueOf(marker.getPosition().latitude + 100);
                upLat= String.format("%.8f", marker.getPosition().latitude);
                upLong= String.format("%.8f", marker.getPosition().longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude)));
                mapCoordinatesTxt.setText(upLat + ":   " + upLong);
            }
        });

    }

    private void CheckPrevious (){
        db.collection("address").whereEqualTo("county", countyTxt.getText().toString())
                .whereEqualTo("region", regionTxt.getText().toString())
                .whereEqualTo("road", roadTxt.getText().toString())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                listAddress = new ArrayList<>();
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        miss = document.toObject(Address.class);
                        listAddress.add(miss);
                    }

                    if(listAddress.size()>0) {

                        for (int i = 0; i < listAddress.size(); i++) {
                            list.add(listAddress.get(i).getNumber());

                            for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
                                Integer lEven = Integer.valueOf(iterator.next());
                                if (lEven % 2 == 0) {
                                    //System.out.println("This is Even Number: " + lEven);
                                    iterator.remove();
                                }
                            }
                            Collections.sort(list);
                            //Collections.reverse(list);
                            if(list.size()!=0) {
                                previousCounterL = Integer.parseInt(list.get(list.size()-1));
                            }

                            addressListR = (ListView) findViewById(R.id.list_address_right);
                            ArrayAdapter<String> leftList= new ArrayAdapter(CreateActivity.this, android.R.layout.simple_list_item_1,list);
                            addressListR.setAdapter(leftList);
                        }

                        for (int i = 0; i < listAddress.size(); i++) {
                            listR.add(listAddress.get(i).getNumber());

                            for (Iterator<String> iterator = listR.iterator(); iterator.hasNext();) {
                                Integer lEven = Integer.valueOf(iterator.next());
                                if (lEven % 2 != 0) {
                                    iterator.remove();
                                }
                            }
                            Collections.sort(listR);
                            //Collections.reverse(listR);
                            if(listR.size()!=0) {
                                previousCounterR = Integer.parseInt(listR.get(listR.size()-1));
                            }

                            addressList = (ListView) findViewById(R.id.list_address_left);
                            ArrayAdapter<String> leftList= new ArrayAdapter(CreateActivity.this, android.R.layout.simple_list_item_1,listR);
                            addressList.setAdapter(leftList);
                        }
                    }else{
                        previousCounterR = 0;
                    }
                    leftBtn.setEnabled(true);
                    rightBtn.setEnabled(true);
                } else {
                    Toast.makeText(CreateActivity.this, "Error loading document", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void PostData() {

        Date currentDate = new Date();
        Map<String, Object> user = new HashMap<>();
        user.put("officer", myid);
        user.put("supervisor", Supervisor);
        user.put("county", countyTxt.getText().toString());
        user.put("region", regionTxt.getText().toString());
        user.put("road", roadTxt.getText().toString());
        user.put("number", Integer.toString(addressNumber));
        user.put("latitude", absoluteLat);
        user.put("longitude", Longitude);
        user.put("fulladdress", fullAddress);
        user.put("category", propertyCategory);
        user.put("addresstype", propertyTypeTxt.getText().toString());
        user.put("timecreated", currentDate);
        user.put("addressname", addressName);
        user.put("verificationstatus", "Not Verified");
        user.put("activestatus", "Active");


        // Add a new document with a generated ID

        db.collection("address")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(CreateActivity.this, "Address Saved", Toast.LENGTH_LONG).show();

                        Bundle params = new Bundle();
                        params.putString("user_id", myid);
                        mFirebaseAnalytics.logEvent("create_address", params);

                        Intent i = new Intent(CreateActivity.this, CreateActivity.class);
                        Bundle location = new Bundle();
                        location.putString("county", countyTxt.getText().toString());
                        location.putString("region", regionTxt.getText().toString());
                        location.putString("road", roadTxt.getText().toString());

                        i.putExtras(location);
                        startActivity(i);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateActivity.this, "Error adding document", Toast.LENGTH_LONG).show();
                    }
                });

        db.collection("address")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                            }
                        } else {
                            Toast.makeText(CreateActivity.this, "Error adding document", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onLocationChanged(Location location) {

        mylocation = location;
        if (mylocation != null) {
            Double latitude1 = mylocation.getLatitude();

            coordinatesTxt.setText(Double.toString(latitude1));
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
        int permissionLocation = ContextCompat.checkSelfPermission(CreateActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }
    }


    private void getMyLocation(){
        if(googleApiClient!=null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(CreateActivity.this,
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
                                            .checkSelfPermission(CreateActivity.this,
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
                                        status.startResolutionForResult(CreateActivity.this,
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
        int permissionLocation = ContextCompat.checkSelfPermission(CreateActivity.this,
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
                    Intent i = new Intent(CreateActivity.this, MainActivity.class);
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
                Intent i = new Intent(CreateActivity.this, MainActivity.class);
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
            user.put("county", countyTxt.getText().toString());
            user.put("region", regionTxt.getText().toString());
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
                            Intent i = new Intent(CreateActivity.this, MainActivity.class);
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        propertyCategory = String.valueOf(categorySpin.getSelectedItem());

        if(!propertyCategory.isEmpty()){

            if(propertyCategory.contentEquals("Residence")) {
                selCat=residence;
                subcategoryCons.setVisibility(View.VISIBLE);
            }else if(propertyCategory.contentEquals("Finance")){
                selCat=finance;
                subcategoryCons.setVisibility(View.VISIBLE);
            }else if(propertyCategory.contentEquals("Government Office")){
                selCat=government;
                subcategoryCons.setVisibility(View.VISIBLE);
            }else if(propertyCategory.contentEquals("Education")){
                selCat=education;
                subcategoryCons.setVisibility(View.VISIBLE);
            }else if(propertyCategory.contentEquals("Hospitals")){
                selCat=hospitals;
                subcategoryCons.setVisibility(View.VISIBLE);
            }else if(propertyCategory.contentEquals("Religion")){
                selCat=religion;
                subcategoryCons.setVisibility(View.VISIBLE);
            }else if(propertyCategory.contentEquals("Transport")){
                selCat=transport;
                subcategoryCons.setVisibility(View.VISIBLE);
            }else if(propertyCategory.contentEquals("Other")){
                selCat=other;
                subcategoryCons.setVisibility(View.VISIBLE);
            }else{
                subcategoryCons.setVisibility(View.GONE);
            }

            ArrayAdapter scsc = new ArrayAdapter(CreateActivity.this, android.R.layout.simple_spinner_item, selCat);
            scsc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            subCategorySpin.setAdapter(scsc);

            subCategorySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    propertyType = String.valueOf(subCategorySpin.getSelectedItem());
                    if(!propertyType.contentEquals("---Select---")) {
                        propertyTypeTxt.setText(propertyType);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void StartTimer(){
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date currentDate = new Date();
                    long diff = currentDate.getTime()- startTimer.getTime();
                    long days = diff / (24 * 60 * 60 * 1000);
                    diff -= days * (24 * 60 * 60 * 1000);
                    long hours = diff / (60 * 60 * 1000);
                    diff -= hours * (60 * 60 * 1000);
                    long minutes = diff / (60 * 1000);
                    diff -= minutes * (60 * 1000);
                    long seconds = diff / 1000;
                    lapseTxt.setText(String.format("%02d", minutes)+ ": " + String.format("%02d", seconds));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 1 * 1000);
    }
}
