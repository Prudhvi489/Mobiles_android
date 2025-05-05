package com.mobivault.utils.cameraGallery

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobivault.R
import com.mobivault.databinding.ActivityGalleryBinding
import com.mobivault.utils.AppMethods
import com.mobivault.utils.AppStrings
import com.mobivault.utils.CLog
import com.mobivault.utils.cameraGallery.adapter.FolderAdapter
import com.mobivault.utils.cameraGallery.model.Folder
import com.mobivault.utils.cameraGallery.model.ImageDataHolder
import com.mobivault.utils.cameraGallery.model.Items
import com.mobivault.utils.goneView
import com.mobivault.utils.visibleView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryActivity : AppCompatActivity() {
    lateinit var binding: ActivityGalleryBinding
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private var allFolders: ArrayList<Folder> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(true)
        }
        binding =DataBindingUtil.setContentView(this,R.layout.activity_gallery)

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    loadFolders()
                } else {
                    Toast.makeText(this,
                        getString(R.string.please_give_storage_permission), Toast.LENGTH_SHORT).show()
                }
            }

        checkPermissions()
        onClicks()
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                loadFolders()
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                loadFolders()
            }
        } else {
            loadFolders()
        }
    }

    private fun onClicks() {
        binding.backIV.setOnClickListener {
            finish()
        }
    }

    private fun loadFolders() {
        allFolders = getAllFolders()
         if (allFolders.isEmpty()) {
            binding.noDataTV.visibleView()
            binding.recyclerView.goneView()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                AppMethods.customAlertWithOK(this,
                    getString(R.string.limited_access_message))
            }
        } else {
            binding.noDataTV.goneView()
            binding.recyclerView.visibleView()
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.adapter = FolderAdapter(this, allFolders)
            { selectedFolder ->
                CLog.e("Gallery", "selectedFolder folders : $selectedFolder")
                CLog.e("Gallery", "selectedFolder folders : $selectedFolder.items")
                ImageDataHolder.images = selectedFolder.items
                val intent = Intent(this, ImageListActivity::class.java)
                intent.putExtra(AppStrings.IntentData.type,AppStrings.Constants.mediaTypeImage)
                intent.putExtra(AppStrings.IntentData.imageFileList, selectedFolder.items)
                resultLauncher.launch(intent)
            }
        }
    }

    private fun getAllFolders(): ArrayList<Folder> {
        val folders = HashMap<String, Folder>()
        val allImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED  // Add the date column for sorting
        )
        // Sort the images by date in descending order (most recent first)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        val cursor = contentResolver.query(allImageUri, projection, null, null, sortOrder)

        try {
            cursor?.use {
                val pathIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA)
                val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                while (cursor.moveToNext()) {
                    val path = cursor.getString(pathIndex)
                    val displayName = cursor.getString(nameIndex)
                    val parentFolder = path.substring(0, path.lastIndexOf('/'))
                    if (folders.containsKey(parentFolder)) {
                        folders[parentFolder]?.items?.add(Items(displayName, path))
                    } else {
                        val items = ArrayList<Items>()
                        items.add(Items(displayName, path))
                        folders[parentFolder] = Folder(parentFolder, items)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }

        return ArrayList(folders.values)
    }

/*
    fun getAllFolders(): ArrayList<Folder> {
        val folders = HashMap<String, Folder>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_ID
        )

        val collection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        val cursor = contentResolver.query(collection, projection, null, null, sortOrder)

        cursor?.use {
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val bucketName = cursor.getString(bucketNameColumn)
                val bucketId = cursor.getString(bucketIdColumn)

                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
                )

                val items = Items(name, contentUri.toString())
                if (folders.containsKey(bucketId)) {
                    folders[bucketId]?.items?.add(items)
                } else {
                    val newFolder = Folder(bucketName, arrayListOf(items))
                    folders[bucketId] = newFolder
                }
            }
        }

        return ArrayList(folders.values)
    }
*/

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val imageUri = data.getStringExtra(AppStrings.IntentData.selectedImageUri)
                    val imageUrl = data.getStringExtra(AppStrings.IntentData.imageFile)
                    if (imageUri != null && imageUrl != null) {
                        setData(imageUri, imageUrl)
                    }
                }
            }
        }

    fun setData(imageUri : String, imageUrl: String) {
        val intent = Intent()
        intent.putExtra(AppStrings.IntentData.selectedImageUri, imageUri)
        intent.putExtra(AppStrings.IntentData.imageFile, imageUrl)
         setResult(RESULT_OK, intent)
        finish()
    }

}