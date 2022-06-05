package com.example.guessmydraw.connection.messages;

import android.os.Parcel;
import android.os.Parcelable;

/***
 * Message to be sent by the player who has to guess to the opponent informing him that the answer has been received.
 */
public class AckMessage implements Parcelable {

    public static byte NET_ID = 6;

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

    public AckMessage() {
    }

    protected AckMessage(Parcel in) {
    }

    public static final Parcelable.Creator<AckMessage> CREATOR = new Parcelable.Creator<AckMessage>() {
        @Override
        public AckMessage createFromParcel(Parcel source) {
            return new AckMessage(source);
        }

        @Override
        public AckMessage[] newArray(int size) {
            return new AckMessage[size];
        }
    };
}
