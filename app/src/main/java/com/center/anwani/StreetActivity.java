package com.center.anwani;

import android.Manifest;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentReference;
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

public class StreetActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String name1 = "nameKey";
    public static final String supervisor1 = "supervisorKey";
    public static final String phoneNumber1 = "phoneKey";
    public static final String Email1 = "emailKey";
    public static final String county1 = "countyKey";
    public static final String region1 = "regionKey";
    public static final String role1 = "roleKey";
    public static final String status1 = "statusKey";
    public static final String myid1 = "myidKey";

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


    TextView countyStreetTxt,regionStreetTxt, addStreetTxt, editStreetTxt, streetCoordiantesTxt, addEntryLatTxt, addEntryLongTxt,
            searchStreetTxt;
    EditText addStreetNameEdt, addStreetFromEdt, streetSearchEdt, streetNameEditEdt, entryStreetEditEdt, exitStreetEditEdt;
    Button addSaveBtn, captureEntryGpsAddBtn, addExitStreetAddBtn, recaptureEntryGpsAddBtn, addCancelBtn, editEntryStreetBtn,
            editExitStreetBtn, saveStreetEditBtn, cancelStreetEditBtn, recaptureStreetLocationBtn;
    ConstraintLayout locationCompleteCons, addStreetCons, searchStreetCons, editStreetCons, streetDetailsEditCons,
                    startStreetEdtCons, exitStreetEdtCons, editStreetLocationCompleteCons;
    String County, Region, countyId, regionId, streetId, Road, fromRoad, exitRoad;

    String Latitude, Longitude, absoluteLat;

    private Location mylocation;
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    LocationRequest locationRequest;
    Context context;
    private ProgressDialog pDialog, startDialog;

    Double lat1, lat2, lon1, lon2;
    List<String> listDistance;
    ArrayList<String> listAllDistance, listReDistance, list,listR;
    List<String> list1;
    List<String> list2;
    List<String> list3;
    ArrayList<String> list4;
    ArrayList<Double> lisLat, resultLat, latitudeList;
    ArrayList<Double> lisLon, resultLong, longitudeList;
    Double diffMax;
    int mCounter;

    Animation animation;
    Animation animSequence;
    private AnimatorSet mSetRightOut, mSetLeftIn, xRotate;
    ImageView gpsSearchImg, gpsOffimg, locationCapturedImg, listImg, backImg;
    private FirebaseFirestore db;

    ArrayList<Street> listStreet;
    String Phone, Email, Name, Supervisor, myCounty, myRegion, myRole, myStatus, addressCountyId, myid;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        Email = sharedpreferences.getString(Email1,"");
        Phone = sharedpreferences.getString(phoneNumber1,"");
        Name = sharedpreferences.getString(name1,"");
        Supervisor = sharedpreferences.getString(supervisor1,"");
        myCounty = sharedpreferences.getString(county1,"");
        myRegion = sharedpreferences.getString(region1,"");
        myRole = sharedpreferences.getString(role1,"");
        myStatus = sharedpreferences.getString(status1,"");
        myid = sharedpreferences.getString(myid1,"");

        endTime = sharedpreferences.getString(endtime1,"");
        startTime = sharedpreferences.getString(starttime1,"");
        regionStatus = sharedpreferences.getString(regionstatus1,"");

        cLat = sharedpreferences.getString(clat1,"");
        cLong = sharedpreferences.getString(clong1,"");
        Radius = sharedpreferences.getString(radius1,"");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(StreetActivity.this);
        InitialCheck();

        setContentView(R.layout.activity_street);

        db = FirebaseFirestore.getInstance();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_more_vert_white_24dp);
        toolbar.setOverflowIcon(drawable);

        pDialog = new ProgressDialog(StreetActivity.this, R.style.mydialog);
        startDialog= new ProgressDialog(StreetActivity.this, R.style.mydialog);

        countyStreetTxt=(TextView) findViewById(R.id.txt_county_street);
        regionStreetTxt=(TextView) findViewById(R.id.txt_region_street);
        addStreetTxt=(TextView) findViewById(R.id.txt_add_street_street);
        editStreetTxt=(TextView) findViewById(R.id.txt_edit_street_street);
        streetCoordiantesTxt=(TextView) findViewById(R.id.txt_coordinates_street_start);
        addStreetNameEdt=(EditText) findViewById(R.id.edt_street_name_add);
        addStreetFromEdt=(EditText) findViewById(R.id.edt_street_from_add);
        addSaveBtn=(Button) findViewById(R.id.btn_save_street_add);
        addCancelBtn=(Button) findViewById(R.id.btn_cancel_street_add);
        captureEntryGpsAddBtn=(Button) findViewById(R.id.btn_capture_entry_gps_add);
        recaptureEntryGpsAddBtn=(Button) findViewById(R.id.btn_recapture_entry_gps_add);
        addExitStreetAddBtn =(Button) findViewById(R.id.btn_add_exit_add);

        locationCompleteCons=(ConstraintLayout) findViewById(R.id.constraint_capturing_complete_add);
        addStreetCons=(ConstraintLayout) findViewById(R.id.cons_add_street_street);

        searchStreetCons=(ConstraintLayout) findViewById(R.id.cons_search_street_edit);
        streetSearchEdt=(EditText) findViewById(R.id.edt_search_street_edt);
        searchStreetTxt=(TextView) findViewById(R.id.txt_search_street_edit);

        editStreetCons=(ConstraintLayout) findViewById(R.id.cons_edit_street_street);
        streetDetailsEditCons =(ConstraintLayout) findViewById(R.id.cons_street_details_edit);
        startStreetEdtCons=(ConstraintLayout) findViewById(R.id.cons_start_street_edit);
        exitStreetEdtCons=(ConstraintLayout) findViewById(R.id.cons_exit_street_edit);
        editStreetLocationCompleteCons=(ConstraintLayout) findViewById(R.id.cons_edit_street_location_edit);
        streetNameEditEdt=(EditText) findViewById(R.id.edt_street_name_edit);
        entryStreetEditEdt=(EditText) findViewById(R.id.edt_start_street_edit);
        exitStreetEditEdt=(EditText) findViewById(R.id.edt_exit_street_edit);

        editEntryStreetBtn=(Button) findViewById(R.id.btn_edit_street_start_edit);
        editExitStreetBtn=(Button) findViewById(R.id.btn_edit_exit_street_edit);
        saveStreetEditBtn=(Button) findViewById(R.id.btn_save_street_edit);
        cancelStreetEditBtn=(Button) findViewById(R.id.btn_cancel_street_edit);
        recaptureStreetLocationBtn=(Button) findViewById(R.id.btn_recapture_street_location_edit);

        startDialog.setMessage("Loading. Please wait...");
        startDialog.setIndeterminate(false);
        startDialog.setCancelable(true);
        startDialog.show();


        if(myRole.contentEquals("Admin")){
            Bundle loca=getIntent().getExtras();
            if (loca != null) {
                countyStreetTxt.setText(loca.getCharSequence("county"));
                County=String.valueOf(loca.getCharSequence("county"));
                regionStreetTxt.setText (loca.getCharSequence("region"));
                Region=String.valueOf(loca.getCharSequence("region"));
                countyId=String.valueOf(loca.getCharSequence("countyid"));
                getRegionId();
            }

        } else if(myRole.contentEquals("Street Data Agent")){
            Bundle loca=getIntent().getExtras();
            if (loca != null) {

                countyStreetTxt.setText(loca.getCharSequence("county"));
                County=String.valueOf(loca.getCharSequence("county"));
                regionStreetTxt.setText (loca.getCharSequence("region"));
                Region=String.valueOf(loca.getCharSequence("region"));

                myCounty = sharedpreferences.getString(county1,"");
                countyStreetTxt.setText(myCounty);
                myRegion = sharedpreferences.getString(region1,"");
                regionStreetTxt.setText(myRegion);
                GetCountyId();
            }

        } else {
        }

        context = getApplicationContext();
        listStreet = new ArrayList<Street>();

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

        gpsSearchImg = (ImageView) findViewById(R.id.img_location_searching);
        locationCapturedImg = (ImageView) findViewById(R.id.img_location_captured);

        editStreetCons.setVisibility(View.GONE);
        addStreetCons.setVisibility(View.VISIBLE);

        addStreetTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addStreetTxt.setBackgroundResource(R.color.White);
                editStreetTxt.setBackgroundResource(R.color.blue);
                addStreetTxt.setTextColor(getResources().getColor(R.color.colourFive));
                editStreetTxt.setTextColor(getResources().getColor(R.color.White));

                editStreetCons.setVisibility(View.GONE);
                addStreetCons.setVisibility(View.VISIBLE);
            }
        });

        editStreetTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editStreetTxt.setBackgroundResource(R.color.White);
                addStreetTxt.setBackgroundResource(R.color.blue);
                addStreetTxt.setTextColor(getResources().getColor(R.color.White));
                editStreetTxt.setTextColor(getResources().getColor(R.color.colourFive));

                editStreetCons.setVisibility(View.VISIBLE);
                addStreetCons.setVisibility(View.GONE);
            }
        });

        searchStreetTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!streetSearchEdt.getText().toString().isEmpty()) {
                    db.collection("counties").document(countyId).collection("regions")
                            .document(regionId).collection("roads")
                            .whereEqualTo("road", streetSearchEdt.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Street street;
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            street=document.toObject(Street.class);
                                            streetId = document.getId();
                                            listStreet.add(street);

                                            Road=listStreet.get(0).getRoad();
                                            fromRoad=listStreet.get(0).getFromroad();
                                            exitRoad = listStreet.get(0).getExitroad();

                                            streetNameEditEdt.setText(Road);
                                            exitStreetEditEdt.setText(exitRoad);
                                            entryStreetEditEdt.setText(fromRoad);

                                            searchStreetCons.setVisibility(View.GONE);
                                            streetDetailsEditCons.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            });
                }else{
                    Toast.makeText(StreetActivity.this, "Add the street to search", Toast.LENGTH_LONG).show();
                }
            }
        });

        editEntryStreetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (googleApiClient==null) {
                    setUpGClient();
                }
                googleApiClient.connect();
                getMyLocation();

                editStreetLocationCompleteCons.setVisibility(View.GONE);
                locationCompleteCons.setVisibility(View.GONE);

                //Make edit location buttons view Gone. To be visible only after clicking save or cancel.
                editEntryStreetBtn.setVisibility(View.GONE);
                editExitStreetBtn.setVisibility(View.GONE);

                exitStreetEdtCons.setVisibility(View.GONE);
                pDialog.setMessage("Capturing your location. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }
        });

        editExitStreetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (googleApiClient==null) {
                    setUpGClient();
                }
                googleApiClient.connect();
                getMyLocation();

                editStreetLocationCompleteCons.setVisibility(View.GONE);
                locationCompleteCons.setVisibility(View.GONE);

                //Make edit location buttons view Gone. To be visible only after clicking save or cancel.
                editEntryStreetBtn.setVisibility(View.GONE);
                editExitStreetBtn.setVisibility(View.GONE);

                startStreetEdtCons.setVisibility(View.GONE);
                pDialog.setMessage("Capturing your location. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }
        });

        saveStreetEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //We are determining what to update based on visibility of the exit and entry cons
                //if visibility of one has been affected, then location has been edited

                if (startStreetEdtCons.getVisibility()==View.VISIBLE&& exitStreetEdtCons.getVisibility()==View.GONE) {
                    Date currentDate = new Date();
                    Map<String, Object> street = new HashMap<>();

                    street.put("road", streetNameEditEdt.getText().toString());
                    street.put("fromroad", entryStreetEditEdt.getText().toString());
                    street.put("exitroad", exitStreetEditEdt.getText().toString());
                    street.put("entrylatitude", absoluteLat);
                    street.put("entrylongitude", Longitude);
                    street.put("officer", myid);
                    street.put("supervisor", Supervisor);
                    street.put("timeupdated", currentDate);

                    db.collection("counties").document(countyId).collection("regions")
                            .document(regionId).collection("roads").document(streetId)
                            .set(street, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Bundle params = new Bundle();
                                    params.putString("user_id", myid);
                                    mFirebaseAnalytics.logEvent("create_street_start", params);

                                    //Reset the whole page if the update is successful including the search list
                                    editStreetLocationCompleteCons.setVisibility(View.GONE);
                                    locationCompleteCons.setVisibility(View.GONE);
                                    startStreetEdtCons.setVisibility(View.VISIBLE);
                                    exitStreetEdtCons.setVisibility(View.VISIBLE);

                                    editEntryStreetBtn.setVisibility(View.GONE);
                                    editExitStreetBtn.setVisibility(View.GONE);

                                    searchStreetCons.setVisibility(View.VISIBLE);
                                    streetSearchEdt.setText("");
                                    streetDetailsEditCons.setVisibility(View.GONE);
                                    listStreet.clear();
                                    streetId="";

                                    //Make edit location buttons visible
                                    editEntryStreetBtn.setVisibility(View.VISIBLE);
                                    editExitStreetBtn.setVisibility(View.VISIBLE);

                                    Toast.makeText(StreetActivity.this, "Street Updated Successfully", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(StreetActivity.this, "Street Update Failed", Toast.LENGTH_LONG).show();
                                }
                            });

                }else if(startStreetEdtCons.getVisibility()==View.GONE && exitStreetEdtCons.getVisibility()==View.VISIBLE){
                    Date currentDate = new Date();
                    Map<String, Object> street = new HashMap<>();

                    street.put("road", streetNameEditEdt.getText().toString());
                    street.put("fromroad", entryStreetEditEdt.getText().toString());
                    street.put("exitroad", exitStreetEditEdt.getText().toString());
                    street.put("exitlatitude", absoluteLat);
                    street.put("exitlongitude", Longitude);
                    street.put("officer", myid);
                    street.put("supervisor", Supervisor);
                    street.put("timeupdated", currentDate);

                    db.collection("counties").document(countyId).collection("regions")
                            .document(regionId).collection("roads").document(streetId)
                            .set(street, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Bundle params = new Bundle();
                                    params.putString("user_id", myid);
                                    mFirebaseAnalytics.logEvent("create_street_end", params);

                                    editStreetLocationCompleteCons.setVisibility(View.GONE);
                                    locationCompleteCons.setVisibility(View.GONE);
                                    startStreetEdtCons.setVisibility(View.VISIBLE);
                                    exitStreetEdtCons.setVisibility(View.VISIBLE);

                                    searchStreetCons.setVisibility(View.VISIBLE);
                                    streetSearchEdt.setText("");
                                    streetDetailsEditCons.setVisibility(View.GONE);
                                    listStreet.clear();
                                    streetId="";

                                    editEntryStreetBtn.setVisibility(View.VISIBLE);
                                    editExitStreetBtn.setVisibility(View.VISIBLE);

                                    Toast.makeText(StreetActivity.this, "Street Updated Successfully", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(StreetActivity.this, "Street Update Failed", Toast.LENGTH_LONG).show();
                                }
                            });

                }else if(startStreetEdtCons.getVisibility()==View.VISIBLE && exitStreetEdtCons.getVisibility()==View.VISIBLE){
                    Date currentDate = new Date();
                    Map<String, Object> street = new HashMap<>();

                    street.put("road", streetNameEditEdt.getText().toString());
                    street.put("fromroad", entryStreetEditEdt.getText().toString());
                    street.put("exitroad", exitStreetEditEdt.getText().toString());
                    street.put("officer", myid);
                    street.put("supervisor", Supervisor);
                    street.put("timeupdated", currentDate);

                    db.collection("counties").document(countyId).collection("regions")
                            .document(regionId).collection("roads").document(streetId)
                            .set(street, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //Reset the whole page if the update is successful including the search list
                                    editStreetLocationCompleteCons.setVisibility(View.GONE);
                                    locationCompleteCons.setVisibility(View.GONE);
                                    startStreetEdtCons.setVisibility(View.VISIBLE);
                                    exitStreetEdtCons.setVisibility(View.VISIBLE);

                                    searchStreetCons.setVisibility(View.VISIBLE);
                                    streetSearchEdt.setText("");
                                    streetDetailsEditCons.setVisibility(View.GONE);
                                    listStreet.clear();
                                    streetId="";

                                    editEntryStreetBtn.setVisibility(View.VISIBLE);
                                    editExitStreetBtn.setVisibility(View.VISIBLE);
                                    Toast.makeText(StreetActivity.this, "Street Updated Successfully", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(StreetActivity.this, "Street Update Failed", Toast.LENGTH_LONG).show();
                                }
                            });
                }else{

                }
            }
        });

        cancelStreetEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Take it back to previous form.
                editStreetLocationCompleteCons.setVisibility(View.GONE);
                locationCompleteCons.setVisibility(View.GONE);
                startStreetEdtCons.setVisibility(View.VISIBLE);
                exitStreetEdtCons.setVisibility(View.VISIBLE);

                //Make edit location buttons visible
                editEntryStreetBtn.setVisibility(View.VISIBLE);
                editExitStreetBtn.setVisibility(View.VISIBLE);
            }
        });

        recaptureStreetLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (googleApiClient==null) {
                    setUpGClient();
                }
                googleApiClient.connect();
                getMyLocation();

                editStreetLocationCompleteCons.setVisibility(View.GONE);
                pDialog.setMessage("Capturing your location. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }
        });

        addExitStreetAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editStreetTxt.setBackground(getResources().getDrawable(R.drawable.custom_clear_button_click));
                addStreetTxt.setBackground(getResources().getDrawable(R.drawable.custom_nutton_click));
                addStreetTxt.setTextColor(getResources().getColor(R.color.White));
                editStreetTxt.setTextColor(getResources().getColor(R.color.tabsBar));

                editStreetCons.setVisibility(View.VISIBLE);
                addStreetCons.setVisibility(View.GONE);
            }
        });

        captureEntryGpsAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (googleApiClient==null) {
                    setUpGClient();
                }
                googleApiClient.connect();
                getMyLocation();

                locationCompleteCons.setVisibility(View.GONE);
                editStreetLocationCompleteCons.setVisibility(View.GONE);
                pDialog.setMessage("Capturing your location. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();

            }
        });

        recaptureEntryGpsAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (googleApiClient==null) {
                    setUpGClient();
                }
                googleApiClient.connect();
                getMyLocation();

                locationCompleteCons.setVisibility(View.GONE);
                pDialog.setMessage("Capturing your location. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();

            }
        });

        addSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if (addStreetNameEdt != null && addStreetFromEdt != null) {

                        pDialog.setMessage("Saving. Please wait...");
                        pDialog.setIndeterminate(false);
                        pDialog.setCancelable(true);
                        pDialog.show();

                        Date currentDate = new Date();
                        Map<String, Object> user = new HashMap<>();
                        user.put("county", County);
                        user.put("region", Region);
                        user.put("road", addStreetNameEdt.getText().toString());
                        user.put("fromroad", addStreetFromEdt.getText().toString());
                        user.put("entrylatitude", absoluteLat);
                        user.put("entrylongitude", Longitude);
                        user.put("exitroad", "");
                        user.put("exitlatitude", "");
                        user.put("exitlongitude", "");
                        user.put("officer", myid);
                        user.put("supervisor", Supervisor);
                        user.put("timecreated", currentDate);
                        user.put("verificationstatus", "Not Verified");
                        user.put("verificationstatusex", "Not Verified");
                        user.put("activestatus", "Active");

                        db.collection("counties").document(countyId).collection("regions")
                                .document(regionId)
                                .collection("roads")
                                .add(user)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(StreetActivity.this, "Successful", Toast.LENGTH_LONG).show();
                                        pDialog.dismiss();

                                        Intent i = new Intent(StreetActivity.this, StreetActivity.class);
                                        Bundle location = new Bundle();
                                        location.putString("county", County);
                                        location.putString("countyid", countyId);
                                        location.putString("region", Region);
                                        location.putString("regionid", regionId);

                                        i.putExtras(location);
                                        startActivity(i);

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pDialog.dismiss();
                                        Toast.makeText(StreetActivity.this, "Error adding document", Toast.LENGTH_LONG).show();
                                    }
                                });

                    } else {
                        Toast.makeText(StreetActivity.this, "Add the missing field", Toast.LENGTH_LONG).show();
                    }
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
        //googleApiClient.connect();

    }

    @Override
    public void onLocationChanged(Location location) {

        mylocation = location;
        if (mylocation != null) {
            Double latitude = mylocation.getLatitude();

            streetCoordiantesTxt.setText(Double.toString(latitude));

            streetCoordiantesTxt.addTextChangedListener(new TextWatcher() {
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
                                    locationCompleteCons.setVisibility(View.VISIBLE);
                                    editStreetLocationCompleteCons.setVisibility(View.VISIBLE);
                                    pDialog.dismiss();
                                    locCount++;
                                    if(locCount==1){
                                        LocationCheck();
                                    }

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
        int permissionLocation = ContextCompat.checkSelfPermission(StreetActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }
    }


    private void getMyLocation(){
        if(googleApiClient!=null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(StreetActivity.this,
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
                                            .checkSelfPermission(StreetActivity.this,
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
                                        status.startResolutionForResult(StreetActivity.this,
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
        int permissionLocation = ContextCompat.checkSelfPermission(StreetActivity.this,
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
                                startDialog.dismiss();
                            }
                        }
                    }
                });
    }

    private void GetCountyId() {
        db.collection("counties").whereEqualTo("county", myCounty)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                countyId = document.getId();
                                getRegionId();
                                //startDialog.dismiss();
                            }
                        } else {
                            Toast.makeText(StreetActivity.this, "Get Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

            //startActivity(new Intent(HomeActivity.this, MainActivity.class));

            sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            editor = sharedpreferences.edit();
            editor.clear();
            editor.commit();
            startActivity(new Intent(StreetActivity.this, LoginActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();
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
                    Intent i = new Intent(StreetActivity.this, MainActivity.class);
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
                Intent i = new Intent(StreetActivity.this, MainActivity.class);
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
                            Intent i = new Intent(StreetActivity.this, MainActivity.class);
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
