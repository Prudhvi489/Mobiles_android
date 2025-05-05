package com.mobivault.utils.cameraGallery

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.YuvImage
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mobivault.databinding.ActivityCameraBinding
import com.mobivault.utils.AppStrings
import com.mobivault.R

import com.otaliastudios.cameraview.CameraException
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraLogger
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.controls.Audio
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.controls.Flash
import com.otaliastudios.cameraview.controls.Mode
import com.otaliastudios.cameraview.frame.Frame
import com.otaliastudios.cameraview.frame.FrameProcessor
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date

class CameraActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private val LOG = CameraLogger.create("DemoApp")
        private const val USE_FRAME_PROCESSOR = false
        private const val DECODE_BITMAP = false
    }

    private val controlPanel: ViewGroup by lazy { findViewById(R.id.controls) }
    private var captureTime: Long = 0
    lateinit var binding: ActivityCameraBinding
    private var currentFilter = 0
    private val allFilters = com.otaliastudios.cameraview.filter.Filters.values()
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    var filePath: String = ""
    private var flashMode: Flash = Flash.OFF
    private var cameraFacing: Facing? = Facing.BACK
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_camera)
//         binding = DataBindingUtil.setContentView(this, R.layout.activity_camera)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(true)
        }
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        CameraLogger.setLogLevel(CameraLogger.LEVEL_VERBOSE)
        binding.camera.mode=Mode.PICTURE
        binding.camera.audio=Audio.OFF
        binding.camera.setLifecycleOwner(this)
        binding.camera.addCameraListener(Listener())
        if (USE_FRAME_PROCESSOR) {
            binding.camera.addFrameProcessor(object : FrameProcessor {
                private var lastTime = System.currentTimeMillis()
                override fun process(frame: Frame) {
                    val newTime = frame.time
                    val delay = newTime - lastTime
                    lastTime = newTime
                    LOG.v("Frame delayMillis:", delay, "FPS:", 1000 / delay)
                    if (DECODE_BITMAP) {
                        if (frame.format == ImageFormat.NV21
                            && frame.dataClass == ByteArray::class.java
                        ) {
                            val data = frame.getData<ByteArray>()
                            val yuvImage = YuvImage(
                                data,
                                frame.format,
                                frame.size.width,
                                frame.size.height,
                                null
                            )
                            val jpegStream = ByteArrayOutputStream()
                            yuvImage.compressToJpeg(
                                Rect(
                                    0, 0,
                                    frame.size.width,
                                    frame.size.height
                                ), 100, jpegStream
                            )
                            val jpegByteArray = jpegStream.toByteArray()
                            val bitmap = BitmapFactory.decodeByteArray(
                                jpegByteArray,
                                0, jpegByteArray.size
                            )
                            bitmap.toString()
                        }
                    }
                }
            })
        }
        // findViewById<View>(R.id.capturePicture).setOnClickListener(this)


        onclicks()
    }

    private fun onclicks() {
        setFlashMode(Flash.OFF)
        binding.cancelPreview.setOnClickListener {
            binding.finalImage.visibility = View.GONE
            binding.cancelPreview.visibility = View.GONE
            binding.verifyPreview.visibility = View.GONE
            binding.capturePicture.visibility = View.VISIBLE
            binding.camera.visibility = View.VISIBLE
            binding.toggleCameraIv.visibility = View.VISIBLE
            binding.flashCl.visibility = View.VISIBLE

        }
        binding.verifyPreview.setOnClickListener {
            val intent = Intent()
            intent.putExtra(AppStrings.IntentData.imageFile, filePath)
            intent.putExtra(AppStrings.IntentData.selectedImageUri, filePath)
            intent.putExtra(AppStrings.IntentData.type_from, AppStrings.Types.imageCamera)
            setResult(RESULT_OK, intent)
            finish()

//            intent.putExtra(AppStrings.IntentData.type, type)
//            intent.putExtra(AppStrings.IntentData.multiImages, multiImages)
            intent.putExtra(AppStrings.IntentData.type_from, AppStrings.Types.imageCamera)
            overridePendingTransition(0, 0)

        }

        binding.flashControl.setOnClickListener {
            showFlashMenu()

        }
        binding.capturePicture.setOnClickListener(this)
        binding.toggleCameraIv.setOnClickListener(this)
    }

    private fun message(content: String, important: Boolean) {
        if (important) {
            LOG.w(content)
            //  Toast.makeText(this, content, Toast.LENGTH_LONG).show()
        } else {
            LOG.i(content)
            // Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
        }
    }

    private inner class Listener : CameraListener() {


        override fun onCameraError(exception: CameraException) {
            super.onCameraError(exception)
            message("Got CameraException #" + exception.reason, true)
        }

        override fun onPictureTaken(result: PictureResult) {
            super.onPictureTaken(result)
            if (binding.camera.isTakingVideo) {
                //message("Captured while taking video. Size=" + result.size, false)
                return
            }

            if (result != null) {
                Log.e(ContentValues.TAG, "onPictureTaken: " + result)
                result.toBitmap(1000, 1000) { bitmap ->

                    var finalBitmap = bitmap
                    binding.finalImage.visibility = View.VISIBLE

                    binding.cancelPreview.visibility = View.VISIBLE
                    binding.verifyPreview.visibility = View.VISIBLE
                    binding.capturePicture.visibility = View.GONE
                    if (cameraFacing == Facing.FRONT) {
                        finalBitmap = bitmap?.let { flipBitmap(it) }
                    }
                    binding.finalImage.setImageBitmap(finalBitmap)
                    binding.camera.visibility = View.GONE
                    binding.toggleCameraIv.visibility = View.GONE
                    binding.flashCl.visibility = View.GONE
                    Log.e(ContentValues.TAG, "onPictureTaken:bt " + bitmap)
                    filePath = saveBitmapIntoSdcard(finalBitmap, timeStamp).toString()
                    Log.e("TAG", "onPictureTaken: filePath")
                }

                val file = File(filesDir, "picture.${"jpg"}")

                print(file)

            }


            // This can happen if picture was taken with a gesture.
            /*  val callbackTime = System.currentTimeMillis()
              if (captureTime == 0L) captureTime = callbackTime - 300
              LOG.w("onPictureTaken called! Launching activity. Delay:", callbackTime - captureTime)
              PicturePreviewActivity.pictureResult = result
              val intent = Intent(this@CameraActivity, PicturePreviewActivity::class.java)
              intent.putExtra("delay", callbackTime - captureTime)
              startActivity(intent)
              captureTime = 0*/
            LOG.w("onPictureTaken called! Launched activity.")
        }

        fun flipBitmap(bitmap: Bitmap): Bitmap? {
            val matrix = Matrix()
            matrix.setScale(-1f, 1f) // Flip horizontally
            matrix.postTranslate(bitmap.width.toFloat(), 0f) // Adjust position

            val flippedBitmap = Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )

            // Ensure the original bitmap is recycled to avoid memory leaks
            if (flippedBitmap != bitmap) {
                bitmap.recycle()
            }

            return flippedBitmap
        }

        override fun onVideoTaken(result: VideoResult) {
            super.onVideoTaken(result)
            LOG.w("onVideoTaken called! Launching activity.")
            /* VideoPreviewActivity.videoResult = result
             val intent = Intent(this@CameraActivity, VideoPreviewActivity::class.java)
             startActivity(intent)
             LOG.w("onVideoTaken called! Launched activity.")*/
        }

        override fun onVideoRecordingStart() {
            super.onVideoRecordingStart()
            LOG.w("onVideoRecordingStart!")
        }

        override fun onVideoRecordingEnd() {
            super.onVideoRecordingEnd()
            message("Video taken. Processing...", false)
            LOG.w("onVideoRecordingEnd!")
        }

        override fun onExposureCorrectionChanged(
            newValue: Float,
            bounds: FloatArray,
            fingers: Array<PointF>?
        ) {
            super.onExposureCorrectionChanged(newValue, bounds, fingers)
            message("Exposure correction:$newValue", false)
        }

        override fun onZoomChanged(newValue: Float, bounds: FloatArray, fingers: Array<PointF>?) {
            super.onZoomChanged(newValue, bounds, fingers)
            message("Zoom:$newValue", false)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            //  R.id.capturePicture -> capturePicture()
            R.id.capturePicture -> capturePicture()
            R.id.toggleCameraIv -> toggleCamera()
        }
    }

    override fun onBackPressed() {
        val b = BottomSheetBehavior.from(controlPanel)
        if (b.state != BottomSheetBehavior.STATE_HIDDEN) {
            b.state = BottomSheetBehavior.STATE_HIDDEN
            return
        }
        super.onBackPressed()
    }


    private fun capturePicture() {
        if (binding.camera.mode == Mode.VIDEO) return run {
            message("Can't take HQ pictures while in VIDEO mode.", false)
        }
        if (binding.camera.isTakingPicture) return
        captureTime = System.currentTimeMillis()
        message("Capturing picture...", false)
        Toast.makeText(this, "Capturing picture...", Toast.LENGTH_SHORT).show()
        binding.camera.takePicture()
    }


    private fun toggleCamera() {
        if (binding.camera.isTakingPicture || binding.camera.isTakingVideo) return
        when (binding.camera.toggleFacing()) {
            Facing.BACK -> message("Switched to back binding.camera!", false)
            Facing.FRONT -> message("Switched to front binding.camera!", false)
        }
        checkCameraFacing()

    }

    private fun saveBitmapIntoSdcard(bitmap: Bitmap?, filename: String): String? {
        Log.e(ContentValues.TAG, "saveBitmapIntoSdcard:@@@@@ ")
        /*
         * check the path and create if needed
         */
        val baseDirectory = createGalleryAlbumDirectory()
        try {

            Date()
            var out: OutputStream? = null
            val file =
                File(
                    baseDirectory,
                    "/" + getString(R.string.app_name) + filename + "${"jpg"}"
                )
            out = FileOutputStream(file)
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            // Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun createGalleryAlbumDirectory(): File {
        // val folder = File(getExternalFilesDir(Environment.DIRECTORY_DCIM).toString())
        val folder = File(getApplicationContext().getExternalFilesDir("").toString())
        folder.mkdirs()
        val extStorageDirectory = folder.absolutePath
        val storageDir =
            File(extStorageDirectory + "/" + getString(R.string.app_name))
        if (storageDir.mkdir()) {
            Log.e(ContentValues.TAG, "Gallery Directory created\": ")
            println("Gallery Directory created")
        } else {
            Log.e(ContentValues.TAG, "Gallery Directory is not created: ")
            println("Gallery Directory is not created or exists")
        }
        return storageDir
    }

    private fun showFlashMenu() {
        checkCameraFacing()

        binding.selectFlashLl.visibility = View.VISIBLE
        binding.flashControl.visibility = View.GONE
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            binding.selectFlashLl.visibility = View.GONE
            binding.flashControl.visibility = View.VISIBLE
        }, 15000)



        binding.flashOffIv.setOnClickListener {
            setFlashMode(Flash.OFF)
            binding.selectFlashLl.visibility = View.GONE
            binding.flashControl.visibility = View.VISIBLE
        }
        binding.flashAutoIv.setOnClickListener {
            setFlashMode(Flash.AUTO)
            binding.selectFlashLl.visibility = View.GONE
            binding.flashControl.visibility = View.VISIBLE
        }

        binding.flashOnIv.setOnClickListener {
            setFlashMode(Flash.ON)
            binding.selectFlashLl.visibility = View.GONE
            binding.flashControl.visibility = View.VISIBLE
        }

    }

    private fun setFlashMode(mode: Flash) {
        flashMode = mode
        updateFlashIcon()
        // Check if flash is supported before setting the flash mode
        if (binding.camera.isOpened) {
            binding.camera.flash = flashMode
        }
    }

    private fun updateFlashIcon() {
        val iconResId = when (flashMode) {
            Flash.ON -> R.drawable.flashon
            Flash.OFF -> R.drawable.flashoff
            Flash.AUTO -> R.drawable.flashauto

            else -> {
                R.drawable.flashoff
            }
        }
        binding.flashControl.setImageResource(iconResId)

        when (flashMode) {
            Flash.ON -> {
                binding.flashOnIv.setImageResource(R.drawable.flashauto)
                binding.flashAutoIv.setImageResource(R.drawable.flashon)
                binding.flashOffIv.setImageResource(R.drawable.flashoff)
            }


            Flash.OFF -> {
                binding.flashOnIv.setImageResource(R.drawable.flashon)
                binding.flashAutoIv.setImageResource(R.drawable.flashauto)
                binding.flashOffIv.setImageResource(R.drawable.flashoff_selected)
            }

            Flash.AUTO -> {
                binding.flashOnIv.setImageResource(R.drawable.flashon)
                binding.flashAutoIv.setImageResource(R.drawable.flashauto_selected)
                binding.flashOffIv.setImageResource(R.drawable.flashoff)
            }

            else -> {

            }
        }
    }

    fun checkCameraFacing() {
        Log.e("TAG", "checkCameraFacing:@@@${binding.camera.toggleFacing()}")
        cameraFacing = binding.camera.toggleFacing()
        setflash()
    }

    private fun setflash() {
        if (cameraFacing == Facing.FRONT) {
            if (!hasFrontFlash(this)) {
                binding.flashCl.visibility = View.GONE
                /*  if (flashMode!=Flash.OFF){
                      setBrightnesshasFlash()
                  }*/
            } else {
                binding.flashCl.visibility = View.VISIBLE

            }
        } else {
            binding.flashCl.visibility = View.VISIBLE

        }

    }

    fun hasFrontFlash(context: Context): Boolean {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

        try {
            for (cameraId in cameraManager.cameraIdList) {
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)

                if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    val available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
                    return available ?: false
                }
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val valid = grantResults.all { it == PERMISSION_GRANTED }
        if (valid && !binding.camera.isOpened) {
            binding.camera.open()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        if (event!!.action == KeyEvent.ACTION_DOWN ) {
            setResult(RESULT_CANCELED)
            finish()
            return true
        }
        return false
    }
}