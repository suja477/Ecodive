package com.ecodiver.dive;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.util.ArrayList;

/**
 * Created by Suja Manu on 10-12-2020.
 */

class FilterUtility {

    // Magic values:
    // float numOfPixels = width * height;
    int thresholdRatio = 2000;
    // cnst thresholdLevel = numOfPixels / thresholdRatio
    float minAvgRed = 60;
    float maxHueShift = 120;
    float blueMagicValue = 1.2f;
    Bitmap mybitmap;
    static float hueshift = 0;

    FilterUtility(Bitmap bitmap) {
        mybitmap = bitmap;

    }


    //  int avg = calculateAverageColor(pixels, width, height);

    /**
     * Calculate the average red, green, blue color values of a bitmap
     *
     * @param bitmap a {@link Bitmap}
     * @return
     */
    public static float[] getAverageColorRGB(Bitmap bitmap) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        float size = width * height;
        int pixelColor;
        float r, g, b;
        r = g = b = 0;
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                pixelColor = bitmap.getPixel(x, y);
                if (pixelColor == 0) {
                    size--;
                    continue;
                }
                r += Color.red(pixelColor);
                g += Color.green(pixelColor);
                b += Color.blue(pixelColor);
            }
        }
        r /= size;
        g /= size;
        b /= size;
        return new float[]{
                r, g, b
        };
    }

    // hue-range: [0, 360] -> Default = 0
    public static Bitmap hueShiftmethod(Bitmap bitmap, float hue) {
        if (bitmap == null) {
            return bitmap;
        }
        if ((hue < 0) || (hue > 360)) {
            return bitmap;
        }//change 0 to 60 color red

        Bitmap newBitmap = bitmap.copy(bitmap.getConfig(), true);
        final float width = newBitmap.getWidth();
        final float height = newBitmap.getHeight();
        float[] hsv = new float[3];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float pixel = newBitmap.getPixel(x, y);
                Color.colorToHSV((int) pixel, hsv);
                hsv[0] = hue;
                newBitmap.setPixel(x, y, Color.HSVToColor(Color.alpha((int) pixel), hsv));
            }
        }

       // bitmap.recycle();
       // bitmap = null;

        return newBitmap;
    }


    public static int[] createHistogram(Bitmap bitmap) {
        int[] redBins = new int[256];

        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int i = 0; i < pixels.length; i++) {
            int red = Color.red(pixels[i]);
            redBins[i] = red;

        }

        int[] normalized = new int[256];
        int minValue = redBins[0];
        for (int i = 1; i < redBins.length; i++) {
            if (redBins[i] < minValue) {
                minValue = redBins[i];
            }
            int maxValue = redBins[0];
            for (int j = 1; i < redBins.length; i++) {
                if (redBins[j] > maxValue) {
                    maxValue = redBins[j];
                }
                //     float min = redBins.m().toFloat()
                //  val max = redBins.max().toFloat()
                float newMin = 0.0f;
                float newMax = 255.0f;

                for (int v = 0; v < redBins.length; v++) {
                    normalized[i] = (int) ((redBins[v] - minValue) *
                            (newMax - newMin) / (maxValue - minValue) + newMin);
                }

            }

        }
        return redBins;
    }


    public static float[] hueShiftRed(float r, float g, float b, float h) {
        float U = (float) Math.cos(h * Math.PI / 180);
        float W = (float) Math.sin(h * Math.PI / 180);

        r = (int) ((0.299 + 0.701 * U + 0.168 * W) * r);
        g = (int) ((0.587 - 0.587 * U + 0.330 * W) * g);
        b = (int) ((0.114 - 0.114 * U - 0.497 * W) * b);

        return new float[]{r, g, b};
    }

    public static float[] normalizingInterval(ArrayList<Float> normArray) {
        float high = 255;
        float low = 0;
        float maxDist = 0;

        for (int i = 1; i < normArray.size(); i++) {
            float dist = normArray.get(i) - normArray.get(i - 1);
            if (dist > maxDist) {
                maxDist = dist;
                high = normArray.get(i);
                low = normArray.get(i - 1);
            }
        }

        return new float[]{low, high};
    }

    public static ColorMatrix applyFilter(Bitmap bitmap) {
        float blueMagicValue = 1.2f;

        int numOfPixels = bitmap.getWidth() * bitmap.getHeight();
        int thresholdRatio = 2000;
        int thresholdLevel = numOfPixels / thresholdRatio;
        // Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePathImage);
        float hue = 0f;
        int maxHueShift = 120;
        int minAvgRed = 60;


        ArrayList<Integer> redBins = new ArrayList();
        ArrayList<Integer> greenBins = new ArrayList();
        ArrayList<Integer> blueBins = new ArrayList();
        // Initialize objects
        for (int i = 0; i < 256; i++) {
            redBins.add(0);
            greenBins.add(0);
            blueBins.add(0);
        }
        float[] rgb = FilterUtility.getAverageColorRGB(bitmap);

        // Calculate shift amount:
        float newAvgRed = rgb[0];
        while (newAvgRed < minAvgRed) {
            rgb = FilterUtility.hueShiftRed(rgb[0], rgb[1], rgb[2], hue);
            newAvgRed = rgb[0] + rgb[1] + rgb[2];
            hue++;
            if (hue > maxHueShift) newAvgRed = 60; // Max value

        }
        Log.i("APPLYFILTER ", " newavgred and hue " + newAvgRed + "---" + hue);


        int[] pixels = new int[(bitmap.getWidth() * bitmap.getHeight())];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

       for (int i = 0; i < pixels.length; i++) {
            float red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
           float[] shifted = hueShiftRed(red, green, blue, hue);// Use new calculated red value
            red = shifted[0] + shifted[1] + shifted[2];
            red = Math.min(255, Math.max(0, red));
           red = Math.round(red);//converting to int
            redBins.set((int) red, redBins.get((int) red) + 1);
            // Log.i("red added",redBins.get(red)+"--"+red);
            greenBins.set(green, greenBins.get(green) + 1);
            blueBins.set(blue, blueBins.get(blue) + 1);
            redBins.add((int) red);///check here---------------------
            greenBins.add(green);
            blueBins.add(blue);

        }


        ArrayList<Float> normalizedRed = new ArrayList();
        ArrayList<Float> normalizedGreen = new ArrayList();
        ArrayList<Float> normalizedBlue = new ArrayList();

        // Push 0 as start value in normalize array:
        normalizedRed.add(0f);
        normalizedGreen.add(0f);
        normalizedBlue.add(0f);
        // Find values under threshold:
        for (int i = 0; i < 256; i++) {
           if (redBins.get(i) - thresholdLevel < 2) normalizedRed.add((float) i);
            if (greenBins.get(i) - thresholdLevel < 2) normalizedGreen.add((float) i);
            if (blueBins.get(i) - thresholdLevel < 2) normalizedBlue.add((float) i);
        }
// Push 255 as end value in normalize array:
        normalizedRed.add(255f);
        normalizedGreen.add(255f);
        normalizedBlue.add(255f);

        float[] adjustRed;//;= new int[3];
        adjustRed = normalizingInterval(normalizedRed);
        float[] adjustGreen;//= new int[3];
        adjustGreen = normalizingInterval(normalizedGreen);
        float[] adjustBlue;//= new int[3];
        adjustBlue = normalizingInterval(normalizedBlue);

        // Make histogram:
        float[] shifted = hueShiftRed(1, 1, 1, hue);

        float redGain = (float) 256 / (adjustRed[1] - adjustRed[0]);
        float greenGain = (float) 256 / (adjustGreen[1] - adjustGreen[0]);
        float blueGain = (float) 256 / (adjustBlue[1] - adjustBlue[0]);

        float redOffset = (float) (-adjustRed[0] / 256) * redGain;
        float greenOffset = (float) (-adjustGreen[0] / 256) * greenGain;
        float blueOffset = (float) (-adjustBlue[0] / 256) * blueGain;

        float adjstRed = shifted[0] * redGain;
        float adjstRedGreen = shifted[1] * redGain;
        float adjstRedBlue = (float) (shifted[2] * redGain * blueMagicValue);
        Log.i("APPLYFILTER ", "histogram  done--------" + adjstRed + " " + adjstRedGreen + " " + adjstRedBlue + " " +
                redOffset + " " + greenGain + " " + greenOffset + " " + blueGain + " " + blueOffset);

        return new ColorMatrix(new float[]{
                adjstRed, adjstRedGreen, adjstRedBlue, 0, redOffset,
                0, greenGain, 0, 0, greenOffset,
                0, 0, blueGain, 0, blueOffset,
                0, 0, 0, 1, 0});
    }
    public static ColorMatrix applyFilterUsingOpenCV(Bitmap bitmap) {
        float blueMagicValue = 1.2f;

        int numOfPixels = bitmap.getWidth() * bitmap.getHeight();
        int thresholdRatio = 2000;
        int thresholdLevel = numOfPixels / thresholdRatio;
        // Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePathImage);
        float hue = 0f;
        int maxHueShift = 120;
        int minAvgRed = 60;


        ArrayList<Integer> redBins = new ArrayList();
        ArrayList<Integer> greenBins = new ArrayList();
        ArrayList<Integer> blueBins = new ArrayList();
        // Initialize objects
        for (int i = 0; i < 256; i++) {
            redBins.add(0);
            greenBins.add(0);
            blueBins.add(0);
        }
        float[] rgb = FilterUtility.getAverageColorRGB(bitmap);

        // Calculate shift amount:
        float newAvgRed = rgb[0];
        while (newAvgRed < minAvgRed) {
            rgb = FilterUtility.hueShiftRed(rgb[0], rgb[1], rgb[2], hue);
            newAvgRed = rgb[0] + rgb[1] + rgb[2];
            hue++;
            if (hue > maxHueShift) newAvgRed = 60; // Max value
            //     Log.i("APPLYFILTER ", " newavgred --hue " + newAvgRed + "-" + hue);
        }


        //createHistogram(newbitmap);


        ArrayList<Float> normalizedRed = new ArrayList();
        ArrayList<Float> normalizedGreen = new ArrayList();
        ArrayList<Float> normalizedBlue = new ArrayList();

//-------------------------------------------------------------------------
        Mat originalImage = new Mat();
        Utils.bitmapToMat(bitmap.copy(Bitmap.Config.ARGB_8888,true), originalImage);
        ArrayList newRedGreen=MyOpencv.createHistogramBlueGreen(originalImage);

        float[] blue= (float[]) newRedGreen.get(0);
        float[] green= (float[]) newRedGreen.get(1);
        //shift image and create seperate value for normalized redbin
        Bitmap newbitmap=hueShiftmethod(bitmap,hue);
        // Using Arrays.asList() method
        Mat imageMat = new Mat();
        Utils.bitmapToMat(newbitmap, imageMat);


        float[] red=  MyOpencv.createHistogram(imageMat);

        for(int i =0;i<red.length;i++)
        {
            /* We are adding each array's element to the ArrayList*/
            normalizedRed.add((float) red[i]);
            normalizedBlue.add((float) blue[i]);
            normalizedGreen.add((float) green[i]);
        }


        //--------------------------------------------------------------

        float[] adjustRed;//;= new int[3];
        adjustRed = normalizingInterval(normalizedRed);
        float[] adjustGreen;//= new int[3];
        adjustGreen = normalizingInterval(normalizedGreen);
        float[] adjustBlue;//= new int[3];
        adjustBlue = normalizingInterval(normalizedBlue);

        // Make histogram:
        float[] shifted = hueShiftRed(1, 1, 1, hue);

        float redGain = (float) 256 / (adjustRed[1] - adjustRed[0]);
        float greenGain = (float) 256 / (adjustGreen[1] - adjustGreen[0]);
        float blueGain = (float) 256 / (adjustBlue[1] - adjustBlue[0]);

        float redOffset = (float) (-adjustRed[0] / 256) * redGain;
        float greenOffset = (float) (-adjustGreen[0] / 256) * greenGain;
        float blueOffset = (float) (-adjustBlue[0] / 256) * blueGain;

        float adjstRed = shifted[0] * redGain;
        float adjstRedGreen = shifted[1] * redGain;
        float adjstRedBlue = (float) (shifted[2] * redGain * blueMagicValue);
        Log.i("APPLYFILTER ", "histogram  done--------" + adjstRed + " " + adjstRedGreen + " " + adjstRedBlue + " " +
                redOffset + " " + greenGain + " " + greenOffset + " " + blueGain + " " + blueOffset);

        return new ColorMatrix(new float[]{
                adjstRed, adjstRedGreen, adjstRedBlue, 0, redOffset,
                0, greenGain, 0, 0, greenOffset,
                0, 0, blueGain, 0, blueOffset,
                0, 0, 0, 1, 0});
    }


    public static Bitmap applyOtherFilter(Bitmap oldBitmap) {
        // copying to newBitmap for manipulation
        Bitmap newBitmap = oldBitmap.copy(Bitmap.Config.ARGB_8888, true);

        // height and width of Image
        int imageHeight = newBitmap.getHeight();
        int imageWidth = newBitmap.getWidth();

        Log.e("Image Size", "Height=" + imageHeight + " Width=" + imageWidth);


        // traversing each pixel in Image as an 2D Array
        for (int i = 0; i < imageWidth; i++) {

            for (int j = 0; j < imageHeight; j++) {

                // getting each pixel
                int oldPixel = oldBitmap.getPixel(i, j);

                // each pixel is made from RED_BLUE_GREEN_ALPHA
                // so, getting current values of pixel
                int oldRed = Color.red(oldPixel);
                int oldBlue = Color.blue(oldPixel);
                int oldGreen = Color.green(oldPixel);
                int oldAlpha = Color.alpha(oldPixel);


                // write your Algorithm for getting new values
                // after calculation of filter
// Algorithm for getting new values after calculation of filter
                // Algorithm for GREY FILTER, by intensity of each pixel
                int intensity = (oldRed + oldBlue + oldGreen) / 3;
                int newRed = intensity;
                int newBlue = intensity;
                int newGreen = intensity;


                // applying new pixel values from above to newBitmap
                int newPixel = Color.argb(oldAlpha, newRed, newGreen, newBlue);
                newBitmap.setPixel(i, j, newPixel);
            }
        }
        return newBitmap;
    }

    public static Bitmap sepiaFilter(Bitmap oldBitmap) {
        // copying to newBitmap for manipulation
        Bitmap newBitmap = oldBitmap.copy(Bitmap.Config.ARGB_8888, true);

        // height and width of Image
        int imageHeight = newBitmap.getHeight();
        int imageWidth = newBitmap.getWidth();

        Log.e("Image Size", "Height=" + imageHeight + " Width=" + imageWidth);


        // traversing each pixel in Image as an 2D Array
        for (int i = 0; i < imageWidth; i++) {

            for (int j = 0; j < imageHeight; j++) {

                // getting each pixel
                int oldPixel = oldBitmap.getPixel(i, j);

                // each pixel is made from RED_BLUE_GREEN_ALPHA
                // so, getting current values of pixel
                int oldRed = Color.red(oldPixel);
                int oldBlue = Color.blue(oldPixel);
                int oldGreen = Color.green(oldPixel);
                int oldAlpha = Color.alpha(oldPixel);


                // write your Algorithm for getting new values
                // Algorithm for getting new values after calculation of filter
// Algorithm for SEPIA FILTER
                int newRed = (int) (0.393 * oldRed + 0.769 * oldGreen + 0.189 * oldBlue);
                int newGreen = (int) (0.349 * oldRed + 0.686 * oldGreen + 0.168 * oldBlue);
                int newBlue = (int) (0.272 * oldRed + 0.534 * oldGreen + 0.131 * oldBlue);


                // applying new pixel values from above to newBitmap
                int newPixel = Color.argb(oldAlpha, newRed, newGreen, newBlue);
                newBitmap.setPixel(i, j, newPixel);
            }
        }
        return newBitmap;
    }


}
