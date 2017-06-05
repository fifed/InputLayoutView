package com.fifed.inputlayoutviews.utils.validators.core;

/**
 * Created by Fedir on 13.07.2016.
 */
public class ValidatorResponse {
   private boolean isValid;
   private String error;

    public ValidatorResponse(String error, boolean isValid) {
        this.error = error;
        this.isValid = isValid;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getError() {
        return error;
    }

}
