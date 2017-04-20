package com.example.xomanonpxo.android_app;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

/**
 * Created by xomanonpxo on 02/03/17.
 */

public class Filters {

    private static int grayAverage(int color){
        return (int) (0.3 * Color.red(color) + 0.59 * Color.green(color) + 0.11 * Color.blue(color));
    }

    private static double distance(float h1, float h2){
        return (h1-h2)*(h1-h2);
    }

    protected static void grayscale(Bitmap bmp) {
        int pixels[] = new int[bmp.getWidth() * bmp.getHeight()], m;
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());

        for (int i = 0; i < bmp.getWidth() * bmp.getHeight(); i++) {
            m = grayAverage(pixels[i]);
            pixels[i] = Color.argb(Color.alpha(pixels[i]), m, m, m);
        }

        bmp.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
    }

    protected static void sepia(Bitmap bmp) {
        int pixels[] = new int[bmp.getWidth() * bmp.getHeight()], r, g, b, rFinal, gFinal, bFinal;
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());

        for (int i = 0; i < bmp.getWidth() * bmp.getHeight(); i++) {
            r = Color.red(pixels[i]);
            g = Color.green(pixels[i]);
            b = Color.blue(pixels[i]);
            rFinal = (int) Math.min(255, ((r * 0.393f) + (g *0.769f) + (b * 0.189f)));
            gFinal = (int) Math.min(255, ((r * 0.349f) + (g *0.686f) + (b * 0.168f)));
            bFinal = (int) Math.min(255, ((r * 0.272f) + (g * 0.534f) + (b *  0.131f)));
            pixels[i] = Color.argb(Color.alpha(pixels[i]), rFinal, gFinal, bFinal);
        }

        bmp.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
    }

    protected static void invert(Bitmap bmp){
        int pixels[] = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());

        for(int i = 0; i < bmp.getWidth() * bmp.getHeight(); i++){
            pixels[i] = Color.argb(Color.alpha(pixels[i]), 255 - Color.red(pixels[i]), 255 - Color.green(pixels[i]), 255 - Color.blue(pixels[i]));
        }

        bmp.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
    }

    protected static void anaglyph(Bitmap bmp){
        Bitmap bmpCopy = bmp.copy(Bitmap.Config.ARGB_8888, true);
        bmp.eraseColor(Color.BLACK);

        int shift = 8, i, j, p, pMin, pMax;

        for(j = shift; j < bmp.getHeight() -shift; ++j){
            for(i = shift; i < bmp.getWidth() - shift; ++i){
                p = bmpCopy.getPixel(i, j);
                pMin = bmpCopy.getPixel(i - shift, j);
                pMax = bmpCopy.getPixel(i + shift, j);
                bmp.setPixel(i, j, Color.argb(Color.alpha(p), Color.red(pMin), Color.green(pMax), Color.blue(pMax)));
            }
        }
    }

    protected static void colorize(Bitmap bmp, int color) {
        int pixels[] = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());

        float[] hsv = new float[3], hsvColor = new float[3];
        Color.RGBToHSV(Color.red(color), Color.green(color), Color.blue(color), hsvColor);

        for(int i = 0; i < bmp.getWidth() * bmp.getHeight(); i++) {
            Color.RGBToHSV(Color.red(pixels[i]), Color.green(pixels[i]), Color.blue(pixels[i]), hsv);
            pixels[i] = Color.HSVToColor(Color.alpha(pixels[i]), new float[]{hsvColor[0], hsv[1], hsv[2]});
        }

        bmp.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
    }

    /**
     * same function, but with ColorMatrix
     */

    public static void colorizeColorMatrix(Bitmap bmp, int color){
        float hsv[] = new float[3];
        Color.RGBToHSV(Color.red(color), Color.green(color), Color.blue(color), hsv);

        float sum = Color.red(color) + Color.green(color) + Color.blue(color);
        float pr = Color.red(color)/sum;
        float pg = Color.green(color)/sum;
        float pb = Color.blue(color)/sum;
        float adjust = (1-pr)+(1-pg)+(1-pb);

        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        pr+pr*adjust, 0, 0, 0, 20,
                        0, pg+pg*adjust, 0, 0, 20,
                        0, 0, pb+pb*adjust, 0, 20,
                        0, 0, 0, 1, 0,
                });

        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);
    }

    protected static void selectHue(Bitmap bmp, int color){
        int pixels[] = new int[bmp.getWidth() * bmp.getHeight()], distanceMax = 800, m;
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());

        float hsvPix[] = new float[3], hsvRef[] = new float[3];

        for(int i = 0; i < bmp.getWidth() * bmp.getHeight(); i++) {
            Color.RGBToHSV(Color.red(pixels[i]), Color.green(pixels[i]), Color.blue(pixels[i]), hsvPix);
            Color.colorToHSV(color, hsvRef);
            if(distance(hsvRef[0], hsvPix[0]) >= distanceMax){
                m = grayAverage(pixels[i]);
                pixels[i] = Color.argb(Color.alpha(pixels[i]), m, m, m);
            }
        }

        bmp.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
    }

    /**
     * @param bmp input bitmap
     * @param r 0 to delete the red channel, 1 to keep it
     * @param g 0 to delete the red channel, 1 to keep it
     * @param b 0 to delete the red channel, 1 to keep it
     */

    protected static void canal(Bitmap bmp, int r, int g, int b){
        int pixels[] = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());

        for(int i = 0; i < bmp.getWidth() * bmp.getHeight(); i++){
             pixels[i] = Color.argb(Color.alpha(pixels[i]), r*Color.red(pixels[i]), g*Color.green(pixels[i]), b*Color.blue(pixels[i]));
            }

        bmp.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
    }

    /**
     * @param bmp input bitmap
     * @param contrast 0..10 1 is default
     * @param brightness -255..255 0 is default
     * @param saturation -255...255 1 is default
     */

    public static void adjust(Bitmap bmp, float brightness, float contrast, float saturation){
        float t = (-.5f * contrast + .5f) * 255.f;

        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, t+brightness,
                        0, contrast, 0, 0, t+brightness,
                        0, 0, contrast, 0, t+brightness,
                        0, 0, 0, 1, 0,
                });

        if(saturation != 1)
            cm.setSaturation(saturation);

        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);
    }

    protected static void histogramEqualization(Bitmap bmp) {

        int a, r, g, b;

        int rLUT[] = histogramLUT(bmp, "red");
        int[] gLUT = histogramLUT(bmp, "green");
        int[] bLUT = histogramLUT(bmp, "blue");

        int pixels[] = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());

        for(int i = 0; i < bmp.getWidth() * bmp.getHeight(); i++) {
            a = Color.alpha(pixels[i]);
            r = Color.red(pixels[i]);
            g = Color.green(pixels[i]);
            b = Color.blue(pixels[i]);

            r = rLUT[r];
            g = gLUT[g];
            b = bLUT[b];

            pixels[i] = Color.argb(a, r, g, b);
        }

        bmp.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
    }

    /**
     * @param bmp input bitmap
     * @param color "red", "green" or "blue" histogram
     */

    private static int[] calculHistogram(Bitmap bmp, String color) {
        int h[] = new int[256];
        for (int v = 0; v < 256; v++) {
            h[v] = 0;
        }

        int pixels[] = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());

        for(int i = 0; i < bmp.getWidth() * bmp.getHeight(); i++) {
            switch(color){
                case "red":
                    h[Color.red(pixels[i])]++;
                    break;
                case "green":
                    h[Color.green(pixels[i])]++;
                    break;
                case "blue":
                    h[Color.blue(pixels[i])]++;
            }
        }

        return h;
    }

    /**
     * @param bmp input bitmap
     * @param color  "red", "green" or "blue" LUT
     */

    private static int[] histogramLUT(Bitmap bmp, String color){

        int histogram[] = calculHistogram(bmp, color);

        int LUT[] = new int[256];

        for(int i = 0; i < LUT.length; i++)
            LUT[i] = 0;

        long sumr = 0;
        int valr;

        float scale_factor = (float) (255.0 / (bmp.getWidth() * bmp.getHeight()));

        for(int i=0; i<LUT.length; i++) {
            sumr += histogram[i];
            valr = (int) (sumr * scale_factor);
            if(valr > 255) {
                LUT[i] = 255;
            }
            else LUT[i] = valr;

        }

        return LUT;
    }
}

