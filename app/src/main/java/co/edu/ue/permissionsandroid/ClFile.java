package co.edu.ue.permissionsandroid;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ClFile {
    private Context context;

    public ClFile(Context context) {
        this.context = context;
    }

    public void saveTextFile(String fileName, String content) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ usa MediaStore (Scoped Storage)
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, fileName + ".txt");
                values.put(MediaStore.Downloads.MIME_TYPE, "text/plain");
                values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                Uri uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri)) {
                        outputStream.write(content.getBytes());
                        outputStream.flush();
                        Toast.makeText(context, "Archivo guardado en Descargas", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                // Para Android 9 o menor (Uso de almacenamiento tradicional)
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName + ".txt");
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(content.getBytes());
                    fos.flush();
                    Toast.makeText(context, "Archivo guardado en: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(context, "Error al guardar el archivo: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}

