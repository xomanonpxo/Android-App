package com.example.xomanonpxo.android_app;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

/**
 * Created by xomanonpxo on 02/03/17.
 */

public class Filters {

    protected static void grayscale(Bitmap bmp) {
        int[] rgb = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(rgb, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for (int i = 0; i < bmp.getWidth() * bmp.getHeight(); i++) {
            int m = (int) (0.3 * Color.red(rgb[i]) + 0.59 * Color.green(rgb[i]) + 0.11 * Color.blue(rgb[i]));
            rgb[i] = Color.argb(Color.alpha(rgb[i]), m, m, m);
        }
        bmp.setPixels(rgb, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
    }

    protected static void sepia(Bitmap bmp) {
        int[] res = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(res, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for (int j = 0; j < bmp.getWidth() * bmp.getHeight(); j++) {
            int r = Color.red(res[j]);
            int g = Color.green(res[j]);
            int b = Color.blue(res[j]);
            int rFinal = (int) Math.min(255, ((r * 0.393f) + (g *0.769f) + (b * 0.189f)));
            int gFinal = (int) Math.min(255, ((r * 0.349f) + (g *0.686f) + (b * 0.168f)));
            int bFinal = (int) Math.min(255, ((r * 0.272f) + (g * 0.534f) + (b *  0.131f)));
            res[j] = Color.argb(Color.alpha(res[j]), rFinal, gFinal, bFinal);
        }
        bmp.setPixels(res, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
    }

    protected static void invert(Bitmap bmp){
        for(int j = 0; j < bmp.getHeight(); j++){
            for(int i = 0; i < bmp.getWidth(); i++){
                int p = bmp.getPixel(i, j);
                bmp.setPixel(i, j, Color.argb(Color.alpha(p), 255 - Color.red(p), 255 - Color.green(p), Color.blue(p)));
            }
        }
    }

    protected static void anaglyph(Bitmap bmp){
        Bitmap bmp2 = bmp.copy(Bitmap.Config.ARGB_8888, true);
        bmp.eraseColor(Color.BLACK);
        int shift = 8;

        for(int j = shift; j < bmp.getHeight() -shift; ++j){
            for(int i = shift; i < bmp.getWidth() - shift; ++i){
                int p = bmp2.getPixel(i, j);
                int pMin = bmp2.getPixel(i - shift, j);
                int pMax = bmp2.getPixel(i + shift, j);
                bmp.setPixel(i, j, Color.argb(Color.alpha(p), Color.red(pMin), Color.green(pMax), Color.blue(pMax)));

            }
        }
    }
    protected static void colorize(Bitmap bmp, int color) {
        int[] rgb = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(rgb, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        float[] hsv = new float[3];
        float[] hsvColor = new float[3];
        Color.RGBToHSV(Color.red(color), Color.green(color), Color.blue(color), hsvColor);
        for(int i = 0; i < bmp.getWidth() * bmp.getHeight(); i++) {
            Color.RGBToHSV(Color.red(rgb[i]), Color.green(rgb[i]), Color.blue(rgb[i]), hsv);
            rgb[i] = Color.HSVToColor(Color.alpha(rgb[i]), new float[]{hsvColor[0], hsv[1], hsv[2]});
        }
        bmp.setPixels(rgb, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
    }

    protected static void selectHue(Bitmap bmp, int color){
        int distanceMax = 800;
        for(int j = 0; j < bmp.getHeight(); ++j) {
            for (int i = 0; i < bmp.getWidth(); ++i) {
                int p = bmp.getPixel(i, j);
                float[] hsvPix = new float[3];
                Color.RGBToHSV(Color.red(p), Color.green(p), Color.blue(p), hsvPix);
                float[] hsvRef = new float[3];
                Color.colorToHSV(color, hsvRef);
                if(distanceHue(hsvRef[0], hsvPix[0]) >= distanceMax){
                    int m = (int) (0.3 * Color.red(p) + 0.59 * Color.green(p) + 0.11 * Color.blue(p));
                    bmp.setPixel(i, j, Color.argb(Color.alpha(p), m, m, m));
                }
            }
        }
    }

    private static double distanceHue(float h1, float h2){
        return (h1-h2)*(h1-h2);
    }

    protected static void redCanal(Bitmap bmp){
        for(int j = 0; j < bmp.getHeight(); ++j) {
            for (int i = 0; i < bmp.getWidth(); ++i) {
                int p = bmp.getPixel(i, j);
                int r = Color.red(p);
                bmp.setPixel(i, j, Color.argb(Color.alpha(p), r, 0, 0));
            }
        }
    }

    protected static void greenCanal(Bitmap bmp){
        for(int j = 0; j < bmp.getHeight(); ++j) {
            for (int i = 0; i < bmp.getWidth(); ++i) {
                int p = bmp.getPixel(i, j);
                int g = Color.green(p);
                bmp.setPixel(i, j, Color.argb(Color.alpha(p), 0, g, 0));
            }
        }
    }

    protected static void blueCanal(Bitmap bmp){
        for(int j = 0; j < bmp.getHeight(); ++j) {
            for (int i = 0; i < bmp.getWidth(); ++i) {
                int p = bmp.getPixel(i, j);
                int b = Color.blue(p);
                bmp.setPixel(i, j, Color.argb(Color.alpha(p), 0, 0, b));
            }
        }
    }

    protected static void luminosity(Bitmap bmp, int value){
        int a, r, g, b;
        int pixel;

        for (int x = 0; x < bmp.getWidth(); ++x){
            for (int y = 0; y < bmp.getHeight(); ++y){

                pixel = bmp.getPixel(x, y);
                a = Color.alpha(pixel);
                r = Color.red(pixel);
                g = Color.green(pixel);
                b = Color.blue(pixel);

                // increase/decrease each channel
                r += value;
                if (r > 255)
                    r = 255;
                else if (r < 0)
                    r = 0;

                g += value;
                if (g > 255)
                    g = 255;
                else if (g < 0)
                    g = 0;

                b += value;
                if (b > 255)
                    b = 255;
                else if (b < 0)
                    b = 0;

                bmp.setPixel(x, y, Color.argb(a, r, g, b));
            }
        }
    }

    protected static void contrast(Bitmap bmp, double contrastVal) {
        int a, r, g, b;
        int pixel;

        double contrast = Math.pow((100 + contrastVal) / 100, 2);

        for (int x = 0; x < bmp.getWidth(); x++) {
            for (int y = 0; y < bmp.getHeight(); y++) {
                pixel = bmp.getPixel(x, y);
                a = Color.alpha(pixel);

                r = Color.red(pixel);
                r = (int)(((((r / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (r > 255)
                    r = 255;
                else if (r < 0)
                    r = 0;

                g = Color.green(pixel);
                g = (int)(((((g / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (g > 255)
                    g = 255;
                else if (g < 0)
                    g = 0;

                b = Color.blue(pixel);
                b = (int)(((((b / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (b > 255)
                    b = 255;
                else if (b < 0)
                    b = 0;

                bmp.setPixel(x, y, Color.argb(a, r, g, b));
            }
        }
    }


    protected static void histogramEqualization(Bitmap bmp) {

        int p;
        int alpha;
        int red;
        int green;
        int blue;

        int[] rLUT = histogramLUTRed(bmp);
        int[] gLUT = histogramLUTGreen(bmp);
        int[] bLUT = histogramLUTBlue(bmp);


        for(int j = 0; j < bmp.getHeight(); j++)
            for(int i = 0; i < bmp.getWidth(); i++){
                p = bmp.getPixel(i,j);
                alpha = Color.alpha(p);
                red = Color.red(p);
                green = Color.green(p);
                blue = Color.blue(p);

                red = rLUT[red];
                green = gLUT[green];
                blue = bLUT[blue];

                bmp.setPixel(i, j, Color.argb(alpha, red, green, blue));

            }

    }

    //Utils
    protected static int[] calculHistogramRed(Bitmap bmp) {
        int h[] = new int[256];
        for (int v = 0; v < 256; v++) {
            h[v] = 0;
        }
        for (int j = 0; j < bmp.getHeight(); j++) {
            for (int i = 0; i < bmp.getWidth(); i++) {
                int p = bmp.getPixel(i, j);
                int m = Color.red(p);
                h[m]++;
            }
        }
        return h;
    }

    protected static int[] calculHistogramGreen(Bitmap bmp) {
        int h[] = new int[256];
        for (int v = 0; v < 256; v++) {
            h[v] = 0;
        }
        for (int j = 0; j < bmp.getHeight(); j++) {
            for (int i = 0; i < bmp.getWidth(); i++) {
                int p = bmp.getPixel(i, j);
                int m = Color.green(p);
                h[m]++;
            }
        }
        return h;
    }

    protected static int[] calculHistogramBlue(Bitmap bmp) {
        int h[] = new int[256];
        for (int v = 0; v < 256; v++) {
            h[v] = 0;
        }
        for (int j = 0; j < bmp.getHeight(); j++) {
            for (int i = 0; i < bmp.getWidth(); i++) {
                int p = bmp.getPixel(i, j);
                int m = Color.blue(p);
                h[m]++;
            }
        }
        return h;
    }

    protected static int minHistogram(int[] tab) {
        int i = 0;
        while (tab[i] == 0 && i != 256) {
            i++;
        }
        return i;
    }

    protected static int maxHistogram(int[] tab) {
        int i = 255;
        while (tab[i] == 0 && i != -1) {
            i--;
        }
        return i;
    }

    private static int[] histogramLUTRed(Bitmap bmp){

        int[] rhistogram = calculHistogramRed(bmp);

        int[] rLUT = new int[256];

        for(int i = 0; i < rLUT.length; i++)
            rLUT[i] = 0;

        long sumr = 0;

        float scale_factor = (float) (255.0 / (bmp.getWidth() * bmp.getHeight()));

        for(int i=0; i<rLUT.length; i++) {
            sumr += rhistogram[i];
            int valr = (int) (sumr * scale_factor);
            if(valr > 255) {
                rLUT[i] = 255;
            }
            else rLUT[i] = valr;

        }

        return rLUT;
    }

    private static int[] histogramLUTGreen(Bitmap bmp){

        int[] ghistogram = calculHistogramGreen(bmp);

        int[] gLUT = new int[256];

        for(int i = 0; i < gLUT.length; i++)
            gLUT[i] = 0;

        long sumg = 0;

        float scale_factor = (float) (255.0 / (bmp.getWidth() * bmp.getHeight()));

        for(int i=0; i<gLUT.length; i++) {
            sumg += ghistogram[i];
            int valg = (int) (sumg * scale_factor);
            if(valg > 255) {
                gLUT[i] = 255;
            }
            else gLUT[i] = valg;

        }

        return gLUT;
    }

    private static int[] histogramLUTBlue(Bitmap bmp){

        int[] bhistogram = calculHistogramBlue(bmp);

        int[] bLUT = new int[256];

        for(int i = 0; i < bLUT.length; i++)
            bLUT[i] = 0;

        long sumb = 0;

        float scale_factor = (float) (255.0 / (bmp.getWidth() * bmp.getHeight()));

        for(int i=0; i<bLUT.length; i++) {
            sumb += bhistogram[i];
            int valg = (int) (sumb * scale_factor);
            if(valg > 255) {
                bLUT[i] = 255;
            }
            else bLUT[i] = valg;

        }

        return bLUT;
    }

}

