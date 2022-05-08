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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(requireContext())
                .setMessage(getString(R.string.conferm_disconnection))
                .setNegativeButton(getString(R.string.annulla), null)
                .setPositiveButton(getString(R.string.si), (dialog, which) -> {

                    FragmentActivity activity = getActivity();
                    if (activity != null){
                        ((DeviceList.DeviceActionListener) activity).disconnect();
                    }
                    else {
                        Log.d("DEBUG", "getActivity is null. Disconnection failed.");
                    }

                } )
                .create();
    }

    public static String TAG = "PurchaseConfirmationDialog";
}