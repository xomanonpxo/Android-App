package com.example.xomanonpxo.android_app;

import android.graphics.Bitmap;
import android.graphics.Color;

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
}
