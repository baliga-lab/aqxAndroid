package org.systemsbiology.baliga.aqx1010;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.AccountPicker;

import org.systemsbiology.baliga.aqx1010.apiclient.AqxSystem;
import org.systemsbiology.baliga.aqx1010.apiclient.GetSystemListTask;
import org.systemsbiology.baliga.aqx1010.apiclient.GetSystemListTaskListener;
import org.systemsbiology.baliga.aqx1010.apiclient.GoogleTokenTask;
import org.systemsbiology.baliga.aqx1010.apiclient.SystemDefaults;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GetSystemListTaskListener {

    /* Google client id, currently we do not use it */
    private String email;

    private void getSystems() {
        if (email == null) pickUserAccount();
        else {
            if (GoogleTokenTask.isDeviceOnline(this)) {
                Log.d("aqx1010", "starting get system list task");
                new GetSystemListTask(MainActivity.this, email, this).execute();
            } else {
                Toast.makeText(this, R.string.not_online, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void pickUserAccount() {
        try {
            this.email = GoogleTokenTask.storedEmail(this);
            Log.d("aqx1010", "Found email: " + email);
            this.getSystems();
        } catch (IOException ex) {
            Log.d("aqx1010", "email not found, getting from account picker");
            String[] accountTypes = new String[] {"com.google"};
            Intent intent = AccountPicker.newChooseAccountIntent(null, null, accountTypes, false, null, null, null, null);
            startActivityForResult(intent, SystemDefaults.REQUEST_CODE_PICK_ACCOUNT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SystemDefaults.REQUEST_CODE_PICK_ACCOUNT) {
            if (resultCode == RESULT_OK) {
                this.email = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                try {
                    GoogleTokenTask.storeEmail(this, this.email);
                    this.getSystems();
                } catch (IOException ex) {
                    Log.e("aqx1010", "io exception", ex);
                }
            } else {
                Toast.makeText(this, R.string.pick_account, Toast.LENGTH_SHORT).show();

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        pickUserAccount();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_systems) {
            // Handle the camera action
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_signout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void systemListRetrieved(List<AqxSystem> systems) {
        ListView listView = (ListView) findViewById(R.id.systemListView);
        AqxSystem[] systemArray = systems.toArray(new AqxSystem[systems.size()]);

        ArrayAdapter<AqxSystem> listAdapter = new ArrayAdapter<>(this, R.layout.system_list_item,
                R.id.systemNameTextView, systemArray);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("aqx1010", "clicked");
                AqxSystem system = (AqxSystem) parent.getAdapter().getItem(position);
                Intent detailIntent = new Intent(MainActivity.this, SystemDetailActivity.class);
                detailIntent.putExtra("system_uid", system.uid);
                startActivity(detailIntent);
            }
        });

    }
}
