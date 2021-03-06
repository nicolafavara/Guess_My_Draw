package com.example.guessmydraw.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.guessmydraw.R;
import com.example.guessmydraw.databinding.FragmentNewWordBinding;
import com.example.guessmydraw.wordViewModel.Word;
import com.example.guessmydraw.wordViewModel.WordViewModel;

public class NewWord extends Fragment implements View.OnClickListener{

    private FragmentNewWordBinding binding;
    private WordViewModel wordViewModel;

    private EditText editText;

    public NewWord() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNewWordBinding.inflate(inflater, container, false);

        wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        editText = binding.editWord;
        binding.buttonSave.setOnClickListener(this);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Hide status bar
        View windowDecorView = requireActivity().getWindow().getDecorView();
        windowDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.button_save){

            if (TextUtils.isEmpty(editText.getText())) {
                Toast.makeText(requireContext(), R.string.empty_word_not_saved, Toast.LENGTH_LONG).show();
            }
            else {
                Word word = new Word(editText.getText().toString());
                wordViewModel.insert(word);
                NavHostFragment.findNavController(this).navigate(R.id.return_to_word_list);
            }
        }
    }
}