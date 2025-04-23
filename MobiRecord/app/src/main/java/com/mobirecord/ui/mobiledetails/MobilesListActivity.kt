package com.mobirecord.ui.mobiledetails


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobirecord.R
import com.mobirecord.core.BaseVMBindingActivity
import com.mobirecord.databinding.ActivityMobilesListBinding
import com.mobirecord.interfaces.BottomSheet
import com.mobirecord.ui.addmobiles.AddMobileDetailsActivity
import com.mobirecord.ui.addmobiles.adapter.MobilesListAdapter
import com.mobirecord.ui.mobiledetails.model.Asset
import com.mobirecord.ui.viewmodel.AddDetailsViewmodel
import com.mobirecord.utils.AppMethods
import com.mobirecord.utils.AppStrings
import com.mobirecord.utils.CLog
import com.mobirecord.utils.goneView
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class MobilesListActivity : BaseVMBindingActivity<ActivityMobilesListBinding, AddDetailsViewmodel>(AddDetailsViewmodel::class.java) ,BottomSheet{
    var mobileListAdapter:MobilesListAdapter?=null
    var minCount: Int = AppStrings.Constants.min
    var maxCount: Int = AppStrings.Constants.max
    var itemsCount: Int = 0
    var pastVisibleItems = 0
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    var list= ArrayList<Asset>()

    lateinit var layoutManager: GridLayoutManager
    private var loading = false
    private var apiLoading = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel=viewModel
        binding.lifecycleOwner= this
        initUI()
        onClicks()
        setObservers()
    }

    private fun setObservers() {
         viewModel.getAssetsResponse.observe(this){
             if(viewModel.page.value==1){

                 list.clear()
             }
             if(it?.status==AppStrings.Constants.success){
                 it.data.let {
                    itemsCount= it?.totalRecords!!
                     it.assets.forEach {
                         list.add(it)
                     }
                 }
                 viewModel.noData.value = list.size == 0
                 mobileListAdapter?.notifyDataSetChanged()
             }else{
                 AppMethods.showToast(this,it?.message)
             }

         }
        viewModel.deleteAssetResponse.observe(this){
            if(it?.status==AppStrings.Constants.success){
                AppMethods.showToast(this,it?.message,AppStrings.SnackbarStatus.success)
                list.clear()
                viewModel.getAssets()
            }else{
                AppMethods.showToast(this,it?.message,AppStrings.SnackbarStatus.error)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.searchString.value=""
        viewModel.getAssets()
    }

    private fun onClicks() {
         binding.addMobileTV.setOnClickListener {
             navToActivity()
         }
        binding.searchTv.setOnClickListener {
//            getProfilesApi(viewModel.searchString.value ?: "")
            //            getProfilesApi(viewModel.searchText.value ?: "", AppStrings.Constants.outerSearch)
            val searchQuery = viewModel.searchString.value

            if (searchQuery.isNullOrEmpty()) {
                // Show a toast if the search string is null or empty
                AppMethods.showToast(
                    this,
                    "Please enter a search query",
                    AppStrings.SnackbarStatus.error
                )
            } else {
                // Proceed with the API call
                viewModel.page.value=1
                viewModel.getAssets()
            }
        }
        binding.searchEt.setOnEditorActionListener { _, actionId, _ ->
            Log.e("TAG", "initUi: setOnEditorActionListener ")
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // User clicked on the search action
                 Log.e("TAG", "initUi: setOnEditorActionListener @@@@@@@@@@@@@@@@@@")
                if (viewModel.searchString.value!!.isNotEmpty() && viewModel.searchString.value != null) {
                    viewModel.page.value=1
                    viewModel.getAssets()
                } else {
                    AppMethods.showToast(this, "Please enter the search text")
                }
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun navToActivity() {
        val intent= Intent(this,AddMobileDetailsActivity::class.java)
        startActivity(intent)

    }

    private fun initUI() {
         binding.commonHeader.backIconIV.goneView()
        binding.commonHeader.titleTV.text= getString(R.string.mobile_record)
        initializeMobileAdapter()
     }



    private fun initializeMobileAdapter() {
        layoutManager = GridLayoutManager(this, 2)

        mobileListAdapter= MobilesListAdapter(this,this,list)
        binding.mobilesRv.adapter=mobileListAdapter
        binding.mobilesRv.layoutManager=layoutManager
        scrollerRecyclerview(this)
     }
    private fun scrollerRecyclerview(requireContext: Context) {
        binding.mobilesRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                Log.e(TAG, "onScrolled: @@@@@@@@@@@@@@@@@@@@@@" )
                pastVisibleItems = layoutManager.childCount
                visibleItemCount = layoutManager.itemCount
                totalItemCount = layoutManager.findLastVisibleItemPosition()
                if (loading && !apiLoading ) {
                    if (totalItemCount >= visibleItemCount - 4) {
                        loading = false
                        if (itemsCount > list.size) {
                           viewModel.page.value= viewModel.page.value?.plus(1)
                            apiLoading = true
                            CLog.e(TAG, "onScrolled: #$$$$$$$$$$$$$$$$$$", )
                            viewModel.getAssets()
                        }
                    }
                }
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                loading = true
            }
        })
    }


    override fun getPersistentView(): ActivityMobilesListBinding {
         return ActivityMobilesListBinding.inflate(layoutInflater)
    }

    override fun editTvClick(dialog: Dialog?, model: Asset) {
        CLog.e(TAG, "editTvClick: edited", )
        val intent = Intent (this,AddMobileDetailsActivity::class.java)
        intent.putExtra(AppStrings.IntentData.imei,model.imei)
        intent.putExtra(AppStrings.IntentData.from,AppStrings.ActivityFrom.addDetailsActivity)
        startActivity(intent)
        dialog?.dismiss()
     }

    override fun deleteTvClick(dialog: Dialog?, model: Asset) {
        CLog.e(TAG, "deleteTvClick: delete imei ${model.imei}", )
        var jsonObject=JSONObject()
        jsonObject.put(AppStrings.InputData.imei,model.imei)
        model.imei?.let { viewModel.deleteAsset(it,jsonObject) }
        dialog?.dismiss()
     }
    fun redirectToMobileDetailsActivity(imei: String?) {
        val intent = Intent (this,MobileDetailsActivity::class.java)
        intent.putExtra(AppStrings.IntentData.imei,imei)
        startActivity(intent)
     }
}