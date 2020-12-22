package com.ecodiver.dive;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import android.graphics.Bitmap;

import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Scalar;
import org.opencv.imgproc.*;

import java.util.ArrayList;
import java.util.List;

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
     * @param  {@link Bitmap}
     * @return
     */
    public static void calculateAverage(Mat img)
    {
      //  Mat img = imread(filepath);
       Scalar mean= Core.mean(img);
       double[] bin=mean.val;

    /*    Scalar m = cv::mean(img);
        Mat bin = img > m[0]; // syntax sugar for 'threshold()'
     //   but better, opencv has an automatic threshold method:

    Mat bin;
        cv::threshold(img, bin, 0, 255, THRESH_OTSU);*/
    }

    public static float[] createHistogram(Mat img){
       // Mat src = Imgcodecs.imread(M);
        if (img.empty()) {
            System.err.println("Cannot read image: " + img);
            System.exit(0);
        }
        List<Mat> bgrPlanes = new ArrayList<>();
        Core.split(img, bgrPlanes);
        int histSize = 256;
        float[] range = {0, 256}; //the upper boundary is exclusive///----------check 256
        MatOfFloat histRange = new MatOfFloat(range);
        boolean accumulate = false;
        Mat bHist = new Mat(), gHist = new Mat(), rHist = new Mat();
      //  Imgproc.calcHist(bgrPlanes, new MatOfInt(0), new Mat(), bHist, new MatOfInt(histSize), histRange, accumulate);
      //  Imgproc.calcHist(bgrPlanes, new MatOfInt(1), new Mat(), gHist, new MatOfInt(histSize), histRange, accumulate);
        Imgproc.calcHist(bgrPlanes, new MatOfInt(2), new Mat(), rHist, new MatOfInt(histSize), histRange, accumulate);

      //  Core.normalize(bHist, bHist, 0, 256, Core.NORM_MINMAX);//check 256
      //  Core.normalize(gHist, gHist, 0, 256, Core.NORM_MINMAX);
       Core.normalize(rHist, rHist, 0, 255, Core.NORM_MINMAX);///----------check 256

     //   float[] bHistData = new float[(int) (bHist.total() * bHist.channels())];
     //   bHist.get(0, 0, bHistData);
      //  float[] gHistData = new float[(int) (gHist.total() * gHist.channels())];
     //   gHist.get(0, 0, gHistData);
        float[] rHistData = new float[(int) (rHist.total() * rHist.channels())];
        rHist.get(0, 0, rHistData);

        return rHistData;
    }

    public static ArrayList createHistogramBlueGreen(Mat img){
        // Mat src = Imgcodecs.imread(M);
        if (img.empty()) {
            System.err.println("Cannot read image: " + img);
            System.exit(0);
        }
        List<Mat> bgrPlanes = new ArrayList<>();
        Core.split(img, bgrPlanes);
        int histSize = 256;
        float[] range = {0, 256}; //the upper boundary is exclusive
        MatOfFloat histRange = new MatOfFloat(range);
        boolean accumulate = false;
        Mat bHist = new Mat(), gHist = new Mat(), rHist = new Mat();
          Imgproc.calcHist(bgrPlanes, new MatOfInt(0), new Mat(), bHist, new MatOfInt(histSize), histRange, accumulate);
         Imgproc.calcHist(bgrPlanes, new MatOfInt(1), new Mat(), gHist, new MatOfInt(histSize), histRange, accumulate);
       // Imgproc.calcHist(bgrPlanes, new MatOfInt(2), new Mat(), rHist, new MatOfInt(histSize), histRange, accumulate);

         Core.normalize(bHist, bHist, 0, 256, Core.NORM_MINMAX);//----------check 256
         Core.normalize(gHist, gHist, 0, 256, Core.NORM_MINMAX);///----------check 256
        //Core.normalize(rHist, rHist, 0, 256, Core.NORM_MINMAX);

          float[] bHistData = new float[(int) (bHist.total() * bHist.channels())];
          bHist.get(0, 0, bHistData);
         float[] gHistData = new float[(int) (gHist.total() * gHist.channels())];
          gHist.get(0, 0, gHistData);
        //float[] rHistData = new float[(int) (rHist.total() * rHist.channels())];
       // rHist.get(0, 0, rHistData);
        ArrayList retturnArray=new ArrayList();
        retturnArray.add(bHistData);
        retturnArray.add(gHistData);
        return retturnArray;
    }


}
