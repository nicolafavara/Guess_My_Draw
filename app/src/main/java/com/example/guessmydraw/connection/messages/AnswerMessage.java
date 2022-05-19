package com.example.guessmydraw.connection.messages;

import android.os.Parcel;
import android.os.Parcelable;

public class AnswerMessage implements Parcelable {

    public static byte NET_ID = 2;
    private String answer;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeByte(NET_ID);
        dest.writeString(this.answer);
    }

    public void readFromParcel(Parcel source) {
        this.answer = source.readString();
    }

    public AnswerMessage() {
    }

    protected AnswerMessage(Parcel in) {

        in.readByte(); // dropped
        this.answer = in.readString();
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public static final Parcelable.Creator<AnswerMessage> CREATOR = new Parcelable.Creator<AnswerMessage>() {
        @Override
        public AnswerMessage createFromParcel(Parcel source) {
            return new AnswerMessage(source);
        }

        @Override
        public AnswerMessage[] newArray(int size) {
            return new AnswerMessage[size];
        }
    };
}
