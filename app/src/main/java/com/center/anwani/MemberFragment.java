package com.center.anwani;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.Set;

public class MemberFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    String[] county = {"---Select---", "Nairobi City County", "Kiambu County"};

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

    TextView nameTxt, supervisorTxt, phoneTxt, emailTxt, countyTxt;
    SharedPreferences sharedpreferences;
    Button streetBtn, addressBtn, viewBtn;
    String Phone, Email, Name, Supervisor, myid, myCounty, myRegion, myRole, myStatus, regionId, countyId, County, Region;
    Spinner countySpin, regionSpin;

    View root;
    private ProgressDialog startDialog, loadDialog;
    private FirebaseFirestore db;
    private ArrayList<Region> listRegion;

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
        listRegion = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        if(myRole.contentEquals("Admin")){
            root = inflater.inflate(R.layout.member_fragment, container, false);
        }else{
            root = inflater.inflate(R.layout.member_fragment_verification, container, false);

            startDialog = new ProgressDialog(getActivity(), R.style.mydialog);
            startDialog.setMessage("Loading. Please wait...");
            startDialog.setIndeterminate(false);
            startDialog.setCancelable(false);
            startDialog.show();
            GetCountyId();
        }


        nameTxt=(TextView) root.findViewById(R.id.txt_name_member);
        supervisorTxt=(TextView) root.findViewById(R.id.txt_supervisor);
        phoneTxt=(TextView) root.findViewById(R.id.txt_phone_member);
        emailTxt=(TextView) root.findViewById(R.id.txt_email_member);
        streetBtn =(Button) root.findViewById(R.id.btn_verify_street);
        addressBtn=(Button) root.findViewById(R.id.btn_verify_address);
        countyTxt = (TextView) root.findViewById(R.id.txt_county_located);
        countySpin = (Spinner) root.findViewById(R.id.spin_county_address);
        regionSpin = (Spinner) root.findViewById(R.id.spin_region);
        viewBtn =(Button) root.findViewById(R.id.btn_view_fragment);

        nameTxt.setText(Name);
        supervisorTxt.setText(myCounty);
        phoneTxt.setText(myRegion);
        emailTxt.setText(myRegion);

        countySpin.setOnItemSelectedListener(this);
        ArrayAdapter cty = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, county);
        cty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countySpin.setAdapter(cty);

        streetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), StreetVerificationActivity.class);
                Bundle location = new Bundle();
                if(myRole.contentEquals("Admin")){
                    location.putString("county", County);
                    location.putString("region", Region);
                    location.putString("countyid", countyId);
                }else {

                    location.putString("county", myCounty);
                    location.putString("region", myRegion);
                    location.putString("countyid", countyId);
                }
                    i.putExtras(location);
                    startActivity(i);
            }
        });

        addressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddressVerificationActivity.class));
            }
        });

        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), ViewActivity.class);
                Bundle location = new Bundle();
                    location.putString("county", myCounty);
                    location.putString("region", myRegion);
                    location.putString("countyid", countyId);

                i.putExtras(location);
                startActivity(i);
            }
        });

        return root;
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
                                streetBtn.setVisibility(View.VISIBLE);
                                startDialog.dismiss();
                            }
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
        } else {
            loadDialog = new ProgressDialog(getActivity(), R.style.mydialog);
            loadDialog.setMessage("Loading. Please wait...");
            loadDialog.setIndeterminate(false);
            loadDialog.setCancelable(false);
            loadDialog.show();
            db.collection("counties").whereEqualTo("county", County)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    countyId = document.getId();
                                    countyTxt.setText(County);
                                    LoadRegion();
                                    //loadDialog.dismiss();
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
                            }
                            ArrayList<String> list = new ArrayList<String>();
                            for (int i = 0; i < listRegion.size(); i++) {
                                list.add(listRegion.get(i).getRegion());
                            }

                            Set<String> set = new HashSet<>(list);
                            list.clear();
                            list.addAll(set);

                            Region = list.get(0);
                            loadDialog.dismiss();
                            streetBtn.setVisibility(View.VISIBLE);

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
}
