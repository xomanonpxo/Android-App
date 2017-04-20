package com.example.xomanonpxo.android_app;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

    Bitmap bmp, bmpTmp;
    StackBitmap stack;

    String imagePath = Environment.getExternalStorageDirectory() + "/Android-Filters-App/" + File.separator + Long.toString(System.currentTimeMillis()) + ".png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createDir();

        selectImage();

        setContentView(R.layout.activity_second);

        display();


    }

    //Create directory of the app
    private void createDir(){
        File sdStorageDir = new File(Environment.getExternalStorageDirectory() + "/Android-Filters-App/");
        sdStorageDir.mkdirs();
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
        bmpTmp = bmp.copy(bmp.getConfig(), true);
        Bitmap[] stackOrig = new Bitmap[]{bmp};
        stack = new StackBitmap(stackOrig);
        imageView.setImageBitmap(stack.getTop());
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
        bmpTmp = bmp.copy(bmp.getConfig(), true);
        Bitmap[] stackOrig = new Bitmap[]{bmp};
        stack = new StackBitmap(stackOrig);
        imageView.setImageBitmap(stack.getTop());
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
                            stack.push(stack.getTop());
                            Filters.adjust(stack.getTop(), 20, 1, 1);
                            imageView.setImageBitmap(stack.getTop());
                        }
                        else if(items[item].equals("Contrast")){
                            stack.push(stack.getTop());
                            Filters.adjust(stack.getTop(), 0, 1.5f, 1);
                            imageView.setImageBitmap(stack.getTop());
                        }
                        else if(items[item].equals("Saturation")){
                            stack.push(stack.getTop());
                            Filters.adjust(stack.getTop(), 0, 1, 1.5f);
                            imageView.setImageBitmap(stack.getTop());
                        }
                        else if(items[item].equals("Histogram Equalization")){
                            stack.push(stack.getTop());
                            Filters.histogramEqualization(stack.getTop());
                            imageView.setImageBitmap(stack.getTop());
                        }
                        else if(items[item].equals("Hue")){
                            ColorPicker colorPicker = new ColorPicker(SecondActivity.this);
                            colorPicker.show();
                            colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                                @Override
                                public void onChooseColor(int position, int color) {
                                    stack.push(stack.getTop());
                                    Filters.colorize(stack.getTop(), color);
                                    imageView.setImageBitmap(stack.getTop());
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
                final CharSequence[] items = {"Grayscale", "Sepia", "Hue selection", "Invert", "Anaglyph 3D", "Red Canal", "Green Canal", "Blue Canal", "Pencil sketch"};
                AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                builder.setTitle("Select a filter");
                builder.setItems(items, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int item){
                        if(items[item].equals("Grayscale")){
                            stack.push(stack.getTop());
                            Filters.grayscale(stack.getTop());
                            imageView.setImageBitmap(stack.getTop());
                        }
                        else if(items[item].equals("Sepia")){
                            stack.push(stack.getTop());
                            Filters.sepia(stack.getTop());
                            imageView.setImageBitmap(stack.getTop());
                        }
                        else if(items[item].equals("Hue selection")){
                            ColorPicker colorPicker = new ColorPicker(SecondActivity.this);
                            colorPicker.show();
                            colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                                @Override
                                public void onChooseColor(int position, int color) {
                                    stack.push(stack.getTop());
                                    Filters.selectHue(stack.getTop(), color);
                                    imageView.setImageBitmap(stack.getTop());
                                }
                                @Override
                                public void onCancel() {
                                }
                            });
                        }
                        else if(items[item].equals("Invert")){
                            stack.push(stack.getTop());
                            Filters.invert(stack.getTop());
                            imageView.setImageBitmap(stack.getTop());
                        }
                        else if(items[item].equals("Anaglyph 3D")){
                            stack.push(stack.getTop());
                            Filters.anaglyph(stack.getTop());
                            imageView.setImageBitmap(stack.getTop());
                        }
                        else if(items[item].equals("Red Canal")){
                            stack.push(stack.getTop());
                            Filters.canal(stack.getTop(), 1, 0, 0);
                            imageView.setImageBitmap(stack.getTop());
                        }
                        else if(items[item].equals("Green Canal")){
                            stack.push(stack.getTop());
                            Filters.canal(stack.getTop(), 0, 1, 0);
                            imageView.setImageBitmap(stack.getTop());
                        }
                        else if(items[item].equals("Blue Canal")){
                            stack.push(stack.getTop());
                            Filters.canal(stack.getTop(), 0, 0, 1);
                            imageView.setImageBitmap(stack.getTop());
                        }
                        else if(items[item].equals("Pencil sketch")){
                            stack.push(stack.getTop());
                            Filters.pencilSketch(stack.getTop());
                            imageView.setImageBitmap(stack.getTop());
                        }
                    }
                });
                builder.show();
            }
        });

        final Button convolution = (Button)findViewById(R.id.convolutionButton);
        convolution.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                final CharSequence[] items = {"Average", "Gaussian", "Sobel", "Laplacian"};
                AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                builder.setTitle("Select a convolution");
                builder.setItems(items, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int item){
                        if(items[item].equals("Average")){
                            stack.push(stack.getTop());
                            ConvolMatrix averageMatrix = new ConvolMatrix(Filters.matrixConfig("average"));
                            ConvolMatrix.applyConvolution(stack.getTop(), averageMatrix);
                            imageView.setImageBitmap(stack.getTop());
                        }
                        else if(items[item].equals("Gaussian")){
                            stack.push(stack.getTop());
                            ConvolMatrix gaussMatrix = new ConvolMatrix(Filters.matrixConfig("gaussian"));
                            ConvolMatrix.applyConvolution(stack.getTop(), gaussMatrix);
                            imageView.setImageBitmap(stack.getTop());
                        }
                        else if(items[item].equals("Sobel")){
                            Bitmap bmpCopy1 = bmp.copy(bmp.getConfig(), true);
                            ConvolMatrix sobelVerticalMatrix = new ConvolMatrix(Filters.matrixConfig("sobelVertical"), 8);
                            ConvolMatrix.applyConvolution(bmpCopy1, sobelVerticalMatrix);
                            Bitmap bmpCopy2 = bmp.copy(bmp.getConfig(), true);
                            ConvolMatrix sobelHorizontalMatrix = new ConvolMatrix(Filters.matrixConfig("sobelHorizontal"), 8);
                            ConvolMatrix.applyConvolution(bmpCopy2, sobelHorizontalMatrix);
                            int p1, p2, value;
                            stack.push(stack.getTop());
                            for(int i = 0; i < stack.getTop().getWidth() - 1; ++i){
                                for(int j = 0; j < stack.getTop().getHeight() -1; ++j){
                                    p1 = bmpCopy1.getPixel(i, j);
                                    p2 = bmpCopy2.getPixel(i, j);
                                    value = Filters.grayAverage((int)Math.sqrt(p1*p1+p2*p2));
                                    stack.getTop().setPixel(i, j, Color.rgb(value, value, value));
                                }
                            }
                            imageView.setImageBitmap(stack.getTop());
                        }
                        else if(items[item].equals("Laplacian")) {
                            stack.push(stack.getTop());
                            ConvolMatrix laplacienMatrix = new ConvolMatrix(Filters.matrixConfig("laplacian"));
                            ConvolMatrix.applyConvolution(stack.getTop(), laplacienMatrix);
                            imageView.setImageBitmap(stack.getTop());
                        }
                    }
                });
                builder.show();
            }
        });

        Button undoButton = (Button)findViewById(R.id.undo);
        undoButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                if(stack.getNumTop() <= 0)
                    Toast.makeText(getApplicationContext(), "No modifications to undo !", Toast.LENGTH_SHORT).show();
                else{
                    stack.pop();
                    imageView.setImageBitmap(stack.getTop());
                }

            }
        });

        Button resetButton = (Button)findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                stack.popAllExceptFirst();
                imageView.setImageBitmap(stack.getTop());
            }
        });

        Button savePicButton = (Button)findViewById(R.id.savePicButton);
        savePicButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                boolean stored = storeImage(stack.getTop());
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