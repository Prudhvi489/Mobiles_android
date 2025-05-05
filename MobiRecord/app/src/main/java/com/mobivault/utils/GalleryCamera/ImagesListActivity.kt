package com.mobivault.utils.cameraGallery

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.mobivault.databinding.ActivityImagesListBinding
import com.mobivault.utils.AppStrings
import com.mobivault.utils.CLog
import com.mobivault.utils.cameraGallery.adapter.ImagesListAdapter
import com.mobivault.utils.cameraGallery.model.ImageDataHolder
import com.mobivault.utils.cameraGallery.model.Items


import java.io.File

class ImageListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImagesListBinding
    private var images: ArrayList<Items> = ArrayList()
    private lateinit var adapter: ImagesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImagesListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntentData()
        setAdapter()
        onClicks()
    }

    private fun getIntentData() {
//        images = intent.parcelableArrayList<Items>(AppStrings.IntentData.images) as ArrayList<Items>
        images = ImageDataHolder.images ?: arrayListOf()
    }

    private fun setAdapter() {
        adapter = ImagesListAdapter(this, images) { selectedItem ->
            //binding.countBadge.text = "Selected: $selectedCount"
            setData(selectedItem)
        }

        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerView.adapter = adapter
    }

    private fun onClicks() {
        binding.backIV.setOnClickListener {
            finish()
        }
    }

    fun setData(item : Items) {
        val imageUri = Uri.fromFile(File(item.imagePath.toString()))
        grantUriPermission(
            this.packageName,
            imageUri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION /*or Intent.FLAG_GRANT_WRITE_URI_PERMISSION*/
        )
        val intent = Intent()
        intent.putExtra(AppStrings.IntentData.selectedImageUri, imageUri.toString())
        intent.putExtra(AppStrings.IntentData.imageFile, item.imagePath)
        CLog.e("", "Selected Image -> ${item.imagePath}")
        setResult(RESULT_OK, intent)
        finish()
    }
}


