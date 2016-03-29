package com.example.a4789.s_parking;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Created by a4789 on 2016/1/21.
 */
public class GetCarData {
    private String email;
    JSONObject jsonObject;
    Connect connect=new Connect();
    String connectResult;
    String reservationUrl="http://140.134.26.143:9427/sparking/getCardata.php";

    public  JSONObject doDetermine(String email) throws ExecutionException, InterruptedException {

        this.email=email;
        Work w= new Work();
        w.execute();
        w.get();
        return jsonObject;
    }

    class Work extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected void onPostExecute(Object o) {}

        @SuppressWarnings("ResourceType")
        @Override
        protected Object doInBackground(Object[] params) {
            String userEmail ="email="+email;
            System.out.println(userEmail);
            connectResult=connect.doPost(reservationUrl,userEmail,null,null,"UTF-8");
            System.out.println(connectResult);
            try {
                jsonObject= new JSONObject(connectResult);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

