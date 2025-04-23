package com.mobirecord.utils.cameraGallery.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mobirecord.databinding.ImagesListAdapterBinding
import com.mobirecord.utils.cameraGallery.model.Items


class ImagesListAdapter(
    private val context: Context,
    private val imageList: ArrayList<Items>,
    private val onItemClick: (Items) -> Unit  // Callback for item click
) : RecyclerView.Adapter<ImagesListAdapter.ViewHolder>() {

    private val selectedItems = mutableSetOf<Items>()

    /*private var onClickItem: ((item: CommonModel) -> Unit)? = null

    fun onItemClicked(listener: (item: CommonModel) -> Unit) {
        onClickItem = listener
    }*/

    class ViewHolder(val binding: ImagesListAdapterBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ImagesListAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = imageList[position]

        Glide.with(context)
            .load(currentItem.imagePath)
            .apply(RequestOptions().centerCrop())
            .into(holder.binding.itemV)

        // Display count if the item is selected
        if (selectedItems.contains(currentItem)) {
            holder.binding.countBadge.apply {
                text = selectedItems.size.toString() // Display the count of selected items
                visibility = android.view.View.VISIBLE
                setBackgroundColor(Color.GREEN) // Customize color if needed
            }
        } else {
            holder.binding.countBadge.visibility = android.view.View.GONE
        }

        holder.binding.root.setOnClickListener {
//            if (selectedItems.contains(currentItem)) {
//                selectedItems.remove(currentItem)
//            } else {
//                selectedItems.add(currentItem)
//            }
//            // Notify the activity about the selection change
//            onSelectionChanged(selectedItems.size)
//            notifyItemChanged(position) // Refresh the item view


            onItemClick(currentItem)

        }
    }

    fun getSelectedItems(): List<Items> {
        return selectedItems.toList()
    }
}
