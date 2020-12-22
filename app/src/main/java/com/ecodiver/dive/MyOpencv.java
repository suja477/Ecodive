package com.ecodiver.dive;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Mat.*;
import android.graphics.Bitmap;

import org.opencv.core.Scalar;
import org.opencv.imgproc.*;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.opencv.imgcodecs.Imgcodecs.imread;

/**
 * Created by Suja Manu on 21-12-2020.
 */

public class MyOpencv {

    // Magic values:
    // float numOfPixels = width * height;
    int thresholdRatio = 2000;
    //         const thresholdLevel = numOfPixels / thresholdRatio
    float minAvgRed = 60;
    float maxHueShift = 120;
    float blueMagicValue = 1.2f;

    Bitmap mybitmap;
    static float hueshift = 0;

      MyOpencv (Bitmap bitmap) {
        mybitmap = bitmap;

    }


    /**
     * Calculate the average red, green, blue color values of a bitmap
     *
     * @param bitmap a {@link Bitmap}
     * @return
     */
    public static void calculateAverage(String filepath)
    {
        Mat img = imread(filepath);
       Scalar mean= Core.mean(img);
       double[] bin=mean.val;

    /*    Scalar m = cv::mean(img);
        Mat bin = img > m[0]; // syntax sugar for 'threshold()'
     //   but better, opencv has an automatic threshold method:

    Mat bin;
        cv::threshold(img, bin, 0, 255, THRESH_OTSU);*/
    }

}
