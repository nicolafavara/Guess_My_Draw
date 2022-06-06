package com.example.guessmydraw.connection.messages;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Message used to send one's nickname to the opponent
 */
public class HandshakeMessage implements Parcelable {

    public static byte NET_ID = 0;
    private String playersName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeByte(NET_ID);
        dest.writeString(this.playersName);
    }

    public void readFromParcel(Parcel source) {

        this.playersName = source.readString();
    }

    public HandshakeMessage() {
    }

    protected HandshakeMessage(Parcel in) {
        in.readByte(); // dropped
        this.playersName = in.readString();
    }

    public String getPlayersName() {
        return playersName;
    }

    public void setPlayersName(String playersName) {
        this.playersName = playersName;
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
