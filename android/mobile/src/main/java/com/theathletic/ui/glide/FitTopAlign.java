package com.theathletic.ui.glide;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import com.bumptech.glide.util.Util;

import java.security.MessageDigest;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A {@link BitmapTransformation} which fits and top aligns images to prevent head cuts.
 */
public final class FitTopAlign extends BitmapTransformation {
    private static final String ID = "com.theathletic.ui.glide.FitTopAlign";
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);

    public FitTopAlign() {
    }

    @Override
    protected Bitmap transform(
        @NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {

        if (toTransform.getWidth() == outWidth && toTransform.getHeight() == outHeight) {
            return toTransform;
        }
        // From ImageView/Bitmap.createScaledBitmap.
        final float scale;
        final float dx;
        Matrix m = new Matrix();
        if (toTransform.getWidth() * outHeight > outWidth * toTransform.getHeight()) {
            scale = (float) outHeight / (float) toTransform.getHeight();
            dx = (outWidth - toTransform.getWidth() * scale) * 0.5f;
        } else {
            scale = (float) outWidth / (float) toTransform.getWidth();
            dx = 0;
        }

        m.setScale(scale, scale);
        m.postTranslate((int) (dx + 0.5f), 0);

        Bitmap result = pool.get(outWidth, outHeight, getNonNullConfig(toTransform));
        // We don't add or remove alpha, so keep the alpha setting of the Bitmap we were given.
        TransformationUtils.setAlpha(toTransform, result);

        applyMatrix(toTransform, result, m);
        return result;
    }

    private static void applyMatrix(@NonNull Bitmap inBitmap, @NonNull Bitmap targetBitmap,
                                    Matrix matrix) {
        BITMAP_DRAWABLE_LOCK.lock();
        try {
            Canvas canvas = new Canvas(targetBitmap);
            canvas.drawBitmap(inBitmap, matrix, DEFAULT_PAINT);
            canvas.setBitmap(null);
        } finally {
            BITMAP_DRAWABLE_LOCK.unlock();
        }
    }

    public static final int PAINT_FLAGS = Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG;
    private static final Paint DEFAULT_PAINT = new Paint(PAINT_FLAGS);

    private static final Lock BITMAP_DRAWABLE_LOCK = new ReentrantLock();

    @NonNull
    private static Bitmap.Config getNonNullConfig(@NonNull Bitmap bitmap) {
        return bitmap.getConfig() != null ? bitmap.getConfig() : Bitmap.Config.ARGB_8888;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof FitTopAlign;
    }

    @Override
    public int hashCode() {
        return Util.hashCode(ID.hashCode());
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
    }
}
