package com.example.guessmydraw.utilities;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TimerModelView extends ViewModel {

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private CountDownTimer countDownTimer;
    private MutableLiveData<Long> timerLiveData;

    public TimerModelView() {
        this.timerLiveData = new MutableLiveData<>(60000L);
    }

    public LiveData<Long> getTimerLiveData() {
        return timerLiveData;
    }

    public void requestTimer() {

        mainHandler.post(() -> {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }

            Log.d("DEBUG", "starting timer.................................................");
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
                Log.d("DEBUG", "Cancelling timer.................................................");
                countDownTimer.cancel();
            }
        });
        timerLiveData.setValue(60000L);
    }
}
