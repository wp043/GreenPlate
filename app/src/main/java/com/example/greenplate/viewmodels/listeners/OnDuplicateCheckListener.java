package com.example.greenplate.viewmodels.listeners;

import com.example.greenplate.models.RetrievableItem;

public interface OnDuplicateCheckListener {
    void onDuplicateCheckCompleted(boolean isDuplicate, RetrievableItem duplicateItem);
}
