package com.center.anwani;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.center.anwani.Helper.CheckNetWorkStatus;
import com.center.anwani.Helper.HttpJsonParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ViewActivity extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener,
        SearchView.OnQueryTextListener{

    public static final String name1 = "nameKey";
    public static final String supervisor1 = "supervisorKey";
    public static final String phoneNumber1 = "phoneKey";
    public static final String Email1 = "emailKey";
    public static final String myid1 = "myidKey";
    private final String COLLECTION_KEY = "address";
    public static final String county1 = "countyKey";
    public static final String region1 = "regionKey";

    private static final String KEY_ENTRY_ROAD = "entry_road";
    private static final String KEY_EXIT_ROAD = "exit_road";
    private static final String KEY_TYPE = "type";

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

    private GoogleMap mMap, streetMap;
    ListView addressList, streetList;
    FirebaseFirestore db;
    Button filterBtn;

    private AddressAdapter addressAdapter;
    private AdressListAdapter addressListAdapter;
    private StreetListAdapter streetListAdapter;
    private ArrayList<Street> listStreet, listStreetSelected;
    public static ArrayList<Street> listAllStreet= new ArrayList<Street>();
    private ArrayList<Address> listAddress;
    public static ArrayList<Address> listAllAddress= new ArrayList<Address>();
    private ArrayList<Region> listRegion;
    ConstraintLayout filterConstraint, filterMenuConstraint, mapConstraint, addressMapCons, streetMapCons;
    TextView mapTxt, listTxt, countyTxt, streetTxt, addressMapTxt, streetMapTxt;
    EditText searchEdt;
    ImageView searchImg;
    String longitude1, latitude1, locationAddress, la, lo, countyId, regionId, County, Region, roadId,
            stlongitude1, stlatitude1, stlocationAddress, stla, stlo, addressId, addressType, addressName, fullAddress,
            adRoad, adCounty, adRegion, adLat, adLong, adEntryRoad, adExitRoad, stCounty, stRegion, stRoad, stLat, stLong,
            propertyCategory;
    SearchView searchSearch;
    ProgressDialog UstartDialog;
    ArrayList<Address> listAddressSelected;

    String Phone, Email, Name, Supervisor, myid;
    SharedPreferences sharedpreferences;
    int success;
    private ProgressDialog pDialog;
    ImageView terrainImg, flatImg, satelliteImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        Email = sharedpreferences.getString(Email1,"");
        Phone = sharedpreferences.getString(phoneNumber1,"");
        Name = sharedpreferences.getString(name1,"");
        Supervisor = sharedpreferences.getString(supervisor1,"");
        myid = sharedpreferences.getString(myid1,"");

        db = FirebaseFirestore.getInstance();
        UstartDialog = new ProgressDialog(ViewActivity.this, R.style.mydialog);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        pDialog = new ProgressDialog(ViewActivity.this, R.style.mydialog);
        pDialog.setMessage("Loading. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        listAddress = new ArrayList<Address>();
        listAllAddress= new ArrayList<Address>();
        listStreet= new ArrayList<Street>();
        listAllStreet= new ArrayList<Street>();
        listStreetSelected = new ArrayList<Street>();
        listAddressSelected= new ArrayList<Address>();

        filterMenuConstraint=(ConstraintLayout) findViewById(R.id.constraint_filter_view);
        mapConstraint= (ConstraintLayout) findViewById(R.id.constraint_map_search);
        mapTxt=(TextView) findViewById(R.id.txt_map_search) ;
        listTxt= (TextView) findViewById(R.id.txt_list_search);
        streetTxt=(TextView) findViewById(R.id.txt_street_view);
        countyTxt =(TextView) findViewById(R.id.txt_county_view);
        filterBtn= (Button) findViewById(R.id.btn_filter_view);
        addressList = (ListView) findViewById(R.id.list_address);
        streetList= (ListView) findViewById(R.id.list_street);
        searchSearch=(SearchView) findViewById(R.id.search_search);
        searchSearch.setOnQueryTextListener(this);

        streetMapTxt=(TextView) findViewById(R.id.txt_street_map);
        addressMapTxt =(TextView) findViewById(R.id.txt_address_map);
        streetMapCons=(ConstraintLayout) findViewById(R.id.cons_map_street);
        addressMapCons= (ConstraintLayout) findViewById(R.id.cons_map_address);

        terrainImg = (ImageView) findViewById(R.id.img_terrain_map);
        flatImg =(ImageView) findViewById(R.id.img_flat_map);
        satelliteImg = (ImageView) findViewById(R.id.img_sattelite_map);

        Bundle loca=getIntent().getExtras();
        if (loca != null) {
            County=String.valueOf(loca.getCharSequence("county"));
            Region=String.valueOf(loca.getCharSequence("region"));
            countyId=String.valueOf(loca.getCharSequence("countyid"));
            //getRegionId();
        }

        LoadAddresses();

        mapTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapTxt.setBackgroundResource(R.color.White);
                listTxt.setBackgroundResource(R.color.blue);
                streetTxt.setBackgroundResource(R.color.blue);
                mapTxt.setTextColor(getResources().getColor(R.color.colourFive));
                listTxt.setTextColor(getResources().getColor(R.color.White));
                streetTxt.setTextColor(getResources().getColor(R.color.White));
                SetAddressMarkers();

                addressList.setVisibility(View.GONE);
                mapConstraint.setVisibility(View.VISIBLE);
                streetList.setVisibility(View.GONE);
            }
        });

        listTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listTxt.setBackgroundResource(R.color.White);
                mapTxt.setBackgroundResource(R.color.blue);
                streetTxt.setBackgroundResource(R.color.blue);
                mapTxt.setTextColor(getResources().getColor(R.color.White));
                listTxt.setTextColor(getResources().getColor(R.color.colourFive));
                streetTxt.setTextColor(getResources().getColor(R.color.White));

                streetList.setVisibility(View.GONE);
                addressList.setVisibility(View.VISIBLE);
                mapConstraint.setVisibility(View.GONE);
            }
        });

        streetTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog.setMessage("Loading. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();

                streetTxt.setBackgroundResource(R.color.White);
                listTxt.setBackgroundResource(R.color.blue);
                mapTxt.setBackgroundResource(R.color.blue);
                streetTxt.setTextColor(getResources().getColor(R.color.colourFive));
                mapTxt.setTextColor(getResources().getColor(R.color.White));
                listTxt.setTextColor(getResources().getColor(R.color.White));
                getRegionId();

                streetList.setVisibility(View.VISIBLE);
                addressList.setVisibility(View.GONE);
                mapConstraint.setVisibility(View.GONE);
            }
        });

        addressMapTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addressMapTxt.setBackgroundResource(R.color.White);
                streetMapTxt.setBackgroundResource(R.color.blue);
                addressMapTxt.setTextColor(getResources().getColor(R.color.colourFive));
                streetMapTxt.setTextColor(getResources().getColor(R.color.White));
                streetMapCons.setVisibility(View.VISIBLE);
                SetAddressMarkers();
            }
        });

        streetMapTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                streetMapTxt.setBackgroundResource(R.color.White);
                addressMapTxt.setBackgroundResource(R.color.blue);
                streetMapTxt.setTextColor(getResources().getColor(R.color.colourFive));
                addressMapTxt.setTextColor(getResources().getColor(R.color.White));
                SetStreetMarkers();
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
        LatLng sydney = new LatLng(-1.289238, 36.820442);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Start"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public void SetAddressMarkers(){
        mMap.clear();
        if (listAddress.size()>0){
            for (int i = 0; i < listAddress.size(); i++) {

                longitude1 = listAddress.get(i).getLongitude();
                latitude1 = listAddress.get(i).getLatitude();
                locationAddress = listAddress.get(i).getFulladdress();

                Double long1 = Double.parseDouble(longitude1);
                Double lat1 = (Double.parseDouble(latitude1) - 100);

                la = listAddress.get(0).getLatitude();
                lo = listAddress.get(0).getLongitude();

                Double lat2 = (Double.parseDouble(la) - 100);
                Double long2 = Double.parseDouble(lo);

                LatLng building = new LatLng(lat1, long1);
                mMap.addMarker(new MarkerOptions().position(building).title(locationAddress));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(building));
                Log.e("PlaceLL", lat1 + " " + long1);

                LatLng start = new LatLng(lat2, long2);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(start));
            }
        }
    }

    public void SetStreetMarkers(){
        mMap.clear();
        if (listStreet.size()>0){

            for (int i = 0; i < listStreet.size(); i++) {

                stlongitude1 = listStreet.get(i).getEntrylongitude();
                stlatitude1 = listStreet.get(i).getEntrylatitude();
                stlocationAddress = listStreet.get(i).getRoad();

                Double stlong1 = Double.parseDouble(stlongitude1);
                Double stlat1 = (Double.parseDouble(stlatitude1) - 100);

                stla = listStreet.get(0).getEntrylatitude();
                stlo = listStreet.get(0).getEntrylongitude();

                Double stlat2 = (Double.parseDouble(stla) - 100);
                Double stlong2 = Double.parseDouble(stlo);

                LatLng building = new LatLng(stlat1, stlong1);
                mMap.addMarker(new MarkerOptions().position(building).title(stlocationAddress));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(building));
                Log.e("PlaceLL", stlat1 + " " + stlong1);

                LatLng start = new LatLng(stlat2, stlong2);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(start));
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        String text = s;
        addressListAdapter.filter(text);
        return false;
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

                                LoadStreets();
                            }
                        }
                    }
                });
    }

    private void LoadAddresses(){
        db.collection(COLLECTION_KEY).whereEqualTo("county", County).whereEqualTo("region", Region)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        Address miss = document.toObject(Address.class);
                        listAddress.add(miss);
                        listAllAddress.add(miss);
                        pDialog.dismiss();
                        //SetAddressMarkers();
                    }

                    addressListAdapter = new AdressListAdapter(ViewActivity.this);
                    addressList.setAdapter(addressListAdapter);

                    addressList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            if (CheckNetWorkStatus.isNetworkAvailable(ViewActivity.this)) {

                                final String latMap=((TextView) view.findViewById(R.id.txt_latitude_item)).getText().toString();
                                final String longMap=((TextView) view.findViewById(R.id.txt_longitude_item)).getText().toString();
                                final String addressNumber=((TextView) view.findViewById(R.id.txt_address_number)).getText().toString();

                                LayoutInflater inflater = LayoutInflater.from(ViewActivity.this);
                                final View dialogView = inflater.inflate(R.layout.alert_address, null);
                                final AlertDialog dialogBuilder = new AlertDialog.Builder(ViewActivity.this).create();
                                dialogBuilder.setView(dialogView);

                                TextView detailsTxt = (TextView)dialogView.findViewById(R.id.txt_details_dialog);
                                TextView mapTxt = (TextView)dialogView.findViewById(R.id.txt_map_dialog);
                                TextView cancelTxt = (TextView)dialogView.findViewById(R.id.txt_cancel_dialog);

                                mapTxt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (CheckNetWorkStatus.isNetworkAvailable(getApplicationContext())) {
                                            db.collection(COLLECTION_KEY)
                                                    .whereEqualTo("latitude", latMap).whereEqualTo("longitude", longMap)
                                                    .whereEqualTo("fulladdress", addressNumber)
                                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            Address miss = document.toObject(Address.class);
                                                            addressId=document.getId();
                                                            listAddressSelected.add(miss);

                                                            fullAddress=listAddressSelected.get(0).getFulladdress();
                                                            addressType=listAddressSelected.get(0).getAddresstype();
                                                            addressName=listAddressSelected.get(0).getAddressname();
                                                            adCounty=listAddressSelected.get(0).getCounty();
                                                            adRegion=listAddressSelected.get(0).getRegion();
                                                            adRoad=listAddressSelected.get(0).getRoad();
                                                            adLat=listAddressSelected.get(0).getLatitude();
                                                            adLong=listAddressSelected.get(0).getLongitude();
                                                            propertyCategory=listAddressSelected.get(0).getCategory();

                                                            UpdateAddressVerified();
                                                            new AddAddress().execute();

                                                        }
                                                    } else {
                                                        Toast.makeText(ViewActivity.this, "Error fetching document", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(ViewActivity.this,"Unable to connect to internet",Toast.LENGTH_LONG).show();
                                        }
                                        dialogBuilder.dismiss();

                                    }
                                });

                                detailsTxt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Intent i = new Intent(ViewActivity.this, MapsActivity.class);
                                        Bundle location = new Bundle();
                                        location.putString("address", addressNumber);
                                        location.putString("latitude", String.valueOf(Double.parseDouble(latMap)-100));
                                        location.putString("longitude", longMap);
                                        location.putString("countyid", countyId);

                                        i.putExtras(location);
                                        startActivity(i);

                                        //Toast.makeText(ViewActivity.this, addressNumber + " ", Toast.LENGTH_LONG).show();
                                        dialogBuilder.dismiss();

                                    }
                                });

                                cancelTxt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogBuilder.dismiss();

                                    }
                                });

                                dialogBuilder.show();

                            } else {
                                Toast.makeText(ViewActivity.this,
                                        "Unable to connect to internet",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                } else {
                    Toast.makeText(ViewActivity.this, "Error adding document", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void LoadStreets(){
        db.collection("counties").document(countyId).collection("regions")
                .document(regionId).collection("roads")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        Street miss = document.toObject(Street.class);
                        listStreet.add(miss);
                        listAllStreet.add(miss);
                        pDialog.dismiss();
                        //SetStreetMarkers();
                    }

                    streetListAdapter = new StreetListAdapter(ViewActivity.this);
                    streetList.setAdapter(streetListAdapter);

                    streetList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            if (CheckNetWorkStatus.isNetworkAvailable(ViewActivity.this)) {

                                final String latMap=((TextView) view.findViewById(R.id.txt_latitude_item)).getText().toString();
                                final String longMap=((TextView) view.findViewById(R.id.txt_longitude_item)).getText().toString();
                                final String streetNumber=((TextView) view.findViewById(R.id.txt_address_number)).getText().toString();

                                LayoutInflater inflater = LayoutInflater.from(ViewActivity.this);
                                final View dialogView = inflater.inflate(R.layout.alert_address, null);
                                final AlertDialog dialogBuilder = new AlertDialog.Builder(ViewActivity.this).create();
                                dialogBuilder.setView(dialogView);

                                TextView detailsTxt = (TextView)dialogView.findViewById(R.id.txt_details_dialog);
                                TextView mapTxt = (TextView)dialogView.findViewById(R.id.txt_map_dialog);
                                TextView cancelTxt = (TextView)dialogView.findViewById(R.id.txt_cancel_dialog);

                                mapTxt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        db.collection("counties").document(countyId).collection("regions")
                                                .document(regionId).collection("roads").whereEqualTo("road", streetNumber)
                                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful()){
                                                    for(QueryDocumentSnapshot document : task.getResult()) {
                                                        Street miss = document.toObject(Street.class);
                                                        listStreetSelected.add(miss);
                                                        roadId=document.getId();

                                                        stRoad=listStreetSelected.get(0).getRoad();
                                                        adEntryRoad=listStreetSelected.get(0).getFromroad();
                                                        adExitRoad=listStreetSelected.get(0).getExitroad();
                                                        stLat=listStreetSelected.get(0).getEntrylatitude();
                                                        stLong=listStreetSelected.get(0).getEntrylongitude();

                                                        UpdateVerifiedStreet();
                                                        new AddStreet().execute();
                                                    }
                                                } else {
                                                    Toast.makeText(ViewActivity.this, "Error adding document", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });

                                        dialogBuilder.dismiss();
                                    }
                                });

                                detailsTxt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Intent i = new Intent(ViewActivity.this, MapsActivity.class);
                                        Bundle location = new Bundle();
                                        location.putString("address", streetNumber);
                                        location.putString("latitude", String.valueOf(Double.parseDouble(latMap)-100));
                                        location.putString("longitude", longMap);
                                        location.putString("countyid", countyId);
                                        location.putString("regionid", regionId);
                                        location.putString("type", "Street");

                                        i.putExtras(location);
                                        startActivity(i);

                                        //Toast.makeText(ViewActivity.this, addressNumber + " ", Toast.LENGTH_LONG).show();
                                        dialogBuilder.dismiss();

                                    }
                                });

                                cancelTxt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogBuilder.dismiss();

                                    }
                                });

                                dialogBuilder.show();

                            } else {
                                Toast.makeText(ViewActivity.this,
                                        "Unable to connect to internet",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                } else {
                    Toast.makeText(ViewActivity.this, "Error adding document", Toast.LENGTH_LONG).show();
                }
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

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void UpdateVerifiedStreet() {
        UstartDialog.setMessage("Verifying. Please wait...");
        UstartDialog.setIndeterminate(false);
        UstartDialog.setCancelable(false);
        UstartDialog.show();


        Date currentDate = new Date();
        Map<String, Object> addressName = new HashMap<>();

        addressName.put("verificationstatus", "Verified");
        addressName.put("verificationdate", currentDate);
        addressName.put("verifiedby", myid);

        db.collection("counties").document(countyId).collection("regions")
                .document(regionId).collection("roads").document(roadId)
                .set(addressName, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
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

            httpParams.put(KEY_ADDRESS, fullAddress);
            httpParams.put(KEY_ADDRESS_NAME, addressName);
            httpParams.put(KEY_ROAD, adRoad);
            httpParams.put(KEY_REGION, adRegion);
            httpParams.put(KEY_COUNTY, adCounty);
            httpParams.put(KEY_LATITUDE, String.format("%.8f", Double.parseDouble(adLat)-100));
            httpParams.put(KEY_LONGITUDE, adLong);
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
            runOnUiThread(new Runnable() {
                public void run() {
                    if (success == 0) {
                        Toast.makeText(ViewActivity.this,"Address Verification Successful",Toast.LENGTH_LONG).show();
                        listAddress.clear();
                        listAllAddress.clear();
                        listAddressSelected.clear();

                        LoadAddresses();
                        UstartDialog.dismiss();
                    } else {
                        Toast.makeText(ViewActivity.this,"Address Verification Failed",Toast.LENGTH_LONG).show();
                        UstartDialog.dismiss();
                    }
                }
            });
        }
    }

    private class AddStreet extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();

            httpParams.put(KEY_ROAD, stRoad);
            httpParams.put(KEY_REGION, Region);
            httpParams.put(KEY_COUNTY, County);
            httpParams.put(KEY_LATITUDE, String.format("%.8f", Double.parseDouble(stLat)-100));
            httpParams.put(KEY_LONGITUDE, stLong);
            httpParams.put(KEY_ENTRY_ROAD, adEntryRoad);
            httpParams.put(KEY_EXIT_ROAD, adExitRoad);
            httpParams.put(KEY_TYPE, "Entry");

            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL + "street.php", "POST", httpParams);
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
                        Toast.makeText(ViewActivity.this,"Street Verification Updated Successfully",Toast.LENGTH_LONG).show();
                        listStreet.clear();
                        listAllStreet.clear();
                        listStreetSelected.clear();

                        LoadStreets();
                        UstartDialog.dismiss();

                    } else {
                        UstartDialog.dismiss();
                        Toast.makeText(ViewActivity.this,"Verification Update Failed",Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }
}