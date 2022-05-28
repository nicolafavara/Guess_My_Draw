package com.example.guessmydraw.utilities;

import android.os.CountDownTimer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TimerModelView extends ViewModel {

    private CountDownTimer countDownTimer;
    private MutableLiveData<Long> timerLiveData;

    public TimerModelView() {
        this.timerLiveData = new MutableLiveData<>();
    }

    public LiveData<Long> getTimerLiveData() {
        return timerLiveData;
    }

    public void requestTimer(long timeInMins) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                timerLiveData.setValue(millisUntilFinished);
            }

            public void onFinish() {
                timerLiveData.setValue(0L);
            }
        };
        countDownTimer.start();
    }
}
