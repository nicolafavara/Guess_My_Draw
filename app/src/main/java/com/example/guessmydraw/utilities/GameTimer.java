package com.example.guessmydraw.utilities;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

public class GameTimer extends Thread {

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final TimerInterface callback;
    private final TextView timerTextView;
    private int counter;
    private CountDownTimer cTimer = null;

    public GameTimer(TextView timerTextView, TimerInterface callback){
        this.timerTextView = timerTextView;
        this.callback = callback;
        this.counter = 60;
    }

    @Override
    public void run() {

        Looper.prepare();
        startTimer();
    }

    public void startTimer() {

        mainHandler.post(() -> {
            cTimer = new CountDownTimer(60000, 1000) {

                public void onTick(long millisUntilFinished) {
                    timerTextView.setText(String.valueOf(counter));
                    counter--;
                }

                public void onFinish() {
                    Log.d("DEBUG", "Timer expired.");
                    callback.onTimerExpired();
                    cancelTimer();
                }
            };
            cTimer.start();
        });
    }

    public void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }

    public interface TimerInterface{
        void onTimerExpired();
    }
}
