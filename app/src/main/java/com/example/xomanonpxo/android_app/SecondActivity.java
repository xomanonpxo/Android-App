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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    //Start the camera
    private void cameraIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //File file = new File(Environment.getExternalStorageDirectory()+File.separator+"image.jpg");
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, REQUEST_CAMERA);
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
        bmp = (Bitmap) data.getExtras().get("data");
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
    public void display(){

        Button luminosityButton = (Button)findViewById(R.id.luminosityButton);
        luminosityButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
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
            }
        });

        Button contrastButton = (Button)findViewById(R.id.contrastButton);
        contrastButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                final AlertDialog.Builder popDialog = new AlertDialog.Builder(SecondActivity.this);
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
            }
        });

        Button histogramEqButton = (Button)findViewById(R.id.histogramEqButton);
        histogramEqButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Filters.histogramEqualization(bmpMod);
            }
        });

        Button filtersButton = (Button)findViewById(R.id.filtersButton);
        filtersButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                final CharSequence[] items = { "Grayscale", "Sepia", "Hue selection", "Invert", "Anaglyph 3D", "Colorize", "Red Canal", "Green Canal", "Blue Canal"};
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
                        else if(items[item].equals("Colorize")){
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
                        else if(items[item].equals("Red Canal")){
                            Filters.redCanal(bmpMod);
                        }
                        else if(items[item].equals("Green Canal")){
                            Filters.greenCanal(bmpMod);
                        }
                        else if(items[item].equals("Blue Canal")){
                            Filters.blueCanal(bmpMod);
                        }
                    }
                });
                builder.show();
            }
        });

        Button convolution = (Button)findViewById(R.id.convolutionButton);
        convolution.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "convolution to be done !", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(), "open failed !", Toast.LENGTH_SHORT).show();
                boolean stored = storeImage(bmpMod, "Test.png");
            }
        });

        imageView = (ImageView)findViewById(R.id.imageView);
        mAttacher = new PhotoViewAttacher(imageView);
    }

    private boolean storeImage(Bitmap imageData, String filename) {
        //get path to external storage (SD card)
        String iconsStoragePath = Environment.getExternalStorageDirectory() + "/Android-App/myImages/";
        File sdIconStorageDir = new File(iconsStoragePath);

        //create storage directories, if they don't exist
        sdIconStorageDir.mkdirs();

        try {
            String filePath = sdIconStorageDir.toString() + filename;
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);

            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

            //choose another format if PNG doesn't suit you
            imageData.compress(Bitmap.CompressFormat.PNG, 100, bos);

            bos.flush();
            bos.close();

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