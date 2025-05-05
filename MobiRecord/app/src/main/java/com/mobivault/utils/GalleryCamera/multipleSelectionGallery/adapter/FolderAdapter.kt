package com.mobivault.utils.cameraGallery.multipleSelectionGallery.adapter

import android.app.Activity
import android.app.Dialog
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobivault.databinding.FolderAdapterBinding
import com.mobivault.utils.cameraGallery.multipleSelectionGallery.GalleryImagesViewActiivty
import com.mobivault.utils.cameraGallery.multipleSelectionGallery.model.FolderModel


class FolderAdapter(
    private val arrayList: ArrayList<FolderModel>,
    private val activity: Activity,
    var dialog: Dialog,
    var type: Int
) : RecyclerView.Adapter<FolderAdapter.ViewHolder?>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        var binding = FolderAdapterBinding.inflate(LayoutInflater.from(viewGroup.context))
        return ViewHolder(binding)

    }

    private var onItemClicked: ((model: FolderModel,folderNameData:String) -> Unit)? = null


    fun itemClickListner(listner: (model: FolderModel,folderNameData:String) -> Unit) {
        onItemClicked = listner
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(
        viewHolder: ViewHolder,
        position: Int
    ) {
        val model = arrayList[position]
        Log.e(TAG, "onBindViewHolder: Folder = " + model.sizeValue)
        val splitName =
            model.folderName!!.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        //
        val folderNameData = splitName[splitName.size - 1].toString()

        viewHolder.binding.folderName.text = folderNameData
//        viewHolder.binding.countTv.text = model.sizeValue.toString()
        Glide.with(activity).load(model.path)
            .into(viewHolder.binding.folderThumbnail)

        viewHolder.binding.root.setOnClickListener {
            if (activity is GalleryImagesViewActiivty) {
                activity.getData(arrayList[position], folderNameData)
            }
            dialog.dismiss()
        }
    }


    class ViewHolder(itemView: FolderAdapterBinding) : RecyclerView.ViewHolder(itemView.root) {
        var binding: FolderAdapterBinding = itemView
    }

    companion object {
        private const val TAG = "FolderAdapter"
    }
}
