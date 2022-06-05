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

/**
 * Disconnectio dialog to be showed during a match when player press back button
 */
public class DisconnectionDialog extends DialogFragment {

    public static String TAG = "DisconnectionDialog";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setMessage(getString(R.string.conferm_disconnection))
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {

                    FragmentActivity activity = requireActivity();
                    DeviceList.DeviceActionListener listener = (DeviceList.DeviceActionListener) activity;
                    listener.disconnect();

                }).create();
    }
}