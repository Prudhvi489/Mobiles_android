package com.mobirecord.dialogs

 import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobirecord.R
import com.mobirecord.databinding.DialogCountryListBinding
import com.mobirecord.dialogs.adapters.ListDisplayAdapter
import com.mobirecord.interfaces.ItemClickInterface
import com.mobirecord.ui.addmobiles.model.ListDataModel

class DialogUtils {

    companion object {
         lateinit var dialogcountryBinding: DialogCountryListBinding


            fun displayList(
                activity: Activity? = null,
                fragment: Fragment? = null,
                list: ArrayList<ListDataModel>,
                from: String? = "",
                isDialCode: Boolean = true,
                text: String? = "",

                ) {
                val selectedClickInterface: ItemClickInterface
                selectedClickInterface = if (fragment != null) {
                    fragment as ItemClickInterface
                } else {
                    activity as ItemClickInterface
                }

                var context = if (fragment != null) {
                    fragment.context
                } else {
                    activity
                }
                val dialog = if (fragment != null) {
                    fragment.context?.let { Dialog(it) }
                } else {
                    activity?.let { Dialog(it) }
                }


                dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialogcountryBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(context), R.layout.dialog_country_list, null, false
                )
                dialog.apply {
                    setContentView(dialogcountryBinding.root)
                    window?.setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
                    )
                }


                val layoutManager = LinearLayoutManager(activity)
                dialogcountryBinding.headerText.text = text

            /*    dialogcountryBinding.dialogCurrencylistRv.layoutManager = layoutManager
                var adapter = ListDisplayAdapter(list, dialog, from, selectedClickInterface,activity)
                dialogcountryBinding.dialogCurrencylistRv.adapter = adapter
*/
                lateinit var adapter: ListDisplayAdapter


                // **Check if the list is empty**
                if (list.isEmpty()) {
                    dialogcountryBinding.noResultsTv.visibility = View.VISIBLE
                    dialogcountryBinding.dialogCurrencylistRv.visibility = View.GONE
                } else {
                    dialogcountryBinding.noResultsTv.visibility = View.GONE
                    dialogcountryBinding.dialogCurrencylistRv.visibility = View.VISIBLE

                     dialogcountryBinding.dialogCurrencylistRv.layoutManager = layoutManager
                     adapter = ListDisplayAdapter(list, dialog, from, selectedClickInterface, activity)
                    dialogcountryBinding.dialogCurrencylistRv.adapter = adapter
                }

                dialog.show()
                dialog.setCancelable(true)

                dialog.setOnCancelListener { dialogInterface: DialogInterface? ->

                }
                dialog.setOnDismissListener { dialogInterface: DialogInterface? ->
                    // nationalitiesSelected()

                }
                dialogcountryBinding.closeIv.setOnClickListener {
                    dialog.dismiss()
                }

                dialogcountryBinding.dialogCurrencySearchEt.addTextChangedListener(object :
                    TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence, start: Int, count: Int, after: Int
                    ) {
                    }

                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        if (isDialCode) {
                            val displayBusinessModels: List<ListDataModel> = filter(
                                list, s.toString(), isDialCode
                            )
                            if (displayBusinessModels.size == 0) {
                                dialogcountryBinding.noResultsTv.visibility = View.VISIBLE


                            } else {
                                dialogcountryBinding.noResultsTv.visibility = View.GONE
                            }
                                adapter.setFilter(displayBusinessModels)

                        } else {
                            var displayBusinessModels: List<ListDataModel> = ArrayList()
                            if (isDialCode) {
                                displayBusinessModels = filter(
                                    list, s.toString(), isDialCode
                                )
                            } else {
                                displayBusinessModels = filter(
                                    list, s.toString(), isDialCode
                                )
                            }

                            if (displayBusinessModels.size == 0) {

                            } else {

                            }
                                adapter.setFilter(displayBusinessModels)

                        }

                    }

                    override fun afterTextChanged(s: Editable) {}
                })


            }





        /***  Filter    ***/
        fun filter(
            models: List<ListDataModel>, query: String, isDialCode: Boolean
        ): List<ListDataModel> {
            var query = query
            query = query.lowercase()
            return if (!query.isEmpty()) {
                val filteredModelList: MutableList<ListDataModel> = ArrayList<ListDataModel>()
                for (model in models) {
                    var name: String = ""
                    var id: String = ""

                    name = model.name.toString().lowercase()
                    id = model.id.toString().lowercase()



                    Log.e("TAg", "filter: query " + query)
                    if (name.contains(query) or id.contains(query)) {
                        filteredModelList.add(model)
                    }
                }
                filteredModelList
            } else {
                models
            }
        }




        private fun hideKeyboard(context: Context?) {
            context?.let {
                val inputMethodManager = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                val view = (it as? Activity)?.currentFocus ?: View(it) // Get current focus or create a new view
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }






    }

}
