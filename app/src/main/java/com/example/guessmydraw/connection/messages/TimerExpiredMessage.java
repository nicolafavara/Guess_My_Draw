package com.example.guessmydraw.connection.messages;

import android.os.Parcel;
import android.os.Parcelable;

public class TimerExpiredMessage implements Parcelable {

    public static byte NET_ID = 4;

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

    public TimerExpiredMessage() {
    }

    protected TimerExpiredMessage(Parcel in) {
    }

    public static final Parcelable.Creator<TimerExpiredMessage> CREATOR = new Parcelable.Creator<TimerExpiredMessage>() {
        @Override
        public TimerExpiredMessage createFromParcel(Parcel source) {
            return new TimerExpiredMessage(source);
        }

        @Override
        public TimerExpiredMessage[] newArray(int size) {
            return new TimerExpiredMessage[size];
        }
    };
}
