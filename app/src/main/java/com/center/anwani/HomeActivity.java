package com.center.anwani;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;import androidx.viewpager.widget.PagerAdapter;import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    public static final String name1 = "nameKey";
    public static final String supervisor1 = "supervisorKey";
    public static final String phoneNumber1 = "phoneKey";
    public static final String Email1 = "emailKey";
    public static final String county1 = "countyKey";
    public static final String region1 = "regionKey";
    public static final String role1 = "roleKey";
    public static final String status1 = "statusKey";

    public static final String starttime1 = "starttimeKey";
    public static final String endtime1 = "endtimeKey";
    public static final String regionstatus1 = "regionstatusKey";

    private static final String TAG = "LoginActivity";

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabsPagerAdapter adapter;
    FloatingActionButton editBtn;

    String Phone, Email, Name, Supervisor, myCounty, myRegion, myRole, myStatus, countyId, addressCountyId;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    private FirebaseFirestore db;

    String startTime, endTime, regionStatus, Reason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_home);

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
        InitialCheck();

        if(myRole.contentEquals("Admin")){
            setContentView(R.layout.activity_home);

        } else if(myRole.contentEquals("Data Collection Agent")){
            setContentView(R.layout.activity_data_collection_home);

        } else if(myRole.contentEquals("Data Verification Agent")){
            setContentView(R.layout.activity_data_verification_home);
        }

        db = FirebaseFirestore.getInstance();
        GetCountyId();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_more_vert_white_24dp);
        toolbar.setOverflowIcon(drawable);

        editBtn=(FloatingActionButton) findViewById(R.id.floatingActionButton);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        if(myRole.contentEquals("Admin")){
            adapter = new TabsPagerAdapter(getSupportFragmentManager());
            adapter.addFragment(new CreateFragment(), "Tab 1");
            adapter.addFragment(new ViewFragment(), "Tab 2");
            adapter.addFragment(new StreetFragment(), "Tab 3");
            adapter.addFragment(new MemberFragment(), "Tab 4");
            GetCountyId();

        } else if(myRole.contentEquals("Data Collection Agent")){
            adapter = new TabsPagerAdapter(getSupportFragmentManager());
            adapter.addFragment(new CreateFragment(), "Tab 1");
            //GetCountyId();

        } else if(myRole.contentEquals("Data Verification Agent")){
            adapter = new TabsPagerAdapter(getSupportFragmentManager());
            adapter.addFragment(new MemberFragment(), "Tab 4");
            //adapter.addFragment(new ViewFragment(), "Tab 2");
            //GetCountyId();
        }

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, AddressActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();
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
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
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
                            }
                        } else {
                            Toast.makeText(HomeActivity.this, "Get Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        db.collection("counties").whereEqualTo("county", myCounty)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                addressCountyId = document.getId();
                            }
                        } else {
                            Toast.makeText(HomeActivity.this, "Get Failed", Toast.LENGTH_LONG).show();
                        }
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
                                Intent i = new Intent(HomeActivity.this, MainActivity.class);
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
                            Intent i = new Intent(HomeActivity.this, MainActivity.class);
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
