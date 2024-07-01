package com.theathletic.ui.gallery

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.theathletic.R
import com.theathletic.activity.BaseActivity

class ImageGalleryActivity : BaseActivity() {

    companion object {
        private const val EXTRA_IMAGES_LIST = "extra_images_list"
        private const val EXTRA_IMAGE_INDEX = "extra_image_index"

        fun newIntent(
            context: Context,
            images: List<String>,
            index: Int
        ): Intent {
            return Intent(context, ImageGalleryActivity::class.java).apply {
                val arrayListImage = ArrayList<String>()
                images.map { arrayListImage.add(it) }

                putExtra(EXTRA_IMAGES_LIST, arrayListImage)
                putExtra(EXTRA_IMAGE_INDEX, index)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images_gallery)

        intent.extras?.let { extras ->
            val imageList = extras.getStringArrayList(EXTRA_IMAGES_LIST)
            val index = extras.getInt(EXTRA_IMAGE_INDEX, 0)

            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ImageGalleryFragment.newInstance(imageList, index))
                .commit()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}