package com.example.xomanonpxo.android_app;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.renderscript.Matrix4f;

/**
 * Created by xomanonpxo on 19/04/17.
 */

public class ConvolMatrix {

    //Données membres d'objets
    public int _size;
    public double[][] _matrix;
    public double _factor = 0;

    //Constructeurs
    public ConvolMatrix(double[][] matrix){
        _size = matrix.length;
        _matrix = new double[_size][_size];
        for(int i = 0; i < _size; ++i)
            for(int j = 0; j < _size; ++j) {
                _matrix[i][j] = matrix[i][j];
                _factor += matrix[i][j];
            }
    }

    public ConvolMatrix(double[][] matrix, double factor){
        _size = matrix.length;
        _matrix = new double[_size][_size];
        for(int i = 0; i < _size; ++i)
            for(int j = 0; j < _size; ++j)
                _matrix[i][j] = matrix[i][j];
        _factor = factor;
    }

    //Accesseurs
    public int getSize(){
        return _size;
    }

    public double getFactor(){
        return _factor;
    }

    public void setFactor(double factor){
        _factor = factor;
    }

    public double getMatrixElement(int x, int y){
        return _matrix[x][y];
    }

    public void setMatrixElement(int x, int y, double value){
        _matrix[x][y] = value;
    }

    //Méthodes de la forme canonique
    public ConvolMatrix clone(){
        return new ConvolMatrix(_matrix, _factor);
    }

    public String toString(){
        StringBuffer str = new StringBuffer("Matrix :\n");
        for(int i = 0; i < _size; ++i){
            for(int j = 0; j < _size; ++j){
                str.append(_matrix[i][j]);
                str.append(" ");
            }
            str.append("\n");
        }
        str.append("Size : ");
        str.append(_size);
        str.append("\nFactor : ");
        str.append(_factor);
        str.append("\n");
        return str.toString();
    }


    public boolean equals(ConvolMatrix matrix){
        if(_size != matrix._size || _factor != matrix._factor)
            return false;
        for(int i = 0; i < _size; ++i)
            for(int j = 0; j < _size; ++j)
                if (_matrix[i][j] != matrix._matrix[i][j])
                    return false;
        return true;
    }

    //méthodes
    public static void applyConvolution(Bitmap bmp, ConvolMatrix matrix){
        Bitmap bmpCopy = bmp.copy(bmp.getConfig(), true);

        int width = bmp.getWidth();
        int height = bmp.getHeight();


        int a, r, g, b;
        int sumR, sumG, sumB;
        int pixels[][] = new int[matrix._size][matrix._size];

        int opt = matrix._size/2;

        for(int y = opt; y < height - opt-1; ++y) {
            for (int x = opt; x < width - opt-1; ++x) {

                // get pixel matrix
                for (int i = 0; i < matrix._size; ++i) {
                    for (int j = 0; j < matrix._size; ++j) {
                        pixels[i][j] = bmpCopy.getPixel(x - 1 + i, y - 1 + j);
                    }
                }

                // get alpha of center pixel
                a = Color.alpha(pixels[1][1]);

                // init color sum
                sumR = 0;
                sumG = 0;
                sumB = 0;

                // get sum of RGB on matrix
                for (int i = 0; i < matrix._size; ++i) {
                    for (int j = 0; j < matrix._size; ++j) {
                        sumR += (Color.red(pixels[i][j]) * matrix._matrix[i][j]);
                        sumG += (Color.green(pixels[i][j]) * matrix._matrix[i][j]);
                        sumB += (Color.blue(pixels[i][j]) * matrix._matrix[i][j]);
                    }
                }

                // get final Red
                r = calculValue(sumR, matrix);

                // get final Green
                g = calculValue(sumG, matrix);

                // get final Blue
                b = calculValue(sumB, matrix);

                // apply new pixel
                bmp.setPixel(x + 1, y + 1, Color.argb(a, r, g, b));
            }
        }
    }

    private static int calculValue(int sum, ConvolMatrix matrix){
        int r = (int) (sum / matrix._factor);
        if (r < 0) {
            r = 0;
        } else if (r > 255) {
            r = 255;
        }
        return r;
    }
}


/* Autre version qui prend en compte les bords, mais trop lente

public static void applyConvolution(Bitmap bmp, ConvolMatrix matrix){
        Bitmap bmpCopy = bmp.copy(bmp.getConfig(), true);

        int width = bmp.getWidth();
        int height = bmp.getHeight();

        int a, r, g, b;
        int sumR, sumG, sumB;
        int iCalc, jCalc;
        int uCalc, vCalc;

        for(int i = 0; i < width; ++i){
            for(int j = 0; j < height; ++j){

                // get alpha of center pixel
                a = Color.alpha(bmpCopy.getPixel(i, j));

                // init color sum
                sumR = 0;
                sumG = 0;
                sumB = 0;

                //calcul en gérant les bords
                for(int u = i-1; u <= i+1; ++i) {
                    uCalc = 0;
                    for (int v = j - 1; v <= i + 1; ++j) {
                        vCalc = 0;
                        if (u < 0) {
                            iCalc = 0;
                        } else if (u >= width) {
                            iCalc = width - 1;
                        } else {
                            iCalc = u;
                        }
                        if (v < 0) {
                            jCalc = 0;
                        } else if (v >= height) {
                            jCalc = height - 1;
                        } else {
                            jCalc = v;
                        }
                        sumR += (Color.red(bmpCopy.getPixel(iCalc, jCalc)) * matrix._matrix[uCalc][vCalc]);
                        sumG += (Color.green(bmpCopy.getPixel(iCalc, jCalc)) * matrix._matrix[uCalc][vCalc]);
                        sumB += (Color.blue(bmpCopy.getPixel(iCalc, jCalc)) * matrix._matrix[iCalc][vCalc]);
                        vCalc += 1;
                    }
                    uCalc += 1;
                }

                // get final Red
                r = calculValue(sumR, matrix);

                // get final Green
                g = calculValue(sumG, matrix);

                // get final Blue
                b = calculValue(sumB, matrix);

                // apply new pixel
                bmp.setPixel(i, j, Color.argb(a, r, g, b));
            }
        }

    }
 */