package com.galleryapp.application;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.galleryapp.data.model.ImageObj;
import com.galleryapp.data.provider.GalleryDBContent;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

public class GalleryApp extends Application {
    public GalleryApp() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        display.getMetrics(metrics);
        // Create global configuration and initialize ImageLoader with this configuration
        File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCacheExtraOptions(metrics.widthPixels, metrics.heightPixels) // default = device screen dimensions
                .discCacheExtraOptions(metrics.widthPixels, metrics.heightPixels, Bitmap.CompressFormat.JPEG, 75, null)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .discCache(new UnlimitedDiscCache(cacheDir)) // default
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileCount(100)
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public Uri saveImage(ImageObj image) {
//        String imageId = imageId(image.getImagePath());
//        Log.d("MediaStore", "THUMBS::imageId = " + imageId);
//        if (imageId != null) {
//            String thumbData = getThumbData(imageId);
//            Log.d("MediaStore", "THUMBS::thumbData = " + thumbData);
//            image.setThumbPath(thumbData);
//        }
        return getContentResolver().insert(GalleryDBContent.GalleryImages.CONTENT_URI, image.toContentValues());
    }

    private String getThumbData(String imageId) {
        Cursor thumbs = getContentResolver()
                .query(
                        MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.Thumbnails.DATA},
                        MediaStore.Images.Thumbnails.IMAGE_ID + "=?",
                        new String[]{imageId},
                        null
                );
        String thumbData = null;
        assert thumbs != null;
        if (thumbs.getCount() > 0) {
            thumbs.moveToLast();
            thumbData = thumbs.getString(thumbs.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
            Log.d("MediaStore", "THUMBS");
            Log.d("MediaStore", "THUMBS::DATA = " + thumbData);
            thumbs.close();
        }
        return thumbData;
    }

    public String imageId(String imagePath) {
        Cursor images = getContentResolver()
                .query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{
                                MediaStore.Images.Media._ID
                        },
                        MediaStore.Images.Media.DATA + "=?",
                        new String[]{imagePath},
                        null
                );
        String imageId = null;
        assert images != null;
        if (images.getCount() > 0) {
            images.moveToLast();
            imageId = images.getString(images.getColumnIndex(MediaStore.Images.Media._ID));
            Log.d("MediaStore", "IMAGES");
            Log.d("MediaStore", "IMAGES::_ID = " + imageId);
            images.close();
        }
        return imageId;
    }
}
