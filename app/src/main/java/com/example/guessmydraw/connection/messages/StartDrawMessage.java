package com.example.guessmydraw.connection.messages;

import android.os.Parcel;
import android.os.Parcelable;

public class StartDrawMessage implements Parcelable {

    public static byte NET_ID = 7;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(NET_ID);
    }

    public void readFromParcel(Parcel source) {
    }

    public StartDrawMessage() {
    }

    protected StartDrawMessage(Parcel in) {
    }

    public static final Parcelable.Creator<StartDrawMessage> CREATOR = new Parcelable.Creator<StartDrawMessage>() {
        @Override
        public StartDrawMessage createFromParcel(Parcel source) {
            return new StartDrawMessage(source);
        }

        @Override
        public StartDrawMessage[] newArray(int size) {
            return new StartDrawMessage[size];
        }
    };
}
