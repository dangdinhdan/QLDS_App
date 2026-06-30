package com.example.mobile.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {
    private static ImageLoader instance;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final LruCache<String, Bitmap> memoryCache;

    private ImageLoader() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static synchronized ImageLoader getInstance() {
        if (instance == null) {
            instance = new ImageLoader();
        }
        return instance;
    }

    public void loadImage(String urlStr, ImageView imageView, int placeholderResId) {
        if (urlStr == null || urlStr.trim().isEmpty()) {
            imageView.setTag(null);
            imageView.setImageResource(placeholderResId);
            return;
        }
        
        Bitmap cached = memoryCache.get(urlStr);
        if (cached != null) {
            imageView.setTag(urlStr);
            imageView.setImageBitmap(cached);
            return;
        }

        // Set placeholder first and tag
        imageView.setTag(urlStr);
        imageView.setImageResource(placeholderResId);

        executor.execute(() -> {
            HttpURLConnection connection = null;
            InputStream input = null;
            try {
                URL url = new URL(urlStr);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.connect();
                input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                if (bitmap != null) {
                    memoryCache.put(urlStr, bitmap);
                    final Bitmap finalBitmap = bitmap;
                    mainHandler.post(() -> {
                        if (urlStr.equals(imageView.getTag())) {
                            imageView.setImageBitmap(finalBitmap);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (input != null) {
                        input.close();
                    }
                } catch (Exception ignored) {}
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }
}
