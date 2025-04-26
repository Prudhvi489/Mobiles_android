package com.mobirecord.ui.addmobiles

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobirecord.R
import com.mobirecord.core.BaseVMBindingActivity
import com.mobirecord.databinding.ActivityAddMobileDetailsBinding
import com.mobirecord.interfaces.CalenderInterface
import com.mobirecord.ui.addmobiles.adapter.MultiMediaAdapter
import com.mobirecord.ui.addmobiles.model.ListDataModel
import com.mobirecord.ui.addmobiles.model.MultiMediaModel
import com.mobirecord.ui.addmobiles.model.MultipleImagesListToBackend
import com.mobirecord.ui.viewmodel.AddDetailsViewmodel
import com.mobirecord.utils.AppMethods
import com.mobirecord.utils.AppStrings
import com.mobirecord.utils.CLog
import com.mobirecord.utils.CropActivity
import com.mobirecord.utils.goneView
import com.mobirecord.utils.setOnSafeClickListener
import com.mobirecord.utils.visibleView
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import java.io.File
import java.io.IOException

@AndroidEntryPoint
class AddMobileDetailsActivity :
    BaseVMBindingActivity<ActivityAddMobileDetailsBinding, AddDetailsViewmodel>(AddDetailsViewmodel::class.java),
    CalenderInterface {
    var multiMediaAdapter: MultiMediaAdapter? = null
    var selectedPhotosCount = 0
    var totalImagesCount = 0
    var statusList = ArrayList<ListDataModel>()
    var imei: String? = ""
    var fromActivity: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        getIntentData()
        initUI()
        onClicks()
        setObservers()


    }

    private fun getIntentData() {
        imei = intent.getStringExtra(AppStrings.IntentData.imei)
        fromActivity = intent.getStringExtra(AppStrings.IntentData.from)
    }

    override fun onResume() {
        super.onResume()

    }

    private fun setObservers() {
        viewModel.validations.observe(this) {
            AppMethods.hideKeyboard(this, binding.customerAddressEt)
            if (it.first) {
                if (fromActivity == AppStrings.ActivityFrom.addDetailsActivity) {
                    if (viewModel.awsList.isEmpty()) {
                        updateAssetApiCall()
                    } else {
                        viewModel.generateUploadrls(this)
                    }
                } else {
//                    createAssetApiCall()
                    viewModel.generateUploadrls(this)
                }
            } else {
                AppMethods.showToast(
                    this,
                    AppMethods.returnMessage(it.second, this),
                    AppStrings.SnackbarStatus.error
                )
            }
        }
        viewModel.createAssetResponse.observe(this) {
            if (it?.status == AppStrings.Constants.success201) {
                AppMethods.showToast(this, it.message, AppStrings.SnackbarStatus.success)
                finish()
            }
        }
        viewModel.updateAssetResponse.observe(this) {
            if (it?.status == AppStrings.Constants.success) {
                AppMethods.showToast(this, it.message, AppStrings.SnackbarStatus.success)
                finish()
            }
        }
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

                    viewModel.buyerName.value = it.buyer_name
                    viewModel.buyerPhoneNumber.value = it.buyer_phone

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
                    it.image_keys?.forEach {
                        viewModel.multiImagesList.add(it.url?.let { it1 ->
                            it.key?.let { it2 ->
                                MultiMediaModel(
                                    path = it1,
                                    mediaType = AppStrings.Constants.mediaTypeImage,
                                    fileName = it2
                                )
                            }
                        })
                        selectedPhotosCount++
                    }
                    multiMediaAdapter?.notifyDataSetChanged()
                    Log.e(TAG, "image list size respnse---->:${viewModel.multiImagesList.size} ", )

                    if(viewModel.multiImagesList.size==0){
                        binding.uploadImageIcon.visibleView()
                        binding.multiMediaRv.goneView()
                    }else{
                        binding.uploadImageIcon.goneView()
                        binding.multiMediaRv.visibleView()
                    }

                 }
            }
        }
        viewModel.getUploadUrlsResponse.observe(this) {
            viewModel.awsList.clear()
            it.let {
                if (it?.status == AppStrings.Constants.success) {
                    Log.e("putUrl", "setObservers:-->getPutUrlsResponse ${it.data}")
                    val filesMap = it.data
                    val uploadTasks = mutableListOf<Call<ResponseBody>>()
                    val fileUploadList = mutableListOf<MultipleImagesListToBackend>()
                    AppMethods.showProgressBar(this)
                    viewModel.imageFileNameList.forEach {
                        var filepath = it.path
                        val filename = it.fileName
                        val name = filesMap?.get(filename)?.key //to send for backend
                        val mediaUrl = filesMap?.get(filename)?.uploadUrl
                        Log.e(TAG, "setObservers:filename-----> ${filename}", )
                        Log.e("TAG", "setObservers:mediaUrl------> ${mediaUrl}")
                        if (mediaUrl != null) {
                            val (call, fileUpload) = AppMethods.uploadFileApi(
                                url = mediaUrl,
                                filepath = filepath,
                                filename = name,
                                context = this
                            )
                            uploadTasks.add(call)
                            fileUploadList.add(fileUpload)
                        }
                    }

                    // Track all upload tasks
                    AppMethods.handleAllUploads(
                        uploadTasks, fileUploadList
                    ) { success, fileList ->
                        if (success) {
                            CLog.e(TAG, "setObservers: success", )
                             AppMethods.dismissProgressBar(this)
                            viewModel.multipleImagesListToBackend.clear()
                            viewModel.multipleImagesListToBackend.addAll(fileList)
                            // All uploads successful, process the fileList
                            if (fromActivity == AppStrings.ActivityFrom.addDetailsActivity) {
                                updateAssetApiCall()
                            } else {
                                createAssetApiCall()
                            }

                        } else {
                            // Handle upload failure
                        }
                    }

                } else {
                    AppMethods.showToast(
                        this, it?.message, AppStrings.SnackbarStatus.error
                    )
                }
            }
        }
    }

    private fun createAssetApiCall() {
        var jsonObject = JSONObject()
        jsonObject.put(AppStrings.InputData.mobileModel, viewModel.mobileModel.value)
        jsonObject.put(AppStrings.InputData.imei, viewModel.imei.value)
        jsonObject.put(AppStrings.InputData.buyerPrice, viewModel.buyerPrice.value?:0)
        jsonObject.put(AppStrings.InputData.sellerPrice, viewModel.sellerPrice.value)
        jsonObject.put(AppStrings.InputData.profit, viewModel.profit.value)
        jsonObject.put(AppStrings.InputData.status, viewModel.status.value)
        jsonObject.put(AppStrings.InputData.sellerName, viewModel.sellerName.value)
        jsonObject.put(AppStrings.InputData.sellerPhone, viewModel.sellerPhoneNumber.value)
        jsonObject.put(AppStrings.InputData.sellerAadhar, viewModel.sellerAadhar.value)
        jsonObject.put(AppStrings.InputData.sellerDate, viewModel.sellerDate.value)
        jsonObject.put(AppStrings.InputData.buyerName, viewModel.buyerName.value)
        jsonObject.put(AppStrings.InputData.buyerPhone, viewModel.buyerPhoneNumber.value)
        jsonObject.put(AppStrings.InputData.buyingDate, viewModel.buyingDate.value)
//        jsonObject.put(AppStrings.InputData.imageKeys, AppMethods.getStringArrayList(viewModel.multipleImagesListToBackend) )
        val imageKeys = AppMethods.getStringArrayList(viewModel.multipleImagesListToBackend)
        Log.d("Debug", "Image Keys: $imageKeys")
        jsonObject.put(AppStrings.InputData.imageKeys, imageKeys)
        viewModel.createAsset(jsonObject)
    }

    private fun updateAssetApiCall() {
        var jsonObject = JSONObject()
        jsonObject.put(AppStrings.InputData.mobileModel, viewModel.mobileModel.value)
        jsonObject.put(AppStrings.InputData.imei, viewModel.imei.value)
        jsonObject.put(AppStrings.InputData.buyerPrice, viewModel.buyerPrice.value)
        jsonObject.put(AppStrings.InputData.sellerPrice, viewModel.sellerPrice.value)
        jsonObject.put(AppStrings.InputData.profit, viewModel.profit.value)
        jsonObject.put(AppStrings.InputData.status, viewModel.status.value)
        jsonObject.put(AppStrings.InputData.sellerName, viewModel.sellerName.value)
        jsonObject.put(AppStrings.InputData.sellerPhone, viewModel.sellerPhoneNumber.value)
        jsonObject.put(AppStrings.InputData.sellerAadhar, viewModel.sellerAadhar.value)
        jsonObject.put(AppStrings.InputData.sellerDate, viewModel.sellerDate.value)
        jsonObject.put(AppStrings.InputData.buyerName, viewModel.buyerName.value)
        jsonObject.put(AppStrings.InputData.buyerPhone, viewModel.buyerPhoneNumber.value)
        jsonObject.put(AppStrings.InputData.buyingDate, viewModel.buyingDate.value)
//        jsonObject.put(AppStrings.InputData.imageKeys, JSONArray(viewModel.multipleImagesListToBackend) )
        val imageKeys = AppMethods.getStringArrayList(viewModel.multipleImagesListToBackend)
        Log.d("Debug", "Image Keys: $imageKeys")
        jsonObject.put(AppStrings.InputData.imageKeys, imageKeys)
        val deletedKeys = AppMethods.deleteMediaIds(viewModel.deletedMediaIds)

        jsonObject.put(AppStrings.InputData.deletedKeys, deletedKeys)
//        jsonObject.put(AppStrings.InputData.deletedKeys, JSONArray(viewModel.deletedMediaIds) )
        viewModel.updateAsset(jsonObject)
    }

    private fun onClicks() {
        binding.commonHeader.backIconIV.setOnSafeClickListener {
            finish()
        }

        binding.uploadImageIcon.setOnSafeClickListener {
            val photosCount = AppStrings.Constants.photosCount - selectedPhotosCount

            AppMethods.picSelectionDialog(
                this,
                resultLauncher,
                isMultipleImages = true,
                maxCount = photosCount,
                fromType = AppStrings.Strings.multipleImages,
                totalCount = totalImagesCount
            )
        }
        binding.sellerDate.setOnClickListener {
            // Disable the button to prevent multiple rapid clicks
            binding.sellerDate.isEnabled = false

            // Show the calendar dialog
            AppMethods.calendarDialog(
                activity = this,
                supportFragmentManager = this.supportFragmentManager,
                selectedDate = viewModel.sellerDate.value.toString(),
                isMobileBuyDate = true
            )

            // Re-enable the button after a short delay (1 second)
            binding.sellerDate.postDelayed({
                binding.sellerDate.isEnabled = true
            }, 1000) // Adjust the delay time as needed
        }

        binding.buyingDate.setOnClickListener {
            // Disable the button to prevent multiple rapid clicks
            binding.buyingDate.isEnabled = false

            // Show the calendar dialog
            AppMethods.calendarDialog(
                activity = this,
                supportFragmentManager = this.supportFragmentManager,
                isMobileBuyDate = false,
                selectedDate = viewModel.buyingDate.value.toString()

            )

            // Re-enable the button after a short delay (1 second)
            binding.buyingDate.postDelayed({
                binding.buyingDate.isEnabled = true
            }, 1000) // Adjust the delay time as needed
        }

        binding.statusEt.setOnClickListener {
            val popupMenu = PopupMenu(this, it)

            popupMenu.menu.add(getString(R.string.available))
            popupMenu.menu.add(getString(R.string.sold))
            popupMenu.menu.add(getString(R.string.pending))

            popupMenu.setOnMenuItemClickListener { item ->
//                binding.statusEt.text = item.title
                viewModel.status.value =
                    item.title.toString() // Or assign to LiveData if it's MutableLiveData<String>
                true
            }

            popupMenu.show()
        }


    }

    private fun initUI() {
        if (!imei.isNullOrEmpty()) {
            viewModel.getAssetById(imei!!)
        }
        if (fromActivity == AppStrings.ActivityFrom.addDetailsActivity) {
            binding.uploadTv.text = getString(R.string.update)
            binding.commonHeader.titleTV.text = getString(R.string.edit_mobile)
            binding.imeiEt.isFocusable = false
            binding.imeiEt.isClickable = false
        } else {
            binding.uploadTv.text = getString(R.string.upload)
            binding.imeiEt.isFocusable = true
            binding.imeiEt.isClickable = true
        }
        statusListData()
        initImagesAdapter()
        setScrollsForEditText()
    }

    private fun statusListData() {
        statusList.add(ListDataModel(name = "available"))
        statusList.add(ListDataModel(name = "sold"))
        statusList.add(ListDataModel(name = "pending"))
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setScrollsForEditText() {
        binding.customerAddressEt.setOnTouchListener(View.OnTouchListener { v, event ->
            if (binding.customerAddressEt.hasFocus()) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_SCROLL -> {
                        v.parent.requestDisallowInterceptTouchEvent(false)
                        return@OnTouchListener true
                    }
                }
            }
            false
        })
    }

    private fun initImagesAdapter() {

        multiMediaAdapter = MultiMediaAdapter(this, viewModel.multiImagesList)
        var linearLayoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        )
        binding.multiMediaRv.layoutManager = linearLayoutManager
        binding.multiMediaRv.adapter = multiMediaAdapter
        multiMediaAdapter!!.onItemClicked { from, pos, model ->

            if (from == 1) {  //for delete
                 /*viewModel.multiImagesList.removeAt(pos)
                 viewModel.awsList.removeAt(pos)
*/

                if (pos >= 0 && pos < viewModel.multiImagesList.size) {
                    viewModel.multiImagesList.removeAt(pos)
                }
                if (pos >= 0 && pos < viewModel.awsList.size) {
                    viewModel.awsList.removeAt(pos)
                    viewModel.imageFileNameList.removeAt(pos)
                }


                if (viewModel.multiImagesList.size == 0) {
                    viewModel.selectedImagePath.value = ""
                    binding.uploadImageIcon.visibleView()

                }
                selectedPhotosCount--
                Log.e(TAG, "initImagesAdapter: image list size -------->${viewModel.multiImagesList.size}", )
                if(fromActivity==AppStrings.ActivityFrom.addDetailsActivity){
                    if (model.fileName.isNotEmpty()) {
                        viewModel.deletedMediaIds.add(model.fileName)
                    }
                }
                multiMediaAdapter!!.notifyDataSetChanged()
                
            } else if (from == 2) { //for add
                binding.uploadImageIcon.performClick()
            }
        }
    }

    override fun getPersistentView(): ActivityAddMobileDetailsBinding {
        return ActivityAddMobileDetailsBinding.inflate(layoutInflater)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val typeFrom = data.getStringExtra(AppStrings.Types.type_from).toString()
                    val multiImages = data.getBooleanExtra(AppStrings.IntentData.multiImages, false)
                    if (typeFrom == AppStrings.Types.imageCamera) {
                        val userImage =
                            data.getStringExtra(AppStrings.IntentData.imageFile).toString()

                        /*
                                                val intent = Intent(this, CropActivity::class.java)
                                                intent.putExtra(
                                                    AppStrings.IntentData.imageFile, userImage
                                                )
                                                cropResultLauncher.launch(intent)*/
                        val imageUri =
                            Uri.parse(data.getStringExtra(AppStrings.IntentData.selectedImageUri)) // This gets the URI of the image

                        CLog.e("TAG", "imageUri: ${imageUri}")
                        val intent = Intent(this, CropActivity::class.java)
                        intent.putExtra(AppStrings.IntentData.imageFile, userImage)
                        intent.putExtra(
                            AppStrings.IntentData.isRectangleCrop, true
                        )
                        intent.putExtra(
                            AppStrings.IntentData.isMultipleImages, multiImages
                        )
                        //                viewModel.selectedImage = File(userImage)
                        //                viewModel.selectedImagePath.value = userImage
                        cropResultLauncher.launch(intent)
                        /*  imageUri?.let {
                              checkImageSizeAndProceed(it, userImage,multiImages)
                          }*/

                    } else if (typeFrom == AppStrings.Types.imageGallery) {

                        var list =
                            data.getStringArrayListExtra(AppStrings.IntentData.imageFileList) as ArrayList<String>
                        CLog.e("TAG", "list size:@@@@${list.size} ")
                        Log.e(TAG, "list size: ${list.size}", )

                         // Clear previous images if needed
                        if (selectedPhotosCount == 0) {
                            viewModel.multiImagesList.clear()
                            viewModel.awsList.clear()
                            viewModel.imageFileNameList.clear()
                        }

                        list.forEachIndexed { index, filePath ->
                            val file = File(Uri.parse(filePath).path ?: "")
                            if (file.exists() && file.canRead()) {
                                CLog.e("FileCheck", "File Exists: ${file.absolutePath}")
                                Log.e(TAG, "image list size result launcher---->:${viewModel.multiImagesList.size} ", )
                                viewModel.multiImagesList.add(
                                    MultiMediaModel(
                                        path = file.absolutePath,
                                        mediaType = AppStrings.Constants.mediaTypeImage)
                                )
                                viewModel.awsList.add(file.name)
                                //list for comparning data in the response
                                viewModel.imageFileNameList.add(
                                    MultiMediaModel(path = file.absolutePath, fileName = file.name)
                                )
                            } else {
                                CLog.e(
                                    "FileCheck",
                                    "File does not exist or cannot be read: ${file.absolutePath}"
                                )
                            }

                        }

                        selectedPhotosCount = selectedPhotosCount + list.size
                        binding.uploadImageIcon.goneView()
                        binding.multiMediaRv.visibleView()

                    }
                    multiMediaAdapter!!.notifyDataSetChanged()

                }
            }
        }

    private fun checkImageSizeAndProceed(uri: Uri, userImage: String, isMultipleImages: Boolean) {
        try {
//            val inputStream = contentResolver.openInputStream(uri)
//            val fileSize = inputStream?.available()?.toLong() ?: 0
            val fileSize = AppMethods.getFileSizeFromUri(this, uri)
            CLog.e("TAG", "checkImageSizeAndProceed: filesize${fileSize}")

            if (fileSize > AppStrings.Constants.MAX_IMAGE_SIZE) { // 50 MB limit
                AppMethods.showToast(
                    this, getString(R.string.image_size_limit), AppStrings.SnackbarStatus.error
                )
            } else {
                //compression
                val compressedImageFile = AppMethods.compressImage(this, uri)

                val intent = Intent(this, CropActivity::class.java)
                intent.putExtra(AppStrings.IntentData.imageFile, compressedImageFile.absolutePath)
                intent.putExtra(
                    AppStrings.IntentData.isRectangleCrop, true
                )
//                viewModel.selectedImage = File(userImage)
//                viewModel.selectedImagePath.value = userImage
                intent.putExtra(
                    AppStrings.IntentData.isMultipleImages, isMultipleImages
                )

                cropResultLauncher.launch(intent)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private var cropResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    viewModel.multiImagesList.add(
                        MultiMediaModel(
                            path = data.getStringExtra(AppStrings.IntentData.imageFile).toString(),
                            mediaType = AppStrings.Constants.mediaTypeImage,
                            )
                    )

                    viewModel.awsList.add(
                        File(data.getStringExtra(AppStrings.IntentData.imageFile)).name
                    )
                    viewModel.imageFileNameList.add(
                        MultiMediaModel(
                            path = data.getStringExtra(
                                AppStrings.IntentData.imageFile
                            ).toString(),
                            fileName = File(data.getStringExtra(AppStrings.IntentData.imageFile)).name
                        )
                    )

//                    binding.selectedImageIV.visibility = View.VISIBLE
                    binding.uploadImageIcon.visibility = View.GONE
                    multiMediaAdapter!!.notifyDataSetChanged()

                    selectedPhotosCount++
                    totalImagesCount++
                }
            }
        }

    override fun dateFrom(from: Boolean, date: String, type: String?) {
        if (from) {

            viewModel.sellerDate.value = date

        } else {
            viewModel.buyingDate.value = date

        }
    }

}