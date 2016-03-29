package com.example.a4789.s_parking;

import java.util.Arrays;

/**
 * Created by yechinkiong on 2016/3/22.
 */
public class RecommendAlgorithm {
    ParkingData parkingData=new ParkingData();
    boolean recommendState[];
    Double alpha;
    Double beta;
    Double kama;
    Double bestRecommend;

    public RecommendAlgorithm(ParkingData parkingData,boolean recommendState[]){
        this.parkingData=parkingData;
        this.recommendState=recommendState;
        SetAlgorithm();
    }
    private void SetAlgorithm(){
        alpha = 0.0;
        beta = 0.0;
        kama = 0.0;
        //recommendState;
        int countTrue = 0, i ;
        double temp;
        if(recommendState[0]==true && recommendState[1]==true && recommendState[2]==true){
            alpha = 0.3;
            beta = 0.4;
            kama = 0.3;
        }else{
            i = 0;
            while (i<recommendState.length) {
                if(recommendState[i]){
                    countTrue++;
                }
                i++;
            }

            temp = 1.0 / countTrue;

            if(recommendState[0]==true){
                alpha = temp;
            }
            if(recommendState[1]==true){
                beta = temp;
            }
            if(recommendState[2]==true){
                kama = temp;
            }
        }
        System.out.println(alpha + " beta " + beta + " kama " + kama);


    }

    public double calculateRecommend(){


        bestRecommend=alpha*parkingData.getDistanceRate()+beta*parkingData.getSpaceRate()+kama*parkingData.getPriceRate();
        return bestRecommend;
    }

}
