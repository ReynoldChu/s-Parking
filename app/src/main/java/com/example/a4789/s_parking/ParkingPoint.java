package com.example.a4789.s_parking;

/**
 * Created by KenChu on 2016/1/21.
 */
public class ParkingPoint {
    private float X;
    private float Y;
    private int trunPoint;// 上一個Trun點
    private int checkDirection; //確認方向 1=直 0=行;

    public ParkingPoint(float x, float y) {
        this.X = x;
        this.Y = y;
    }
    public ParkingPoint(float x, float y,int trunPoint,int checkDirection) {
        this.X = x;
        this.Y = y;
        this.trunPoint=trunPoint;
        this.checkDirection=checkDirection;
    }

    public void setX(float x) {
        this.X = x;
    }

    public void setY(float y) {
        this.Y = y;
    }

    public float getX() {
        return X;
    }

    public float getY() { return Y; }

    public int getTurnpaint() { return trunPoint; }

    public int getCheckDirection() { return checkDirection; }

}

