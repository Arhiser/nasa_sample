package com.arhiser.nasa_sample;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.IOException;

public class PhotoActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_WRITE_STORAGE = 1111;

    private static final String EXTRA_URL = "PhotoActivity.EXTRA_URL";

    private SubsamplingScaleImageView imageView;
    private Toolbar toolbar;

    private boolean isToolbarVisible;

    private Bitmap photo;

    public static void start(Context caller, String url) {
        Intent intent = new Intent(caller, PhotoActivity.class);
        intent.putExtra(EXTRA_URL, url);
        caller.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_photo);

        imageView = findViewById(R.id.image);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);

        ImageLoader.getInstance().loadImage(getIntent().getStringExtra(EXTRA_URL), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (!isFinishing()) {
                    photo = loadedImage;
                    imageView.setImage(ImageSource.cachedBitmap(loadedImage));
                    findViewById(R.id.progress).setVisibility(View.GONE);
                }
            }
        });

        toolbar.post(() -> {
            if (!isFinishing()) {
                hideActionBar();
            }
        });

        imageView.setOnClickListener(v -> {
            if (photo != null && !isToolbarVisible) {
                showActionBar();
            }
        });

        imageView.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() != MotionEvent.ACTION_DOWN
                    && motionEvent.getAction() != MotionEvent.ACTION_UP) {
                if (isToolbarVisible) {
                    hideActionBar();
                }
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_set_wallpaper:
                setWallpaper();
                hideActionBar();
                return true;
            case R.id.action_share:
                performSharing();
                hideActionBar();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void hideActionBar() {
        toolbar.animate().translationY(-toolbar.getHeight()).setDuration(300).start();
        isToolbarVisible = false;
    }

    private void showActionBar() {
        toolbar.animate().translationY(0).setDuration(300).start();
        isToolbarVisible = true;
    }

    private void performSharing() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), photo, getIntent().getStringExtra(EXTRA_URL), "");
            Uri uri = Uri.parse(path);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/jpeg");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(intent, getString(R.string.share)));
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_STORAGE);
            }
        }
    }

    private void setWallpaper() {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        try {
            wallpaperManager.setBitmap(photo);
            Snackbar.make(imageView, getString(R.string.set_as_wallpaper_completed), Snackbar.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_WRITE_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                performSharing();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
