package com.example.a4789.s_parking;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ice on 2016/3/7.
 */
public class ParkingData implements Comparable{
    String parkingName,parkingAddress;
    Double distance ,distanceRate, spaceRate ,priceRate,bestRecommend;
    int emptySpaces , price , parkingNumber ,parkingMax;
    LatLng parkingPlace;


    public ParkingData(){}




    public void setParkingName(String parkingName){
        this.parkingName=parkingName;
    }
    public  String getParkingName(){
        return parkingName;
    }

    public void setDistance(Double distance){
        this.distance=distance;
    }
    public  Double getDistance(){
        return distance;
    }

    public void setDistanceRate(Double distanceRate){
        this.distanceRate=distanceRate;
    }
    public  Double getDistanceRate(){
        return distanceRate;
    }

    public void setParkingAddress(String parkingAddress){
        this.parkingAddress=parkingAddress;
    }
    public  String getParkingAddress(){
        return parkingAddress;
    }

    public void setSpaceRate(Double spaceRate){
        this.spaceRate=spaceRate;
    }
    public  Double getSpaceRate(){
        return spaceRate;
    }

    public void setEmptySpaces(int emptySpaces) {
        this.emptySpaces = emptySpaces;
    }
    public  int getEmptySpaces(){
        return emptySpaces;
    }

    public void setPrice(int price) {
        this.price = price;
    }
    public  int getPrice(){
        return price;
    }

    public void setPriceRate(double priceRate) {
        this.priceRate = priceRate;
    }
    public  double getPriceRate(){
        return priceRate;
    }

    public void setParkingPlace(LatLng parkingPlace) {
        this.parkingPlace = parkingPlace;
    }
    public  LatLng getParkingPlace(){
        return parkingPlace;
    }

    public void setParkingNumber(int parkingNumber) {
        this.parkingNumber = parkingNumber;
    }
    public  int getParkingNumber(){
        return parkingNumber;
    }

    public void setParkingMax(int parkingMax) {
        this.parkingMax = parkingMax;
    }
    public  int getParkingMax(){
        return parkingMax;
    }


    public void setBestRecommend(Double bestRecommend) {
        this.bestRecommend = bestRecommend;
    }
    public  Double getBestRecommend(){
        return bestRecommend;
    }


    @Override
    public int compareTo(Object another) {
        ParkingData parkingDataCompare=(ParkingData)another;


        if(this.getBestRecommend()>parkingDataCompare.getBestRecommend()) {return  1;}
        else if (this.getBestRecommend()<parkingDataCompare.getBestRecommend()) {return -1;}
        else {return 0;}


    }
}
