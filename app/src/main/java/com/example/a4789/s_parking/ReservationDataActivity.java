package com.example.a4789.s_parking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class ReservationDataActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar_ReservationData;
    private DrawerLayout drawerLayout_Menu;
    private NavigationView navigationView_Menu;
    private TextView textView_navigationViewName,textView_navigationViewEmail,textView_ReservationInformation;
    private Button button_GotoNavigation,button_GotoReservation;
    private Bundle reservationDataBundle, parkingDataBundle;
    private Intent intoMain = new Intent(),intoOutsiteNav = new Intent(),intoAlt = new Intent(), intoResData = new Intent(),intoRes = new Intent();
    private JSONObject jsonObject;
    private ProgressDialog pDialog;
    private DetermineReserved determineReserved = new DetermineReserved();
    private int reservationState,parkingNumber,state;
    private String statemsg,connectResult,informationMessage;
    private Connect connect=new Connect();
    private String getParkingdataurl ="http://140.134.26.143:9427/sparking/getParkingdata.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_data);
        waitUserData();
        InterfaceInit();
        checkReservation();
    }
    private void InterfaceInit() {

        toolbar_ReservationData = (Toolbar) findViewById(R.id.Toolbar_ReservationData);
        drawerLayout_Menu = (DrawerLayout) findViewById(R.id.DrawerLayout_Menu);
        navigationView_Menu = (NavigationView) findViewById(R.id.NavigationView_Menu);
        textView_ReservationInformation=(TextView)findViewById(R.id.TextView_ReservationInformation);
        button_GotoNavigation=(Button)findViewById(R.id.Button_GotoNavigation);
        button_GotoReservation=(Button)findViewById(R.id.Button_GotoReservation);
        parkingDataBundle=new Bundle();

        intoMain.setClass(this, MainActivity.class);
        intoAlt.setClass(this,AlterUserData.class);
        intoOutsiteNav.setClass(this,OutsideNavigation.class);
        intoResData.setClass(this,ReservationDataActivity.class);
        intoRes.setClass(this, ReservationActivity.class);

        setSupportActionBar(toolbar_ReservationData);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout_Menu, toolbar_ReservationData, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout_Menu.setDrawerListener(toggle);
        toggle.syncState();
        navigationView_Menu.setNavigationItemSelectedListener(this);

        if(navigationView_Menu.getHeaderCount() > 0) {
            View menuHeader = navigationView_Menu.getHeaderView(0);
            textView_navigationViewName =(TextView)menuHeader.findViewById(R.id.TextView_Name);
            textView_navigationViewEmail=(TextView)menuHeader.findViewById(R.id.TextView_Email);
            textView_navigationViewName.setText(reservationDataBundle.getString("username"));
            textView_navigationViewEmail.setText(reservationDataBundle.getString("email"));
        }

    }
    private void waitUserData() {
        reservationDataBundle =this.getIntent().getExtras();
        GetData getData=new GetData();
        getData.execute();
        try {
            getData.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        pDialog.dismiss();
    }
    private void checkReservation() {
        try {
            jsonObject=determineReserved.doDetermine(reservationDataBundle.getString("email"));
            reservationState=jsonObject.getInt("state");
            parkingNumber=jsonObject.getInt("park_number");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(reservationState == 1){
            intoRes.putExtras(reservationDataBundle);
            startActivity(intoRes);
        }
        else{
            setInformation();
        }
    }
    private void setInformation() {
        GetParkingData g=new GetParkingData();
        g.execute();
        try {
            g.get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //informationMessage=new String("");
        informationMessage= "Reserved Car Park:\t"+parkingDataBundle.getString("park_name")+
                            "\nTotal Parking Space:\t" + parkingDataBundle.getInt("park_max")+
                            "\nAvailable Parking:\t" + parkingDataBundle.getInt("park_emptyspaces")+
                            "\nAddress:\t"+parkingDataBundle.getString("park_address")+
                            "\nPrice:\t"+parkingDataBundle.getString("park_price") + "NT Per Hour";
        textView_ReservationInformation.setText(informationMessage);
        button_GotoNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intoOutsiteNav.putExtras(reservationDataBundle);
                startActivity(intoOutsiteNav);
            }
        });

        button_GotoReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intoRes.putExtras(reservationDataBundle);
                startActivity(intoRes);
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.DrawerLayout_Menu);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.reservation_data, menu);
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

        if (id == R.id.NavigationView_navigation) {
            intoOutsiteNav.putExtras(reservationDataBundle);
            startActivity(intoOutsiteNav);
        }  else if (id == R.id.NavigationView_reservation) {
            intoResData.putExtras(reservationDataBundle);
            startActivity(intoResData);
        } else if (id == R.id.NavigationView_AlterUserData) {
            intoAlt.putExtras(reservationDataBundle);
            startActivity(intoAlt);
        } else if (id == R.id.NavigationView_AboutUs) {

        } else if (id == R.id.NavigationView_SignOut) {
            startActivity(intoMain);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.DrawerLayout_Menu);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class GetParkingData extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ReservationDataActivity.this);
            pDialog.setMessage("GetParkingData in...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Object o) {
            pDialog.dismiss();
           // Toast.makeText(getBaseContext(), "預約的停車場:\t"+parkingDataBundle.getString("park_name"), Toast.LENGTH_SHORT).show();
            //Toast.makeText(getBaseContext(), statemsg, Toast.LENGTH_SHORT).show();
        }

        @SuppressWarnings("ResourceType")
        @Override
        protected Object doInBackground(Object[] params) {


            connectResult=connect.doPost(getParkingdataurl, "park_number="+parkingNumber,null,null,"UTF-8");
            System.out.println(connectResult);
            try {
                jsonObject= new JSONObject(connectResult);
                state=jsonObject.getInt("state");

                if(state==0) {
                    parkingDataBundle.putInt("park_number", jsonObject.getInt("park_number"));
                    parkingDataBundle.putString("park_name", jsonObject.getString("park_name"));
                    parkingDataBundle.putInt("park_max", jsonObject.getInt("park_max"));
                    parkingDataBundle.putInt("park_emptyspaces", jsonObject.getInt("park_emptyspaces"));
                    parkingDataBundle.putString("park_latitude", jsonObject.getString("park_latitude"));
                    parkingDataBundle.putString("park_longitude", jsonObject.getString("park_longitude"));
                    parkingDataBundle.putString("park_address", jsonObject.getString("park_address"));
                    parkingDataBundle.putString("park_price", jsonObject.getString("park_price"));
                    statemsg=jsonObject.getString("message");
                }
                else{
                    statemsg=jsonObject.getString("message");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                statemsg="connection error!!!";
            }
            return null;
        }
    }

    class GetData extends AsyncTask
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ReservationDataActivity.this);
            pDialog.setMessage("Connection...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Object o) {
            pDialog.dismiss();
        }

        @SuppressWarnings("ResourceType")
        @Override
        protected Object doInBackground(Object[] params) {
            GetUserData getUserData=new GetUserData(reservationDataBundle.getString("email"));
            jsonObject=getUserData.getdata();
            try {
                int state= jsonObject.getInt("state");
                if(state == 0) {
                    reservationDataBundle.putString("username", jsonObject.getString("username"));
                    reservationDataBundle.putString("password", jsonObject.getString("password"));
                    reservationDataBundle.putString("email", jsonObject.getString("email"));
                    reservationDataBundle.putString("phone", jsonObject.getString("phone"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
