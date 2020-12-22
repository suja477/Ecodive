package com.ecodiver.dive;

import android.graphics.Bitmap;
import android.graphics.ColorMatrix;

/**
 * Created by Suja Manu on 12-12-2020.
 */

public interface AsyncTaskCompleteListener<T> {
    public void onTaskComplete(ColorMatrix result);
    public void onTaskComplete(Bitmap bitmap);
}
