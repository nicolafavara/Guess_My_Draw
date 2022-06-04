package com.example.guessmydraw;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.guessmydraw.fragments.DeviceList;
import com.example.guessmydraw.fragments.FirstScreen;
import com.example.guessmydraw.fragments.GameLobby;
import com.example.guessmydraw.fragments.Loading;

import java.util.Objects;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private final static String TAG = "BR_RECEIVER";
    private final WifiP2pManager manager;
    private final WifiP2pManager.Channel channel;
    private final MainActivity activity;

    //TODO trovare un modo per evitare di usare queste due stringhe
    private final String firstFragmentClassName = FirstScreen.class.getSimpleName();
    private final String gameClassName = GameLobby.class.getSimpleName();
    private final String deviceListClassName = DeviceList.class.getSimpleName();

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity activity) {

        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                activity.setIsWifiP2pEnabled(true);
                Log.d(TAG, "P2P state enabled.");
            } else {
                activity.setIsWifiP2pEnabled(false);
                activity.resetData();
                Log.d(TAG, "P2P state changed not enabled.");
            }
        }
        else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // A change in the list of available peers occurred.

            if (manager == null) return;

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }

            Log.d(TAG, "Intent WIFI_P2P_PEERS_CHANGED_ACTION received.");

            Fragment frag = activity.getForegroundFragment();

            //TODO TROVARE UN MODO PIU' "PULITO" PER FARLO
            if(frag != null && frag.toString().startsWith(deviceListClassName)){

                Log.d(TAG, "requesting peers...");
                WifiP2pManager.PeerListListener listener = (WifiP2pManager.PeerListListener) frag;
                manager.requestPeers(channel, listener);
            }
            else{
                Log.d(TAG, "Requesting peers failed because the current fragment is not deviceList.");
            }

        }
        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            Log.d(TAG, "Intent WIFI_P2P_CONNECTION_CHANGED_ACTION received.");
            if (manager == null) return;

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {
                Log.d(TAG, "Network Info: Connected.");
                activity.isWifiP2pConnected = true;

                Fragment frag = activity.getForegroundFragment();
                if(frag != null && frag.toString().startsWith(Loading.class.getSimpleName())){
                    Log.d(TAG, "Current fragment is Loading: requesting connection info...");
                    WifiP2pManager.ConnectionInfoListener listener = (WifiP2pManager.ConnectionInfoListener) frag;
                    manager.requestConnectionInfo(channel, listener);
                }
                else if(frag != null && frag.toString().startsWith(deviceListClassName)){
                    Log.d(TAG, "Current fragment is device list: starting loading page...");
                    Navigation.findNavController(activity, R.id.my_nav_host_fragment).navigate(R.id.start_loading_page);
                }
                else{
                    Log.d(TAG, "Current fragment is not what you aspected to be.");
                }

            }
            else{

                Log.d(TAG, "Network Info: Not Connected.");
                activity.isWifiP2pConnected = false;

                NavDestination dest = Navigation.findNavController(activity, R.id.my_nav_host_fragment).getCurrentDestination();
                if (dest == null) return;

                String fragmentLabel = Objects.requireNonNull(dest.getLabel()).toString();

                if(!fragmentLabel.equals(activity.getResources().getString(R.string.first_screen_label))
                    && !fragmentLabel.equals(activity.getResources().getString(R.string.device_list_label))
                        && !fragmentLabel.equals(activity.getResources().getString(R.string.match_results_label))){

                    Log.d(TAG, "Connection interrupted...returning to device list fragment. ");

                    Toast.makeText(activity, R.string.p2p_disconnection, Toast.LENGTH_LONG).show();
                    Navigation.findNavController(activity, R.id.my_nav_host_fragment).navigate(R.id.disconnection);
                }

            }
        }
        //else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {}
    }


}
