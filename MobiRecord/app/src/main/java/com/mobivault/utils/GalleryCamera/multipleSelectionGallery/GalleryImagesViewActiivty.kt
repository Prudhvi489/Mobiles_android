package com.mobivault.utils.cameraGallery.multipleSelectionGallery

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobivault.R
import com.mobivault.core.BaseVMBindingActivity
import com.mobivault.databinding.ActivityGalleryImagesViewActiivtyBinding
import com.mobivault.databinding.FolderListDialogBinding
import com.mobivault.ui.viewmodel.AddDetailsViewmodel
import com.mobivault.utils.AppMethods
import com.mobivault.utils.AppStrings
import com.mobivault.utils.CLog
import com.mobivault.utils.cameraGallery.multipleSelectionGallery.adapter.FolderAdapter
import com.mobivault.utils.cameraGallery.multipleSelectionGallery.adapter.GalleryImagesVideosAdapter
import com.mobivault.utils.cameraGallery.multipleSelectionGallery.model.FolderModel
import com.mobivault.utils.cameraGallery.multipleSelectionGallery.model.ImageVideoModel


import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GalleryImagesViewActiivty :
    BaseVMBindingActivity<ActivityGalleryImagesViewActiivtyBinding, AddDetailsViewmodel>(
        AddDetailsViewmodel::class.java
    ) {
    private val galleryList = ArrayList<ImageVideoModel>()
    private val selectedList = ArrayList<ImageVideoModel>()
    private var filterImagesArrayList = ArrayList<ImageVideoModel>()
    private val folderList = ArrayList<FolderModel>()
    private var galleryAdapter: GalleryImagesVideosAdapter? = null
    private var maxCount = 0
    private var totalImagesCount = 500
    private var type = ""
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(true)
        }
        getIntentData()
        initUi()
        onClicks()
    }

    override fun getPersistentView(): ActivityGalleryImagesViewActiivtyBinding {
        return ActivityGalleryImagesViewActiivtyBinding.inflate(layoutInflater)
    }

    private fun getIntentData() {
        maxCount = intent.getIntExtra(AppStrings.IntentData.maxCount, 0)
        type = intent.getStringExtra(AppStrings.IntentData.type_from).toString()
        Log.e("GalleryImagesViewActiivty", "type -> $type , maxCount -> $maxCount")
        if (maxCount > 10 || maxCount == -1) {
            maxCount = 10
        }
    }

    private fun onClicks() {
        binding.cancelIcon.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.galleryNameEt.setOnClickListener { getFolderDialog() }

        binding.doneTV.setOnClickListener {
            var selectedImages: ArrayList<String>? = ArrayList()
            selectedList.forEach {
                if (it.select) {
                    selectedImages?.add(it.path.toString())
//                    selectedImages?.add(it.contentUri!!.toString()).toString()
                }
            }
            CLog.e("", "GalleryImagesViewActiivty -> selectedImages -> $selectedImages")

            if (selectedImages?.isNotEmpty() == true) {
                val intent = Intent()
                intent.putExtra(AppStrings.IntentData.type, type)
                intent.putExtra(AppStrings.Types.type_from, type)
                intent.putExtra(AppStrings.IntentData.imageFileList, selectedImages)
                setResult(RESULT_OK, intent)
                finish()
            } else {
                AppMethods.showToast(this, getString(R.string.please_select_atleast_one_image))
            }

        }
    }

    private fun initUi() {


//        checkingPermissions()
        launchGallery()
        galleryAdapter = GalleryImagesVideosAdapter(this, filterImagesArrayList, maxCount)
        galleryAdapter?.onClickListener { item ->
            if (item.select) {
                selectedList.add(item)
            } else {
                if (selectedList.contains(item)) {
                    selectedList.remove(item)
                }
            }

        }
        binding.galleryRv.adapter = galleryAdapter
        binding.galleryRv.layoutManager = GridLayoutManager(this, 3)

    }

    private fun checkingPermissions() {
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    launchGallery()
                } else {
                    Toast.makeText(this, "Please give storage permission", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                launchGallery()
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                launchGallery()
            }
        } else {
            launchGallery()
        }
    }


    private fun launchGallery() {
        loadFilesFromGallery()
    }
    /*private fun loadFilesFromGallery() {
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE
        )

        val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"
        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )

        val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC"
        val uriExternal = MediaStore.Files.getContentUri("external")

        val cursor = contentResolver.query(
            uriExternal,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            val mimeTypeColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val mimeType = it.getString(mimeTypeColumn)
                val path = it.getString(dataColumn)
                val contentUri = ContentUris.withAppendedId(uriExternal, id)

                if (mimeType?.startsWith("image/") == true) {
                    val model = ImageVideoModel(
                        path = path,
                        isImage = true,
                        select = false,
                        contentUri = contentUri
                    )

                    galleryList.add(model)
                    filterImagesArrayList.add(model)
                }
            }

            galleryAdapter?.notifyDataSetChanged()
            notifyAdapter()
        }
    }*/


 /*   private fun loadFilesFromGallery() {
        // Define the necessary columns to query
        val projection = arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE
        )

        // Filter by media type (image or video)
        val selection =
            "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"
        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )

        // Sort by date added in descending order
        val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC"

        // Query the MediaStore
        val cursor = contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        // Process the cursor and add items directly to the lists
        cursor?.use {
            val columnIndexData = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            val columnIndexMimeType = it.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)

            while (it.moveToNext()) {
                val absolutePathOfImage = it.getString(columnIndexData)
                val mimeType = it.getString(columnIndexMimeType)

                // Only add images, skip videos
                if (mimeType?.startsWith("image/") == true) {
                    // Add directly to the main lists
                    val model = ImageVideoModel(absolutePathOfImage, select = false)
                    galleryList.add(model)
                    filterImagesArrayList.add(model)
                }
            }

            // Notify adapter on the main thread after loading all data
            galleryAdapter?.notifyDataSetChanged()
            notifyAdapter()
        }
    }*/
 private fun loadFilesFromGallery() {
     val projection = arrayOf(
         MediaStore.MediaColumns.DATA,
         MediaStore.MediaColumns.MIME_TYPE
     )

     val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"
     val selectionArgs = arrayOf(
         MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
         MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
     )

     val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC"

     val cursor = contentResolver.query(
         MediaStore.Files.getContentUri("external"),
         projection,
         selection,
         selectionArgs,
         sortOrder
     )

     cursor?.use {
         val columnIndexData = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
         val columnIndexMimeType = it.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)

         while (it.moveToNext()) {
             val absolutePathOfImage = it.getString(columnIndexData)
             val mimeType = it.getString(columnIndexMimeType)

             if (!mimeType.isNullOrEmpty() && mimeType.startsWith("image/")) {
                      val model = ImageVideoModel().apply {
                         path = absolutePathOfImage
                         select = false
                     }
                     galleryList.add(model)
                     filterImagesArrayList.add(model)
              }
         }

         try {
             galleryAdapter?.notifyDataSetChanged()
             notifyAdapter()
         } catch (e: Exception) {
             e.printStackTrace()
         }
     }
 }


    private fun notifyAdapter() {
        val hashMap = HashMap<String, ArrayList<String>>()
        val allPaths = ArrayList<String>()
        for (item in galleryList) {
            val split = item.path!!.split("/")
            val fileName = item.path!!.replace(split.last(), "")

            hashMap.computeIfAbsent(fileName) { ArrayList() }.add(item.path!!)
            allPaths.add(item.path!!)
        }

        folderList.clear()
        hashMap["Gallery"] = allPaths

        for (key in hashMap.keys) {
            hashMap[key]?.let { FolderModel(key, hashMap[key]?.get(0), it.size) }
                ?.let { folderList.add(it) }
        }

        folderList.sortByDescending { it.sizeValue }
    }

    private fun getFolderDialog() {
        val dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val folderBinding: FolderListDialogBinding =
            FolderListDialogBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(folderBinding.root)

        val folderAdapter = FolderAdapter(folderList, this, dialog, 2).apply {
            itemClickListner { it, folderNameData ->
                if (it.folderName == "Gallery") {
                    setAdapter(galleryList)
                } else {
                    setAdapter(filteredVal(it.folderName!!, galleryList))
                }
                binding.galleryNameEt.text = folderNameData
            }
        }
        folderBinding.folderRv.layoutManager = LinearLayoutManager(this)
        folderBinding.folderRv.adapter = folderAdapter
        folderBinding.cancelTv.setOnClickListener {
            dialog.dismiss()
        }
        folderAdapter.notifyDataSetChanged()
        dialog.show()
    }

    private fun setAdapter(mCountryItemArrayList: ArrayList<ImageVideoModel>) {
        filterImagesArrayList.clear()
        filterImagesArrayList.addAll(mCountryItemArrayList)
        binding.galleryRv.adapter?.notifyDataSetChanged()
    }

    fun getData(folderModel: FolderModel, folderNameData: String?) {
        if (folderModel.folderName.equals("Gallery")) {
            setAdapter(galleryList)
        } else {
            setAdapter(filteredVal(folderModel.folderName ?: "", galleryList))
        }
        binding.galleryNameEt.text = folderNameData
    }

    private fun filteredVal(
        folderName: String,
        galleryList: ArrayList<ImageVideoModel>
    ): ArrayList<ImageVideoModel> {
        val filteredData = ArrayList<ImageVideoModel>()
        for (item in galleryList) {
            val split = item.path?.split("/")
            val fileName = item.path?.replace(split!!.last(), "")
            if (fileName.equals(folderName, ignoreCase = true)) {
                filteredData.add(item)
            }
        }
        return filteredData
    }
}
