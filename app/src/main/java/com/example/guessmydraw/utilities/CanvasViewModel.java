package com.example.guessmydraw.utilities;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class CanvasViewModel extends AndroidViewModel {

    private Path path = null;
    private Bitmap bitmap = null;

    public CanvasViewModel(@NonNull Application application) {
        super(application);

    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {

        this.bitmap = bitmap;
    }
}
