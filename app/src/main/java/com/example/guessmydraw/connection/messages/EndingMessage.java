package com.example.guessmydraw.connection.messages;

import android.os.Parcel;
import android.os.Parcelable;

public class EndingMessage implements Parcelable {

    public static byte NET_ID = 5;

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

    public EndingMessage() {
    }

    protected EndingMessage(Parcel in) {
    }

    public static final Parcelable.Creator<EndingMessage> CREATOR = new Parcelable.Creator<EndingMessage>() {
        @Override
        public EndingMessage createFromParcel(Parcel source) {
            return new EndingMessage(source);
        }

        @Override
        public EndingMessage[] newArray(int size) {
            return new EndingMessage[size];
        }
    };
}
