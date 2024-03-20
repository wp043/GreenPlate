package com.example.greenplate.viewmodels.listeners;

import com.example.greenplate.models.RetrievableItem;

import java.util.List;

public interface OnDataRetrievedCallback {
    void onDataRetrieved(List<RetrievableItem> items);
}
