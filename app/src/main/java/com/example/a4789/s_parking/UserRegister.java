package com.example.a4789.s_parking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserRegister extends AppCompatActivity {

    private String url ="http://140.134.26.143:9427/sparking/register.php";
    private Button button_Register;
    private EditText editText_Username, editText_Password, editText_Phone, editText_Email;
    private String userData, connectResult,statemsg;
    private Toolbar toolbar_UserRegister;
    private ProgressDialog pDialog;
    private Connect connect =new Connect();
    private Intent intoMain = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        InterfaceInit();
        AssignAction();


    }

    private void InterfaceInit() {
        toolbar_UserRegister = (Toolbar) findViewById(R.id.toolbar);
        button_Register = (Button)findViewById(R.id.Button_Register);
        editText_Username =(EditText)findViewById(R.id.EditText_Username);
        editText_Password =(EditText)findViewById(R.id.EditText_Password);
        editText_Phone =(EditText)findViewById(R.id.EditText_Phone);
        editText_Email =(EditText)findViewById(R.id.EditText_Email);
        intoMain.setClass(this, MainActivity.class);
        setSupportActionBar(toolbar_UserRegister);

    }

    private void AssignAction() {
        button_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(editText_Email.getText().toString().trim()) || "".equals(editText_Password.getText().toString().trim())) {
                    statemsg = "帳號(信箱)、密碼不能為空";
                    Toast.makeText(getBaseContext(), statemsg, Toast.LENGTH_LONG).show();
                } else {
                    Register r = new Register();
                    r.execute();
                }
            }
        });
    }

    class Register extends AsyncTask
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UserRegister.this);
            pDialog.setMessage("Register...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Object o) {
            pDialog.dismiss();
            Toast.makeText(getBaseContext(), statemsg, Toast.LENGTH_LONG).show();
        }

        @SuppressWarnings("ResourceType")
        @Override
        protected Object doInBackground(Object[] params) {


            userData = "";
            userData = "email=" + editText_Email.getText().toString() + "&password=" + editText_Password.getText().toString();
            userData += "&phone=" + editText_Phone.getText().toString() + "&username=" + editText_Username.getText().toString();
            System.out.println(userData);
            connectResult = connect.doPost(url, userData, null, null, "UTF-8");

            if (connectResult.equals("0")) {
                statemsg="註冊成功";
                startActivity(intoMain);
            } else if (connectResult.equals("1")) {
                statemsg="帳號重複";
            } else if (connectResult.equals("2")) {
                statemsg= "註冊失敗";
            } else {
                statemsg= "連線錯誤!!!";
            }

            return null;
        }
    }

}
