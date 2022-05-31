package com.example.guessmydraw.fragments;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.guessmydraw.utilities.EmptyListObserver;
import com.example.guessmydraw.R;
import com.example.guessmydraw.utilities.RVAdapter;
import com.example.guessmydraw.databinding.FragmentDeviceListBinding;

import java.util.Objects;

public class DeviceList extends Fragment implements WifiP2pManager.PeerListListener, SwipeRefreshLayout.OnRefreshListener {

    private FragmentDeviceListBinding binding;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RVAdapter adapter;

    public DeviceList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDeviceListBinding.inflate(inflater, container, false);

        swipeRefreshLayout = binding.swiperefresh;
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(this);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout.setRefreshing(false);

        RecyclerView recyclerView = binding.deviceListRv;

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);

        Log.d("DEBUG", "Creating adapter...");
        this.adapter = new RVAdapter(getContext());
        recyclerView.setAdapter(this.adapter);

        EmptyListObserver observer = new EmptyListObserver(recyclerView, view.findViewById(R.id.empty_data_parent));
        Objects.requireNonNull(recyclerView.getAdapter()).registerAdapterDataObserver(observer);
        Log.d("DEBUG", "Adapter created.");
    }

    @Override
    public void onResume() {
        super.onResume();
        // Hide status bar
        View windowDecorView = requireActivity().getWindow().getDecorView();
        windowDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {

        Log.d("DEBUG", "I found something !!!");

        if (adapter != null){
            adapter.updateList(peerList);
        }
        else {
            Log.d("DEBUG", "Adapter is null.");
        }

        if (peerList.getDeviceList().size() == 0) {
            Log.d("DEBUG", "No devices found");
        }

    }

    public void clearPeers() {
        adapter.clearList();
    }

    @Override
    public void onRefresh() {

        clearPeers();
        ((DeviceActionListener) requireActivity()).refresh();
        swipeRefreshLayout.setRefreshing(false);
    }

    public interface DeviceActionListener {

        void refresh();

        void connect(WifiP2pConfig config);

        void disconnect();
    }

//    private static String getDeviceStatus(int deviceStatus) {
//        Log.d("DEBUG", "Peer status :" + deviceStatus);
//        switch (deviceStatus) {
//            case WifiP2pDevice.AVAILABLE:
//                return "Available";
//            case WifiP2pDevice.INVITED:
//                return "Invited";
//            case WifiP2pDevice.CONNECTED:
//                return "Connected";
//            case WifiP2pDevice.FAILED:
//                return "Failed";
//            case WifiP2pDevice.UNAVAILABLE:
//                return "Unavailable";
//            default:
//                return "Unknown";
//
//        }
//    }
//
//    /**
//     * Update UI for this device.
//     *
//     * @param device WifiP2pDevice object
//     */
//    public void updateThisDevice(WifiP2pDevice device) {
//        this.dev = device;
//        StringBuilder s = new StringBuilder();
//        s.append("device name = ").append(device.deviceName).append(", device status = ").append(getDeviceStatus(device.status));
//        Log.d("DEBUG", s.toString());
////        TextView view = (TextView) mContentView.findViewById(R.id.my_name);
////        view.setText(device.deviceName);
////        view = (TextView) mContentView.findViewById(R.id.my_status);
////        view.setText(getDeviceStatus(device.status));
//    }
}