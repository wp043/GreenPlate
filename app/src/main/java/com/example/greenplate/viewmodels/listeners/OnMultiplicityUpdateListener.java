package com.example.greenplate.viewmodels.listeners;

import com.example.greenplate.models.GreenPlateStatus;

public interface OnMultiplicityUpdateListener {
    void onMultiplicityUpdateSuccess(GreenPlateStatus status);
    void onMultiplicityUpdateFailure(GreenPlateStatus status);
}
