package com.mobirecord.ui.mobiledetails

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobirecord.databinding.ImagePagerItemBinding
import com.mobirecord.utils.AppMethods

class ImagePagerAdapter(var activity: Activity, val imageList: List<String>) :
    RecyclerView.Adapter<ImagePagerAdapter.ViewHolder>() {

    class ViewHolder(var binding: ImagePagerItemBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding = ImagePagerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ImagePagerAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = imageList[position]
        AppMethods.loadImage(holder.binding.imageIV, item)

    }

    override fun getItemCount(): Int = imageList.size
}
