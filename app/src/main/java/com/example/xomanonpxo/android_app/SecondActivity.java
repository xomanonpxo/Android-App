package com.example.xomanonpxo.android_app;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.FileChannel;

import petrov.kristiyan.colorpicker.ColorPicker;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static android.R.attr.data;
import static android.R.attr.seekBarStyle;

public class SecondActivity extends AppCompatActivity {

    //Request codes
    public static final int REQUEST_CAMERA = 0;
    public static final int SELECT_FILE = 1;

    ImageView imageView;
    PhotoViewAttacher mAttacher;

    Bitmap bmp;
    Bitmap bmpMod;

    String imagePath = Environment.getExternalStorageDirectory() + "/Android-Filters-App/" + File.separator + Long.toString(System.currentTimeMillis()) + ".png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createDir();

        selectImage();

        setContentView(R.layout.activity_second);

        display();


    }

    //Given the choice, start the good intent
    private void selectImage(){
        Intent intent = getIntent();
        int choice = (int)intent.getIntExtra("choice", -1);

        if(choice == 0){
            cameraIntent();
        }
        if(choice == 1){
            galleryIntent();
        }
    }

    private void createDir(){
        File sdStorageDir = new File(Environment.getExternalStorageDirectory() + "/Android-Filters-App/");
        sdStorageDir.mkdirs();
    }

    //Start the camera
    private void cameraIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imagePath = Environment.getExternalStorageDirectory() + "/Android-Filters-App/" + File.separator + Long.toString(System.currentTimeMillis()) + ".png";
        File file = new File(imagePath);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    //Start the gallery
    private void galleryIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    //Given the request code, call a function processing the image picked in gallery or taken by the camera
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_CAMERA){
                onCaptureImageResult(data);

            }
            else if (requestCode == SELECT_FILE){
                onSelectFromGalleryResult(data);
            }
        }
        else {
            Toast.makeText(this, "You haven't picked any image.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    //Show image picked from the camera
    private void onCaptureImageResult(Intent data) {
        bmp = BitmapFactory.decodeFile(imagePath);
        bmpMod = bmp.copy(bmp.getConfig(), true);
        imageView.setImageBitmap(bmpMod);
        mAttacher.update();
    }

    //Show image picked from the gallery
    private void onSelectFromGalleryResult(Intent data){
        bmp = null;
        if (data != null) {
            try {
                bmp = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        bmpMod = bmp.copy(bmp.getConfig(), true);
        imageView.setImageBitmap(bmpMod);
        mAttacher.update();
    }

    //Handle the display of the activity
    private void display(){

        Button adjustButton = (Button)findViewById(R.id.adjustButton);
        adjustButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                final CharSequence[] items = {"Luminosity", "Contrast", "Saturation", "Histogram Equalization", "Hue"};
                AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                builder.setTitle("Select an adjustment");
                builder.setItems(items, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int item){
                        if(items[item].equals("Luminosity")){
                            Filters.adjust(bmpMod, 20, 1, 1);
                        }
                        else if(items[item].equals("Contrast")){
                            Filters.adjust(bmpMod, 0, 1.5f, 1);
                        }
                        else if(items[item].equals("Saturation")){
                            Filters.adjust(bmpMod, 0, 1, 1.5f);
                        }
                        else if(items[item].equals("Histogram Equalization")){
                            Filters.histogramEqualization(bmpMod);
                        }
                        else if(items[item].equals("Hue")){
                            ColorPicker colorPicker = new ColorPicker(SecondActivity.this);
                            colorPicker.show();
                            colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                                @Override
                                public void onChooseColor(int position, int color) {
                                    Filters.colorize(bmpMod, color);
                                }
                                @Override
                                public void onCancel() {
                                }
                            });
                        }
                    }
                });
                builder.show();
            }
        });

        Button effectsButton = (Button)findViewById(R.id.effectsButton);
        effectsButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                final CharSequence[] items = {"Grayscale", "Sepia", "Hue selection", "Invert", "Anaglyph 3D", "Red Canal", "Green Canal", "Blue Canal"};
                AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                builder.setTitle("Select a filter");
                builder.setItems(items, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int item){
                        if(items[item].equals("Grayscale")){
                            Filters.grayscale(bmpMod);
                        }
                        else if(items[item].equals("Sepia")){
                            Filters.sepia(bmpMod);
                        }
                        else if(items[item].equals("Hue selection")){
                            ColorPicker colorPicker = new ColorPicker(SecondActivity.this);
                            colorPicker.show();
                            colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                                @Override
                                public void onChooseColor(int position, int color) {
                                    Filters.selectHue(bmpMod, color);
                                }
                                @Override
                                public void onCancel() {
                                }
                            });
                        }
                        else if(items[item].equals("Invert")){
                            Filters.invert(bmpMod);
                        }
                        else if(items[item].equals("Anaglyph 3D")){
                            Filters.anaglyph(bmpMod);
                        }
                        else if(items[item].equals("Red Canal")){
                            Filters.canal(bmpMod, 1, 0, 0);
                        }
                        else if(items[item].equals("Green Canal")){
                            Filters.canal(bmpMod, 0, 1, 0);
                        }
                        else if(items[item].equals("Blue Canal")){
                            Filters.canal(bmpMod, 0, 0, 1);
                        }
                    }
                });
                builder.show();
            }
        });

        final Button convolution = (Button)findViewById(R.id.convolutionButton);
        convolution.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                final CharSequence[] items = {"Average", "Gaussian", "Sobel", "Laplacien"};
                AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                builder.setTitle("Select a convolution");
                builder.setItems(items, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int item){
                        if(items[item].equals("Average")){
                            double AverageConfig[][] = new double[][] {
                                    { 1, 1, 1, 1, 1 },
                                    { 1, 1, 1, 1, 1 },
                                    { 1, 1, 1, 1, 1 },
                                    { 1, 1, 1, 1, 1 },
                                    { 1, 1, 1, 1, 1 }
                            };
                            ConvolMatrix averageMatrix = new ConvolMatrix(AverageConfig);
                            ConvolMatrix.applyConvolution(bmpMod, averageMatrix);
                        }
                        else if(items[item].equals("Gaussian")){
                            double GaussianConfig[][] = new double[][] {
                                    { 1, 2, 3, 2, 1 },
                                    { 2, 6, 8, 6, 2 },
                                    { 3, 8, 10, 8, 3 },
                                    { 2, 6, 8, 6, 2 },
                                    { 1, 2, 3, 2, 1 }
                            };
                            ConvolMatrix gaussMatrix = new ConvolMatrix(GaussianConfig);
                            ConvolMatrix.applyConvolution(bmpMod, gaussMatrix);
                        }
                        else if(items[item].equals("Sobel")){
                            Bitmap bmpCopy1 = bmp.copy(bmp.getConfig(), true);
                            double sobelVerticalConfig[][] = new double[][] {
                                    { 1, 2, 1 },
                                    { 0, 0, 0 },
                                    { -1, -2, -1 }
                            };
                            ConvolMatrix sobelVerticalMatrix = new ConvolMatrix(sobelVerticalConfig);
                            ConvolMatrix.applyConvolution(bmpCopy1, sobelVerticalMatrix);
                            Bitmap bmpCopy2 = bmp.copy(bmp.getConfig(), true);
                            double sobelHorizontalConfig[][] = new double[][] {
                                    { -1, 0, 1 },
                                    { -2, 0, 2 },
                                    { -1, 0, 1 }
                            };
                            ConvolMatrix sobelHorizontalMatrix = new ConvolMatrix(sobelHorizontalConfig);
                            ConvolMatrix.applyConvolution(bmpCopy2, sobelHorizontalMatrix);
                            int p1, p2;
                            for(int i = 0; i < bmpMod.getWidth(); ++i){
                                for(int j = 0; j < bmpMod.getHeight(); ++j){
                                    p1 = bmpCopy1.getPixel(i, j);
                                    p2 = bmpCopy2.getPixel(i, j);

                                    bmpMod.setPixel(i, j, (int)Math.sqrt(p1*p1+p2*p2));
                                }
                            }
                            Toast.makeText(getApplicationContext(), Double.toString(sobelHorizontalMatrix.getFactor()), Toast.LENGTH_SHORT).show();
                        }
                        else if(items[item].equals("Laplacien")) {
                            double laplacienConfig[][] = new double[][] {
                                    { 1, 1, 1 },
                                    { 1, -8, 1 },
                                    { 1, 1, 1 }
                            };
                            ConvolMatrix laplacienMatrix = new ConvolMatrix(laplacienConfig);
                            ConvolMatrix.applyConvolution(bmpMod, laplacienMatrix);

                            Toast.makeText(getApplicationContext(), "Laplacien to be done !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();
            }
        });

        Button resetButton = (Button)findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                bmpMod = bmp.copy(bmp.getConfig(), true);
                imageView.setImageBitmap(bmpMod);
                mAttacher.update();
            }
        });

        Button savePicButton = (Button)findViewById(R.id.savePicButton);
        savePicButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                boolean stored = storeImage(bmpMod);
                Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                Toast.makeText(getApplicationContext(), "Your pic has been saved !", Toast.LENGTH_SHORT).show();
            }
        });

        imageView = (ImageView)findViewById(R.id.imageView);
        mAttacher = new PhotoViewAttacher(imageView);
    }

    private boolean storeImage(Bitmap imageData) {
         try {
             FileOutputStream fileOutputStream = new FileOutputStream(imagePath);

             BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

             imageData.compress(Bitmap.CompressFormat.PNG, 100, bos);

             bos.flush();
             bos.close();

             //Add in gallery
             Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
             Uri contentUri = Uri.fromFile(new  File(imagePath));
             mediaScanIntent.setData(contentUri);
             this.sendBroadcast(mediaScanIntent);

         } catch (FileNotFoundException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return false;
        } catch (IOException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return false;
        }
        return true;
    }

}

