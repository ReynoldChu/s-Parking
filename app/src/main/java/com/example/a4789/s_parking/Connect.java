package com.example.a4789.s_parking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by a4789 on 2015/11/9.
 */
public class Connect {

    public String doPost(String sURL, String data, String cookie,
                         String referer, String charset) {

        System.out.println("start");

        boolean doSuccess = false;
        BufferedWriter wr = null;
        String result="";
        try {

            URL url = new URL(sURL);
            HttpURLConnection URLConn = (HttpURLConnection) url
                    .openConnection();

            URLConn.setDoOutput(true);
            URLConn.setDoInput(true);
            ((HttpURLConnection) URLConn).setRequestMethod("POST");
            URLConn.setUseCaches(false);
            URLConn.setAllowUserInteraction(true);
            HttpURLConnection.setFollowRedirects(true);
            URLConn.setInstanceFollowRedirects(true);

            if (cookie != null)
                URLConn.setRequestProperty("Cookie", cookie);
            if (referer != null)
                URLConn.setRequestProperty("Referer", referer);

            URLConn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            URLConn.setRequestProperty("Content-Length", String.valueOf(data
                    .getBytes().length));

            DataOutputStream dos = new DataOutputStream(URLConn
                    .getOutputStream());
            dos.writeBytes(data);

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(URLConn.getInputStream(),
                            charset));
            String line;
            while ((line = rd.readLine()) != null) {
                result+=line;
            }
            System.out.println("result"+result);
            rd.close();
        } catch (IOException e) {
            result = "false";
            System.out.println(e);

        } finally {
            if (wr != null) {
                try {
                    wr.close();
                } catch (IOException ex) {
                    System.out.println(ex);
                }
                wr = null;
            }
        }

        return result;
    }

    public String doGET(String urlStr){

        String myData;

        try
        {
            URL url = new URL(urlStr);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setDoOutput(true);
            http.setRequestMethod("GET");
            InputStream entity = http.getInputStream();


            if(entity != null){
                BufferedReader br = new BufferedReader(new InputStreamReader(entity));
                StringBuffer sb = new StringBuffer();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                String str = sb.toString();
                br.close();

                myData=str;

                return myData;
            }
            else{

                return "nodata";
            }
        }

        catch(Exception e){
            return "error";
        }
    }
}
