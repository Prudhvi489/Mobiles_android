package com.mobirecord.ui.addmobiles.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
 import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobirecord.databinding.MobileListItemBinding
import com.mobirecord.ui.mobiledetails.MobilesListActivity
import com.mobirecord.ui.mobiledetails.model.Asset
import com.mobirecord.utils.AppMethods
import com.mobirecord.utils.CLog

class MobilesListAdapter(
    var activity: Activity? = null,
    var context: Context? = null,
    var list: ArrayList<Asset>
  ) : RecyclerView.Adapter<MobilesListAdapter.MobileListViewHolder>() {
    class MobileListViewHolder(var binding: MobileListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): MobileListViewHolder {
          var binding = MobileListItemBinding.inflate(LayoutInflater.from(parent.context))
        return MobileListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    override fun onBindViewHolder(holder: MobileListViewHolder, position: Int) {
        val item = list!![position]
        CLog.e("TAG", "onBindViewHolder:imei ${item.imei}", )

        holder.binding.viewModel=item
        CLog.e("TAG", "setObservers:  Adapter Item -> $item")
        holder.binding.dotsSiv.setOnClickListener {
            AppMethods.bottomSheetDialog(
                activity!!,
                 item,

            )
        }
        holder.binding.albumImageSIV.setOnClickListener {
            (activity as? MobilesListActivity)?.redirectToMobileDetailsActivity(item.imei)
        }

    }
}
