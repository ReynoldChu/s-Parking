package com.example.a4789.s_parking;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;

/**
 * Created by curtis on 5/31/2015.
 */
public class CalculateDistanceGPS {
    public double CalculateDistance(LatLng placeStart,LatLng placeEnd)
    {
        double radLatitude1 = placeStart.latitude * Math.PI / 180;
        double radLatitude2 = placeEnd.latitude * Math.PI / 180;
        double l = radLatitude1 - radLatitude2;
        double p = placeStart.longitude * Math.PI / 180 - placeEnd.longitude * Math.PI / 180;
        double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(l / 2), 2)
                + Math.cos(radLatitude1) * Math.cos(radLatitude2)
                * Math.pow(Math.sin(p / 2), 2)));
        distance = distance * 6378137.0;
        distance = Math.round(distance * 10000) / 10000;

        return distance ;
    }

    protected String ShowDistanceText(double distance)
    {
        if(distance < 1000 ) return String.valueOf((int)distance) + "m" ;
        else return new DecimalFormat("#.00").format(distance/1000) + "km" ;
    }
}
