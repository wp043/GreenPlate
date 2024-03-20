package com.example.greenplate.viewmodels.listeners;

import com.example.greenplate.models.GreenPlateStatus;

public interface OnIngredientRemoveListener {
    void onIngredientRemoveSuccess(GreenPlateStatus status);
    void onIngredientRemoveFailure(GreenPlateStatus statuse);
}
