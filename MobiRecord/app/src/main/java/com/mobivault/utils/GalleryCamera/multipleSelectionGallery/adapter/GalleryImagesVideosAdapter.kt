package com.mobivault.utils.cameraGallery.multipleSelectionGallery.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobivault.R
import com.mobivault.databinding.GalleryItemBinding
import com.mobivault.utils.CLog
import com.mobivault.utils.cameraGallery.multipleSelectionGallery.model.ImageVideoModel


class GalleryImagesVideosAdapter(
    private val activity: Activity,
    private val galleryImagesVideosList: ArrayList<ImageVideoModel>,
    private val maxSelection: Int
) : RecyclerView.Adapter<GalleryImagesVideosAdapter.MyViewHolder>() {
    var previousPosition = 0
    private val selectedImages = mutableListOf<ImageVideoModel>()
    private val selectedItems = mutableListOf<Int>()
    class MyViewHolder(var binding: GalleryItemBinding) : RecyclerView.ViewHolder(binding.root)

    var onClick : ((item : ImageVideoModel) -> Unit)? = null
    fun onClickListener(listener: (item : ImageVideoModel) -> Unit) {
        onClick = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.gallery_item, parent, false
            )
        )
    }
    override fun getItemCount(): Int = galleryImagesVideosList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = galleryImagesVideosList[position]
        CLog.e("TAG", "onBindViewHolder:path----->${data.path}  ", )

        // Set initial UI state based on data's selection status
        holder.binding.mediaCountTv.visibility = if (data.select) View.VISIBLE else View.GONE
        holder.binding.mediaCountTv.text = data.count?.toString() ?: ""
        holder.binding.selectRl.setBackgroundResource(
            if (data.select) R.drawable.radius_80_pale_blue_bg else R.drawable.radio_disable
        )
        // Use application context if you don’t need activity-specific context
//        Glide.with(activity).load(data.contentUri).into(holder.binding.imageView)
//        AppMethods.loadImage(holder.binding.imageView, data.path ?: "")
        Glide.with(holder.binding.imageView.context).load(data.path).into(holder.binding.imageView)


        /*    // Load image thumbnail using Glide
            Glide.with(mainActivity)
                .load(data.path)
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(200, 200)
                .centerCrop()
                .into(holder.binding.imageView)*/


        // Handle item selection and deselection
        holder.itemView.setOnClickListener {
            if (data.select) {
                // Deselect the image
                selectedImages.remove(data)
                selectedItems.remove(position)
                data.select = false
                data.count = null  // Clear count when deselected
                notifyItemChanged(position)
            } else {
                if (selectedImages.size < maxSelection) {
                    // Select the image if within selection limit
                    selectedImages.add(data)
                    selectedItems.add(position)
                    data.select = true
                } else {
                    Toast.makeText(
                        holder.itemView.context,
                        activity.getString(R.string.your_selection_limit_exceed),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
            }

            onClick?.invoke(data)

            // Update the counts for all selected items after each selection/deselection
            updateSelectedCounts()
        }
    }

    private fun updateSelectedCounts() {
        // Re-assign count based on the updated order in selectedImages
        selectedImages.forEachIndexed { index, image ->
            image.count = index + 1
        }
        // Refresh UI for items that need count updates
        selectedItems.forEach { position ->
            notifyItemChanged(position)  // Refresh only selected items
        }
    }


}