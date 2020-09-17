package com.center.anwani;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.center.anwani.Helper.CheckNetWorkStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {

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

    public static final String starttime1 = "starttimeKey";
    public static final String endtime1 = "endtimeKey";
    public static final String regionstatus1 = "regionstatusKey";

    public static final String clat1 = "clatKey";
    public static final String clong1 = "clongKey";
    public static final String radius1 = "radiusKey";

    private static final String TAG = "LoginActivity";
    EditText phoneEdt, passwordEdt;
    CheckBox termsCheck;
    TextView forgotTxt;
    Button signupBtn, loginBtn;
    private FirebaseAuth mAuth;
    String Phone, Email, Password, Name, Supervisor, myCounty, myRegion, myRole, myStatus;
    FirebaseFirestore db;
    String myid, msg;
    ArrayList<Users> listUser;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    private ProgressDialog pDialog;
    EditText emailEdt;

    String startTime, endTime, regionStatus, Reason, cLat, cLong, Radius;
    ArrayList<StartCheck> startList;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(LoginActivity.this);
        listUser = new ArrayList<Users>();
        startList=new ArrayList<StartCheck>();

        loginBtn=(Button) findViewById(R.id.btn_login);
        forgotTxt=(TextView) findViewById(R.id.txt_forgot_password);
        emailEdt=(EditText) findViewById(R.id.edt_username);
        passwordEdt=(EditText) findViewById(R.id.edt_password);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        if (sharedpreferences.getBoolean("logged", false)) {

            sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            Email = sharedpreferences.getString(Email1,"");
            Phone = sharedpreferences.getString(phoneNumber1,"");
            Name = sharedpreferences.getString(name1,"");
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

            GoToMainActivityAuto();
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNetWorkStatus.isNetworkAvailable(LoginActivity.this)) {
                    if (emailEdt.getText().toString().isEmpty() ||
                            passwordEdt.getText().toString().isEmpty()) {
                        passwordEdt.setError("No details entered!");
                        Toast.makeText(LoginActivity.this, "Please enter a valid email and password!", Toast.LENGTH_LONG).show();

                    } else {
                        pDialog = new ProgressDialog(LoginActivity.this, R.style.mydialog);
                        pDialog.setMessage("Logging in. Please wait...");
                        pDialog.setIndeterminate(false);
                        pDialog.setCancelable(false);
                        pDialog.show();

                        mAuth.signInWithEmailAndPassword(emailEdt.getText().toString(), passwordEdt.getText().toString())
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            //Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            //updateUI(user);
                                            Email = user.getEmail();
                                            pDialog.setMessage("Fetching details. Please wait...");
                                            GoToMainActivity();
                                        } else {
                                            pDialog.dismiss();
                                            passwordEdt.setError("Incorrect Email or Password!");
                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                                            //Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                            updateUI(null);
                                        }

                                    }
                                });
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "Unable to connect to internet", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    public  void updateUI(FirebaseUser user){

    }

    public void GoToMainActivity(){

        db.collection("users").whereEqualTo("email", emailEdt.getText().toString())
                .whereEqualTo("status", "Active")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Users miss;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                miss=document.toObject(Users.class);
                                listUser.add(miss);

                                Name = listUser.get(0).getName();
                                Supervisor = listUser.get(0).getSupervisor();
                                Email = listUser.get(0).getEmail();
                                Phone = listUser.get(0).getPhone();

                                myCounty = listUser.get(0).getCounty();
                                myRegion = listUser.get(0).getRegion();
                                myRole = listUser.get(0).getRole();
                                myStatus = listUser.get(0).getStatus();

                                myid = document.getId();
                                InitialCheck();


                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Account Not Found", Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }
    public void ProcessAll(){
        editor = sharedpreferences.edit();
        editor.putString(name1, Name);
        editor.putString(supervisor1, Supervisor);
        editor.putString(phoneNumber1, Phone);
        editor.putString(Email1, Email);
        editor.putString(myid1, myid);
        editor.putString(county1, myCounty);
        editor.putString(region1, myRegion);
        editor.putString(role1, myRole);
        editor.putString(status1, myStatus);

        editor.putString(starttime1, startTime);
        editor.putString(endtime1, endTime);
        editor.putString(regionstatus1, regionStatus);
        editor.putString(clat1, cLat);
        editor.putString(clong1, cLong);
        editor.putString(radius1, Radius);

        editor.commit();
        pDialog.dismiss();

        if(myRole.contentEquals("Street Data Agent")){
            sharedpreferences.edit().putBoolean("logged",true).apply();

            Bundle params = new Bundle();
            params.putString("user_id", myid);
            mFirebaseAnalytics.logEvent("street_agent_login", params);

            Intent i = new Intent(LoginActivity.this, StreetActivity.class);
            Bundle location = new Bundle();
            location.putString("county", myCounty);
            location.putString("region", myRegion);
            i.putExtras(location);
            startActivity(i);

        }else{
            sharedpreferences.edit().putBoolean("logged",true).apply();

            Bundle params = new Bundle();
            params.putString("user_id", myid);
            mFirebaseAnalytics.logEvent("agent_login", params);

            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
        }

    }
    public void GoToMainActivityAuto(){

        if(myRole.contentEquals("Street Data Agent")){
            sharedpreferences.edit().putBoolean("logged",true).apply();

            Bundle params = new Bundle();
            params.putString("user_id", myid);
            mFirebaseAnalytics.logEvent("street_agent_login", params);

            Intent i = new Intent(LoginActivity.this, StreetActivity.class);
            Bundle location = new Bundle();
            location.putString("county", myCounty);
            location.putString("region", myRegion);
            i.putExtras(location);
            startActivity(i);

        }else{
            sharedpreferences.edit().putBoolean("logged",true).apply();

            Bundle params = new Bundle();
            params.putString("user_id", myid);
            mFirebaseAnalytics.logEvent("agent_login", params);

            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
        }

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();
    }

    private void InitialCheck(){
        pDialog.setMessage("Verifying details. Please wait...");
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
                        //endTime="5:00PM";
                        regionStatus=startList.get(0).getRegionstatus();
                        cLat=startList.get(0).getLatitude();
                        cLong=startList.get(0).getLongitude();
                        Radius=startList.get(0).getRadius();

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
                                    ProcessAll();
                                }else{
                                    pDialog.dismiss();
                                    passwordEdt.setText("");
                                    Toast.makeText(LoginActivity.this, "Platform offline in your region", Toast.LENGTH_LONG).show();
                                }
                            }else {
                                pDialog.dismiss();
                                passwordEdt.setText("");
                                Toast.makeText(LoginActivity.this, "Platform offline at this time", Toast.LENGTH_LONG).show();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });
    }
}
