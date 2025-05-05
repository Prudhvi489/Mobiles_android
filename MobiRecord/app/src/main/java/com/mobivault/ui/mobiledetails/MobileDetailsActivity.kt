package com.mobivault.ui.mobiledetails

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.mobivault.R
import com.mobivault.core.BaseVMBindingActivity
import com.mobivault.databinding.ActivityMobileDetailsBinding
import com.mobivault.ui.viewmodel.AddDetailsViewmodel
import com.mobivault.utils.AppMethods
import com.mobivault.utils.AppStrings
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MobileDetailsActivity :
    BaseVMBindingActivity<ActivityMobileDetailsBinding, AddDetailsViewmodel>(AddDetailsViewmodel::class.java) {
    var viewPagerAdapter: ImagePagerAdapter? = null
    private val handler = Handler(Looper.getMainLooper())

    val imageList :ArrayList<String>?= arrayListOf()
    var imei: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        getIntentData()
        initUI()
        onClicks()
        setObservers()

    }

    private fun setObservers() {
        viewModel.getAssetByIdResponse.observe(this) {
            if (it?.status == AppStrings.Constants.success) {
                it.data?.let {
                    viewModel.mobileModel.value = it.mobile_model
                    viewModel.imei.value = it.imei
                    viewModel.buyerPrice.value = it.buyer_price
                    viewModel.sellerPrice.value = it.seller_price
                    viewModel.profit.value = it.profit
                    viewModel.status.value = it.status
                    viewModel.sellerName.value = it.seller_name
                    viewModel.sellerPhoneNumber.value = it.seller_phone
                    viewModel.sellerAadhar.value = it.seller_aadhar
                    viewModel.sellerDate.value = if (!it.seller_date.isNullOrEmpty()) {
                        AppMethods.convertStrDateToStrDate(
                            it.seller_date,
                            AppStrings.DateFormat.yyyy_mm_dd_t_hh_mm_ss_sss_z,
                            AppStrings.DateFormat.yyyy_mm_dd
                        )
                    } else {
                        ""
                    }

                    viewModel.buyingDate.value = if (!it.buying_date.isNullOrEmpty()) {
                        AppMethods.convertStrDateToStrDate(
                            it.buying_date,
                            AppStrings.DateFormat.yyyy_mm_dd_t_hh_mm_ss_sss_z,
                            AppStrings.DateFormat.yyyy_mm_dd
                        )
                    } else {
                        ""
                    }

                    viewModel.buyerName.value = it.buyer_name
                    viewModel.buyerPhoneNumber.value = it.buyer_phone
                    it.imageUrls?.forEach {
                        imageList?.add(it)
                    }
                    Log.e(TAG, "setObservers: images size--->${imageList!!.size}", )
                    viewPagerAdapter?.notifyDataSetChanged()

                }
            }
        }
    }

    private fun getIntentData() {
        imei = intent.getStringExtra(AppStrings.IntentData.imei)
    }

    private fun onClicks() {
        binding.commonHeader.backIconIV.setOnClickListener {
            finish()
        }
    }

    private fun initUI() {
        binding.commonHeader.titleTV.text = getString(R.string.mobile_details)
        //static list after that need to add the dynamic list

        initializeViewPager()
    }

    override fun onResume() {
        super.onResume()
        imei?.let { viewModel.getAssetById(it) }
    }

    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            if (!imageList.isNullOrEmpty()) {
                val nextItem = (binding.viewPager.currentItem + 1) % imageList!!.size
                binding.viewPager.setCurrentItem(nextItem, true)
                handler.postDelayed(this, 3000)
            }
        }
    }


    private fun initializeViewPager() {
        viewPagerAdapter = ImagePagerAdapter(this, imageList!!)
        binding.viewPager.adapter = viewPagerAdapter
        // Set up indicator
        binding.indicator.attachTo(binding.viewPager)
        // Start Auto Swipe
        handler.postDelayed(autoScrollRunnable, 3000)
    }

    override fun getPersistentView(): ActivityMobileDetailsBinding {
        return ActivityMobileDetailsBinding.inflate(layoutInflater)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(autoScrollRunnable) // Prevent memory leaks
    }
}