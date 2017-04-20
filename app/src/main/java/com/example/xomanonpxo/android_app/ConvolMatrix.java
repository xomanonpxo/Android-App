package com.example.xomanonpxo.android_app;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.renderscript.Matrix4f;

/**
 * Created by xomanonpxo on 19/04/17.
 */

public class ConvolMatrix {

    //Données membres d'objets
    public int Size;
    public double[][] Matrix;
    public double Factor = 0;

    //Constructeurs
    public ConvolMatrix(double[][] matrix){
        Size = matrix.length;
        Matrix = new double[Size][Size];
        for(int i = 0; i < Size; ++i)
            for(int j = 0; j < Size; ++j) {
                Matrix[i][j] = matrix[i][j];
                Factor += matrix[i][j];
            }
    }

    public ConvolMatrix(double[][] matrix, double factor){
        Size = matrix.length;
        Matrix = new double[Size][Size];
        for(int i = 0; i < Size; ++i)
            for(int j = 0; j < Size; ++j)
                Matrix[i][j] = matrix[i][j];
        Factor = factor;
    }

    //Accesseurs
    public int getSize(){
        return Size;
    }

    public double getFactor(){
        return Factor;
    }

    public void setFactor(double factor){
        Factor = factor;
    }

    public double getMatrixElement(int x, int y){
        return Matrix[x][y];
    }

    public void setMatrixElement(int x, int y, double value){
        Matrix[x][y] = value;
    }

    //Méthodes de la forme canonique
    public ConvolMatrix clone(){
        return new ConvolMatrix(Matrix, Factor);
    }

    public String toString(){
        StringBuffer str = new StringBuffer("Matrix :\n");
        for(int i = 0; i < Size; ++i){
            for(int j = 0; j < Size; ++j){
                str.append(Matrix[i][j]);
                str.append(" ");
            }
            str.append("\n");
        }
        str.append("Size : ");
        str.append(Size);
        str.append("\nFactor : ");
        str.append(Factor);
        str.append("\n");
        return str.toString();
    }


    public boolean equals(ConvolMatrix matrix){
        if(Size != matrix.Size || Factor != matrix.Factor)
            return false;
        for(int i = 0; i < Size; ++i)
            for(int j = 0; j < Size; ++j)
                if (Matrix[i][j] != matrix.Matrix[i][j])
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
        int pixels[][] = new int[matrix.Size][matrix.Size];

        int opt = matrix.Size/2;

        for(int y = opt; y < height - opt-1; ++y) {
            for (int x = opt; x < width - opt-1; ++x) {

                // get pixel matrix
                for (int i = 0; i < matrix.Size; ++i) {
                    for (int j = 0; j < matrix.Size; ++j) {
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
                for (int i = 0; i < matrix.Size; ++i) {
                    for (int j = 0; j < matrix.Size; ++j) {
                        sumR += (Color.red(pixels[i][j]) * matrix.Matrix[i][j]);
                        sumG += (Color.green(pixels[i][j]) * matrix.Matrix[i][j]);
                        sumB += (Color.blue(pixels[i][j]) * matrix.Matrix[i][j]);
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
        int r = (int) (sum / matrix.Factor);
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
                        sumR += (Color.red(bmpCopy.getPixel(iCalc, jCalc)) * matrix.Matrix[uCalc][vCalc]);
                        sumG += (Color.green(bmpCopy.getPixel(iCalc, jCalc)) * matrix.Matrix[uCalc][vCalc]);
                        sumB += (Color.blue(bmpCopy.getPixel(iCalc, jCalc)) * matrix.Matrix[iCalc][vCalc]);
                        vCalc += 1;
                    }
                    uCalc += 1;
                }

                // get final Red
                r = (int)(sumR / matrix.Factor);
                if (r < 0) {
                    r = 0;
                } else if (r > 255) {
                    r = 255;
                }

                // get final Green
                g = (int)(sumG / matrix.Factor);
                if(g < 0) {
                    g = 0;
                } else if(g > 255) {
                    g = 255;
                }

                // get final Blue
                b = (int)(sumB / matrix.Factor);
                if(b < 0) {
                    b = 0;
                }
                else if(b > 255) {
                    b = 255;
                }

                // apply new pixel
                bmp.setPixel(i, j, Color.argb(a, r, g, b));
            }
        }

    }
 */