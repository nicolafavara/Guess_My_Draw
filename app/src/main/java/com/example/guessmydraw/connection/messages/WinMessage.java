package com.example.guessmydraw.connection.messages;

import android.os.Parcel;
import android.os.Parcelable;

public class WinMessage implements Parcelable  {

    public static byte NET_ID = 3;

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

    public WinMessage() {
    }

    protected WinMessage(Parcel in) {
    }

    public static final Parcelable.Creator<WinMessage> CREATOR = new Parcelable.Creator<WinMessage>() {
        @Override
        public WinMessage createFromParcel(Parcel source) {
            return new WinMessage(source);
        }

        @Override
        public WinMessage[] newArray(int size) {
            return new WinMessage[size];
        }
    };
}
