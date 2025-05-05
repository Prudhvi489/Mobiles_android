package com.mobivault.utils

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Environment
import android.os.Parcelable
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.mobivault.R
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class PicSelectActivity : AppCompatActivity() {
    private val TAG = "PicSelectActivity"
    val REQUEST_GALLERY_PHOTO = 1
    var camera_album = "camera_album"
    var storagePicFile: File? = null
    var mCurrentPhotoPath = ""
    var appPackageName: String? = null
    var type: String? = ""
    var type_from: String? = ""
    var multiImages: Boolean = false
    var multiImagesList: ArrayList<String> = ArrayList()
    var multiuriImagesList: ArrayList<String> = ArrayList()
    private fun exifOrientationToDegrees(exifOrientation: Int): Float {
        try {
            if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
                return 90F
            } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
                return 180F
            } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
                return 270F
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0F
    }

    fun intentToString(intent: Intent?): String? {
        if (intent == null) {
            return null
        }
        val result = intent.toString() + " " + bundleToString(intent.extras)
        return result
    }


    fun bundleToString(bundle: Bundle?): String {
        val out = StringBuilder("Bundle[")
        if (bundle == null) {
            out.append("null")
        } else {
            var first = true
            for (key in bundle.keySet()) {
                if (!first) {
                    out.append(", ")
                }
                out.append(key).append('=')
                val value = bundle[key]
                if (value is IntArray) {
                    out.append(Arrays.toString(value as IntArray?))
                } else if (value is ByteArray) {
                    out.append(Arrays.toString(value as ByteArray?))
                } else if (value is BooleanArray) {
                    out.append(Arrays.toString(value as BooleanArray?))
                } else if (value is ShortArray) {
                    out.append(Arrays.toString(value as ShortArray?))
                } else if (value is LongArray) {
                    out.append(Arrays.toString(value as LongArray?))
                } else if (value is FloatArray) {
                    out.append(Arrays.toString(value as FloatArray?))
                } else if (value is DoubleArray) {
                    out.append(Arrays.toString(value as DoubleArray?))
                } else if (value is Array<*>) {
                    out.append(Arrays.toString(value as Array<String?>?))
                } else if (value is Array<*>) {
                    out.append(Arrays.toString(value as Array<CharSequence?>?))
                } else if (value is Array<*>) {
                    out.append(Arrays.toString(value as Array<Parcelable?>?))
                } else if (value is Bundle) {
                    out.append(bundleToString(value as Bundle?))
                } else {
                    out.append(value)
                }
                first = false
            }
        }
        out.append("]")
        return out.toString()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appPackageName = applicationContext.packageName
        getIntentData()
    }

    fun getIntentData() {
        try {
            val intent = intent
            type_from = intent.extras!!.getString(AppStrings.IntentData.type_from)
            type = intent.extras!!.getString(AppStrings.IntentData.type)
            multiImages = intent.extras!!.getBoolean(AppStrings.IntentData.isMultipleImages)
            when (type_from) {
                AppStrings.Types.imageCamera -> {
                    dispatchTakePictureIntent()
                }
                AppStrings.Types.imageGallery -> {
                    getImageFromGallery()
                }
                AppStrings.Types.document -> {
                    getDocument()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun picAudio() {
        val intent_upload = Intent()
        intent_upload.type = "audio/*"
        intent_upload.action = Intent.ACTION_GET_CONTENT
        someActivityResultLauncher.launch(intent_upload)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    // image Rotation
    fun uploadVideoFromGallery() {
        val i = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        someActivityResultLauncher.launch(i)
    }

    fun captureVideo() {
        val captureintent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        captureintent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60)
        // set the video image quality to high
        captureintent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
        someActivityResultLauncher.launch(captureintent)
    }

    @Throws(IOException::class)
    private fun createImageFile1(): File? {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        storagePicFile = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = storagePicFile?.absolutePath.toString()
        return storagePicFile
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
        // Create the File where the photo should go
        var photoFile: File? = null
        try {
            photoFile = createImageFile1()
        } catch (ex: IOException) {
            // Error occurred while creating the File
            ex.printStackTrace()
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            val photoURI = FileProvider.getUriForFile(
                this@PicSelectActivity,
                "$appPackageName.provider",
                photoFile
            )
            CLog.e(TAG, "dispatchTakePictureIntent: $photoURI", )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            someActivityResultLauncher.launch(takePictureIntent)
            val resolvedIntentActivities = packageManager.queryIntentActivities(
                takePictureIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
            for (resolvedIntentInfo in resolvedIntentActivities) {
                val packageName = resolvedIntentInfo.activityInfo.packageName
                grantUriPermission(
                    packageName,
                    photoURI,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

                )
            }
        }
//        }
    }


    fun getImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        val mimeTypes = arrayOf("image/jpeg", "image/jpg", "image/png")
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
//        intent.putExtra(AppStrings.IntentData.type_from, REQUEST_GALLERY_PHOTO)
//        intent.action = Intent.ACTION_GET_CONTENT
        someActivityResultLauncher.launch(intent)

    }

    fun getDocument() {
        val doc_intent = Intent()
        val mimeTypes = "application/pdf"

        doc_intent.type =
            "application/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        doc_intent.action = Intent.ACTION_GET_CONTENT
        someActivityResultLauncher.launch(doc_intent)

    }

    var someActivityResultLauncher = registerForActivityResult(
        StartActivityForResult()
    ) { result: ActivityResult ->
        val data = result.data
        val resultCode = result.resultCode
        CLog.e(TAG, "resultCode: $resultCode", )

        if (resultCode == RESULT_CANCELED) {
            finish()
        } else if (type_from == AppStrings.Types.imageCamera && resultCode == RESULT_OK) {
            try {
                intentToString(data)
                val b = getImage1(storagePicFile!!.absolutePath)
                val timeStamp =
                    SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val savedImagePath = saveBitmapIntoSdcard(b, "$timeStamp.jpg")
                CLog.e(TAG, "savedImagePath:$savedImagePath ", )
                updatePhotoItem(savedImagePath)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }  else if (type_from == AppStrings.Types.imageGallery) {
            CLog.e(TAG, "type_from:   $type_from", )

            try {
                if (!multiImages) {
                    val selectedImageUri = data!!.data
                    updatePhotoItem1(getPath(this, selectedImageUri!!).toString(), selectedImageUri)


//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                        this.getContentResolver().takePersistableUriPermission(
//                            selectedImageUri,
//                            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
//                        )
//                    }
                } else if (data!!.clipData != null) {
                        val count =
                            data.clipData!!.itemCount //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                        for (i in 0 until count) {
                            val imageUri = data.clipData!!.getItemAt(i).uri
                            multiImagesList.add(getPath(this, imageUri).toString())
                            multiuriImagesList.add(imageUri.toString())
                        }
                        sendMultiData()
                } else {
                    if (!multiImages) {
                        val selectedImageUri = data.data
                        updatePhotoItem(getPath(this, selectedImageUri!!).toString())
                    } else {
                        val selectedImageUri = data.data
                        multiImagesList.add(getPath(this, selectedImageUri!!).toString())
                        multiuriImagesList.add(selectedImageUri.toString())

                        sendMultiData()
                    }
                }
            } catch (e: Exception) {
                return@registerForActivityResult
            }
        }  else if (type_from == AppStrings.Types.document) {
            if (resultCode == RESULT_OK) {

                //the selected audio.
                val uri = data!!.data
                CLog.e(TAG, "uri $uri: ")
                updateAudioItem(getPath(this, uri!!).toString())
            }

        }
    }

    fun updateAudioItem(path: String) {
        val intent = Intent()
//        intent.putExtra(AppStrings.IntentData.audioFile, path)
//        intent.putExtra(AppStrings.IntentData.from, type_from)
        setResult(RESULT_OK, intent)
        finish()
        overridePendingTransition(0, 0)
    }


    fun getRealPathFromURI(activity: Activity, contentUri: Uri): String? {
        var column_index = 0
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Audio.Media.DATA)
            cursor = activity.contentResolver.query(contentUri, proj, null, null, null)
            column_index = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)!!
            cursor.moveToFirst()
            return cursor.getString(column_index)
        } catch (e: java.lang.IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return cursor!!.getString(column_index)
    }


    @Throws(IOException::class)
    fun saveBitmapIntoSdcard1(activity: Activity, bitmap: Bitmap, filename: String): String? {
        /*
         * check the path and create if needed
         */
        val baseDirectory: File = createGalleryAlbumDirectory()
        if (!baseDirectory.exists()) {
            baseDirectory.mkdirs()
        }
        if (baseDirectory.mkdir()) {
            println("Directory created")
        } else {
            println("Directory is not created or exists")
        }
        try {
            Date()
            var out: OutputStream? = null
            val file = File(baseDirectory, "/$filename")
            out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out)
            out.flush()
            out.close()
            // Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
            return file.absolutePath
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getThumbnailPathForLocalFile(context: Activity, fileUri: String): String? {
        val cancellationSignal = CancellationSignal()
        cancellationSignal.throwIfCanceled()
        val file = File(fileUri)
        try {
            val bitmap: Bitmap?
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                val retriever = MediaMetadataRetriever()
//                retriever.setDataSource(fileUri)
//                val width =
//                    Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))
//                val height =
//                    Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))
//                retriever.release()
//                CLog.e(TAG, "getThumbnailPathForLocalFile: @@## "+height)
//                bitmap = ThumbnailUtils.createVideoThumbnail(
//                    file,
//                    Size(width, height),
//                    cancellationSignal
//                )
//            } else {
            bitmap = ThumbnailUtils.createVideoThumbnail(
                fileUri,
                MediaStore.Images.Thumbnails.MINI_KIND
            )
//            }
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            var tumbnail_path = ""
            try {
                tumbnail_path = saveBitmapIntoSdcard(bitmap, timeStamp).toString()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return tumbnail_path
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun sendMultiData() {
        CLog.e(TAG, "sendMultiData: multiImagesList" + multiImagesList.toString())
        val intent = Intent()
        intent.putExtra(AppStrings.IntentData.type, type)
//        intent.putExtra(AppStrings.IntentData.multiImages, multiImages)
//        intent.putExtra(AppStrings.IntentData.multiuriImagesList, multiuriImagesList)
//        intent.putExtra(AppStrings.IntentData.from, type_from)
//        intent.putExtra(AppStrings.IntentData.imageFileList, multiImagesList)

        setResult(RESULT_OK, intent)
        deleteTempFile()
        finish()
        overridePendingTransition(0, 0)
    }

    fun getPath(context: Context, uri: Uri): String? {

        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            CLog.e(TAG, "getPath: 11 ")
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                CLog.e(TAG, "getPath: 22 ")
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

                // TODO handle non-primary volumes
            } else if (isGoogleDriveUri(uri)) {
                CLog.e(TAG, "getPath: 33 ")
                return getDriveFilePath(uri, this).toString()
            } else if (isDownloadsDocument(uri)) {
                CLog.e(TAG, "getPath: 44 ")

                if (DocumentsContract.getDocumentId(uri) != null && DocumentsContract.getDocumentId(
                        uri
                    ).startsWith("msf:")
                ) {
                    val file: File = File(
                        context.cacheDir,
                        "temp" + System.currentTimeMillis() + Objects.requireNonNull(
                            context.contentResolver.getType(
                                uri
                            )
                        )?.split("/")?.get(1)
                    )
                    try {
                        context.contentResolver.openInputStream(uri).use { inputStream ->
                            FileOutputStream(file).use { output ->
                                val buffer = ByteArray(4 * 1024) // or other buffer size
                                var read: Int
                                while (inputStream?.read(buffer).also { read = it!! } != -1) {
                                    output.write(buffer, 0, read)
                                }
                                output.flush()
                                return file.toString()
                            }
                        }
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                    }
                    return null
                } else {
                    CLog.e(TAG, "getPath: 55 ")

                    try {
                        val id = DocumentsContract.getDocumentId(uri)

                        if (id != null && id.startsWith("raw:")) {
                            return id.substring(4)
                        }

                        val contentUriPrefixesToTry = arrayOf(
                            "content://downloads/public_downloads",
                            "content://downloads/my_downloads",
                            "content://downloads/all_downloads"
                        )

                        for (contentUriPrefix in contentUriPrefixesToTry) {
                            val contentUri = ContentUris.withAppendedId(
                                Uri.parse(contentUriPrefix),
                                java.lang.Long.valueOf(id)
                            )
                            try {
                                val path = getDataColumn(context, contentUri, null, null)
                                if (path != null) {
                                    return path
                                }
                            } catch (e: java.lang.Exception) {
                            }
                        }

                        // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams

                        // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
                        val fileName: String = getFileName(context, uri).toString()
                        val cacheDir: File = getDocumentCacheDir(context)!!
                        val file: File = generateFileName(fileName, cacheDir)!!
                        var destinationPath: String? = null
                        if (file != null) {
                            destinationPath = file.absolutePath
                            saveFileFromUri(context, uri, destinationPath)
                        }

                        return destinationPath

                    } catch (e: java.lang.Exception) {
                    }

                }


            } else if (isMediaDocument(uri)) {
                CLog.e(TAG, "getPath: 66 ")

                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )

                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            CLog.e(TAG, "getPath: 66 ")
            return uri.path
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            CLog.e(TAG, "getPath: 77 ")
            return getRealPathFromURI(context, uri)
        }


        return null
    }

    private fun saveFileFromUri(context: Context, uri: Uri, destinationPath: String) {
        var `is`: InputStream? = null
        var bos: BufferedOutputStream? = null
        try {
            `is` = context.contentResolver.openInputStream(uri)
            bos = BufferedOutputStream(FileOutputStream(destinationPath, false))
            val buf = ByteArray(1024)
            `is`!!.read(buf)
            do {
                bos.write(buf)
            } while (`is`.read(buf) !== -1)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`?.close()
                bos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun getDocumentCacheDir(context: Context): File? {
        val dir = File(context.cacheDir, "documents")
        if (!dir.exists()) {
            dir.mkdirs()
        }
//        logDir(context.cacheDir)
//        logDir(dir)
        return dir
    }

   /* private fun logDir(dir: File) {
        if (!DEBUG) return
        val files = dir.listFiles()
        for (file in files) {
        }
    }*/

    @Nullable
    fun generateFileName(@Nullable name: String?, directory: File?): File? {
        var name = name ?: return null
        var file = File(directory, name)
        if (file.exists()) {
            var fileName = name
            var extension = ""
            val dotIndex = name.lastIndexOf('.')
            if (dotIndex > 0) {
                fileName = name.substring(0, dotIndex)
                extension = name.substring(dotIndex)
            }
            var index = 0
            while (file.exists()) {
                index++
                name = "$fileName($index)$extension"
                file = File(directory, name)
            }
        }
        try {
            if (!file.createNewFile()) {
                return null
            }
        } catch (e: IOException) {
            return null
        }
//        logDir(directory!!)
        return file
    }

    fun getFileName(context: Context, uri: Uri): String? {
        val mimeType = context.contentResolver.getType(uri)
        var filename: String? = null
        if (mimeType == null && context != null) {
            val path = getPath(context, uri)
            if (path == null) {
                filename = getName(uri.toString())
            } else {
                val file = File(path)
                filename = file.name
            }
        } else {
            val returnCursor = context.contentResolver.query(
                uri, null,
                null, null, null
            )
            if (returnCursor != null) {
                val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                returnCursor.moveToFirst()
                filename = returnCursor.getString(nameIndex)
                returnCursor.close()
            }
        }
        return filename
    }

    fun getName(filename: String?): String? {
        if (filename == null) {
            return null
        }
        val index = filename.lastIndexOf('/')
        return filename.substring(index + 1)
    }

    private fun getDriveFilePath(uri: Uri, context: Context): File? {
        val returnUri: Uri = uri
        val returnCursor: Cursor =
            context.contentResolver.query(returnUri, null, null, null, null) ?: return null
        /*
     * Get the column indexes of the data in the Cursor,
     *     * move to the first row in the Cursor, get the data,
     *     * and display it.
     * */
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex: Int = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        val size = returnCursor.getLong(sizeIndex).toString()
        val file = File(context.cacheDir, name)
        try {
            val inputStream: InputStream = context.contentResolver.openInputStream(uri)!!
            val outputStream = FileOutputStream(file)
            var read = 0
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable: Int = inputStream.available()

            //int bufferSize = 1024;
            val bufferSize = Math.min(bytesAvailable, maxBufferSize)
            val buffers = ByteArray(bufferSize)
            while (inputStream.read(buffers).also { read = it } != -1) {
                outputStream.write(buffers, 0, read)
            }
            inputStream.close()
            outputStream.close()
        } catch (e: Exception) {
        } finally {
            returnCursor.close()
        }
        return file
    }

    fun getRealPathFromURI(context: Context, contentUri: Uri?): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } finally {
            cursor?.close()
        }
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } catch (e: Exception) {
        } finally {
            cursor?.close()
        }
        return null
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    fun isGoogleDriveUri(uri: Uri): Boolean {
        return "com.google.android.apps.docs.storage" == uri.authority || "com.google.android.apps.docs.storage.legacy" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    @Throws(IOException::class)
    fun getImage1(path: String?): Bitmap? {
        var pqr: Bitmap? = null
        try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, options)
            var srcWidth = options.outWidth
            var srcHeight = options.outHeight
            val REQUIRED_SIZE = 580
            var inSampleSize = 1
            while (srcWidth / 2 >= REQUIRED_SIZE) {
                srcWidth /= 2
                srcHeight /= 2
                inSampleSize *= 2
            }
            options.inJustDecodeBounds = false
            options.inDither = false
            options.inSampleSize = inSampleSize
            options.inScaled = false
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            val sampledSrcBitmap = BitmapFactory.decodeFile(path, options)
            val exif = ExifInterface(path!!)
            val s = exif.getAttribute(ExifInterface.TAG_ORIENTATION)
            val matrix = Matrix()
            val rotation = rotationForImage(this@PicSelectActivity, Uri.fromFile(File(path)))
            if (rotation != 0f) {
                matrix.preRotate(rotation)
            }
            pqr = Bitmap.createBitmap(
                sampledSrcBitmap,
                0,
                0,
                sampledSrcBitmap.width,
                sampledSrcBitmap.height,
                matrix,
                true
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return pqr
    }


    fun rotationForImage(context: Context, uri: Uri): Float {
        try {
            if (uri.scheme == "content") {
                val projection = arrayOf(MediaStore.Images.ImageColumns.ORIENTATION)
                val c = context.contentResolver.query(
                    uri, projection,
                    null, null, null
                )
                if (c!!.moveToFirst()) {
                    return c.getInt(0).toFloat()
                }
            } else if (uri.scheme == "file") {
                try {
                    val exif = ExifInterface(uri.path!!)
                    val rotation = exifOrientationToDegrees(
                        exif
                            .getAttributeInt(
                                ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL
                            )
                    ).toInt()
                    return rotation.toFloat()
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0f
    }


    fun getRealPathFromURI(contentUri: Uri?): String {
        var column_index = 0
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Audio.Media.DATA)
            cursor = contentResolver.query(contentUri!!, proj, null, null, null)
            // Cursor cursor = getContentResolver().query(contentUri, proj,
            // null, null, null); //Since manageQuery is deprecated
            column_index = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)!!
            cursor.moveToFirst()
            return cursor.getString(column_index)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return cursor!!.getString(column_index)
    }

    fun createBaseDirectory(): File {
        val folder = File(getExternalFilesDir(Environment.DIRECTORY_DCIM).toString())
        folder.mkdirs()
        val extStorageDirectory = folder.absolutePath
        val storageDir = File(extStorageDirectory + "/" + getString(R.string.app_name))
        if (storageDir.mkdir()) {
            println("Gallery Directory created")
        } else {
            println("Gallery Directory is not created or exists")
        }
        return storageDir
    }

    fun createGalleryAlbumDirectory(): File {
        val folder = File(getExternalFilesDir(Environment.DIRECTORY_DCIM).toString())
        folder.mkdirs()
        val extStorageDirectory = folder.absolutePath
        val storageDir = File(extStorageDirectory + "/" + getString(R.string.app_name))
        if (storageDir.mkdir()) {
            println("Gallery Directory created")
        } else {
            println("Gallery Directory is not created or exists")
        }
        return storageDir
    }

    fun createCameraAlbumDirectory(): File {
        val extStorageDirectory = createBaseDirectory().absolutePath
        val storageDir = File("$extStorageDirectory/$camera_album")
        if (storageDir.mkdir()) {
            println("Camera Directory created")
        } else {
            println("Camera Directory is not created or exists")
        }
        return storageDir
    }

    @Throws(IOException::class)
    private fun saveBitmapIntoSdcard(bitmap: Bitmap?, filename: String): String? {
        /*
         * check the path and create if needed
         */
        val baseDirectory = createGalleryAlbumDirectory()
        try {
            Date()
            var out: OutputStream? = null
            val file = File(baseDirectory, "/" + getString(R.string.app_name) + filename)
            out = FileOutputStream(file)
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 70, out)
            out.flush()
            out.close()
            // Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun updatePhotoItem(path: String?) {
        val intent = Intent()
        CLog.e("","updatePhotoItem() called with: path = $path")
        intent.putExtra(AppStrings.IntentData.imageFile, path)
        intent.putExtra(AppStrings.IntentData.type, type)
        intent.putExtra(AppStrings.IntentData.multiImages, multiImages)
        intent.putExtra(AppStrings.Types.type_from, type_from)
        intent.putExtra(
            AppStrings.IntentData.selectedImageUri,
            Uri.fromFile(File(path.toString())).toString()
        )

        setResult(RESULT_OK, intent)
        deleteTempFile()
        finish()
        overridePendingTransition(0, 0)
    }

    fun updatePhotoItem1(path: String?, selectedImageUri: Uri) {
        CLog.e(
            TAG,
            "updatePhotoItem1() called with: path = $path, selectedImageUri = $selectedImageUri"
        )
        val intent = Intent()
        intent.putExtra(AppStrings.IntentData.imageFile, path)
        intent.putExtra(AppStrings.IntentData.type, type)
        intent.putExtra(AppStrings.IntentData.multiImages, multiImages)
        intent.putExtra(AppStrings.IntentData.from, type_from)
        intent.putExtra(AppStrings.IntentData.selectedImageUri, selectedImageUri.toString())

        setResult(RESULT_OK, intent)
        deleteTempFile()
        finish()
        overridePendingTransition(0, 0)


    }

    private fun deleteTempFile() {
//        val files =this.cacheDir.listFiles()
//        if (files != null) {
//            for (file in files) {
//                if (file.name.contains("temp")) {
//                    file.delete()
//                }
//            }
//        }
    }

}