/*
Toast.makeText(getApplicationContext(), "Color tone selection to be done!", Toast.LENGTH_SHORT).show();
 */
//System.currentTimeMillis()

/*
luminosity

                final AlertDialog.Builder popDialog = new AlertDialog.Builder(SecondActivity.this);
                final SeekBar seek = new SeekBar(SecondActivity.this);
                seek.setMax(200);
                seek.setProgress(100);
                seek.setKeyProgressIncrement(10);

                popDialog.setTitle("Please select the luminosity ");
                popDialog.setView(seek);
                seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                        Filters.luminosity(bmpMod, progress-100);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
                popDialog.show();

Filters.change(bmpMod, 20, 1, 1);
 */

/*
        Button contrastButton = (Button)findViewById(R.id.contrastButton);
        contrastButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                /*final AlertDialog.Builder popDialog = new AlertDialog.Builder(SecondActivity.this);
                final SeekBar seek = new SeekBar(SecondActivity.this);
                seek.setMax(200);
                seek.setProgress(100);
                seek.setKeyProgressIncrement(10);

                popDialog.setTitle("Please select the contrast ");
                popDialog.setView(seek);
                seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                        Filters.contrast(bmpMod, progress-100);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
                popDialog.show();
Filters.change(bmpMod, 0, 1.5f, 1);
        }
        });

        Button saturationButton = (Button)findViewById(R.id.saturationButton);
        saturationButton.setOnClickListener(new View.OnClickListener(){
public void onClick(View view) {
        Filters.change(bmpMod, 0, 1, 1.5f);
        }
        });

        Button histogramEqButton = (Button)findViewById(R.id.histogramEqButton);
        histogramEqButton.setOnClickListener(new View.OnClickListener(){
public void onClick(View view) {
        Filters.histogramEqualization(bmpMod);
        }
        });

 */