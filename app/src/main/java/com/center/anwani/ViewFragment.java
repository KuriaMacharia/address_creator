package com.center.anwani;

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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ViewFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    String[] county = {"---Select---", "Nairobi City County", "Kiambu County"};


    Button viewBtn;
    TextView countyTxt, coordinatesTxt;
    Spinner regionSpin, countySpin;
    ImageButton addRoad, addRegion;
    ArrayAdapter rr;
    String County, Region, countyId;
    private FirebaseFirestore db;
    private ArrayList<Region> listRegion;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.view_fragment, container, false);
        db = FirebaseFirestore.getInstance();
        listRegion = new ArrayList<>();

        viewBtn = (Button) root.findViewById(R.id.btn_view_street);
        countyTxt = (TextView) root.findViewById(R.id.txt_county_located);
        coordinatesTxt = (TextView) root.findViewById(R.id.txt_coordinates);
        countySpin = (Spinner) root.findViewById(R.id.spin_county_address);
        regionSpin = (Spinner) root.findViewById(R.id.spin_region);

        addRegion = (ImageButton) root.findViewById(R.id.img_add_region);

        db = FirebaseFirestore.getInstance();
        contentConstraint = (ConstraintLayout) root.findViewById(R.id.constraint_main_content);
        consDetails=(ConstraintLayout) root.findViewById(R.id.constraint_details_region_road);

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
        ArrayAdapter cty = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, county);
        cty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countySpin.setAdapter(cty);


        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), ViewActivity.class);
                Bundle location = new Bundle();
                location.putString("county", County);
                location.putString("countyid", countyId);
                location.putString("region", Region);

                i.putExtras(location);
                startActivity(i);
            }
        });
        return root;
    }

    private void LoadRegion() {

        db.collection("counties").document(countyId).collection("regions")
                //.whereEqualTo("county", County)
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

                            regionSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                    Region = String.valueOf(regionSpin.getSelectedItem());
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        County = String.valueOf(countySpin.getSelectedItem());
        if (County.contentEquals("---Select---")) {
            consDetails.setVisibility(View.GONE);
        } else {
            countyTxt.setText(County);
            ///LoadRegion();

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
}
