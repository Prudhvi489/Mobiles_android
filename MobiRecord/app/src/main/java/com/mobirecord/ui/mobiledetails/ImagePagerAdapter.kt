package com.mobirecord.ui.mobiledetails

import android.app.Activity
import android.util.Log
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
        Log.e("TAG", "onBindViewHolder: image${item}", )

        AppMethods.glideCoverImage(holder.itemView.context,item, holder.binding.imageIV)

    }

    override fun getItemCount(): Int = imageList.size
}
