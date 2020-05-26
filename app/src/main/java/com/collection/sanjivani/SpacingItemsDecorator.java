package com.collection.sanjivani;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpacingItemsDecorator extends RecyclerView.ItemDecoration {

    private final int spacingBetweenRecyclerViewItems;

    public SpacingItemsDecorator(int spacingBetweenRecyclerViewItems) {
        this.spacingBetweenRecyclerViewItems = spacingBetweenRecyclerViewItems;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.bottom = spacingBetweenRecyclerViewItems;
    }
}
