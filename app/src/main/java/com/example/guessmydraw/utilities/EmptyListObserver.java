package com.example.guessmydraw.utilities;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class EmptyListObserver extends RecyclerView.AdapterDataObserver {

    private final View emptyView;
    private final RecyclerView recyclerView;

    public EmptyListObserver(RecyclerView recyclerView, View view){

        this.recyclerView = recyclerView;
        this.emptyView = view;
        checkIfEmpty();
    }

    private void checkIfEmpty() {

        if (emptyView != null && recyclerView.getAdapter() != null) {

            if(recyclerView.getAdapter().getItemCount() == 0){

                emptyView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
            else{
                emptyView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onChanged() {
        super.onChanged();
        checkIfEmpty();
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
        super.onItemRangeChanged(positionStart, itemCount);
    }

}
