package com.center.anwani;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CreateFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    public static final String name1 = "nameKey";
    public static final String supervisor1 = "supervisorKey";
    public static final String phoneNumber1 = "phoneKey";
    public static final String Email1 = "emailKey";
    public static final String county1 = "countyKey";
    public static final String region1 = "regionKey";
    public static final String role1 = "roleKey";
    public static final String status1 = "statusKey";

    private static final String TAG = "LoginActivity";

    String[] county = {"---Select---", "Nairobi City County", "Kiambu County"};

    Button locateBtn, createBtn, viewBtn, searchBtn, saveBtn, searchStreetBtn;
    TextView countyTxt, coordinatesTxt, regionTxt;
    Spinner regionSpin, roadSpin, countySpin;
    ImageButton addRoad, addRegion;
    ArrayAdapter rr;
    String County, Region, Road, countyId, regionId;
    EditText roadEdt, regionEdt;
    private FirebaseFirestore db;
    private ArrayList<Region> listRegion, listRegion2;
    ArrayList<String> listRoad;

    ConstraintLayout consDetails, contentConstraint;
    List<String> listDistance;
    ArrayList<String> listAllDistance, listReDistance;
    List<String> list1;
    List<String> list2;
    List<String> list3;
    ArrayList<String> list4;
    ArrayList<Double> lisLat, resultLat;
    ArrayList<Double> lisLon, resultLong;

    private ArrayList<Address> listAddress;
    List<Address> longitudeList, latitudeList, commonList;
    String Phone, Email, Name, Supervisor, myCounty, myRegion, myRole, myStatus, addressCountyId;

    SharedPreferences sharedpreferences;
    View root;
    private ProgressDialog startDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        sharedpreferences = this.getActivity().getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        Email = sharedpreferences.getString(Email1,"");
        Phone = sharedpreferences.getString(phoneNumber1,"");
        Name = sharedpreferences.getString(name1,"");
        Supervisor = sharedpreferences.getString(supervisor1,"");

        myCounty = sharedpreferences.getString(county1,"");
        myRegion = sharedpreferences.getString(region1,"");
        myRole = sharedpreferences.getString(role1,"");
        myStatus = sharedpreferences.getString(status1,"");

        if(myRole.contentEquals("Admin")){
            root = inflater.inflate(R.layout.create_layout, container, false);

        } else if(myRole.contentEquals("Data Collection Agent")){
            root = inflater.inflate(R.layout.create_layout_data_collection, container, false);
            db = FirebaseFirestore.getInstance();

            startDialog = new ProgressDialog(getActivity(), R.style.mydialog);
            startDialog.setMessage("Loading. Please wait...");
            startDialog.setIndeterminate(false);
            startDialog.setCancelable(false);
            startDialog.show();

            GetCountyId();

        } else if(myRole.contentEquals("Data Verification Agent")){
            root = inflater.inflate(R.layout.create_layout_data_collection, container, false);
        }

        //View root = inflater.inflate(R.layout.create_layout, container, false);

        db = FirebaseFirestore.getInstance();
        listRegion = new ArrayList<>();

        createBtn = (Button) root.findViewById(R.id.btn_create_address);
        saveBtn = (Button) root.findViewById(R.id.btn_save_main);
        searchStreetBtn = (Button) root.findViewById(R.id.btn_search_street_create);
        countyTxt = (TextView) root.findViewById(R.id.txt_county_located);
        regionTxt = (TextView) root.findViewById(R.id.txt_region);
        coordinatesTxt = (TextView) root.findViewById(R.id.txt_coordinates);
        countySpin = (Spinner) root.findViewById(R.id.spin_county_address);
        regionSpin = (Spinner) root.findViewById(R.id.spin_region);
        roadSpin = (Spinner) root.findViewById(R.id.spin_road);
        roadEdt = (EditText) root.findViewById(R.id.edt_road_main);
        regionEdt = (EditText) root.findViewById(R.id.edt_region_main);

        addRoad = (ImageButton) root.findViewById(R.id.img_add_road);
        addRegion = (ImageButton) root.findViewById(R.id.img_add_region);

        //db = FirebaseFirestore.getInstance();
        contentConstraint = (ConstraintLayout) root.findViewById(R.id.constraint_main_content);
        consDetails=(ConstraintLayout) root.findViewById(R.id.constraint_details_region_road);

        countyTxt.setText(myCounty);
        regionTxt.setText(myRegion);

        listDistance = new ArrayList<String>();
        listAllDistance = new ArrayList<String>();
        listReDistance = new ArrayList<String>();
        lisLat = new ArrayList<Double>();
        lisLon = new ArrayList<Double>();
        resultLat = new ArrayList<Double>();
        resultLong = new ArrayList<Double>();

        list1 = new ArrayList<>();
        list2 = new ArrayList<>();
        list3 = new ArrayList<>();
        list4 = new ArrayList<>();

        listAddress = new ArrayList<Address>();
        longitudeList = new ArrayList<>();
        latitudeList = new ArrayList<>();
        commonList = new ArrayList<>();

        countySpin.setOnItemSelectedListener(this);
        regionSpin.setOnItemSelectedListener(this);
        roadSpin.setOnItemSelectedListener(this);

        ArrayAdapter cty = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, county);
        cty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countySpin.setAdapter(cty);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(countySpin.getVisibility()==View.VISIBLE) {
                    Intent i = new Intent(getActivity(), CreateActivity.class);
                    Bundle location = new Bundle();
                    location.putString("county", County);
                    location.putString("region", Region);
                    location.putString("road", Road);
                    i.putExtras(location);
                    startActivity(i);
                }else{
                    Intent i = new Intent(getActivity(), CreateActivity.class);
                    Bundle location = new Bundle();
                    location.putString("county", myCounty);
                    location.putString("region", myRegion);
                    location.putString("road", Road);
                    i.putExtras(location);
                    startActivity(i);
                }
            }
        });

        searchStreetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), StreetSearchActivity.class);
                Bundle location = new Bundle();

                if(countySpin.getVisibility()==View.VISIBLE) {
                    location.putString("county", County);
                    location.putString("region", Region);
                    location.putString("countyid", countyId);
                }else{
                    location.putString("county", myCounty);
                    location.putString("region", myRegion);
                    location.putString("countyid", countyId);
                }

                i.putExtras(location);
                startActivity(i);
            }
        });

        addRoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (roadEdt.getVisibility() == View.GONE) {
                    roadEdt.setVisibility(View.VISIBLE);
                    roadSpin.setVisibility(View.GONE);
                    if (regionEdt.getVisibility() == View.GONE || !Region.isEmpty()){
                        saveBtn.setVisibility(View.VISIBLE);
                    }else if(regionEdt.getVisibility() == View.VISIBLE && regionEdt.getText().toString().isEmpty()){
                        saveBtn.setVisibility(View.GONE);
                    }
                    createBtn.setVisibility(View.GONE);
                    addRoad.setImageDrawable(getResources().getDrawable(R.drawable.ic_clear_white_24dp));

                } else {
                    roadEdt.setVisibility(View.GONE);
                    roadSpin.setVisibility(View.VISIBLE);
                    saveBtn.setVisibility(View.GONE);
                    createBtn.setVisibility(View.VISIBLE);
                    addRoad.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white_24dp));
                }
            }
        });

        addRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (regionEdt.getVisibility() == View.GONE) {
                    regionEdt.setVisibility(View.VISIBLE);
                    roadEdt.setVisibility(View.VISIBLE);
                    regionSpin.setVisibility(View.GONE);
                    roadSpin.setVisibility(View.GONE);
                    //saveBtn.setVisibility(View.VISIBLE);
                    createBtn.setVisibility(View.GONE);
                    addRegion.setImageDrawable(getResources().getDrawable(R.drawable.ic_clear_white_24dp));
                    addRoad.setImageDrawable(getResources().getDrawable(R.drawable.ic_clear_white_24dp));
                } else {
                    roadEdt.setVisibility(View.GONE);
                    regionEdt.setVisibility(View.GONE);
                    regionSpin.setVisibility(View.VISIBLE);
                    roadSpin.setVisibility(View.VISIBLE);
                    saveBtn.setVisibility(View.GONE);
                    createBtn.setVisibility(View.VISIBLE);
                    addRegion.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white_24dp));
                    addRoad.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white_24dp));
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (regionSpin.getVisibility() == View.GONE) {
                    if (regionEdt != null && roadEdt != null) {

                        Map<String, Object> user = new HashMap<>();
                        user.put("county", County);
                        user.put("region", regionEdt.getText().toString());
                        user.put("road", roadEdt.getText().toString());

                        db.collection("counties")
                                .add(user)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        LoadRegion();
                                        Toast.makeText(getActivity(), "Successful", Toast.LENGTH_LONG).show();

                                        roadEdt.setVisibility(View.GONE);
                                        regionEdt.setVisibility(View.GONE);
                                        regionSpin.setVisibility(View.VISIBLE);
                                        roadSpin.setVisibility(View.VISIBLE);
                                        saveBtn.setVisibility(View.GONE);
                                        createBtn.setVisibility(View.VISIBLE);
                                        addRegion.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_black_24dp));
                                        addRoad.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_black_24dp));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), "Error adding document", Toast.LENGTH_LONG).show();
                                    }
                                });

                    } else {
                        Toast.makeText(getActivity(), "Add the missing field", Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (roadEdt != null && Region != null) {
                        Map<String, Object> user = new HashMap<>();
                        user.put("county", County);
                        user.put("region", Region);
                        user.put("road", roadEdt.getText().toString());

                        db.collection("counties")
                                .add(user)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        LoadRegion();
                                        Toast.makeText(getActivity(), "Successful", Toast.LENGTH_LONG).show();

                                        roadEdt.setVisibility(View.GONE);
                                        roadSpin.setVisibility(View.VISIBLE);
                                        saveBtn.setVisibility(View.GONE);
                                        createBtn.setVisibility(View.VISIBLE);
                                        addRoad.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_black_24dp));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), "Error adding document", Toast.LENGTH_LONG).show();
                                    }
                                });
                    } else {
                        Toast.makeText(getActivity(), "Add the missing field", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        return root;
    }

    private void LoadRegion() {
        db.collection("counties").document(countyId).collection("regions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        listRegion = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Region miss = document.toObject(Region.class);
                                listRegion.add(miss);
                                consDetails.setVisibility(View.VISIBLE);
                            }
                            ArrayList<String> list = new ArrayList<String>();
                            for (int i = 0; i < listRegion.size(); i++) {
                                list.add(listRegion.get(i).getRegion());
                            }

                            Set<String> set = new HashSet<>(list);
                            list.clear();
                            list.addAll(set);

                            Region = list.get(0);

                            ArrayAdapter scsc = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, list);
                            scsc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            regionSpin.setAdapter(scsc);

                            //if(regionSpin.isSelected()) {

                                regionSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        Region = String.valueOf(regionSpin.getSelectedItem());

                                        if (!Region.isEmpty()) {

                                            db.collection("counties")
                                                    .document(countyId).collection("regions")
                                                    //.document(regionId).collection("roads")
                                                    .whereEqualTo("region", Region)
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            //listRegion = new ArrayList<>();
                                                            //listRoad = new ArrayList<String>();
                                                            if (task.isSuccessful()) {
                                                                for (QueryDocumentSnapshot document : task.getResult()) {

                                                                    regionId = document.getId();
                                                                    db.collection("counties")
                                                                            .document(countyId).collection("regions")
                                                                            .document(regionId).collection("roads")
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    listRegion = new ArrayList<>();
                                                                                    listRoad = new ArrayList<String>();
                                                                                    if (task.isSuccessful()) {
                                                                                        for (QueryDocumentSnapshot document : task.getResult()) {

                                                                                            //regionId = document.getId();
                                                                                            Region miss = document.toObject(Region.class);
                                                                                            listRegion.add(miss);
                                                                                        }
                                                                                        for (int i = 0; i < listRegion.size(); i++) {
                                                                                            listRoad.add(listRegion.get(i).getRoad());
                                                                                        }

                                                                                        Set<String> set = new HashSet<>(listRoad);
                                                                                        listRoad.clear();
                                                                                        listRoad.addAll(set);
                                                                                        //Road=listRoad.get(0);

                                                                                        ArrayAdapter scsc = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, listRoad);
                                                                                        scsc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                                                        roadSpin.setAdapter(scsc);

                                                                                        roadSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                            @Override
                                                                                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                                                                //Road=roadSpin.getItemAtPosition(i).toString();
                                                                                                //roadSpin.setSelection(i, false);

                                                                                                Road = String.valueOf(roadSpin.getSelectedItem());
                                                                                                createBtn.setVisibility(View.VISIBLE);
                                                                                                searchStreetBtn.setVisibility(View.VISIBLE);
                                                                                            }

                                                                                            @Override
                                                                                            public void onNothingSelected(AdapterView<?> adapterView) {

                                                                                            }
                                                                                        });


                                                                                    } else {
                                                                                        Toast.makeText(getActivity(), "Get Failed", Toast.LENGTH_LONG).show();
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            } else {
                                                                Toast.makeText(getActivity(), "Get Failed", Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });
                            //}

                        } else {
                            Toast.makeText(getActivity(), "Get Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        County = String.valueOf(countySpin.getSelectedItem());

        if (County.contentEquals("---Select---")) {
                consDetails.setVisibility(View.GONE);
        } else {
            countyTxt.setText(County);
            //LoadRegion();

            db.collection("counties").whereEqualTo("county", County)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            listRegion = new ArrayList<>();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    countyId = document.getId();
                                    LoadRegion();

                                }
                            } else {
                                Toast.makeText(getActivity(), "Get Failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void GetCountyId(){
        db.collection("counties").whereEqualTo("county", myCounty)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                countyId = document.getId();
                                LoadCreateRegion();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Get Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void LoadCreateRegion(){
        db.collection("counties")
                .document(countyId).collection("regions")
                .whereEqualTo("region", myRegion)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //listRegion = new ArrayList<>();
                        //listRoad = new ArrayList<String>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                regionId = document.getId();
                                db.collection("counties")
                                        .document(countyId).collection("regions")
                                        .document(regionId).collection("roads")
                                        .whereEqualTo("verificationstatus", "Verified")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                listRegion = new ArrayList<>();
                                                listRoad = new ArrayList<String>();
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                                        //regionId = document.getId();
                                                        Region miss = document.toObject(Region.class);
                                                        listRegion.add(miss);
                                                    }
                                                    for (int i = 0; i < listRegion.size(); i++) {
                                                        listRoad.add(listRegion.get(i).getRoad());
                                                    }

                                                    Set<String> set = new HashSet<>(listRoad);
                                                    listRoad.clear();
                                                    listRoad.addAll(set);
                                                    Road=listRoad.get(0);
                                                    startDialog.dismiss();

                                                    createBtn.setVisibility(View.VISIBLE);
                                                    searchStreetBtn.setVisibility(View.VISIBLE);

                                                    ArrayAdapter scsc = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, listRoad);
                                                    scsc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                    roadSpin.setAdapter(scsc);

                                                    roadSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                        @Override
                                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                            //Road=roadSpin.getItemAtPosition(i).toString();
                                                            //roadSpin.setSelection(i, false);

                                                            Road = String.valueOf(roadSpin.getSelectedItem());
                                                        }

                                                        @Override
                                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                                        }
                                                    });


                                                } else {
                                                    Toast.makeText(getActivity(), "Get Failed", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(getActivity(), "Get Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}

