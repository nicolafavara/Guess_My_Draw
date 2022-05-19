package com.example.guessmydraw.wordViewModel;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.guessmydraw.fragments.ChooseWord;


public class WordToChooseListAdapter extends ListAdapter<Word, WordViewHolder> {

    private Fragment fragment;

    public WordToChooseListAdapter(@NonNull DiffUtil.ItemCallback<Word> diffCallback, @NonNull ChooseWord fragment) {
        super(diffCallback);
        this.fragment = fragment;
    }

    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return WordViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(WordViewHolder holder, int position) {
        Word current = getItem(position);
        holder.bind(current.getWord());

        holder.itemView.setOnClickListener((itemView) -> {

            NavHostFragment.findNavController(fragment).getPreviousBackStackEntry().getSavedStateHandle().set("chosenWord", current.getWord());
            NavHostFragment.findNavController(fragment).popBackStack();
        });
    }

    public static class WordDiff extends DiffUtil.ItemCallback<Word> {

        @Override
        public boolean areItemsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
            return oldItem.getWord().equals(newItem.getWord());
        }
    }
}
