package com.example.guessmydraw;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.guessmydraw.connection.NetworkEventCallback;
import com.example.guessmydraw.connection.Receiver;
import com.example.guessmydraw.connection.Sender;
import com.example.guessmydraw.connection.SenderInLoop;
import com.example.guessmydraw.connection.messages.DrawMessage;
import com.example.guessmydraw.databinding.ActivityMainBinding;
import com.example.guessmydraw.fragments.DeviceList;
import com.example.guessmydraw.fragments.FirstScreen;
import com.example.guessmydraw.fragments.Loading;
import com.example.guessmydraw.utilities.ActivityViewModel;
import com.example.guessmydraw.utilities.GameViewModel;

import java.net.InetAddress;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements FirstScreen.FirstScreenListener, DeviceList.DeviceActionListener, Loading.GameCallback {

    static final String TAG = "DEBUG_MainActivity";
    private ActivityMainBinding binding;

    private final IntentFilter intentFilter = new IntentFilter();

    private WifiP2pManager.Channel channel;
    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    protected boolean isWifiP2pConnected = false;
    private boolean isRefreshing = false;
    private BroadcastReceiver broadcastReceiver = null;

    private GameViewModel gameViewModel;
    //private ActivityViewModel activityViewModel;

    private Receiver receiver;
    private Sender mainSender;
    private SenderInLoop mainSenderInLoop;

    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher, as an instance variable.
    protected final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    Log.e(TAG, "Fine location permission is not granted!");
                    finish();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(view);

        // Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        if (!initP2p()) {
            finish();
        }

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);
        //activityViewModel = new ViewModelProvider(this).get(ActivityViewModel.class);

    }

    /**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    private boolean initP2p() {
        // Device capability definition check
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT)) {
            Log.e(TAG, "Wi-Fi Direct is not supported by this device.");
            return false;
        }

        // Hardware capability check
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            Log.e(TAG, "Cannot get Wi-Fi system service.");
            return false;
        }

        //TODO SE LO XIAOMI HA IL WI_FI SPENTO L'APP CRASHA
        if (!wifiManager.isP2pSupported()) {
            Log.e(TAG, "Wi-Fi Direct is not supported by the hardware or Wi-Fi is off.");
            return false;
        }

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        if (manager == null) {
            Log.e(TAG, "Cannot get Wi-Fi Direct system service.");
            return false;
        }

        channel = manager.initialize(this, getMainLooper(), null);
        if (channel == null) {
            Log.e(TAG, "Cannot initialize Wi-Fi Direct.");
            return false;
        }

        return true;
    }

    /**
     * register the BroadcastReceiver with the intent values to be matched
     */
    @Override
    public void onResume() {
        super.onResume();
        broadcastReceiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    /**
     * Remove all peers and clear all fields. This is called on
     * BroadcastReceiver receiving a state change event.
     */
    public void resetData() {
        DeviceList deviceListFrag = (DeviceList) getSupportFragmentManager().findFragmentById(R.id.device_list);

        if (deviceListFrag != null) {
            deviceListFrag.clearPeers();
        }
    }

    public void registerForReceiver(@NonNull NetworkEventCallback callback){
        if (receiver == null){
            receiver = new Receiver();
            receiver.start();
            Log.e(TAG, "RECEIVER IS NULL !!!");
        }
        receiver.setNetworkEventCallback(callback);
    }

    private boolean checkBeforeDiscovery(){

        //Check if user is already connected with someone
        if (isWifiP2pConnected){
            Toast.makeText(this, R.string.p2p_already_connected, Toast.LENGTH_LONG).show();
            //TODO rimuovere disconnect(?)
            disconnect();
        }

        //check if WiFi is enabled
        if (!isWifiP2pEnabled) {
            Toast.makeText(this, R.string.p2p_off_warning, Toast.LENGTH_SHORT).show();
            return false;
        }

        //Check if GPS is enabled
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!gps_enabled) {
            Toast.makeText(MainActivity.this, "Per trovare un giocatore nei paraggi è necessario attivare il GPS.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void startDiscovery() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            return;
        }

        if(!checkBeforeDiscovery()) return;

        Toast.makeText(MainActivity.this, "Starting discovery...", Toast.LENGTH_SHORT).show();
        Log.d("DEBUG", "Starting discovery...");

        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                NavDestination dest = Navigation.findNavController(MainActivity.this, R.id.my_nav_host_fragment).getCurrentDestination();
                if (dest == null) return;

                String fragmentLabel = Objects.requireNonNull(dest.getLabel()).toString();
                if (fragmentLabel.equals(getResources().getString(R.string.first_screen_label))){
                    Navigation.findNavController(MainActivity.this, R.id.my_nav_host_fragment).navigate(R.id.show_device_list);
                }
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(MainActivity.this, "Discovery Failed : " + reasonCode, Toast.LENGTH_SHORT).show();
                Log.d("DEBUG", "Discovery Failed : " + reasonCode);
            }
        });
    }

    @Override
    public void refresh(){

        isRefreshing = true;
        //to refresh the device search, we call again the method to request again the discovery phase
        startDiscovery();
    }

    @Override
    public void connect(WifiP2pConfig config) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            return;
        }
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                //TODO assicurarsi ci si trovi nel fragment giusto prima
                Navigation.findNavController(MainActivity.this, R.id.my_nav_host_fragment).navigate(R.id.start_loading_page);
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(MainActivity.this, "Connect failed. Retry (Reason: (" + reason + ").", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void disconnect() {

        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "Disconnected.");

                NavDestination dest = Navigation.findNavController(MainActivity.this, R.id.my_nav_host_fragment).getCurrentDestination();
                if (dest == null) return;

                String fragmentLabel = Objects.requireNonNull(dest.getLabel()).toString();
                Log.d(TAG, "FragmentLabel: " + fragmentLabel);
                if (!fragmentLabel.equals(getResources().getString(R.string.match_results_label))){
                    Navigation.findNavController(MainActivity.this, R.id.my_nav_host_fragment).navigate(R.id.disconnection);
                }
                else{
                    Navigation.findNavController(MainActivity.this, R.id.my_nav_host_fragment).navigate(R.id.return_to_first_screen);
                }

                Log.d(TAG, "Disconnection completed.");
                isWifiP2pConnected = false;
            }

        });
    }

    @Override
    public void askForConnectionInfo(WifiP2pManager.ConnectionInfoListener listener) {
        manager.requestConnectionInfo(channel, listener);
    }

    public Fragment getForegroundFragment(){

        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.my_nav_host_fragment);
        if (navHostFragment != null) {
            return navHostFragment.getChildFragmentManager().getFragments().get(0);
        }
        return null;
    }

    public void initSenders(String address){
        Log.d(TAG, "INIT_SENDERS");
        this.mainSender = new Sender(address);
        this.mainSender.start();
        this.mainSenderInLoop = new SenderInLoop(address);
        this.mainSenderInLoop.start();
    }

    public void sendMessage(Bundle bundle){
        Log.d(TAG, "sendMessage");
        this.mainSender.sendMessage(bundle);
    }

    public void sendMessageInLoop(Bundle bundle){
        Log.d(TAG, "sendMessageInLoop");
        this.mainSenderInLoop.sendMessage(bundle);
    }

    public void stopSenderInLoop(){
        this.mainSenderInLoop.stopLoop();
    }

}
