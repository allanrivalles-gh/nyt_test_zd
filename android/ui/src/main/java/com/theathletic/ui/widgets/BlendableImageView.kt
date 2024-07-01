package com.theathletic.ui.widgets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.theathletic.ui.utility.SimpleOnLayoutChangeListener

/**
 * This allows you to set drawables as a blend overlay for an image. Normal [ImageView] only let
 * you send single colors as blend overlays, but by calling [setDrawableMask], you can use any
 * drawable (e.g. a gradient drawable) to color the src image.
 */
class BlendableImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var maskLayer: Bitmap? = null

    val paint: Paint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
    }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        maskLayer?.let { canvas.drawBitmap(it, 0.0f, 0.0f, paint) }
    }

    fun setDrawableMask(drawable: Drawable?) {
        if (this.isLaidOut) {
            createNewMask(drawable)
        } else {
            addOnLayoutChangeListener(
                SimpleOnLayoutChangeListener { listener, _ ->
                    removeOnLayoutChangeListener(listener)
                    createNewMask(drawable)
                }
            )
        }
    }

    private fun createNewMask(drawable: Drawable?) {
        if (drawable == null) {
            maskLayer = null
            invalidate()
            return
        }

        val mask: Bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(mask)
        drawable.setBounds(0, 0, measuredWidth, measuredHeight)
        drawable.draw(canvas)

        maskLayer = mask
        invalidate()
    }
}