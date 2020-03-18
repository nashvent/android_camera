package com.multiviral.cameraactivity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView1;
    private ImageView imageView2;
    private static final int REQUEST_CAPTURE_IMAGE = 100;

    String imageFilePath;
    String pathDni;
    String pathSign;
    HashMap<String, String> imagesCamera = new HashMap<String, String>();
    HashMap<String, ImageView> imageViewCamera = new HashMap<String, ImageView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView1 = (ImageView)this.findViewById(R.id.imageView1);
        imageViewCamera.put("dni",imageView1 );
        imageView2 = (ImageView)this.findViewById(R.id.imageView2);
        imageViewCamera.put("sign",imageView2 );

        Button dniButton = (Button) this.findViewById(R.id.button1);
        dniButton.setOnClickListener(new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v)
            {
                openCameraIntent("dni");
            }
        });

        Button signButton = (Button) this.findViewById(R.id.button2);
        signButton.setOnClickListener(new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v)
            {
                openCameraIntent("sign");
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 123);
        }

        if (savedInstanceState != null)
        {
            imagesCamera = (HashMap<String, String>) savedInstanceState.getSerializable("imagesCamera");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("imagesCamera", imagesCamera);
    }

    private void openCameraIntent(String key) {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(pictureIntent.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            try {
                photoFile = createImageFile(key);
            } catch (IOException ex) {}
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(MainActivity.this, getPackageName() + ".provider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAPTURE_IMAGE ) {
            setImageView("dni");
            setImageView("sign");
            /* //Log.d("Camera", String.valueOf(data));
            if (data != null && data.getExtras() != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(imageBitmap);
            }*/
        }
    }



    private File createImageFile(String key) throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_"+key;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Log.d("Directory", String.valueOf(storageDir));
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        Log.d("Directory", String.valueOf(image));
        imageFilePath = image.getAbsolutePath();
        Log.d("ImagePath",imageFilePath);
        imagesCamera.put(key,imageFilePath);
        Log.d("CameraKey",key);
        return image;
    }

    public void setImageView(String key){
        String imagePath = imagesCamera.get(key);
        if(imagePath != ""){
            Bitmap bmImg = BitmapFactory.decodeFile(imagePath);
            ImageView tempImageView=imageViewCamera.get(key);
            tempImageView.setImageBitmap(bmImg);
        }
    }
}
