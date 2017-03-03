package com.example.xomanonpxo.android_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.net.URI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView photoView = (ImageView) findViewById(R.id.photoView);
        ImageView galleryView = (ImageView) findViewById(R.id.galleryView);

        photoView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SecondActivity.class);
                int choice = 0;
                intent.putExtra("choice", choice);
                startActivity(intent);
            }
        });

        galleryView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SecondActivity.class);
                int choice = 1;
                intent.putExtra("choice", choice);
                startActivity(intent);
            }
        });
    }
}
