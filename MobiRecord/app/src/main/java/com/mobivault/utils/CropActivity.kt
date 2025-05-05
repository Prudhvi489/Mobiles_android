package com.mobivault.utils

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mobivault.databinding.ActivityCropBinding

import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class CropActivity : AppCompatActivity() {

    @Inject
    lateinit var sm: SessionManager


    lateinit var binding: ActivityCropBinding
    val IMAGE_PATH = "image-path"
    val ORIENTATION_IN_DEGREES = "orientation_in_degrees"

    var mImagePath: String = ""
    private var mSaveUri: Uri? = null
    private var isRectangleCrop: Boolean = false
    var isMultipleImages: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(true)
        }
        binding = ActivityCropBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        supportActionBar?.hide()

    }


    private fun initUI() {
        mImagePath = intent.getStringExtra(AppStrings.IntentData.imageFile).toString()
        isMultipleImages = intent.getBooleanExtra(AppStrings.IntentData.isMultipleImages, false)
        isRectangleCrop = intent.getBooleanExtra(AppStrings.IntentData.isRectangleCrop, false)
        // start picker to get image for cropping and then use the image in cropping activity
        if (isRectangleCrop) {
            binding.cropImageView.setAspectRatio(2, 2)
        } else {
            binding.cropImageView.setAspectRatio(1, 1)

        }
        binding.cropImageView.setFixedAspectRatio(true)



        mSaveUri = getImageUri(mImagePath)
        binding.cropImageView.setImageUriAsync(Uri.fromFile(File(mImagePath)))



        binding.imageCropBackLl.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
        binding.imageCropDoneLl.setOnClickListener {
            binding.cropImageView.getCroppedImageAsync()
        }
        binding.cropImageView.setOnCropImageCompleteListener { view, result ->
            result?.bitmap?.let {
                saveImage(it)

            }
        }
        // Rotate button action
        binding.rotateImageButton.setOnClickListener {
            binding.cropImageView.rotateImage(90) // Rotate by 90 degrees each time
        }

        /*  binding.cropImageView.setOnCropImageCompleteListener { view, result ->
              result?.bitmap?.let {
                  if (!isImageSaved) { // Check if the image hasn't been saved yet
                      saveImage(it)
                   }
              }
          }*/
    }

    private fun saveImage(b: Bitmap) {
        if (mSaveUri != null) {
            val timeStamp: String = SimpleDateFormat("yyyy_MM_dd__HH_mm_ss").format(Date())
            var imagePath = ""
            try {
                imagePath = AppMethods.saveBitmapIntoSdcardFromCropPicture(
                    this@CropActivity, b,
                    "$timeStamp.jpeg"
                ).toString()
            } catch (e: Exception) {
                setResult(RESULT_CANCELED)
                finish()
                e.printStackTrace()
            }
            val extras = Bundle()
            val intent = Intent(mSaveUri.toString())
            intent.putExtras(extras)
            intent.putExtra(AppStrings.IntentData.imageFile, imagePath)
            intent.putExtra(AppStrings.IntentData.isMultipleImages, isMultipleImages)
//            intent.putExtra(ORIENTATION_IN_DEGREES, Util.getOrientationInDegree(this))
            setResult(RESULT_OK, intent)
        } else {
        }
        b.recycle()
        finish()
    }

    private fun getImageUri(path: String): Uri {
        return Uri.fromFile(File(path))
    }

}