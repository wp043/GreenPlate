package com.example.greenplate.viewmodels;

import com.example.greenplate.viewmodels.listeners.OnDataRetrievedCallback;

public interface Manager {
    /**
     * Retrieve all items from db.
     * @param callback callback function
     */
    void retrieve(OnDataRetrievedCallback callback);
}
