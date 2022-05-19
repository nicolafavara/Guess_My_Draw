package com.example.guessmydraw.fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.guessmydraw.R;
import com.example.guessmydraw.databinding.FragmentMatchResultsBinding;
import com.example.guessmydraw.databinding.FragmentPartialResultsBinding;
import com.example.guessmydraw.utilities.DisconnectionDialog;

public class MatchResults extends Fragment {

    private FragmentMatchResultsBinding binding;

    public MatchResults() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                new DisconnectionDialog().show(getChildFragmentManager(), DisconnectionDialog.TAG);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMatchResultsBinding.inflate(inflater, container, false);
        Button button = binding.returnToFirstScreenButton;
        button.setOnClickListener(view -> {
            //TODO MAKE DISCONNECTION
            NavHostFragment.findNavController(this).navigate(R.id.return_to_first_screen);
        });
        return binding.getRoot();
    }
}