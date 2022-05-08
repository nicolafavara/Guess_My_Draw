package com.example.guessmydraw.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.guessmydraw.databinding.FragmentFirstScreenBinding;

public class FirstScreen extends Fragment {

    private FragmentFirstScreenBinding binding;

    public FirstScreen() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFirstScreenBinding.inflate(inflater, container, false);

        binding.discoverButton.setOnClickListener(view -> {
            ((FirstScreenListener) requireActivity()).startDiscovery();
        });

        binding.showWordButton.setOnClickListener(view -> {
            ((FirstScreenListener) requireActivity()).showWordList();
        });

        return binding.getRoot();
    }

    public interface FirstScreenListener {

        void startDiscovery();
        void showWordList();
    }
}