package com.example.meiro;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CameraX extends AppCompatActivity {

    private Executor executor = Executors.newSingleThreadExecutor();
    private int REQUEST_CODE_PERMISSIONS = 1001;
    private int FLASH_MODE_X;
    private int LENS_FACING;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE","android.permission.READ_EXTERNAL_STORAGE"};

    ImageView click;
    ImageView close;
    ImageView flash;

    Camera camera;
    ImageCapture imageCapture;
    ProcessCameraProvider cameraProvider;
    CameraSelector cameraSelector;
    Preview preview;
    PreviewView previewView;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_x);

        flash=findViewById(R.id.flash);
        FLASH_MODE_X=ImageCapture.FLASH_MODE_OFF;

//        switchCamera=findViewById(R.id.switchCamera);
        LENS_FACING=CameraSelector.LENS_FACING_BACK;
        click=findViewById(R.id.button);
        close=findViewById(R.id.close);

        if(allPermissionsGranted()){
            startCamera(); //start camera if permission has been granted by user
        } else{
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private void startCamera() {
        previewView = findViewById(R.id.previewx);
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            bindCameraUseCases();
        }, ContextCompat.getMainExecutor(this));


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CameraX.this,MainActivity.class));
            }
        });
        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FLASH_MODE_X==2){
                    flash.setImageResource(R.drawable.ic_flash_on);
                    camera.getCameraControl().enableTorch(true);
                    FLASH_MODE_X=ImageCapture.FLASH_MODE_ON;
                    Toast.makeText(CameraX.this, "Flash On", Toast.LENGTH_SHORT).show();
                }else{
                    flash.setImageResource(R.drawable.ic_flash_off);
                    camera.getCameraControl().enableTorch(false);
                    FLASH_MODE_X=ImageCapture.FLASH_MODE_OFF;
                    Toast.makeText(CameraX.this, "Flash off", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        switchCamera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(LENS_FACING==CameraSelector.LENS_FACING_BACK){
//                    LENS_FACING=CameraSelector.LENS_FACING_FRONT;
//
//                    Toast.makeText(CameraX.this, "Front Camera", Toast.LENGTH_SHORT).show();
//                }else{
//                    LENS_FACING=CameraSelector.LENS_FACING_BACK;
//                    Toast.makeText(CameraX.this, "Back Camera", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,System.currentTimeMillis());
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"image/jpeg");
                ImageCapture.OutputFileOptions outputFileOptions =
                        new ImageCapture.OutputFileOptions.Builder(getContentResolver(),MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues).build();
                imageCapture.takePicture(outputFileOptions, executor,
                        new ImageCapture.OnImageSavedCallback() {
                            @Override
                            public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
                                Log.e("CameraX", "Saved");
                            }
                            @Override
                            public void onError(ImageCaptureException error) {
                                Log.e("CameraX", error.toString());
                            }
                        }
                );
            }
        });
    }

    private void bindCameraUseCases() {

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        // Set up the view finder use case to display camera preview
        preview = new Preview.Builder().build();
        // Set up the capture use case to allow users to take photos
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setFlashMode(FLASH_MODE_X)
                .build();

        // Choose the camera by requiring a lens facing
        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(LENS_FACING)
                .build();
        // Connect the preview use case to the previewView
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        // Attach use cases to the camera with the same lifecycle owner
        camera = cameraProvider.bindToLifecycle(
                ((LifecycleOwner) this),
                cameraSelector,
                preview,
                imageCapture);
    }

    private boolean allPermissionsGranted(){

        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }
}