package com.example.guessmydraw.connection.messages;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Message used to request match termination
 */
public class EndMatchRequestMessage implements Parcelable {

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

    public EndMatchRequestMessage() {
    }

    protected EndMatchRequestMessage(Parcel in) {
    }

    public static final Parcelable.Creator<EndMatchRequestMessage> CREATOR = new Parcelable.Creator<EndMatchRequestMessage>() {
        @Override
        public EndMatchRequestMessage createFromParcel(Parcel source) {
            return new EndMatchRequestMessage(source);
        }

        @Override
        public EndMatchRequestMessage[] newArray(int size) {
            return new EndMatchRequestMessage[size];
        }
    };
}
