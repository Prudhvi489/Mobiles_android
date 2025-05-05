package com.mobivault.ui.addmobiles.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobivault.R
import com.mobivault.databinding.ImageListItemBinding
import com.mobivault.ui.addmobiles.model.MultiMediaModel
import com.mobivault.utils.AppMethods
import com.mobivault.utils.goneView
import com.mobivault.utils.visibleView


class MultiMediaAdapter(var context: Context, val list: ArrayList<MultiMediaModel>) :
    RecyclerView.Adapter<MultiMediaAdapter.ViewHolder>() {
    class ViewHolder(var binding: ImageListItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    private var onItemClicked: ((from:Int,pos: Int, model: MultiMediaModel) -> Unit)? =
        null


    fun onItemClicked(listener: (from:Int,pos: Int, model: MultiMediaModel) -> Unit) {
        onItemClicked = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding = ImageListItemBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.viewModel=list[position]
        holder.binding.cancelIv.setOnClickListener {
            AppMethods.showConfirmationDialog(
                context = context,
                message = context.getString(R.string.are_you_sure_you_want_to_delete),
                onPositiveClick = {
                    onItemClicked?.invoke(1, position, list[position]) // Perform deletion
                }
            )
        }

        holder.binding.addIV.setOnClickListener {
            onItemClicked?.invoke(2,position, list[position])
        }

        if (position == 0) {
            holder.binding.addIV.visibleView()
        } else {
            holder.binding.addIV.goneView()
        }
    }
}