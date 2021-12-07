package com.reactlibrary;

import android.net.Uri;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Map;

public class GetPixelColorModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext reactContext;
    private Bitmap bitmap;

    public GetPixelColorModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "GetPixelColor";
    }

    @ReactMethod
    public void init(String encodedImage, Callback callback) {
        try {
            final byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            this.bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            callback.invoke(null, true);
        } catch (Exception e) {
            callback.invoke(e.getMessage());
        }
    }

    @ReactMethod
    public void getRGB(int x, int y, Callback callback) {
        try {
            final int pixel = this.bitmap.getPixel(x, y);

            final int red = Color.red(pixel);
            final int green = Color.green(pixel);
            final int blue = Color.blue(pixel);

            final WritableArray result = new WritableNativeArray();
            result.pushInt(red);
            result.pushInt(green);
            result.pushInt(blue);

            callback.invoke(null, result);
        } catch (Exception e) {
            callback.invoke(e.getMessage());
        }
    }

    @ReactMethod
    public void cropImage(ReadableMap crop, Callback callback) {
        try {
            final int width = crop.getInt("width");
            final int height = crop.getInt("height");
            final int x = crop.getInt("x");
            final int y = crop.getInt("y");
            System.out.println("X: " + x + "   Y: " + y);
            System.out.println(width + " " + height);

            Bitmap newBitmap = Bitmap.createBitmap(this.bitmap, x, y, width, height);
            String result = this.convertToBase64(newBitmap);

            callback.invoke(null, result);
        } catch (Exception e) {
            callback.invoke(e.getMessage());
        }
    }

    @ReactMethod
    public void getPixels(Callback callback) {
        try {
            final int width = this.bitmap.getWidth();
            final int height = this.bitmap.getHeight();
            System.out.println("width: " + width + " height: " + height);

            final WritableMap result = new WritableNativeMap();
            final WritableArray data = new WritableNativeArray();

            for (int x = 0; x < height; x++) {
                for (int y = 0; y < width; y++) {

                    final int pixel = this.bitmap.getPixel(y,x);

                    final int red = Color.red(pixel);
                    final int green = Color.green(pixel);
                    final int blue = Color.blue(pixel);
                    final int alpha = Color.alpha(pixel);

                    data.pushInt(red);
                    data.pushInt(green);
                    data.pushInt(blue);
                    data.pushInt(alpha);
                }
            }
            result.putInt("width", width);
            result.putInt("height", height);
            result.putArray("data", data);

            callback.invoke(null, result);
        } catch (Exception e) {
            callback.invoke(e.getMessage());
        }
    }

    public static String convertToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }
}
