package com.example.guessmydraw.utilities;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.example.guessmydraw.R;
import com.example.guessmydraw.fragments.DeviceList;

public class DisconnectionDialog extends DialogFragment {

    public static String TAG = "DisconnectionDialog";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setMessage(getString(R.string.conferm_disconnection))
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {

                    FragmentActivity activity = getActivity();
                    if (activity != null){
                        DeviceList.DeviceActionListener listener = (DeviceList.DeviceActionListener) activity;
                        listener.disconnect();
                    }
                    else {
                        Log.d("DEBUG", "getActivity is null. Disconnection failed.");
                    }

                } )
                .create();
    }
}