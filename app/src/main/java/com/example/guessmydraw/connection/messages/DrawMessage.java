package com.example.guessmydraw.connection.messages;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * message that contains the information necessary to have the current player's drawing reproduced on the opponent's display
 */
public class DrawMessage implements Parcelable {

    public static byte NET_ID = 1;

    private int motionEventAction = -1;
    private float currentX = 0f;
    private float currentY = 0f;
    private float x2 = 0f;
    private float y2 = 0f;
    private int paintColor;

    @NonNull
    @Override
    public String toString() {

        String eventAction = "";
        switch (motionEventAction){
            case 0: eventAction = "DOWN";
                break;
            case 1: eventAction = "UP";
                break;
            case 2: eventAction = "MOVE";
                break;
        }

        return "DrawMessage{" +
                "currentX =" + currentX +
                ", currentY =" + currentY +
                ", x2 =" + x2 +
                ", y2 =" + y2 +
                ", motionEventAction =" + eventAction +
                ", color = " + paintColor +
                '}';
    }

    public DrawMessage() {}

    public DrawMessage(float currentX, float currentY, float x2, float y2, int motionEventAction, int paintColor) {
        this.currentX = currentX;
        this.currentY = currentY;
        this.x2 = x2;
        this.y2 = y2;
        this.motionEventAction = motionEventAction;
        this.paintColor = paintColor;
    }


    public float getCurrentX() {
        return currentX;
    }

    public void setCurrentX(float currentX) {
        this.currentX = currentX;
    }

    public float getCurrentY() {
        return currentY;
    }

    public void setCurrentY(float currentY) {
        this.currentY = currentY;
    }

    public float getX2() {
        return x2;
    }

    public void setX2(float x2) {
        this.x2 = x2;
    }

    public float getY2() {
        return y2;
    }

    public void setY2(float y2) {
        this.y2 = y2;
    }

    public int getMotionEventAction() {
        return motionEventAction;
    }

    public void setMotionEventAction(int motionEventAction) {
        this.motionEventAction = motionEventAction;
    }

    public int getPaintColor() {
        return paintColor;
    }

    public void setPaintColor(int paintColor) {
        this.paintColor = paintColor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(NET_ID);
        dest.writeFloat(this.currentX);
        dest.writeFloat(this.currentY);
        dest.writeFloat(this.x2);
        dest.writeFloat(this.y2);
        dest.writeInt(this.motionEventAction);
        dest.writeInt(this.paintColor);
    }

    public void readFromParcel(Parcel source) {
        this.currentX = source.readFloat();
        this.currentY = source.readFloat();
        this.x2 = source.readFloat();
        this.y2 = source.readFloat();
        this.motionEventAction = source.readInt();
        this.paintColor = source.readInt();
    }

    protected DrawMessage(Parcel in) {
        in.readByte(); // dropped
        this.currentX = in.readFloat();
        this.currentY = in.readFloat();
        this.x2 = in.readFloat();
        this.y2 = in.readFloat();
        this.motionEventAction = in.readInt();
        this.paintColor = in.readInt();
    }

    public static final Creator<DrawMessage> CREATOR = new Creator<DrawMessage>() {
        @Override
        public DrawMessage createFromParcel(Parcel source) {
            return new DrawMessage(source);
        }

        @Override
        public DrawMessage[] newArray(int size) {
            return new DrawMessage[size];
        }
    };
}
