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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class AlterUserData extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String alterUrl ="http://140.134.26.143:9427/sparking/alter.php";
    private String getDataUrl ="http://140.134.26.143:9427/sparking/getdata.php";
    private Toolbar toolbar_AlterUserData;
    private DrawerLayout drawerLayout_Menu;
    private NavigationView navigationView_Menu;
    private Button button_Alter;
    private EditText editText_Password, editText_Username, editText_Phone;
    private TextView textView_Email, textView_navigationViewName,textView_navigationViewEmail;
    private String connectResult, Userdata,statemsg;
    private ProgressDialog pDialog;
    private Connect connect=new Connect();
    private Intent intoMain = new Intent(),intoOutsiteNav = new Intent(),intoAlt = new Intent(), intoResData = new Intent();
    private JSONObject jsonObject;
    private Bundle alterUserDataBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alter_user_data);
        WaitUserData();
        InterfaceInit();
        AssignAction();

    }

    private void InterfaceInit()  {
        drawerLayout_Menu = (DrawerLayout) findViewById(R.id.DrawerLayout_Menu);
        toolbar_AlterUserData = (Toolbar) findViewById(R.id.Toolbar_AlterUserData);
        button_Alter = (Button)findViewById(R.id.Button_Alter);
        textView_Email =(TextView)findViewById(R.id.TextView_Email);
        editText_Password =(EditText)findViewById(R.id.EditText_Password);
        editText_Username =(EditText)findViewById(R.id.EditText_Username);
        editText_Phone =(EditText)findViewById(R.id.EditText_Phone);
        navigationView_Menu = (NavigationView) findViewById(R.id.NavigationView_Menu);

        setSupportActionBar(toolbar_AlterUserData);
        textView_Email.setText(alterUserDataBundle.getString("email"));
        editText_Password.setText(alterUserDataBundle.getString("password"));
        editText_Username.setText(alterUserDataBundle.getString("username"));
        editText_Phone.setText(alterUserDataBundle.getString("phone"));
        intoMain.setClass(this, MainActivity.class);
        intoAlt.setClass(this,AlterUserData.class);
        intoOutsiteNav.setClass(this,OutsideNavigation.class);
        intoResData.setClass(this,ReservationDataActivity.class);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout_Menu, toolbar_AlterUserData, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout_Menu.setDrawerListener(toggle);
        toggle.syncState();
        navigationView_Menu.setNavigationItemSelectedListener(this);

        if(navigationView_Menu.getHeaderCount() > 0) {
            View menuHeader = navigationView_Menu.getHeaderView(0);
            textView_navigationViewName =(TextView)menuHeader.findViewById(R.id.TextView_Name);
            textView_navigationViewEmail=(TextView)menuHeader.findViewById(R.id.TextView_Email);
            textView_navigationViewName.setText(alterUserDataBundle.getString("username"));
            textView_navigationViewEmail.setText(alterUserDataBundle.getString("email"));
        }

    }
    private void WaitUserData() {
        alterUserDataBundle =this.getIntent().getExtras();
        GetData getData=new GetData();
        getData.execute();
        try {
            getData.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void AssignAction() {
        button_Alter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if ("".equals(editText_Password.getText().toString().trim())) {
                    statemsg = "密碼不能為空";
                    Toast.makeText(getBaseContext(), statemsg, Toast.LENGTH_SHORT).show();
                } else {
                    Alter alter = new Alter();
                    alter.execute();
                }
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
            intoOutsiteNav.putExtras(alterUserDataBundle);
            startActivity(intoOutsiteNav);
        }  else if (id == R.id.NavigationView_reservation) {
            intoResData.putExtras(alterUserDataBundle);
            startActivity(intoResData);
        } else if (id == R.id.NavigationView_AlterUserData) {
            intoAlt.putExtras(alterUserDataBundle);
            startActivity(intoAlt);
        } else if (id == R.id.NavigationView_AboutUs) {

        } else if (id == R.id.NavigationView_SignOut) {
            startActivity(intoMain);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.DrawerLayout_Menu);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class Alter extends AsyncTask
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AlterUserData.this);
            pDialog.setMessage("Alter...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Object o) {
            pDialog.dismiss();
            Toast.makeText(getBaseContext(), statemsg, Toast.LENGTH_SHORT).show();
        }

        @SuppressWarnings("ResourceType")
        @Override
        protected Object doInBackground(Object[] params) {

            Userdata = "";
            Userdata = "email=" + textView_Email.getText().toString() + "&password=" + editText_Password.getText().toString();
            Userdata += "&username=" + editText_Username.getText().toString() + "&phone=" + editText_Phone.getText().toString();
            System.out.println(Userdata);
            connectResult = connect.doPost(alterUrl, Userdata, null, null, "utf8");

            if (connectResult.equals("0")) {
                statemsg="修改成功";
                intoAlt.putExtras(alterUserDataBundle);
                startActivity(intoAlt);

            } else if (connectResult.equals("1")) {
                statemsg="修改失敗";
            } else {
                statemsg="連線錯誤!!!";
            }

            return null;
        }
    }

    class GetData extends AsyncTask
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AlterUserData.this);
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
            GetUserData getUserData=new GetUserData(alterUserDataBundle.getString("email"));
            jsonObject=getUserData.getdata();
            try {
            int state= jsonObject.getInt("state");
                if(state == 0) {
                    alterUserDataBundle.putString("username", jsonObject.getString("username"));
                    alterUserDataBundle.putString("password", jsonObject.getString("password"));
                    alterUserDataBundle.putString("email", jsonObject.getString("email"));
                    alterUserDataBundle.putString("phone", jsonObject.getString("phone"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }



}
