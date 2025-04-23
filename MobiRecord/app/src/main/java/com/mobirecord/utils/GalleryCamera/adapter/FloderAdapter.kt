package com.mobirecord.utils.cameraGallery.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mobirecord.databinding.FolderAdapterBinding
import com.mobirecord.utils.CLog
import com.mobirecord.utils.cameraGallery.model.Folder

import java.io.File

class FolderAdapter(private val context: Context, private val folderList: ArrayList<Folder>, private val onItemClick: (Folder) -> Unit  // Callback for item click
    ) :
    RecyclerView.Adapter<FolderAdapter.ViewHolder>() {

    class ViewHolder(val binding: FolderAdapterBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = FolderAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return folderList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentFolder = folderList[position]
        CLog.e("", "onBindViewHolder: " + currentFolder.items)
        val floderName = File(currentFolder.folderName).name
        holder.binding.folderName.text = floderName
        Glide.with(context)
            .load(currentFolder.items[0].imagePath) // Load the first image as the folder thumbnail
            .apply(RequestOptions().centerCrop()).into(holder.binding.folderThumbnail)

        holder.binding.root.setOnClickListener {
            onItemClick(currentFolder)
        }
    }
}
