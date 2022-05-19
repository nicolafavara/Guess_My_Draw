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
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.guessmydraw.fragments.DeviceList;
import com.example.guessmydraw.fragments.FirstScreen;
import com.example.guessmydraw.fragments.GameLobby;
import com.example.guessmydraw.fragments.Loading;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

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
            } else {
                activity.setIsWifiP2pEnabled(false);
                activity.resetData();
            }
            Log.d(MainActivity.TAG, "P2P state changed - " + state);
        }
        else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // A change in the list of available peers occurred.

            if (manager == null) return;

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }

            Fragment frag = activity.getForegroundFragment();

            //TODO TROVARE UN MODO PIU' "PULITO" PER FARLO
            if(frag != null && frag.toString().startsWith(deviceListClassName)){

                Log.d(MainActivity.TAG, "requesting peers...");
                WifiP2pManager.PeerListListener listener = (WifiP2pManager.PeerListListener) frag;
                manager.requestPeers(channel, listener);
            }

        }
        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            if (manager == null) return;

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {

                activity.isWifiP2pConnected = true;

                Fragment frag = activity.getForegroundFragment();
                if(frag != null && frag.toString().startsWith(Loading.class.getSimpleName())){
                    Log.d(MainActivity.TAG, "requesting connection info...");
                    WifiP2pManager.ConnectionInfoListener listener = (WifiP2pManager.ConnectionInfoListener) frag;
                    manager.requestConnectionInfo(channel, listener);
                }
                else if(frag != null && frag.toString().startsWith(deviceListClassName)){
                    Navigation.findNavController(activity, R.id.my_nav_host_fragment).navigate(R.id.start_loading_page);
                }

            }
            else{

                activity.isWifiP2pConnected = false;

                Log.d(MainActivity.TAG, "---> networkInfo.isConnected() in false");

                Fragment frag = activity.getForegroundFragment();

                //TODO TROVARE UN MODO PIU' "PULITO" PER FARLO
                if(frag != null && !frag.toString().startsWith(firstFragmentClassName)
                        && !frag.toString().startsWith(deviceListClassName)){

                    Toast.makeText(activity, R.string.p2p_disconnection, Toast.LENGTH_LONG).show();
                    Navigation.findNavController(activity, R.id.my_nav_host_fragment).navigate(R.id.disconnection);
                }

            }
        }
        //else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {}
    }


}
