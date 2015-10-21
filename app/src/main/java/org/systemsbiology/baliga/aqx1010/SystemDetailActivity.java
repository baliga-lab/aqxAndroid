package org.systemsbiology.baliga.aqx1010;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.systemsbiology.baliga.aqx1010.apiclient.AqxSystemDetails;
import org.systemsbiology.baliga.aqx1010.apiclient.GetSystemDetailsTask;
import org.systemsbiology.baliga.aqx1010.apiclient.GetSystemDetailsTaskListener;
import org.systemsbiology.baliga.aqx1010.apiclient.GoogleTokenTask;

import java.io.IOException;
import java.text.DateFormat;

public class SystemDetailActivity extends AppCompatActivity
implements GetSystemDetailsTaskListener {

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private Toolbar toolbar;
    private String systemUID, systemName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        systemUID = getIntent().getStringExtra("system_uid");

        setContentView(R.layout.activity_system_detail);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        // The {@link android.support.v4.view.PagerAdapter} that will provide
        // fragments for each of the sections. We use a
        // {@link FragmentPagerAdapter} derivative, which will keep every
        // loaded fragment in memory. If this becomes too memory intensive, it
        // may be best to switch to a
        // {@link android.support.v4.app.FragmentStatePagerAdapter}.
        SectionsPagerAdapter sectionsPagerAdapter =
                new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        try {
            String email = GoogleTokenTask.storedEmail(this);
            if (GoogleTokenTask.isDeviceOnline(this)) {
                new GetSystemDetailsTask(this, email, systemUID, this).execute();
            } else {
                Toast.makeText(this, R.string.not_online, Toast.LENGTH_LONG).show();
            }

        } catch (IOException ex) {
            Log.e("aqx1010", "cound not retrieve mail", ex);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_system_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void detailsRetrieved(AqxSystemDetails details) {
        Log.d("aqx1010", String.format("System name: %s, created on: %s",
                details.name, details.creationDate.toString()));
        this.systemName = details.name;
        toolbar.setTitle(details.name);

        // We can update the fragment contents by retrieving their
        // elements using their id
        TextView startDateView = (TextView) this.findViewById(R.id.startDateView);
        if (details.startDate != null) {
            DateFormat dateFormat = DateFormat.getDateInstance();
            startDateView.setText(dateFormat.format(details.startDate));
        } else {
            startDateView.setText("-");
        }
        TextView aqxTechniqueView = (TextView) this.findViewById(R.id.aqxTechniqueView);
        aqxTechniqueView.setText(details.aqxTechnique);
        if (details.aquaticOrganisms.length > 0) {
            TextView aqorgView = (TextView) this.findViewById(R.id.aqorgView);
            aqorgView.setText(String.format("%s (%d)", details.aquaticOrganisms[0].name,
                    details.aquaticOrganisms[0].count));
        }
        if (details.crops.length > 0) {
            TextView cropView = (TextView) this.findViewById(R.id.cropView);
            cropView.setText(String.format("%s (%d)", details.crops[0].name,
                    details.crops[0].count));
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a OverviewFragment (defined as a static inner class below).
            if (position == 1) {
                return new MeasurementTypesFragment();
            }
            return new OverviewFragment();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "OVERVIEW";
                case 1:
                    return "MEASUREMENTS";
            }
            return null;
        }
    }

    public static class MeasurementTypesFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_measurement_types, container, false);
            ListView listView = (ListView) rootView.findViewById(R.id.measurementTypeListView);
            ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this.getContext(),
                    R.layout.meastype_list_item, R.id.measTypeTextView,
                    new String[] {"Light", "Temperature", "Dissolved Oxygen", "pH", "Ammonium", "Nitrate"});
            listView.setAdapter(listAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = null;
                    switch (position) {
                        case 0: // light
                            intent = new Intent(getContext(), MeasureLightActivity.class);
                            break;
                        case 1: // temp
                            intent = new Intent(getContext(), MeasureGenericActivity.class);
                            intent.putExtra("measure_type", "temp");
                            break;
                        case 2: // dio
                            intent = new Intent(getContext(), MeasureGenericActivity.class);
                            intent.putExtra("measure_type", "dio");
                            break;
                        case 3: // ph
                            intent = new Intent(getContext(), MeasureChemistryActivity.class);
                            intent.putExtra("measure_type", "ph");
                            break;
                        case 4: // ammonium
                            intent = new Intent(getContext(), MeasureChemistryActivity.class);
                            intent.putExtra("measure_type", "nh4");
                            break;
                        case 5: // nitrate
                            intent = new Intent(getContext(), MeasureChemistryActivity.class);
                            intent.putExtra("measure_type", "no3");
                            break;
                        default:
                            break;
                    }
                    SystemDetailActivity activity = (SystemDetailActivity) MeasurementTypesFragment.this.getActivity();
                    intent.putExtra("system_uid", activity.systemUID);
                    intent.putExtra("system_name", activity.systemName);
                    startActivity(intent);
                }
            });
            return rootView;
        }
    }

    public static class OverviewFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_system_detail, container, false);
            TextView creationDateView = (TextView) rootView.findViewById(R.id.startDateView);
            if (creationDateView != null) creationDateView.setText(R.string.title_activity_main);
            return rootView;
        }
    }
}
