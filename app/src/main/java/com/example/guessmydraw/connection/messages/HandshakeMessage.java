package com.example.guessmydraw.connection.messages;

import android.os.Parcel;
import android.os.Parcelable;

public class HandshakeMessage implements Parcelable {

    public static byte NET_ID = 0;

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

    public HandshakeMessage() {
    }

    protected HandshakeMessage(Parcel in) {
    }

    public static final Creator<HandshakeMessage> CREATOR = new Creator<HandshakeMessage>() {
        @Override
        public HandshakeMessage createFromParcel(Parcel source) {
            return new HandshakeMessage(source);
        }

        @Override
        public HandshakeMessage[] newArray(int size) {
            return new HandshakeMessage[size];
        }
    };
}
