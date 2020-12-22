package com.ecodiver.dive;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.os.AsyncTask;

/**
 * Created by Suja Manu on 12-12-2020.
 */

class FilterAsyncTask extends AsyncTask<Bitmap, String, ColorMatrix> {

    private final Context context;
    private final AsyncTaskCompleteListener listener;
    ProgressDialog progDailog;
    public FilterAsyncTask(Context ctx, AsyncTaskCompleteListener listener){
        this.context = ctx;
        this.listener = listener;

    }

    @Override
    protected ColorMatrix doInBackground(Bitmap... bitmaps) {
       ColorMatrix colorMatrix=FilterUtility.applyFilter(bitmaps[0]);

        return colorMatrix;
    }

    @Override
    protected void onPostExecute(ColorMatrix colorMatrix) {
        super.onPostExecute(colorMatrix);
     //   progDailog.dismiss();
        listener.onTaskComplete(colorMatrix);
    }
}
