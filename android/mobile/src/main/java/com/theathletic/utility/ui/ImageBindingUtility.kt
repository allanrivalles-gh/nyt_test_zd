package com.theathletic.utility.ui

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.github.chrisbanes.photoview.PhotoView
import com.theathletic.AthleticApplication
import com.theathletic.data.SizedImages
import com.theathletic.extension.extGetActivityContext
import com.theathletic.extension.extIsAvailable
import com.theathletic.graphic.BlurTransformation
import com.theathletic.graphic.GlideImageSize
import com.theathletic.ui.glide.FitTopAlign
import com.theathletic.utility.ColorUtility
import kotlin.math.min
import timber.log.Timber

@SuppressLint("CheckResult")
@Suppress("LongParameterList", "LongMethod", "ComplexMethod")
@BindingAdapter(
    value = [
        "imageUrl", "imageCircular", "imageBlurred", "centerCrop",
        "roundedCorners", "withTransition", "asGif", "imagePlaceholder", "imageError",
        "withErrorBackground", "scaleType", "isBlackAndWhite", "fitWidthNoCrop",
        "blackWhiteAlpha", "fitWidthMaxWidth", "topAlignImage"
    ],
    requireAll = false
)
fun loadImage(
    imageView: ImageView,
    url: String?,
    circular: Boolean = false,
    blurred: Boolean = false,
    centerCrop: Boolean = true,
    roundedCorners: Float? = null,
    withTransition: Boolean = true,
    asGif: Boolean = false,
    placeholder: Drawable? = null,
    error: Drawable? = null,
    withErrorBackground: Boolean = false,
    scaleType: ImageView.ScaleType? = null,
    isBlackAndWhite: Boolean = false,
    fitWidthNoCrop: Boolean = false,
    blackWhiteAlpha: Float? = 0f,
    fitWidthMaxWidth: Float = Float.MIN_VALUE,
    topAlignImage: Boolean = false
) {
    if (url.isNullOrEmpty()) {
        if (error != null)
            imageView.setImageDrawable(error)
        return
    }

    Timber.v("[GLIDE] Load URL: $url")

    // Is BlackAndWhite
    if (isBlackAndWhite) {
        val c = 1.2f
        val b = -80f
        val cm = ColorMatrix(
            floatArrayOf(
                c, 0f, 0f, 0f,
                b, 0f, c, 0f,
                0f, b, 0f, 0f,
                c, 0f, b, 0f,
                0f, 0f, 1f, 0f
            )
        )

        val matrix = ColorMatrix()
        matrix.setSaturation(0f)
        matrix.postConcat(cm)
        imageView.colorFilter = ColorMatrixColorFilter(matrix)
        imageView.alpha = blackWhiteAlpha ?: 1f
    } else {
        imageView.clearColorFilter()
    }

    // Gif Check
    if (asGif)
        imageView.scaleType = ImageView.ScaleType.CENTER

    if (!imageView.context.extGetActivityContext().extIsAvailable())
        return

    // Init
    val image = Glide.with(imageView.context.extGetActivityContext() ?: imageView.context).load(url)
    image.apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))

    // Apply image sized to fit the width of the screen - solution for views with wrap_content in layout_height
    // Tt based on https://stackoverflow.com/a/53407904/2295238
    if (fitWidthNoCrop) {
        applyFitWidthImageSizing(imageView, url, image, fitWidthMaxWidth)
    }

    // Transition
    if (withTransition)
        image.transition(DrawableTransitionOptions().crossFade())
    else
        image.transition(DrawableTransitionOptions().dontTransition())

    if (topAlignImage) {
        image.transform(FitTopAlign())
    } else if (centerCrop) {
        image.apply(RequestOptions.centerCropTransform())
    }

    if (roundedCorners != null)
        image.apply(RequestOptions().transform(CenterCrop(), RoundedCorners(roundedCorners.toInt())))

    // Placeholder
    if (placeholder != null)
        image.apply(RequestOptions.placeholderOf(placeholder))

    // Error or Null Placeholder
    if (error != null)
        image.apply(RequestOptions.errorOf(error))

    // Circular Image
    if (circular)
        image.apply(RequestOptions.circleCropTransform())

    // Fit Center Image
    if (scaleType != null && scaleType == ImageView.ScaleType.FIT_CENTER)
        image.apply(RequestOptions.fitCenterTransform())

    // Apply blur
    if (blurred && centerCrop) {
        image.apply(RequestOptions.bitmapTransform(MultiTransformation(BlurTransformation(50, 3), CenterCrop())))
    } else if (blurred) {
        image.apply(RequestOptions.bitmapTransform(BlurTransformation(50, 3)))
    }

    image.listener(object : RequestListener<Drawable> {
        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            // Gif Check
            if (asGif)
                imageView.scaleType = ImageView.ScaleType.CENTER_CROP

            // zoomable Check
            (imageView as? PhotoView)?.let {
                it.scaleType = ImageView.ScaleType.FIT_CENTER
            }

            Timber.v("[GLIDE] Successfully loaded: $url with resolution: ${resource?.intrinsicWidth}x${resource?.intrinsicHeight}")
            return false
        }

        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            Timber.e("[GLIDE] Error loading: $url: ${e?.causes}")

            if (withErrorBackground) {
                val shape = GradientDrawable().apply { setColor(ColorUtility.getBestColorFromString("#e6e6e6")) }
                if (roundedCorners != null)
                    shape.cornerRadius = roundedCorners
                imageView.background = shape
                imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
            }
            return false
        }
    })

    if (!fitWidthNoCrop) {
        image.into(imageView)
    }

    // Animated Placeholder
    if (placeholder != null && placeholder is AnimationDrawable)
        placeholder.start()
}

