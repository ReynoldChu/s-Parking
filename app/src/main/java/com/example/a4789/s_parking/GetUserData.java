package com.example.a4789.s_parking;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by a4789 on 2015/12/16.
 */
class GetUserData{

    JSONObject jsonObject;
    String email;
    public GetUserData(String email){
        this.email=email;
    }
    public JSONObject getdata(){
        String connectResult, Userdata,statemsg;
        Connect connect= new Connect();

        Userdata = "";
        Userdata ="email="+email;
        System.out.println(Userdata);
        connectResult = connect.doPost("http://140.134.26.143:9427/sparking/getdata.php", Userdata, null, null, "UTF-8");

        try {
            jsonObject= new JSONObject(connectResult);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


}

