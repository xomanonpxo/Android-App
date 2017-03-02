package com.example.xomanonpxo.android_app;

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
import android.widget.TextView;
import android.widget.Toast;

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

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static android.R.attr.data;

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
                Toast.makeText(getApplicationContext(), "luminosity !", Toast.LENGTH_LONG).show();
            }
        });

        Button contrastButton = (Button)findViewById(R.id.contrastButton);
        contrastButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "contrast !", Toast.LENGTH_LONG).show();
            }
        });

        Button histogramEqButton = (Button)findViewById(R.id.histogramEqButton);
        histogramEqButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "historigram eq !", Toast.LENGTH_LONG).show();
            }
        });

        Button filtersButton = (Button)findViewById(R.id.filtersButton);
        filtersButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                final CharSequence[] items = { "Grayscale", "Sepia", "Color tone selection", "Invert", "Anaglyph 3D"};
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
                        else if(items[item].equals("Color tone selection")){
                            Toast.makeText(getApplicationContext(), "Color tone selection to be done!", Toast.LENGTH_SHORT).show();
                        }
                        else if(items[item].equals("Invert")){
                            Filters.invert(bmpMod);
                        }
                        else if(items[item].equals("Anaglyph 3D")){
                            Filters.anaglyph(bmpMod);
                        }
                    }
                });
                builder.show();
            }
        });

        Button convolution = (Button)findViewById(R.id.convolutionButton);
        convolution.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "convolution !", Toast.LENGTH_LONG).show();
            }
        });

        Button resetButton = (Button)findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "reset !", Toast.LENGTH_LONG).show();
            }
        });

        Button savePicButton = (Button)findViewById(R.id.savePicButton);
        savePicButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "save pic !", Toast.LENGTH_LONG).show();
            }
        });

        imageView = (ImageView)findViewById(R.id.imageView);
        mAttacher = new PhotoViewAttacher(imageView);
    }

}