@Suppress("LongParameterList")
@androidx.databinding.BindingAdapter(
    value = [
        "imageUrlList", "preferredUrlSize", "imageCircular", "imageBlurred", "centerCrop",
        "roundedCorners", "withTransition", "asGif", "imagePlaceholder", "imageError",
        "withErrorBackground", "scaleType", "isBlackAndWhite", "fitWidthNoCrop",
        "blackWhiteAlpha", "fitWidthMaxWidth", "topAlignImage"
    ],
    requireAll = false
)
fun loadImage(
    imageView: ImageView,
    urlList: SizedImages?,
    preferredUrlSize: Float? = null,
    circular: Boolean = false,
    blurred: Boolean = false,
    centerCrop: Boolean = true,
    roundedCorners: Float? = null,
    withTransition: Boolean = true,
    asGif: Boolean = false,
    placeholder: Drawable? = null,
    error: Drawable? = null,
    withErrorBackground: Boolean = false,
    scaleType: ImageView.ScaleType? = null,
    isBlackAndWhite: Boolean = false,
    fitWidthNoCrop: Boolean = false,
    blackWhiteAlpha: Float? = 0f,
    fitWidthMaxWidth: Float = Float.MIN_VALUE,
    topAlignImage: Boolean = false
) {

    var imageUrl: String? = null
    if (!urlList.isNullOrEmpty()) {
        imageUrl = if (preferredUrlSize == null) {
            urlList.firstOrNull()?.uri
        } else {
            selectImageUrlBasedOnPreferredSize(
                urlList,
                preferredUrlSize
            )
        }
    }

    loadImage(
        imageView = imageView,
        url = imageUrl,
        circular = circular,
        blurred = blurred,
        centerCrop = centerCrop,
        roundedCorners = roundedCorners,
        withTransition = withTransition,
        asGif = asGif,
        placeholder = placeholder,
        error = error,
        withErrorBackground = withErrorBackground,
        scaleType = scaleType,
        isBlackAndWhite = isBlackAndWhite,
        fitWidthNoCrop = fitWidthNoCrop,
        blackWhiteAlpha = blackWhiteAlpha,
        fitWidthMaxWidth = fitWidthMaxWidth,
        topAlignImage = topAlignImage
    )
}

private fun applyFitWidthImageSizing(
    imageView: ImageView,
    url: String?,
    image: RequestBuilder<Drawable>,
    maxWidthPixels: Float
) {
    val systemWidthDp = Resources.getSystem().displayMetrics.widthPixels.toFloat()
    val imageWidthDp = if (maxWidthPixels > 0) min(systemWidthDp, maxWidthPixels) else systemWidthDp
    Glide.with(AthleticApplication.getContext())
        .`as`(GlideImageSize::class.java)
        .apply(
            RequestOptions()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
        )
        .load(url)
        .into(object : CustomTarget<GlideImageSize>() {
            override fun onLoadCleared(placeholder: Drawable?) {
                // Tt this needs to be implemented - but we can really skip this, because we do not use image resource from this load
                // Tt http://bumptech.github.io/glide/javadocs/490/com/bumptech/glide/request/target/CustomTarget.html
            }

            override fun onResourceReady(
                resource: GlideImageSize,
                transition: Transition<in GlideImageSize>?
            ) {
                val imageAspectRatio = resource.width.toDouble() / resource.height.toDouble()
                Timber.i("Article title original: resource.width: ${resource.width}, resource.height: ${resource.height}")
                image.override(imageWidthDp.toInt(), (imageWidthDp / imageAspectRatio).toInt())
                    .into(imageView)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {}
        })
}

private fun selectImageUrlBasedOnPreferredSize(urlList: SizedImages?, preferredSize: Float): String? {
    var imageUrl: String? = null
    urlList?.forEach {
        if (it.width >= preferredSize) {
            imageUrl = it.uri
            return@forEach
        }
    }
    if (imageUrl == null) imageUrl = urlList?.lastOrNull()?.uri
    return imageUrl
}