package com.example.guessmydraw.utilities;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TimerModelView extends ViewModel {

    private final static String TAG = "TimerModelView";

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final MutableLiveData<Long> timerLiveData = new MutableLiveData<>(60000L);
    private CountDownTimer countDownTimer;

    public TimerModelView() {}

    public LiveData<Long> getTimerLiveData() {
        return timerLiveData;
    }

    public void requestTimer() {

        mainHandler.post(() -> {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }

            Log.d(TAG, "starting timer...");
            countDownTimer = new CountDownTimer(60000, 1000) {

                public void onTick(long millisUntilFinished) {
                    timerLiveData.setValue(millisUntilFinished);
                }

                public void onFinish() {
                    timerLiveData.setValue(0L);
                }
            };
            countDownTimer.start();
        });
    }

    public void cancelTimer(){
        mainHandler.post(() -> {
            if (countDownTimer != null) {
                Log.d(TAG, "Cancelling timer...");
                countDownTimer.cancel();
            }
        });
        timerLiveData.setValue(60000L);
    }
}
