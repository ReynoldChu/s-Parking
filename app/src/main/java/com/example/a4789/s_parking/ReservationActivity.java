package com.example.a4789.s_parking;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

public class ReservationActivity extends AppCompatActivity
        implements OnMapReadyCallback,NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar_Reservation;
    private DrawerLayout drawerLayout_Menu;
    private NavigationView navigationView_Menu;
    private TextView textView_navigationViewName,textView_navigationViewEmail;
    private Intent intoMain = new Intent(),intoAlt = new Intent(), intoRes=new Intent(),intoOutsiteNav=new Intent();
    private Bundle outsideReservationBundle,parkNumberBundle;
    private MapFragment mapFragment;
    private GoogleMap myMap;
    private LatLng parkingPlace;
    private LatLng myPlace;
    private String myAddress;
    private boolean getService = false;
    private String reservationMessage ,parkNumberKey;
    private int parkNumber,parkingAmount,count;
    int recommendState=1;
    private int[] parkNumberArray;
    double[] distanceArray=new double[100];
    double distanceMax;
    int[] priceArray=new int[100];
    int priceMax;
    Button button_gotoreservation,button_recommend;
    private  ParkingData parkingDataArray[];

    GPSTracker userGPS;
    Connect connect=new Connect();
    private ProgressDialog pDialog;
    String statemsg,parkingData,connectResult,selectParkingName;
    String getParkingdataurl ="http://140.134.26.143:9427/sparking/getParkingdata.php";
    String reservationUrl="http://140.134.26.143:9427/sparking/reservation.php";
    JSONObject jsonObject;
    int state, selectParkingID;
    Bundle bundle=new Bundle();
    AlertDialog.Builder navigationBuilder,reservationBuilder;
    CalculateDistanceGPS calculateDistanceGPS=new CalculateDistanceGPS();
    Spinner spinner_searchDistance;
    String[] spinnerItemSearch;
    double parkingDistance;
    int[] parkingPlaceMarkerNumber=new int[]{R.mipmap.parkingplaceone,R.mipmap.parkingplacetwo,R.mipmap.parkingplacethree,R.mipmap.parkingplacefour,R.mipmap.parkingplacefive};
    int searchRange=0 , basisRange=5000;

    //reservation dialog massage use
    final CharSequence[] itemReservation = {" Distance "," Parking Free "," Price "};
    boolean itemReservationInitial[] = {true,true,true};
    // arraylist to keep the selected items
    final ArrayList seletedItemReservation=new ArrayList();
    RecommendAlgorithm recommendAlgorithm ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reservation);

        InterfaceInit();


        getMyGPS();



    }
    private void InterfaceInit()  {
        outsideReservationBundle=this.getIntent().getExtras();
        navigationBuilder = new AlertDialog.Builder(this);
        toolbar_Reservation = (Toolbar) findViewById(R.id.Toolbar_Reservation);
        drawerLayout_Menu = (DrawerLayout) findViewById(R.id.DrawerLayout_Menu);
        navigationView_Menu = (NavigationView) findViewById(R.id.NavigationView_Menu);
        mapFragment =  (MapFragment) getFragmentManager().findFragmentById(R.id.Fragment_OutsideMap);
        parkNumberBundle=new Bundle();
        setSpinner();
        setButton();


        mapFragment.getMapAsync(this);
        intoMain.setClass(this, MainActivity.class);
        intoAlt.setClass(this, AlterUserData.class);
        intoRes.setClass(this,ReservationDataActivity.class);
        intoOutsiteNav.setClass(this,OutsideNavigation.class);
        navigationView_Menu.setNavigationItemSelectedListener(this);
        setSupportActionBar(toolbar_Reservation);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle
                (this, drawerLayout_Menu, toolbar_Reservation, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout_Menu.setDrawerListener(toggle);
        toggle.syncState();



        if(navigationView_Menu.getHeaderCount() > 0) {
            View menuHeader = navigationView_Menu.getHeaderView(0);
            textView_navigationViewName =(TextView)menuHeader.findViewById(R.id.TextView_Name);
            textView_navigationViewEmail=(TextView)menuHeader.findViewById(R.id.TextView_Email);
            textView_navigationViewName.setText(outsideReservationBundle.getString("username"));
            textView_navigationViewEmail.setText(outsideReservationBundle.getString("email"));
        }
    }
    public void setButton(){
        button_recommend=(Button)findViewById(R.id.Button_Recommend);
        button_recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlertDialog();
            }
        });
    }
    public void setSpinner(){

        spinner_searchDistance=(Spinner)findViewById(R.id.Spinner_SearchDistance);
        spinnerItemSearch=new String[]{"5 km","10 km","15 km","20 km"};
        spinner_searchDistance.setAdapter(new ArrayAdapter(this,android.R.layout.simple_spinner_item, spinnerItemSearch));
        spinner_searchDistance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchRange = basisRange * (position + 1);
                if (myPlace != null) {
                    resetMarker();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }
    public void setAlertDialog(){
        reservationBuilder=new AlertDialog.Builder(ReservationActivity.this);
        reservationBuilder.setTitle("Please select recommendation preference :");

        reservationBuilder.setMultiChoiceItems(itemReservation, itemReservationInitial, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    if (which == 0) {
                        itemReservationInitial[0] = true;
                    } else if (which == 1) {
                        itemReservationInitial[1] = true;
                    } else if (which == 2) {
                        itemReservationInitial[2] = true;
                    }
                } else {
                    if (which == 0) {
                        itemReservationInitial[0] = false;
                    } else if (which == 1) {
                        itemReservationInitial[1] = false;
                    } else if (which == 2) {
                        itemReservationInitial[2] = false;
                    }
                }
            }
        });
        reservationBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*
                button_recommend.setText("Recommend :\n");
                if (itemReservationInitial[0]) {
                    button_recommend.setText(button_recommend.getText() + " Distance");
                }
                if (itemReservationInitial[1]) {
                    button_recommend.setText(button_recommend.getText() + " ParkingFree");
                }
                if (itemReservationInitial[2]) {
                    button_recommend.setText(button_recommend.getText() + " Price");
                }
                */
                resetMarker();
            }
        });
        reservationBuilder.setCancelable(false);
        reservationBuilder.show();
        /*
        reservationBuilder=new AlertDialog.Builder(ReservationActivity.this)
                .setTitle("Recommend")
                .setMessage("Please select recommendation preference :")
                .setNeutralButton("Distance", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        recommendState = 1;
                        button_recommend.setText("Recommend :\n Distance");
                        resetMarker();
                    }
                })
                .setNegativeButton("Space", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        recommendState = 2;
                        button_recommend.setText("Recommend :\n Space");
                        resetMarker();
                    }
                }).setPositiveButton("Price", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        recommendState = 3;
                        button_recommend.setText("Recommend :\n Price");
                        resetMarker();
                    }
                });

        reservationBuilder.show();
*/
    }

    public void getMyGPS(){
        userGPS = new GPSTracker(this);

        if(userGPS.canGetLocation()) {
            Double longitude = userGPS.getLongitude();
            Double latitude = userGPS.getLatitude();
            myPlace=new LatLng(latitude,longitude);
            myAddress=myPlace.latitude+","+myPlace.longitude;

        } else {
            Toast.makeText(this, "please enable GPS locating service", Toast.LENGTH_LONG).show();
            userGPS.showSettingsAlert();
        }
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
        getMenuInflater().inflate(R.menu.alter_user_data, menu);
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
            intoOutsiteNav.putExtras(outsideReservationBundle);
            startActivity(intoOutsiteNav);
        }  else if (id == R.id.NavigationView_reservation) {
            intoRes.putExtras(outsideReservationBundle);
            startActivity(intoRes);
        } else if (id == R.id.NavigationView_AlterUserData) {
            intoAlt.putExtras(outsideReservationBundle);
            startActivity(intoAlt);
        } else if (id == R.id.NavigationView_AboutUs) {

        } else if (id == R.id.NavigationView_SignOut) {
            startActivity(intoMain);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.DrawerLayout_Menu);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    class Reservation extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ReservationActivity.this);
            pDialog.setMessage("Reservation in...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Object o) {
            pDialog.dismiss();
            //Toast.makeText(getBaseContext(), statemsg, Toast.LENGTH_SHORT).show();

        }

        @SuppressWarnings("ResourceType")
        @Override
        protected Object doInBackground(Object[] params) {

            parkingData ="email="+outsideReservationBundle.getString("email")+"&parkNumber="+ selectParkingID;
            System.out.println(parkingData);
            connectResult=connect.doPost(reservationUrl, parkingData,null,null,"UTF-8");
            try {
                jsonObject= new JSONObject(connectResult);
                state=jsonObject.getInt("state");

                if(state==0) {
                    outsideReservationBundle.putString("spaceNumber",jsonObject.getString("spaceNumber"));
                    outsideReservationBundle.putString("email",jsonObject.getString("email"));
                   /* reservationMessage =
                            "您已預約成功，以下是您的預約資訊:\n"+
                                    "會員:\t"+jsonObject.getString("email")+
                                    "\n預約的停車場:\t"+selectParkingName+
                                    "\n預約的車位是:\t"+jsonObject.getInt("spaceNumber");*/
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

    class GetParkingData extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ReservationActivity.this);
            pDialog.setMessage("GetParkingData in...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Object o) {
            pDialog.dismiss();
            //Toast.makeText(getBaseContext(), statemsg, Toast.LENGTH_SHORT).show();

        }

        @SuppressWarnings("ResourceType")
        @Override
        protected Object doInBackground(Object[] params) {


            parkingData ="park_number="+parkNumber;
            connectResult=connect.doPost(getParkingdataurl, parkingData,null,null,"UTF-8");
            System.out.println(connectResult);
            try {
                jsonObject= new JSONObject(connectResult);
                state=jsonObject.getInt("state");

                if(state==0) {
                    bundle.putInt("park_number", jsonObject.getInt("park_number"));
                    bundle.putString("park_name", jsonObject.getString("park_name"));
                    bundle.putInt("park_max", jsonObject.getInt("park_max"));
                    bundle.putInt("park_emptyspaces", jsonObject.getInt("park_emptyspaces"));
                    bundle.putString("park_latitude", jsonObject.getString("park_latitude"));
                    bundle.putString("park_longitude", jsonObject.getString("park_longitude"));
                    bundle.putString("email",outsideReservationBundle.getString("email"));
                    bundle.putString("park_address", jsonObject.getString("park_address"));
                    bundle.putInt("park_price", jsonObject.getInt("park_price"));

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







    public void initialMarker(){
        if(myPlace!=null){
            myMap.addMarker(new MarkerOptions().position(myPlace).title("My place").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }


        parkingAmount=5;    //未來上網取得
        parkNumberArray=new int[parkingAmount];


        for(int i=0;i<parkingAmount;i++){
            parkNumberArray[i]=i+1;
        }



        parkingDataArray=new ParkingData[parkingAmount];



        GetParkingData g;

        for (count = 0; count < parkingAmount; count++) {

            parkNumber = parkNumberArray[count];
            System.out.println(parkNumber);

            g = new GetParkingData();
            g.execute();
            try {
                g.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            pDialog.dismiss();

            parkingPlace = new LatLng(Double.parseDouble(bundle.getString("park_latitude")), Double.parseDouble(bundle.getString("park_longitude")));
            parkingDistance=calculateDistanceGPS.CalculateDistance(myPlace, parkingPlace);
            System.out.println(parkingDistance);


            parkNumberKey = bundle.getString("park_name");
            parkNumberBundle.putInt(parkNumberKey, parkNumber);


            parkingDataArray[count] = new ParkingData();
            parkingDataArray[count].setParkingName(parkNumberKey);
            parkingDataArray[count].setDistance(parkingDistance);
            parkingDataArray[count].setParkingPlace(parkingPlace);
            parkingDataArray[count].setEmptySpaces(bundle.getInt("park_emptyspaces"));
            parkingDataArray[count].setParkingMax(bundle.getInt("park_max"));
            parkingDataArray[count].setParkingAddress(bundle.getString("park_address"));
            parkingDataArray[count].setPrice(bundle.getInt("park_price"));
            parkingDataArray[count].setSpaceRate(1-((double) bundle.getInt("park_emptyspaces") / (double) bundle.getInt("park_max")));
            parkingDataArray[count].setParkingNumber(parkNumber);


            distanceArray[count]=parkingDistance;
            priceArray[count]=bundle.getInt("park_price");

        }

        Arrays.sort(distanceArray);
        distanceMax=distanceArray[distanceArray.length-1];
        Arrays.sort(priceArray);
        priceMax=priceArray[distanceArray.length-1];

        for(int i=0;i<parkingAmount;i++){
            parkingDataArray[i].setDistanceRate(parkingDataArray[i].getDistance() / distanceMax);
            parkingDataArray[i].setPriceRate((double)parkingDataArray[i].getPrice() /(double) priceMax);
        }
        resetMarker();
    }

    public void resetMarker(){
        myMap.clear();
        if(myPlace!=null){
            myMap.addMarker(new MarkerOptions().position(myPlace).title("My place").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }

        for(int i=0;i<parkingAmount;i++){
            parkingDataArray[i].setBestRecommend(new RecommendAlgorithm(parkingDataArray[i], itemReservationInitial).calculateRecommend());
        }

        Arrays.sort(parkingDataArray);


        for (int i = 0; i < parkingDataArray.length; i++) {

            if(parkingDataArray[i].getDistance()<searchRange){
                if(i<5 ){
                    myMap.addMarker(new MarkerOptions().position(parkingDataArray[i].getParkingPlace()).title(parkingDataArray[i].getParkingName()).snippet("Total Parking Space:" + parkingDataArray[i].getParkingMax() + "\nAvailable Parking Space:" + parkingDataArray[i].getEmptySpaces() + "\nAddress:" + parkingDataArray[i].getParkingAddress() + "\nPrice:" + parkingDataArray[i].getPrice() + "NT Per Hour").icon(BitmapDescriptorFactory.fromResource(parkingPlaceMarkerNumber[i])));
                }
                else{
                    myMap.addMarker(new MarkerOptions().position(parkingDataArray[i].getParkingPlace()).title(parkingDataArray[i].getParkingName()).snippet("Total Parking Space:" + parkingDataArray[i].getParkingMax() + "\nAvailable Parking Space:" + parkingDataArray[i].getEmptySpaces() + "\nAddress:" + parkingDataArray[i].getParkingAddress() + "\nPrice:" + parkingDataArray[i].getPrice() + "NT Per Hour").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                }
            }

        }
    }



    public void setInfoWindowClick(){
        myMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                System.out.println("1111111: " + marker.getSnippet());
                StringTokenizer token = new StringTokenizer(marker.getSnippet(),":");
                token.nextToken();
                token.nextToken();
                String tempEmptySpace = token.nextToken().toString().substring(0, 1);
                System.out.println("22222222: " + tempEmptySpace);
                //parkingDataArray[Integer.parseInt(marker.getSnippet())].getEmptySpaces() > 0
                if (Integer.parseInt(tempEmptySpace)<=0) {
                    Toast.makeText(getApplication(),"sorry , Car Park full",Toast.LENGTH_LONG).show();
                    return;
                }

                selectParkingID = parkNumberBundle.getInt(marker.getTitle());
                reservationMessage = "Car Park Name:\t" + marker.getTitle() +
                        "\nAvailable:\t" + marker.getSnippet();

                bundle.putString("park_name", marker.getTitle());
                bundle.putString("park_longitude", String.valueOf(marker.getPosition().longitude));
                bundle.putString("park_latitude", String.valueOf(marker.getPosition().latitude));
                bundle.putString("email", outsideReservationBundle.getString("email"));
                bundle.putString("username", outsideReservationBundle.getString("username"));
                navigationBuilder.setTitle("Reservation");
                navigationBuilder.setMessage("The Car Park information is shown below: \nPlease confirm reservation\n\n" + reservationMessage);

                navigationBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Reservation r = new Reservation();
                        r.execute();
                        try {
                            r.get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        intoRes.putExtras(bundle);
                        startActivity(intoRes);
                    }
                });
                navigationBuilder.setNeutralButton("Cancel", null);
                navigationBuilder.show();


            }
        });
    }

    public void markerClick() {
        myMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (marker.getTitle().equals("My place")) {

                    myMap.setInfoWindowAdapter(null);
                } else {
                    myMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
                    setInfoWindowClick();
                }


                return false;
            }
        });
    }


    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {


        @Override
        public View getInfoWindow(Marker marker) {
            View infoWindow = getLayoutInflater().inflate(R.layout.my_infowindow, null);
            TextView title = ((TextView)infoWindow.findViewById(R.id.textView_title));
            title.setText(marker.getTitle());
            TextView snippet = ((TextView)infoWindow.findViewById(R.id.textView_snippet));
            snippet.setText(marker.getSnippet());
            button_gotoreservation = (Button) infoWindow.findViewById(R.id.button_gotoreservation);


            return infoWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;

        if (myPlace != null) {
            initialMarker();
            markerClick();
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPlace, 15.0f));
        }
    }
}