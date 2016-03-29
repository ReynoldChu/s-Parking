package com.example.a4789.s_parking;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Created by a4789 on 2016/1/27.
 */
public class GetSpaceData {

    private String email;
    private int parkNumber;
    JSONObject jsonObject;
    Connect connect=new Connect();
    String connectReservationResult;
    String reservationUrl="http://140.134.26.143:9427/sparking/parkCar.php";

    public  JSONObject doDetermine(String email,int parkNumber) throws ExecutionException, InterruptedException {

        this.email=email;
        this.parkNumber=parkNumber;

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
        protected void onPostExecute(Object o) {        }

        @SuppressWarnings("ResourceType")
        @Override
        protected Object doInBackground(Object[] params) {
            String userEmail ="email="+email+"&parkNumber="+parkNumber;
            System.out.println(userEmail);
            connectReservationResult=connect.doPost(reservationUrl,userEmail,null,null,"UTF-8");
            System.out.println(connectReservationResult);
            try {
                jsonObject= new JSONObject(connectReservationResult);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

