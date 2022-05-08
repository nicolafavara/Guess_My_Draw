package com.example.guessmydraw.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.guessmydraw.R;
import com.example.guessmydraw.databinding.FragmentChooseWordBinding;
import com.example.guessmydraw.databinding.FragmentGameLobbyBinding;
import com.example.guessmydraw.databinding.FragmentWordListBinding;
import com.example.guessmydraw.wordViewModel.WordListAdapter;
import com.example.guessmydraw.wordViewModel.WordToChooseListAdapter;
import com.example.guessmydraw.wordViewModel.WordViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ChooseWord extends Fragment {

    private FragmentChooseWordBinding binding;
    private WordViewModel wordViewModel;

    public ChooseWord() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChooseWordBinding.inflate(inflater, container, false);

        RecyclerView recyclerView = binding.recyclerview;
        final WordToChooseListAdapter adapter = new WordToChooseListAdapter(new WordToChooseListAdapter.WordDiff(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        wordViewModel.getAllWords().observe(getViewLifecycleOwner(), words -> {
            // Update the cached copy of the words in the adapter.
            adapter.submitList(words);
        });

        return binding.getRoot();
    }
}