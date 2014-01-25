package org.spilth.screenshotsaturday.loaders;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageLoader {
  public static Bitmap getBitmapFromUrl(String imageUrl) {
    byte[] bytes = null;
    Bitmap bitmap = null;

    try {
      URL url = new URL(imageUrl);

      HttpURLConnection connection = (HttpURLConnection) url
        .openConnection();
      try {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = connection.getInputStream();
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
          return null;
        }

        int bytesRead = 0;
        byte[] buffer = new byte[1024];
        while ((bytesRead = in.read(buffer)) > 0) {
          out.write(buffer, 0, bytesRead);
        }
        out.close();
        bytes = out.toByteArray();
        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
      } finally {
        connection.disconnect();
      }

    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return bitmap;
  }
}
