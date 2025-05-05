    package com.mobivault.dialogs.adapters

    import android.app.Activity
    import android.app.Dialog
    import android.view.LayoutInflater
    import android.view.ViewGroup
    import androidx.databinding.DataBindingUtil
    import androidx.recyclerview.widget.RecyclerView
    import com.mobivault.R
    import com.mobivault.databinding.RowCategoriesTypesBinding
    import com.mobivault.interfaces.ItemClickInterface
    import com.mobivault.ui.addmobiles.model.ListDataModel
    import com.mobivault.utils.AppMethods

    class ListDisplayAdapter(
        var list: ArrayList<ListDataModel>,
        var dialog: Dialog,
        var from: String? = "",
        var selectedClickInterface: ItemClickInterface,
        var activity:Activity?=null,
        var activity1:Activity?=null
    ) :
        RecyclerView.Adapter<ListDisplayAdapter.viewHolder>() {
        inner class viewHolder(var binding: RowCategoriesTypesBinding) :
            RecyclerView.ViewHolder(binding.root) {


        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder =
            viewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.row_categories_types,
                    parent,
                    false
                )
            )


        override fun onBindViewHolder(holder: viewHolder, position: Int) {
            var model = list[position]
              holder.binding.rowCountryNameTv.text = model.name
            holder.binding.viewModel = model
            if (model.isSelected) {
                holder.binding.rowCountryNameTv.setTextColor(holder.itemView.context.resources.getColor(R.color.violet_color))
            } else {
                holder.binding.rowCountryNameTv.setTextColor(holder.itemView.context.resources.getColor(R.color.black))
            }
            holder.itemView.setOnClickListener {
                AppMethods.hideKeyboardForCountry(activity!!,holder.itemView)
                updateSelection(position)
                selectedClickInterface.onClickType(
                    from.toString(),
                    model.id.toString(),
                    model.name.toString(),
                    model.specification,
                    dialog,
                    )
            }
        }


        fun updateSelection(position: Int) {
            for (i in list.indices) {
                list[i].isSelected = i == position
            }
            notifyDataSetChanged()
        }
        fun setFilter(models: List<ListDataModel>?) {
    //        list.clear()
    //        list = ArrayList<ListDataModel>()
    //        list.addAll(models!!)
    //        notifyDataSetChanged()

            val selectedItemId = list.find { it.isSelected }?.id  // Store selected item ID
            list.clear()
            list.addAll(models ?: emptyList())

            // Restore selection state
            list.forEach { it.isSelected = it.id == selectedItemId }

            notifyDataSetChanged()
        }




        override fun getItemCount(): Int {
            return list.size
        }
    }