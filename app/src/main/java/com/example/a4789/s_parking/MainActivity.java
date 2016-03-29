package com.example.a4789.s_parking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private String url ="http://140.134.26.143:9427/sparking/login.php";
    private Button button_Login;
    private EditText editText_Email,editText_Password;
    private TextView textView_RegisterLink,textView_Msg;
    private Toolbar toolbar_MainActivity;
    private ProgressDialog pDialog;
    private String userData,connectResult,statemsg;
    private Intent intoReg = new Intent(),intoMLV = new Intent(),intoAlt = new Intent(),intoResData=new Intent();
    private int state=99;
    private Bundle bundle = new Bundle();
    private JSONObject jsonObject;
    private Connect connect=new Connect();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InterfaceInit();
        AssignAction();

    }

    private void InterfaceInit() {
        toolbar_MainActivity = (Toolbar) findViewById(R.id.toolbar);
        button_Login = (Button)findViewById(R.id.Button_Login);
        editText_Email =(EditText)findViewById(R.id.EditText_Email);
        editText_Password =(EditText)findViewById(R.id.EditText_Password);
        textView_RegisterLink=(TextView)findViewById(R.id.TextView_RegisterLink);
        textView_Msg=(TextView)findViewById(R.id.TextView_Msg);

        setSupportActionBar(toolbar_MainActivity);
        intoReg.setClass(this,InsideNavigation.class);
        intoAlt.setClass(this, AlterUserData.class);
        intoResData.setClass(this, ReservationDataActivity.class);
    }

    private void AssignAction() {
        button_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login login = new Login();
                login.execute();

            }
        });

        textView_RegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("email", "1234");
                bundle.putString("name","1234");
                intoReg.putExtras(bundle);
                startActivity(intoReg);
            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    class Login extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Logging in...");
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

            userData ="email="+editText_Email.getText().toString()+"&password="+editText_Password.getText().toString();
            System.out.println(userData);
            connectResult=connect.doPost(url, userData,null,null,"UTF-8");
            try {
                jsonObject= new JSONObject(connectResult);
                state=jsonObject.getInt("state");

                if(state==0) {
                    bundle.putString("username",jsonObject.getString("username"));
                    bundle.putString("email",jsonObject.getString("email"));
                    statemsg="Login Success";
                    intoResData.putExtras(bundle);
                    startActivity(intoResData);
                }
                 else{
                    statemsg=jsonObject.getString("message");
                }


            } catch (JSONException e) {
                e.printStackTrace();
                statemsg="Connection Error!!!";
            }
            return null;
        }
    }

}
