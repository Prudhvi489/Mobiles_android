package com.mobivault.ui.mobiledetails


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.PopupMenu
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mobivault.R
import com.mobivault.core.BaseVMBindingActivity
import com.mobivault.databinding.ActivityMobilesListBinding
import com.mobivault.databinding.FilterBottomSheetBinding
import com.mobivault.interfaces.BottomSheet
import com.mobivault.ui.addmobiles.AddMobileDetailsActivity
import com.mobivault.ui.addmobiles.adapter.MobilesListAdapter
import com.mobivault.ui.login.LoginActivity
import com.mobivault.ui.mobiledetails.model.Asset
import com.mobivault.ui.viewmodel.AddDetailsViewmodel
import com.mobivault.utils.AppMethods
import com.mobivault.utils.AppStrings
import com.mobivault.utils.CLog
import com.mobivault.utils.SessionManager
import com.mobivault.utils.goneView
import com.mobivault.utils.visibleView
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import javax.inject.Inject

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
    @Inject
   lateinit var sm:SessionManager

    lateinit var layoutManager: GridLayoutManager
    private var loading = false
    private var apiLoading = false
    lateinit var bottomSheetDialog: BottomSheetDialog
    lateinit var bottomSheetBinding: FilterBottomSheetBinding
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
        viewModel.assignTempValues()
        viewModel.clearTempValues()
        binding.filterStatusTextBTN.text =getString(R.string.all).replaceFirstChar { it.uppercase() }
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

            if (searchQuery==null) {
                // Show a toast if the search string is null or empty
                AppMethods.showToast(
                    this,
                    getString(R.string.please_enter_a_search_query),
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
                if (viewModel.searchString.value != null) {
                    viewModel.page.value=1
                    viewModel.getAssets()
                } else {
                    AppMethods.showToast(this, "Please enter the search text")
                }
                return@setOnEditorActionListener true
            }
            false
        }
        binding.commonHeader.logoutBtn.setOnClickListener {

            AppMethods.showConfirmationDialog(
                context = this,
                message = getString(R.string.are_you_sure_you_want_to_logout),
                onPositiveClick = {
                    sm.clearSession()
                    finish()
                    val intent=Intent(this,LoginActivity::class.java)
                    startActivity(intent)
                 }
            )

        }
        binding.commonHeader.filterIC.setOnClickListener {
            openFilterBottomSheet()
        }
    }

    private fun navToActivity() {
        val intent= Intent(this,AddMobileDetailsActivity::class.java)
        startActivity(intent)

    }

    private fun initUI() {
         binding.commonHeader.backIconIV.goneView()
        binding.commonHeader.titleTV.text= getString(R.string.mobile_record)
        binding.commonHeader.logoutBtn.visibleView()
//        if(list.size==0){
//            binding.commonHeader.filterIC.goneView()
//        }else{
            binding.commonHeader.filterIC.visibleView()
//        }
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
//                if (loading && !apiLoading ) {
                if (loading ) {
                    if (totalItemCount >= visibleItemCount - 4) {
                        loading = false
                        if (itemsCount > list.size) {
                           viewModel.page.value= viewModel.page.value?.plus(1)
//                            apiLoading = true
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
        intent.putExtra(AppStrings.IntentData.imei,model.id)//nothing but id
        intent.putExtra(AppStrings.IntentData.from,AppStrings.ActivityFrom.addDetailsActivity)
        startActivity(intent)
        dialog?.dismiss()
     }

    override fun deleteTvClick(dialog: Dialog?, model: Asset) {

        AppMethods.showConfirmationDialog(
            context = this,
            message = getString(R.string.are_you_sure_you_want_to_delete),
            onPositiveClick = {
                CLog.e(TAG, "deleteTvClick: delete imei ${model.imei}", )
                var jsonObject=JSONObject()
                jsonObject.put(AppStrings.InputData.id,model.id)
                model.id?.let { viewModel.deleteAsset(it,jsonObject) }
                dialog?.dismiss()
            }, onNegativeClick = {
                dialog?.dismiss()
            }
        )
     }
    fun redirectToMobileDetailsActivity(imei: String?) {
        val intent = Intent (this,MobileDetailsActivity::class.java)
        intent.putExtra(AppStrings.IntentData.imei,imei)
        startActivity(intent)
     }
    private fun openFilterBottomSheet() {
        // Initialize ViewBinding
        val inflater = LayoutInflater.from(this)
        val bottomSheetBinding = FilterBottomSheetBinding.inflate(inflater)

        // Create BottomSheetDialog and set transparent background
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        bottomSheetBinding.viewmodel = viewModel
        bottomSheetBinding.lifecycleOwner = this

        bottomSheetDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
        )

        bottomSheetBinding.statusET.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.menu.add(getString(R.string.all))
            popupMenu.menu.add(getString(R.string.available))
            popupMenu.menu.add(getString(R.string.sold))
            popupMenu.menu.add(getString(R.string.pending))
            viewModel.filterStatusTemp.value =getString(R.string.all)

            popupMenu.setOnMenuItemClickListener { item ->
//                binding.statusEt.text = item.title
                viewModel.filterStatusTemp.value =
                    item.title.toString() // Or assign to LiveData if it's MutableLiveData<String>
                true
            }
            popupMenu.show()
        }
        bottomSheetBinding.clearFilterTv.setOnClickListener {
            viewModel.clearTempValues()
//            bottomSheetDialog.dismiss()
        }
        bottomSheetBinding.applyBtn.setOnClickListener {
            viewModel.assignTempValues()
            binding.filterStatusTextBTN.text = viewModel.filterStatus.value.toString().replaceFirstChar { it.uppercase() }
            viewModel.getAssets()
            bottomSheetDialog.dismiss()
        }
        bottomSheetBinding.cancelBtn.setOnClickListener {
            viewModel.reassignTempValues()
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }
}