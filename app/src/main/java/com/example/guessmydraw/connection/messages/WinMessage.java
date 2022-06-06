package com.example.guessmydraw.connection.messages;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Message used to inform the current player that the opponent has guessed correctly
 */
public class WinMessage implements Parcelable  {

    public static byte NET_ID = 3;
    private float remainingSeconds;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeByte(NET_ID);
        dest.writeFloat(this.remainingSeconds);
    }

    public void readFromParcel(Parcel source) {

        this.remainingSeconds = source.readFloat();
    }

    public WinMessage() {
    }

    protected WinMessage(Parcel in) {
        in.readByte(); // dropped
        this.remainingSeconds = in.readFloat();
    }

    public float getRemainingSeconds() {
        return remainingSeconds;
    }

    public void setRemainingSeconds(float remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
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
