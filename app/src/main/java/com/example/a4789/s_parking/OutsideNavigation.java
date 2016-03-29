package com.example.a4789.s_parking;

import android.animation.AnimatorSet;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class OutsideNavigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,OnMapReadyCallback,LocationListener {

    private Toolbar toolbar_AlterUserData;
    private DrawerLayout drawerLayout_Menu;
    private NavigationView navigationView_Menu;
    private TextView textView_navigationViewName,textView_navigationViewEmail;
    private Intent intoMain = new Intent(),intoAlt = new Intent(),intoResData=new Intent(),intoInsideNav=new Intent();
    private Bundle outsideNavigationBundle;
    private MapFragment mapFragment;
    private GoogleMap myMap;
    private String parkingAddress="0,0";
    private LatLng myPlace=new LatLng(0,0);
    private String myAddress;
    private LocationManager locationService ;
    private String urlStr ;
    private GetCarData getCarData = new GetCarData();
    GPSTracker userGPS;
    AlertDialog.Builder navigationBuilder;
    LatLng parkingPlace=new LatLng(0,0);
    JSONObject jsonObject,carDataJsonObject;
    Intent intoRes=new Intent();
    private DetermineReserved determineReserved = new DetermineReserved();
    int parkingNumber,navigationState;
    String parkingPlaceName;
    Bundle insidedata = new Bundle();
    Message insidehandleMessage=new Message();
    int check=0;

    Thread thread=new Thread();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outside_navigation);

        InterfaceInit();
        checkReservation();
        determineNavigationState();
        thread.start();
        getMyGPS();



        locationService = (LocationManager) getSystemService(LOCATION_SERVICE); //??蝟餌絞摰???

        if (PackageManager.PERMISSION_GRANTED == getPackageManager().checkPermission("android.permission.ACCESS_FINE_LOCATION","com.example.a4789.s_parking")&& locationService.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            locationService.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        }
        else if (PackageManager.PERMISSION_GRANTED == getPackageManager().checkPermission("android.permission.ACCESS_COARSE_LOCATION","com.example.a4789.s_parking")&& locationService.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            locationService.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
        }
        else{}

    }
    private void InterfaceInit()  {
        outsideNavigationBundle=this.getIntent().getExtras();
        navigationBuilder = new AlertDialog.Builder(this);
        toolbar_AlterUserData = (Toolbar) findViewById(R.id.Toolbar_OutsideNavigation);
        drawerLayout_Menu = (DrawerLayout) findViewById(R.id.DrawerLayout_Menu);
        navigationView_Menu = (NavigationView) findViewById(R.id.NavigationView_Menu);
        mapFragment =  (MapFragment) getFragmentManager().findFragmentById(R.id.Fragment_OutsideMap);

        mapFragment.getMapAsync(this);
        intoMain.setClass(this, MainActivity.class);
        intoAlt.setClass(this, AlterUserData.class);
        intoResData.setClass(this, ReservationDataActivity.class);
        intoRes.setClass(this, ReservationActivity.class);
        intoInsideNav.setClass(this, InsideNavigation.class);
        setSupportActionBar(toolbar_AlterUserData);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle
                (this, drawerLayout_Menu, toolbar_AlterUserData, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout_Menu.setDrawerListener(toggle);
        toggle.syncState();
        navigationView_Menu.setNavigationItemSelectedListener(this);

        if(navigationView_Menu.getHeaderCount() > 0) {
            View menuHeader = navigationView_Menu.getHeaderView(0);
            textView_navigationViewName =(TextView)menuHeader.findViewById(R.id.TextView_Name);
            textView_navigationViewEmail=(TextView)menuHeader.findViewById(R.id.TextView_Email);
            textView_navigationViewName.setText(outsideNavigationBundle.getString("username"));
            textView_navigationViewEmail.setText(outsideNavigationBundle.getString("email"));
        }
    }
    private void checkReservation(){
        try {


            jsonObject=determineReserved.doDetermine(outsideNavigationBundle.getString("email"));
            navigationState=jsonObject.getInt("state");
            if(navigationState == 1) {
                setAlertDialog();
            }else{

                parkingNumber = jsonObject.getInt("park_number");
                parkingPlace = new LatLng(Double.parseDouble(jsonObject.getString("park_latitude")), Double.parseDouble(jsonObject.getString("park_longitude")));
                parkingPlaceName = jsonObject.getString("park_name");
            }

        }catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }

    public void setAlertDialog(){
        navigationBuilder=new AlertDialog.Builder(OutsideNavigation.this)
                .setTitle("No reservation data")
                .setMessage("Please make a reservation first")
                .setPositiveButton("Make reservation", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        intoRes.putExtras(outsideNavigationBundle);
                        startActivity(intoRes);
                    }
                });
        navigationBuilder.show();

    }



    public void getMyGPS(){
        userGPS = new GPSTracker(this);

        if(userGPS.canGetLocation()) {

            Double latitude = userGPS.getLatitude();
            Double longitude = userGPS.getLongitude();
            myPlace=new LatLng(latitude, longitude);
            myAddress=myPlace.latitude+","+myPlace.longitude;
           // Toast.makeText(getApplicationContext(),"Your Location is -\n: " + myAddress , Toast.LENGTH_SHORT).show();

        } else {
            userGPS.showSettingsAlert();
        }
    }




    public void determineNavigationState() {

        thread= new Thread(){
            public void run() {
                check = 0;
                while (check == 0) {
                    try {
                        thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        jsonObject = determineReserved.doDetermine(outsideNavigationBundle.getString("email"));
                        carDataJsonObject = getCarData.doDetermine(outsideNavigationBundle.getString("email"));
                        System.out.println("thread" + carDataJsonObject);

                        navigationState = jsonObject.getInt("state");
                        System.out.println("caronpark_state:" + carDataJsonObject.getInt("caronpark_state"));
                        if (carDataJsonObject.getInt("caronpark_state") == 1) {

                            System.out.println("123--------------------------");
                            h.sendMessage(new Message());
                            break;
                        }

                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        };

    }

    Handler h = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            System.out.println("456--------------------------");
            intoInsideNav.putExtras(outsideNavigationBundle);
            startActivity(intoInsideNav);
            check=1;
            finish();

        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String handleMessage = data.getString("handleMessage");//?key銝剔?摮葡摮val

            if (handleMessage.equals("nodata")) {
               Toast.makeText(getApplicationContext(), handleMessage, Toast.LENGTH_LONG).show();
            } else if (handleMessage.equals("error")) {
                Toast.makeText(getApplicationContext(), handleMessage, Toast.LENGTH_LONG).show();
            }
            else {
                try {
                    //?撠蝯?
                    JSONObject jsonObject = new JSONObject(handleMessage);
                    JSONArray routesObject = jsonObject.getJSONArray("routes");
                    JSONObject target = routesObject.getJSONObject(0);
                    JSONArray legsArray = target.getJSONArray("legs");
                    JSONObject legsObject = legsArray.getJSONObject(0);
                    JSONArray jsonArray = legsObject.getJSONArray("steps");
                    LatLng prePlace = myPlace;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        JSONObject loc = jObject.getJSONObject("end_location");

                        LatLng place = new LatLng(loc.getDouble("lat"), loc.getDouble("lng"));
                        draw(prePlace, place);
                        prePlace = place;
                    }
                } catch (JSONException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }


        }
    };


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //
            // TODO: http request.
            //
            Message handleMessage = new Message();
            Bundle data = new Bundle();

            String connectData;
            connectData = new Connect().doGET(urlStr);
            data.putString("handleMessage", connectData);
            handleMessage.setData(data);
            handler.sendMessage(handleMessage);

        }
    };


    public void draw(LatLng loc1, LatLng loc2) {
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.add(loc1, loc2);
        polylineOptions.color(Color.BLUE);
        Polyline polyline = myMap.addPolyline(polylineOptions);
        polyline.setWidth(5);
    }

    public void initialMarker(){
        myMap.addMarker(new MarkerOptions().position(myPlace).title("My place").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        System.out.println("parkingPlace:" + parkingPlace);
        myMap.addMarker(new MarkerOptions().position(parkingPlace).title(parkingPlaceName));

    }

    public void navigation(LatLng clickPlace){
        myMap.clear();
        initialMarker();
        parkingAddress = clickPlace.latitude + "," + clickPlace.longitude;
        urlStr = "http://maps.googleapis.com/maps/api/directions/json?origin=" + myAddress + "&destination=" + parkingAddress + "&sensor=false";
        System.out.println(urlStr);
        new Thread(runnable).start();
    }


    @Override
    public  void onDestroy(){
        super.onDestroy();
        if (PackageManager.PERMISSION_GRANTED == getPackageManager().checkPermission("android.permission.ACCESS_FINE_LOCATION", "com.example.a4789.s_parking")) {
            locationService.removeUpdates(this);
        }
        finish();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;

        getMyGPS();
        initialMarker();
        navigation(parkingPlace);

        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPlace, 15.0f));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.NavigationView_navigation) {

        }  else if (id == R.id.NavigationView_reservation) {
            intoResData.putExtras(outsideNavigationBundle);
            startActivity(intoResData);
        } else if (id == R.id.NavigationView_AlterUserData) {
            intoAlt.putExtras(outsideNavigationBundle);
            startActivity(intoAlt);
        } else if (id == R.id.NavigationView_AboutUs) {

        } else if (id == R.id.NavigationView_SignOut) {
            startActivity(intoMain);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.DrawerLayout_Menu);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onLocationChanged(Location location) {
        myMap.clear();
        getMyGPS();
        initialMarker();
        navigation(parkingPlace);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

