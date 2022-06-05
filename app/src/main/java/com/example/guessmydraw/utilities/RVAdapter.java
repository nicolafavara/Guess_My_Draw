package com.example.guessmydraw.utilities;


import android.content.Context;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guessmydraw.R;
import com.example.guessmydraw.fragments.DeviceList;

import java.util.ArrayList;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.DeviceViewHolder> {

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView devName;
        TextView devAddress;

        DeviceViewHolder(View itemView) {

            super(itemView);
            cardView = itemView.findViewById(R.id.card);
            devName = itemView.findViewById(R.id.device_name);
            devAddress = itemView.findViewById(R.id.device_address);
        }
    }

    private final ArrayList<WifiP2pDevice> devices = new ArrayList<>();
    private final Context context;
    private final static String TAG = "RVAdapter";

    public RVAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_card, parent, false);
        return new DeviceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int i) {

        WifiP2pDevice device = devices.get(i);
        if (device != null){

            holder.devName.setText(device.deviceName);
            holder.devAddress.setText(device.deviceAddress);

            holder.itemView.setTag(device);
            holder.itemView.setOnClickListener((itemView) -> {

                Toast.makeText(itemView.getContext(), R.string.connecting, Toast.LENGTH_LONG).show();

                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;

                ((DeviceList.DeviceActionListener) context).connect(config);

            });
        }
        else{
            Log.d(TAG, "in onBindViewHolder: index is out of bound ! ! ! ");
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void updateList(WifiP2pDeviceList devices) {
        updateDeviceList(devices);
    }

    public void clearList() {
        clearDeviceList();
    }

    private void updateDeviceList(WifiP2pDeviceList newList) {

        clearDeviceList();
        devices.addAll(newList.getDeviceList());
        notifyDataSetChanged();
    }

    private void clearDeviceList() {

        int size = devices.size();
        devices.clear();
        notifyItemRangeRemoved(0, size);
    }

}
