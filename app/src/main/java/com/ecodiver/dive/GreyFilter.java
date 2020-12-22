package com.ecodiver.dive;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.os.AsyncTask;

/**
 * Created by Suja Manu on 19-12-2020.
 */

class GreyFilter extends AsyncTask <Bitmap, String, Bitmap> {
    private Bitmap Bitmap;
    private final Context context;
    private final AsyncTaskCompleteListener listener;
    ProgressDialog progDailog;
    public GreyFilter(Context ctx, AsyncTaskCompleteListener listener){
        this.context = ctx;
        this.listener = listener;

    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        listener.onTaskComplete(bitmap);
    }

    @Override
    protected android.graphics.Bitmap doInBackground(Bitmap... bitmaps) {
        Bitmap newBitmap=FilterUtility.applyOtherFilter(bitmaps[0]);
        return newBitmap;
    }
}
