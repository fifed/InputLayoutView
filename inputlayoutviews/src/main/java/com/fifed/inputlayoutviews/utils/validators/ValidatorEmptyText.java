package com.fifed.inputlayoutviews.utils.validators;


import android.content.Context;

import com.fifed.inputlayoutviews.utils.validators.core.TextValidator;
import com.fifed.inputlayoutviews.utils.validators.core.ValidatorResponse;

/**
 * Created by Fedir on 13.07.2016.
 */
public class ValidatorEmptyText implements TextValidator {
    String errorText;

    public ValidatorEmptyText(String errorText) {
        this.errorText = errorText;
    }

    @Override
    public  ValidatorResponse isValidText(String text, Context context) {
        if(text.length() == 0){
            return new ValidatorResponse(errorText, false);
        } else return new ValidatorResponse(null, true);
    }
}

