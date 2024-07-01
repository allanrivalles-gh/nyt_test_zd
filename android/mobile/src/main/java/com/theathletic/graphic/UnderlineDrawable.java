package com.theathletic.graphic;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;


public class UnderlineDrawable extends Drawable
{
    private final Paint mPaint;
    private final RectF mRect;
    private final float mThickness;
    private final int mColor;
    private int mHeight;
    private int mWidth;


    private UnderlineDrawable(float thickness, int color)
    {
        mPaint = new Paint();
        mRect = new RectF();
        mThickness = thickness;
        mColor = color;
    }


    public static UnderlineDrawable createUnderlineDrawable(float thickness, int color)
    {
        return new UnderlineDrawable(thickness, color);
    }


    @Override
    protected void onBoundsChange(Rect bounds)
    {
        mHeight = bounds.height();
        mWidth = bounds.width();
    }


    @Override
    public void draw(@NonNull Canvas canvas)
    {
        // Set the correct values in the Paint
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL);

        // Adjust the rect
        mRect.left = 0;
        mRect.top = mHeight - mThickness;
        mRect.right = mWidth;
        mRect.bottom = mHeight;

        // Draw it
        canvas.drawRect(mRect, mPaint);
    }


    @Override
    public int getOpacity()
    {
        return PixelFormat.OPAQUE;
    }


    @Override
    public void setAlpha(int arg0)
    {
    }


    @Override
    public void setColorFilter(ColorFilter arg0)
    {
    }
}