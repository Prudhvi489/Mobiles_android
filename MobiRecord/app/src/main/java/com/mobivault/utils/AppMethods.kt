    package com.mobivault.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.text.Html
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.Shimmer
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mobivault.BuildConfig
import com.mobivault.R
import com.mobivault.databinding.DialogPicselectionBinding
import com.mobivault.di.AppModule
import com.mobivault.interfaces.CalenderInterface
import com.mobivault.utils.dialogs.DialogUtils
import com.tuneconnect.network.RestApi
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import com.facebook.shimmer.ShimmerDrawable
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.mobivault.databinding.AlertLayoutBinding
import com.mobivault.databinding.MobileOptionsBottomSheetBinding
import com.mobivault.interfaces.BottomSheet
import com.mobivault.ui.addmobiles.model.MultipleImagesListToBackend
import com.mobivault.ui.mobiledetails.model.Asset
import com.mobivault.utils.cameraGallery.CameraActivity
import com.mobivault.utils.cameraGallery.multipleSelectionGallery.GalleryImagesViewActiivty
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import org.json.JSONArray

import java.io.IOException
import java.io.InputStream
import java.net.SocketTimeoutException
import java.net.URLConnection
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

    class AppMethods {

    companion object {
        var bottomSheetInterface: BottomSheet? = null

        var token = ""
        var readImagePermission = ""
        var writeImagePermission = ""
        var readVideoPermission = ""
        var writeVideoPermission = ""
        var progressBar: Dialog? = null

        private var selectedDateInMillis: Long? = null
        private const val REQUEST_OVERLAY_PERMISSION = 1001
        val TAG= "APPMethods"

        private fun checkDecimal(value: Double): Boolean {
            return value % 1 != 0.0
        }
        fun handleResponse(response: String): String {
            try {
                val jsonObject = JSONObject(response)
                // val status = jsonObject.getInt("status")
                val message = jsonObject.getString("message")
                return message
            } catch (e: Exception) {

            }
            return ""
        }
        suspend fun refreshAccessToken(activity: Activity? = null, callback: (Boolean) -> Unit) {

            var sm = SessionManager(activity!!.applicationContext)

            CLog.e(TAG, "refreshTokenApi: @@@@@@@@@@")
            CLog.e(TAG, "refreshTokenApi: sm =${sm.getData(AppStrings.SessionValues.userId, "")}")



            getRetrofitInstance()

            val restApi = getRetrofitInstance().create(RestApi::class.java)
            var headers = HashMap<String, String>()
            headers[AppStrings.Constants.authorization] =
                sm.getData(
                    AppStrings.SessionValues.refreshToken,
                    sm.getData(AppStrings.SessionValues.refreshToken, "")
                )

            var response = restApi.refreshToken(
                headers
            )
            response.enqueue(object : Callback<Any?> {
                @SuppressLint("SuspiciousIndentation")
                override fun onResponse(call: Call<Any?>, response: Response<Any?>) {
                    if (response.code() == 200 || response.code() == 201 || response.code() == 202 || response.code() == 204) {
                        response.body()?.let {
                            CLog.e(TAG, "onResponse: response :$response")
                            val rawData = Gson().toJsonTree(response.body()).asJsonObject.toString()
                            val jsonObject = JSONObject(rawData)
                            CLog.e(TAG, "onResponse: refresh token -> ${jsonObject}")
                            var data = jsonObject.getJSONObject(AppStrings.ResponseData.data)
                            sm.saveData(
                                AppStrings.SessionValues.accessToken,
                                data.getString(AppStrings.SessionValues.accessToken)
                            )
                            sm.saveData(
                                AppStrings.SessionValues.refreshToken,
                                data.getString(AppStrings.SessionValues.refreshToken)
                            )
                            // Notify callback that token has been refreshed
                            callback(true)
                            CLog.e(TAG, "Ajay: :::::::: ")
                        }
                    } else if (response.code() == 401) {
                        CLog.e(TAG, "onResponse: errorcode:${response.errorBody().toString()}")
                        //  callback(false)
                        if (response.errorBody() != null) {
                            //  val json = JSONObject(response.errorBody()!!.string())
                            // Notify callback that token refresh failed
                            DialogUtils.unAuthorizedDialog(
                                activity,
                                sm,
                                handleResponse(
                                    response.errorBody()?.string() ?: "Some Exception Occurred"
                                )
                            )
                            //  callback(false)

                        }
                    } else {
                        CLog.e(TAG, "onResponse: errorcode:${response.code()}")
                    }

                }

                override fun onFailure(call: Call<Any?>, t: Throwable) {
                    CLog.e(TAG, "onFailure: @@@@")
                    // Notify callback that token refresh failed
                    callback(false)

                }
            })


        }

        fun customAlertWithOK(
            context: Context, msg: String, title: String? = ""/*, callbacks: (String) -> Unit*/
        ) {

            val alertDialog = AlertDialog.Builder(context).create()
            val alertView = AlertLayoutBinding.inflate(LayoutInflater.from(context))
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertDialog.setCanceledOnTouchOutside(false)
            alertDialog.window?.attributes?.windowAnimations = R.style.popup_window_animation

            if (title?.isNotEmpty() == true) {
                alertView.titleTV.text = title
            } else {
                alertView.titleTV.goneView()
            }
            alertView.msgTV.setText(
                Html.fromHtml(msg), TextView.BufferType.SPANNABLE
            )

            alertView.okBTN.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.setView(alertView.root)
            alertDialog.setCancelable(true)
            if (!alertDialog.isShowing) {
                alertDialog.show()
            }


////            val alertDialog = AlertDialog.Builder(context).create()
//            val alertDialog = Dialog(context)
//            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            /*val alertView: AlertLayoutBinding = DataBindingUtil.inflate(
//                LayoutInflater.from(context), R.layout.alert_layout, null, false
//            )*/
//            val alertView: AlertLayoutBinding = AlertLayoutBinding.inflate(LayoutInflater.from(context))
//            val width = (context.resources.displayMetrics.widthPixels * 0.70).toInt()
//            val height = (context.resources.displayMetrics.widthPixels * 0.45).toInt()
////            alertDialog.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
//            alertDialog.window?.attributes?.windowAnimations = R.style.popup_window_animation
//            alertDialog.setCanceledOnTouchOutside(false)
//
//            if (title?.isNotEmpty() == true) {
//                alertView.titleTV.text = title
//            } else {
//                alertView.titleTV.goneView()
//            }
//            alertView.msgTV.setText(
//                Html.fromHtml(msg), TextView.BufferType.SPANNABLE
//            )
//
//            alertView.okBTN.setOnClickListener {
//                alertDialog.dismiss()
////                callbacks("OK")
//            }
//            alertDialog.setContentView(alertView.root)
//            alertDialog.setCancelable(true)
//            if (!alertDialog.isShowing) {

//                alertDialog.show()
//            }
        }

        fun picSelectionDialog(
            activity: Activity,
            resultLauncher: ActivityResultLauncher<Intent>,
            type: String = "",
            from: String =Constants.image.type,
            isMultipleImages: Boolean = false,
            maxCount: Int = 0,
            fromType: String = "",
            totalCount: Int? = null,
        ) {
            CLog.e(
                "",
                "picSelectionDialog -> from : $from , max count : $maxCount, multiple selection : $isMultipleImages, total count: $totalCount"
            )



            if (maxCount != 0) {
                if (PermissionUtil.checkStoragePermission(activity)) {
                    val dialog = Dialog(activity)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    val dialogBinding: DialogPicselectionBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(activity), R.layout.dialog_picselection, null, false
                    )
                    dialog.setContentView(dialogBinding.root)
                    val width = (activity.resources.displayMetrics.widthPixels * 0.70).toInt()
                    val height = (activity.resources.displayMetrics.widthPixels * 0.45).toInt()
                    dialog.window?.setLayout(width, height)
                    dialog.show()
                    dialog.setCancelable(true)
                    dialogBinding.cameraTV.setOnClickListener {
                        if (PermissionUtil.checkCameraPermission(activity)) {
//                            val intent = Intent(activity, PicSelectActivity::class.java)


                            /*intent.putExtra(
                         AppStrings.IntentData.type_from, AppStrings.Types.imageCamera


                     )*/

                            if (from.equals(Constants.image.type)){
                                val intent = Intent(activity, PicSelectActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                                intent.putExtra(
                                    AppStrings.IntentData.isMultipleImages, isMultipleImages
                                )

                                if (from.equals(Constants.image.type)) {
                                    intent.putExtra(
                                        AppStrings.IntentData.type_from, AppStrings.Types.imageCamera
                                    )
                                } else if (from.equals(Constants.imagevideo.type)) {
                                    intent.putExtra(
                                        AppStrings.IntentData.type_from,
                                        AppStrings.Types.imageVideoGallery
                                    )
                                } else {
                                    intent.putExtra(
                                        AppStrings.IntentData.type_from, AppStrings.Types.videoCamera
                                    )
                                }
                                intent.putExtra(AppStrings.IntentData.type, type)
                                resultLauncher.launch(intent)
                                dialog.dismiss()

                            }else{


                                val intent = Intent(activity, CameraActivity::class.java)
//                                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                                intent.putExtra(
                                    AppStrings.IntentData.type_from, AppStrings.Types.imageCamera
                                )
                                intent.putExtra(
                                    AppStrings.IntentData.isMultipleImages, isMultipleImages
                                )
                                resultLauncher.launch(intent)
                                dialog.dismiss()
                            }

                        } else {
                            activity.requestPermissions(
                                arrayOf(
                                    Manifest.permission.CAMERA
                                ), AppStrings.Constants.permissions
                            )
                        }
                    }

                    dialogBinding.galleryTV.setOnClickListener {

                                 val intent = Intent(
                                    activity, GalleryImagesViewActiivty::class.java
                                ) // multiple images
                                intent.putExtra(AppStrings.IntentData.maxCount, maxCount)
                                     intent.putExtra(
                                        AppStrings.IntentData.type_from,
                                        AppStrings.Types.imageGallery
                                    )

                                intent.putExtra(
                                    AppStrings.IntentData.isMultipleImages, isMultipleImages
                                )
                                resultLauncher.launch(
                                    intent
                                )

                        dialog.dismiss()
                    }
                    dialogBinding.cancelCV.setOnClickListener {
                        dialog.dismiss()
                    }
                }
                else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14 and above
                        CLog.e(TAG, "requestWritePermission UPSIDE_DOWN_CAKE")
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                activity, Manifest.permission.READ_MEDIA_IMAGES
                            )
                        ) {
                            showSettingsDialog(activity)
                        } else {
                            activity.requestPermissions(
                                arrayOf(
                                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                                    Manifest.permission.READ_MEDIA_IMAGES,
                                    Manifest.permission.READ_MEDIA_VIDEO
                                ), 11
                            )
                        }

                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        // Request permissions for devices running Android 13
                        readImagePermission = Manifest.permission.READ_MEDIA_IMAGES
                        writeImagePermission = Manifest.permission.READ_MEDIA_IMAGES
                        readVideoPermission = Manifest.permission.READ_MEDIA_VIDEO
                        writeVideoPermission = Manifest.permission.READ_MEDIA_VIDEO
                        activity.requestPermissions(
                            arrayOf(
                                writeImagePermission,
                                readImagePermission,
                                readVideoPermission,
                                writeVideoPermission,
                                Manifest.permission.CAMERA
                            ), AppIntegers.ResultCode.permissions
                        )
                    } else {
                        readImagePermission = Manifest.permission.READ_EXTERNAL_STORAGE
                        writeImagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
                        readVideoPermission = Manifest.permission.READ_EXTERNAL_STORAGE
                        writeVideoPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE

                        activity.requestPermissions(
                            arrayOf(
                                writeImagePermission,
                                readImagePermission,
                                readVideoPermission,
                                writeVideoPermission,
                                Manifest.permission.CAMERA
                            ), AppIntegers.ResultCode.permissions
                        )
                    }

                }
            } else {
                DialogUtils.alertDialog(activity, null,
                    activity.getString(R.string.your_limit_is_over))
            }
        }

        fun getToken(
            sm: SessionManager? = null, defaultToken: Boolean = false
        ): HashMap<String, String> {
            val headers = HashMap<String, String>()

            if (defaultToken) {
                headers[AppStrings.Constants.authorization] = AppStrings.Constants.defaultToken
            } else {
//                headers[AppStrings.Constants.authorization] =
//                    sm?.getData<String>(AppStrings.SessionValues.accessToken, "").toString()
                val token = sm?.getData<String>(AppStrings.SessionValues.accessToken, "") ?: ""
//                headers[AppStrings.Constants.authorization] = "Bearer $token"
                headers[AppStrings.Constants.authorization] = "$token"

            }
            return headers

        }

        fun bottomSheetDialog(
            context: Context, model: Asset
        ) {
            val bottomSheetBinding =
                MobileOptionsBottomSheetBinding.inflate(LayoutInflater.from(context))
            val bottomSheetDialog = BottomSheetDialog(context, R.style.ModalBottomSheetStyle)
            bottomSheetInterface = context as BottomSheet
            // Configure the bottom sheet dialog
            bottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            bottomSheetDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            bottomSheetDialog.setContentView(bottomSheetBinding.root)
            bottomSheetDialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
            )

            bottomSheetBinding.editTv.setOnClickListener {
                bottomSheetInterface!!.editTvClick(bottomSheetDialog, model)
            }
            bottomSheetBinding.deleteTv.setOnClickListener {
                bottomSheetInterface!!.deleteTvClick(bottomSheetDialog, model)
            }
            bottomSheetBinding.cancelTv.setOnClickListener {
                bottomSheetDialog.cancel()
            }

            // Set up click listeners or any other customization
            // Show the bottom sheet
            bottomSheetDialog.show()
        }



        /*fun permissionAlert(context: Context, message: String) {
            // Inflate the custom dialog layout using data binding
            val binding = DataBindingUtil.inflate<DialogCustomOverlayPermissionBinding>(
                LayoutInflater.from(context),
                R.layout.dialog_custom_overlay_permission,
                null,
                false
            )

            // Set the dialog title and message
            binding.dialogTitle.text = context.getString(R.string.permission_required)
            binding.dialogMessage.text = message
            binding.btnPositive.text = context.getString(R.string.app_settings)
            binding.btnNegative.text = context.getString(R.string.cancel)

            // Create the dialog using AlertDialog.Builder
            val dialog = AlertDialog.Builder(context)
                .setView(binding.root)
                .create() // Create the AlertDialog instance
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // Apply configurations and show the dialog
            dialog?.apply {
                setContentView(binding.root) // Set the custom view
                window?.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
                ) // Set dialog width to MATCH_PARENT and height to WRAP_CONTENT
                setCancelable(true) // Make the dialog cancellable
                show() // Display the dialog
            }
            binding.btnPositive.setOnClickListener {
                dialog.dismiss()
                openAppSettings(context)
            }
            binding.btnNegative.setOnClickListener {
                dialog.dismiss()
            }
        }repository*/

        @SuppressLint("UseCompatLoadingForDrawables", "RestrictedApi")
        fun showToast(
            activity: Activity,
            message: String? = null,
            status: String = AppStrings.SnackbarStatus.error,

            ) {
            try {
                val parentLayout = activity.findViewById<View>(android.R.id.content)
                val snackbar = Snackbar.make(parentLayout, "", Snackbar.LENGTH_LONG)

                // Get the Snackbar's layout view
                val layout = snackbar.view as Snackbar.SnackbarLayout
                // Hide the text
                val textView =
                    layout.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                textView.visibility = View.INVISIBLE
                val sbView = snackbar.view
                sbView.setBackgroundColor(activity.resources.getColor(R.color.transparent, null))
                val mInflater = LayoutInflater.from(activity)
                // Inflate our custom view
                val snackView: View = mInflater.inflate(R.layout.my_snackbar, null)
                // Configure the view
                val background = snackView.findViewById<LinearLayout>(R.id.backgroundLl)
                val textViewTop = snackView.findViewById<TextView>(R.id.messageTv)
                val image = snackView.findViewById<ImageView>(R.id.errorIv)
                if (status == AppStrings.SnackbarStatus.success) {
                    background.background =
                        activity.resources.getDrawable(R.drawable.rectangle_success, null)
                    image.setImageDrawable(
                        activity.resources.getDrawable(
                                R.drawable.ic_success, null
                        )
                    )
                } /*else if (status == AppStrings.SnackbarStatus.informative || status == AppStrings.SnackbarStatus.delete) {
                    background.background =
                        activity.resources.getDrawable(R.drawable.rounded_rectangle_black, null)
                    image.setImageDrawable(
                        activity.resources.getDrawable(
                            R.drawable.ic_success, null
                        )
                    )
                }*/ else if (status == AppStrings.SnackbarStatus.error) {
                    background.background =
                        activity.resources.getDrawable(R.drawable.rectangle_error, null)
                    image.setImageDrawable(
                        activity.resources.getDrawable(
                            R.drawable.ic_error, null
                        )
                    )
                }
                Handler(Looper.myLooper()!!).postDelayed(
                    { setScaleAnimation(image) }, 500
                )
                textViewTop.text = message
                //If the view is not covering the whole snackbar layout, add this line
                layout.setPadding(0, 0, 0, 0)

                // Add the view to the Snackbar's layout
                layout.addView(snackView, 0)
                // Show the Snackbar
                snackbar.show()
            } catch (e: Exception) {
            }
        }
        fun setScaleAnimation(iv: ImageView) {
            val scale = ScaleAnimation(
                0f,
                1f,
                0f,
                1f,
                ScaleAnimation.RELATIVE_TO_SELF,
                .5f,
                ScaleAnimation.RELATIVE_TO_SELF,
                .5f
            )
            scale.duration = 500
            scale.interpolator = OvershootInterpolator()
            iv.startAnimation(scale)
        }



        /*fun returnMessage(type: String, activity: Activity): String {

            if (type == AppStrings.ValidationTypes.invalidMobileNumber) {
                return activity.resources.getString(R.string.invalidMobileNumber)
            }
            return ""
        }*/
/*
        fun loadcircleCropImage(view: RoundedCornerImageView, url: String) {
            Log.e("TAG", "loadImageInGlide:url= ${url}")
            Glide.with(view.context).load(url).placeholder(shimmerEffectForGlide(view.context))
                .error(R.drawable.avatar_ic) // Optional: Error drawable if Glide fails to load the image
                .circleCrop() // Apply the circleCrop transformation for a rounded image
                .into(view)


        }*/

      /*  fun getDeviceId(activity: Context, viewModel: BaseViewModel): String {
            viewModel.validateNetwork {
                val sm = SessionManager(activity)
                try {
                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.e(
                                ContentValues.TAG,
                                "Fetching FCM registration token failed",
                                task.exception
                            )
                            return@OnCompleteListener
                        }
                        // Get new FCM registration token
                        token = task.result.toString()
                        sm.saveData(AppStrings.SessionValues.deviceId, token)
                        Log.e(ContentValues.TAG, "getDeviceId: token:$token")
                    })
                } catch (e: Exception) {
                    Log.e("TAG", "getDeviceId: Exception ->" + e)
                }
            }
            return token
        }*/

       /* fun encodeCredentials(clientId: String, clientSecret: String): String {
            val credentials = "$clientId:$clientSecret"
            return android.util.Base64.encodeToString(
                credentials.toByteArray(),
                android.util.Base64.NO_WRAP
            )
        }
*/
        fun getRetrofitInstance(baseUrl: String?=""): Retrofit {
            lateinit var retrofit: Retrofit
            val gson = GsonBuilder().setLenient().create()
            retrofit = Retrofit.Builder().baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson)).client(AppModule.http())
                .build()
            return retrofit
        }





        /*fun loadImageInGlide(view: AppCompatImageView, url: String) {

            Log.e("TAG", "loadImageInGlide:url= ${url}")

            val options = RequestOptions()
                .centerCrop()
                .placeholder(shimmerEffectForGlide(view.context)) // Placeholder while loading
                 .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL)

            Glide.with(view.context)
                .load(url)
                .apply(options)
                .placeholder(shimmerEffectForGlide(view.context))
                .into(view)

        }*/


        fun <T> parseJson(json: String, type: Class<T>): T {
            val gson = Gson()
            return gson.fromJson(json, type)
        }

        fun onBackPressed(context: Context) {
            val intent = Intent()
            intent.action = Intent.ACTION_MAIN
            intent.addCategory(Intent.CATEGORY_HOME)
            context.startActivity(intent)
        }

        fun showKeyboard(context: Context, editText: AppCompatEditText) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }

        fun hideKeyboard(activity: Activity, editText: AppCompatEditText) {
// Get the InputMethodManager instance
            val inputMethodManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
// Hide the keyboard
            inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
        }

        fun showDefaultPermissionAlertForRecordAudio(context: Context) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Permission Required")
            builder.setMessage(
                context.getString(
                    R.string.the_camera_and_microphone_permissions_is_required_to_take_photo
                )
            )
            builder.setPositiveButton("App Settings") { _, _ ->
                // Open the app settings page so the user can enable the permission manually
                openAppSettings(context)
            }
            builder.setNegativeButton("Cancel") { _, _ ->
                // Handle the Cancel button click if needed
            }
            builder.setCancelable(false)
            builder.show()
        }

        fun openAppSettings(context: Context) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            context.startActivity(intent)
        }

        fun booleanToInt(value: Boolean): Int {
            return if (value) 1 else 0
        }

        fun calendarDialog(
            activity: Activity? = null,
            fragment: Fragment? = null,
            supportFragmentManager: FragmentManager,
            isMobileBuyDate: Boolean = false,
            mobileBuyDate: String? = null,
            type: String? = null,
            selectedDate: String? = null
        ) {
            CLog.e(TAG, "calendarDialog: fromDate =${mobileBuyDate}")
            val calenderInterface: CalenderInterface
            calenderInterface = if (fragment != null) {
                fragment as CalenderInterface
            } else {
                activity as CalenderInterface
            }
            val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")) // Set time zone to UTC
            val today = MaterialDatePicker.todayInUtcMilliseconds()

            cal.timeInMillis = today
            cal.set(cal[Calendar.YEAR], cal[Calendar.MONTH], cal[Calendar.DATE])
            var startFrom = cal.timeInMillis

            if (!mobileBuyDate.isNullOrEmpty() && !isMobileBuyDate) {
                // Parse the input date string
                val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                dateFormatter.timeZone = TimeZone.getTimeZone("UTC") // Set time zone to UTC
                val startDate = dateFormatter.parse(mobileBuyDate)

                // Set the calendar instance to the parsed date
                cal.time = startDate
                startFrom = cal.timeInMillis
            }

            val dateValidator: CalendarConstraints.DateValidator =
                DateValidatorPointForward.from(startFrom)

            val constraintsBuilderRange =
                CalendarConstraints.Builder().setStart(startFrom).setValidator(dateValidator)
                    .build()

            // Initialize MaterialDatePicker with initial selection if fromDate is not null or empty
            val builder = MaterialDatePicker.Builder.datePicker().setCalendarConstraints(
                constraintsBuilderRange
            )

            if (!selectedDate.isNullOrEmpty()) {
                // Parse the input date string
                val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                dateFormatter.timeZone = TimeZone.getTimeZone("UTC") // Set time zone to UTC
                val startDate = dateFormatter.parse(selectedDate)
                builder.setSelection(startDate!!.time)
                if (startDate != null) {
                    builder.setSelection(startDate.time)
                } else {
                    // Handle the case where parsing failed
                    CLog.e(TAG, "Failed to parse selectedDate: $selectedDate")
                }
            }

            val dateRange = builder.build()

            dateRange.show(supportFragmentManager, "DATE_PICKER")

            dateRange.addOnPositiveButtonClickListener {
                // formatting date in dd-mm-yyyy format.
                val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
                val date = dateFormatter.format(Date(it))

                calenderInterface.dateFrom(isMobileBuyDate, date, type)
            }

            dateRange.addOnNegativeButtonClickListener {
                // Handle negative button click
            }

            dateRange.addOnCancelListener {
                // Handle cancel
            }
        }

            fun saveBitmapIntoSdcardFromCropPicture(
                activity: Activity, bitmap: Bitmap, filename: String
            ): String? {
                /*
         * check the path and create if needed
         */
                val baseDirectory: File = createCropPictureAlbumDirectory(activity)
                try {
                    Date()
                    var out: OutputStream? = null
                    val file = File(
                        baseDirectory, "/" + activity.getString(R.string.app_name) + filename
                    )
                    out = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out)
                    out.flush()
                    out.close()
                    // Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                    return file.absolutePath
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                return null
            }



        private fun createCropPictureAlbumDirectory(activity: Activity): File {
            val extStorageDirectory: String = createBaseDirectory(activity).absolutePath ?: ""
            val storageDir = File(extStorageDirectory + "/" + activity.getString(R.string.app_name))
            if (storageDir.mkdir()) {
                println("Directory created")
            } else {
                println("Directory is not created or exists")
            }
            return storageDir
        }

        private fun createBaseDirectory(activity: Context): File {
            val extStorageDirectory: String = createDirectoryPath(activity)
            val storageDir = File(
                extStorageDirectory + "/" + activity.getString(
                    R.string.app_name
                )
            )
            if (storageDir.mkdir()) {
                println("Directory created")
            } else {
                println("Directory is not created or exists")
            }
            return storageDir
        }

        fun createDirectoryPath(context: Context): String {
            val folder = File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM).toString())
            folder.mkdirs()
            return folder.absolutePath
        }
        fun isValidEmail(email: String?): Boolean {
            val emailPattern = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}"
            val pattern = Pattern.compile(emailPattern)
            val matcher = pattern.matcher(email)
            return matcher.matches()
        }
        fun isValidPassword(password: String): Boolean {
            // Regex for strong password validation
            val passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#\$%^&+=!])(?=\\S+\$).{8,}\$"
            return password.matches(passwordRegex.toRegex())
        }
        fun arePasswordsMatched(password: String, confirmPassword: String): Boolean {
            return password == confirmPassword
        }
        fun showhidePwd(context: Context, editText: TextInputEditText, hideStatus: Boolean) {
            editText.isLongClickable = false
            val cursorPosition = editText.selectionStart // Preserve cursor position

            if (hideStatus) {
                editText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                editText.transformationMethod = null
                editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null, null, ContextCompat.getDrawable(context, R.drawable.show_password), null
                )
            } else {
                editText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                editText.transformationMethod = PasswordTransformationMethod.getInstance()
                editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null, null, ContextCompat.getDrawable(context, R.drawable.hide_password), null
                )
            }

            editText.setSelection(cursorPosition) // Restore cursor position
            editText.requestFocus()
        }

        fun convertStrDateToStrDate(
            date1: String? = "", dateFormat1: String? = "", dateFormat2: String? = ""
        ): String? {
            Log.e(
                TAG,
                "convertStrDateToStrDate() called with: date1 = $date1, dateFormat1 = $dateFormat1, dateFormat2 = $dateFormat2"
            )
            if (date1?.isEmpty() == true) {
                return ""
            }
            val inputFormat = SimpleDateFormat(dateFormat1, Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(date1)

            val outputFormat = SimpleDateFormat(dateFormat2, Locale.getDefault())
            outputFormat.timeZone = TimeZone.getDefault() // Convert to local time zone

            return outputFormat.format(date!!)
        }



        fun getImagePath(url: String): String {
            return BuildConfig.SERVER_URL + url
        }

/*
        fun getDateId(format: String?): String? {
            val dateFormat = SimpleDateFormat(format)
            return dateFormat.format(Timestamp(Date()).toDate())
        }
*/

        fun getFormattedNumber(number: Int): String {
            return if (number > 99) {
                "99+"
            } else {
                number.toString()
            }
        }


        /*fun loadImageOnGlide(
            view: RoundedCornerImageView,
            url1: String,
        ) {
            try {

                var url = BuildConfig.SERVER_URL + url1

                Glide.with(view.context).load(url).placeholder(shimmerEffectForGlide(view.context))
                  .centerCrop()
                    .into(view)
            } catch (e: Exception) {

            }

        }
*/

       /* fun formatTime(timestamp: Timestamp): String {
            // Convert Timestamp to milliseconds since epoch
            val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
            val date = Date(milliseconds)

            // Create a SimpleDateFormat for the time format without leading zero in the hour
            val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
            return sdf.format(date)
        }
*/




        fun redirectToWeb(url: String, context: Context) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            context.startActivity(intent)
        }

       /* fun shimmerEffectForGlide(context: Context): ShimmerDrawable {
            val shimmer = Shimmer.ColorHighlightBuilder().setDuration(2000).setBaseAlpha(0.9f)
                .setHighlightAlpha(0.93f).setWidthRatio(1.5f)
                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT).setAutoStart(true)
                //.setBaseColor(ContextCompat.getColor(context, android.R.color.darker_gray))
                .setBaseColor(
                    ContextCompat.getColor(
                        context, R.color.shimmer_placeholder
                    )
                ) // Replace with your custom highlight color
                .setHighlightColor(ContextCompat.getColor(context, android.R.color.white)).build()

            val shimmerDrawable = ShimmerDrawable().apply {
                setShimmer(shimmer)
            }
            return shimmerDrawable
        }*/




        fun copyTextToClipboard(
            context: Context,
            text: String,
            message: String = "Text copied to clipboard"
        ) {
            // Get the clipboard manager
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            // Create a ClipData object
            val clip = ClipData.newPlainText("Copied Text", text)

            // Set the ClipData to the clipboard
            clipboard.setPrimaryClip(clip)

            // Notify the user
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        fun isValidUserName(input: String): Boolean {
            // Regular expression to allow alphabets, numbers, and spaces
            val regex = "^[a-zA-Z0-9 ]*$".toRegex()
            return regex.matches(input)
        }



        fun convertDateFormat(inputDate: String): String? {
            // Define the input date format
            val inputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

            // Define the output date format
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            return try {
                // Parse the input date string
                val date = inputFormat.parse(inputDate)

                // Format the date into the desired output format
                date?.let { outputFormat.format(it) }
            } catch (e: ParseException) {
                // Handle parse exception if the input date is not in the expected format
                e.printStackTrace()
                null
            }
        }


        fun clearAllNotifications(context: Context) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()
        }

        fun convertDateFormat1(inputDate: String): String? {
            // Define the input date format
            val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

            // Define the output date format
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            return try {
                // Parse the input date string
                val date = inputFormat.parse(inputDate)

                // Format the date into the desired output format
                date?.let { outputFormat.format(it) }
            } catch (e: ParseException) {
                // Handle parse exception if the input date is not in the expected format
                e.printStackTrace()
                null
            }
        }

        fun checkPermissions(context: Context): Boolean {
            for (permission in getRequiredPermissions()) {
                val permissionCheck = ContextCompat.checkSelfPermission(context, permission)
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
            return true
        }

        // Obtain recording, camera and other permissions required to implement real-time audio and video interaction
        fun getRequiredPermissions(): Array<String> {
            // Determine the permissions required when targetSDKVersion is 31 or above
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                arrayOf<String>(
                    Manifest.permission.RECORD_AUDIO,  // Recording permission
                    Manifest.permission.CAMERA,  // Camera permission
                    Manifest.permission.READ_PHONE_STATE,  // Permission to read phone status
                    Manifest.permission.BLUETOOTH_CONNECT // Bluetooth connection permission
                )
            } else {
                arrayOf<String>(
                    Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA
                )
            }
        }

        fun getRequiredMediaPermissions(): Array<String> {
            // Request permissions for devices running Android 13
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
//                Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.CAMERA
                )
//                        readVideoPermission = Manifest.permission.READ_MEDIA_VIDEO
//                        writeVideoPermission = Manifest.permission.READ_MEDIA_VIDEO
            } else {
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
//                        readVideoPermission = Manifest.permission.READ_EXTERNAL_STORAGE
//                        writeVideoPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
            }
        }

        fun checkSelfPermission(context: Activity, permission: String, requestCode: Int): Boolean {
            return if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                // Permission is not granted, check if we should show a rationale
                if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                    ActivityCompat.requestPermissions(context, arrayOf(permission), requestCode)

                } else {
                    // Request the permission directly
                    ActivityCompat.requestPermissions(context, arrayOf(permission), requestCode)
                }
                false
            }
        }


        fun isBatteryOptimized(context: Context): Boolean {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            CLog.e(
                TAG,
                "isBatteryOptimized: ${powerManager.isIgnoringBatteryOptimizations(context.packageName)}",
            )
            return powerManager.isIgnoringBatteryOptimizations(context.packageName)
        }





        fun getVersion(context: Context): String {
            var versionName = ""
            try {
                versionName = context.packageManager
                    .getPackageInfo(context.packageName, 0).versionName!!.toString()
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()

            }
            return "v$versionName"
        }



        fun getDeviceDetails(): String {
            val deviceName = "${Build.MANUFACTURER} ${Build.MODEL}"
            val androidVersion = Build.VERSION.RELEASE
            return "Device Name: $deviceName, Android Version: $androidVersion"
        }

        fun glideCoverImage(context: Context, filePath: String, view: ShapeableImageView) {

            /* Log.e(TAG, "glide:${filePath} ")
             Glide.with(view.context).load(filePath)
                 .error(context.resources.getDrawable(R.drawable.default_profile_ic)).centerCrop()
                 .diskCacheStrategy(DiskCacheStrategy.DATA).into(view)*/
            val options = RequestOptions()
                .centerCrop()
                .placeholder(shimmerEffectForGlide(view.context)) // Placeholder while loading
                 .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL)

            Glide.with(view.context)
                .load(filePath)
                .apply(options)
                .placeholder(shimmerEffectForGlide(view.context))
                .into(view)
        }

        fun shimmerEffectForGlide(context: Context): ShimmerDrawable {
            val shimmer = Shimmer.ColorHighlightBuilder().setDuration(2000).setBaseAlpha(0.9f)
                .setHighlightAlpha(0.93f).setWidthRatio(1.5f)
                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT).setAutoStart(true)
                //.setBaseColor(ContextCompat.getColor(context, android.R.color.darker_gray))
                .setBaseColor(
                    ContextCompat.getColor(
                        context, R.color.shimmer_placeholder
                    )
                ) // Replace with your custom highlight color
                .setHighlightColor(ContextCompat.getColor(context, android.R.color.white)).build()

            val shimmerDrawable = ShimmerDrawable().apply {
                setShimmer(shimmer)
            }
            return shimmerDrawable
        }

        fun loadcircleCropImage(view: AppCompatImageView, url: String) {
            CLog.e(TAG, "loadImageInGlide:url= ${url}")
            Glide.with(view.context).load(url)
                .placeholder(shimmerEffectForGlide(view.context)) // Optional: Placeholder drawable while the image is loading
                 .circleCrop() // Apply the circleCrop transformation for a rounded image
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(view)


        }
        private fun showSettingsDialog(activity: Activity) {
            AlertDialog.Builder(activity).setTitle(activity.getString(R.string.permission_required))
                .setMessage(activity.getString(R.string.this_app_needs_storage_and_camera_permissions_to_function_properly_please_enable_them_in_settings))
                .setPositiveButton(activity.getString(R.string.go_to_settings)) { _, _ ->
                    openAppSettings(activity)
                }.setNegativeButton(activity.getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }.setCancelable(false).show()
        }
        fun getFileSizeFromUri(context: Context, uri: Uri): Long {
            CLog.e(TAG, "getFileSizeFromUri: URI = $uri, Scheme = ${uri.scheme}")
            return try {
                when {
                    uri.scheme?.equals("content", ignoreCase = true) == true -> {
                        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                            if (cursor.moveToFirst()) {
                                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                                if (sizeIndex != -1) {
                                    cursor.getLong(sizeIndex)
                                } else {
                                    CLog.e(TAG, "Column OpenableColumns.SIZE not found.")
                                    0
                                }
                            } else {
                                CLog.e(TAG, "Cursor is empty for content URI.")
                                0
                            }
                        } ?: 0
                    }
                    uri.scheme?.equals("file", ignoreCase = true) == true -> {
                        val filePath = uri.path
                        if (!filePath.isNullOrEmpty()) {
                            val file = File(filePath)
                            if (file.exists()) file.length() else {
                                CLog.e(TAG, "File not found at path: $filePath")
                                0
                            }
                        } else {
                            CLog.e(TAG, "Invalid file path: $filePath")
                            0
                        }
                    }
                    else -> {
                        CLog.e(TAG, "Unsupported URI scheme: ${uri.scheme}")
                        0
                    }
                }
            } catch (e: Exception) {
                CLog.e(TAG, "Exception in getFileSizeFromUri: ${e.localizedMessage}")
                e.printStackTrace()
                0
            }
        }

        fun compressImage(context: Context, uri: Uri): File {
            val inputStream = context.contentResolver.openInputStream(uri) ?: throw IOException(
                "Unable to open input stream"
            )
            val originalBitmap = BitmapFactory.decodeStream(inputStream)

            // Determine the image format
            val imageFormat = getImageFormat(context.contentResolver.openInputStream(uri))

            // Create a temporary file with appropriate suffix
            val suffix = when (imageFormat) {
                Bitmap.CompressFormat.JPEG -> ".jpg"
                Bitmap.CompressFormat.PNG -> ".png"
                else -> ".jpg" // Default to JPG if format is unknown
            }

            val compressedImageFile =
                File.createTempFile("compressed_image", suffix, context.cacheDir)
            val outputStream = FileOutputStream(compressedImageFile)

            // Compress the bitmap to the detected format with 80% quality
            originalBitmap.compress(imageFormat, 80, outputStream)

            outputStream.flush()
            outputStream.close()

            return compressedImageFile
        }


        private fun getImageFormat(inputStream: InputStream?): Bitmap.CompressFormat {
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeStream(inputStream, null, options)
            return when (options.outMimeType) {
                "image/jpeg" -> Bitmap.CompressFormat.JPEG
                "image/png" -> Bitmap.CompressFormat.PNG
                else -> Bitmap.CompressFormat.JPEG // Default to JPG if format is unknown
            }
        }

        fun showConfirmationDialog(
            context: Context,
            message: String = context.getString(R.string.are_you_sure_you_want_to_proceed),
            positiveButtonText: String = context.getString(R.string.ok),
            negativeButtonText: String = context.getString(R.string.cancel),
            onPositiveClick: (() -> Unit)? = null,
            onNegativeClick: (() -> Unit)? = null
        ) {
            val alertDialog = AlertDialog.Builder(context)
            alertDialog.setMessage(message)

            alertDialog.setPositiveButton(positiveButtonText) { dialog, _ ->
                onPositiveClick?.invoke()
                dialog.dismiss()
            }

            alertDialog.setNegativeButton(negativeButtonText) { dialog, _ ->
                onNegativeClick?.invoke()
                dialog.dismiss()
            }

            val dialog: AlertDialog = alertDialog.create()
            dialog.show()
        }


        fun loadImage(view: ImageView, url: String) {
            Glide.with(view.context).load(url).placeholder(shimmerEffectForGlide(view.context))
                .into(view)
        }
        fun getFilePathFromUri(context: Context, uri: Uri): String? {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    return cursor.getString(columnIndex)
                }
            }
            return null
        }
        fun calendarDialog(
            activity: Activity? = null,
            fragment: Fragment? = null,
            supportFragmentManager: FragmentManager,
            fromDate: String? = null,
            type: String? = null,
            selectedDate: String? = null
        ) {
            CLog.e(TAG, "calendarDialog: fromDate = $fromDate")
            val calenderInterface: CalenderInterface = fragment as? CalenderInterface ?: activity as CalenderInterface

            val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            val today = MaterialDatePicker.todayInUtcMilliseconds()
            cal.timeInMillis = today

            var startFrom = cal.timeInMillis

            if (!fromDate.isNullOrEmpty()) {
                val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                dateFormatter.timeZone = TimeZone.getTimeZone("UTC")
                val startDate = dateFormatter.parse(fromDate)
                cal.time = startDate ?: Date()
                startFrom = cal.timeInMillis
            }

            val dateValidator = DateValidatorPointForward.from(startFrom)
            val constraintsBuilderRange = CalendarConstraints.Builder()
                .setStart(startFrom)
                .setValidator(dateValidator)
                .build()

            val builder = MaterialDatePicker.Builder.datePicker().setCalendarConstraints(constraintsBuilderRange)

            if (!selectedDate.isNullOrEmpty()) {
                val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                dateFormatter.timeZone = TimeZone.getTimeZone("UTC")
                val startDate = dateFormatter.parse(selectedDate)
                startDate?.let {
                    builder.setSelection(it.time)
                } ?: CLog.e(TAG, "Failed to parse selectedDate: $selectedDate")
            }

            val dateRange = builder.build()
            dateRange.show(supportFragmentManager, "DATE_PICKER")

            dateRange.addOnPositiveButtonClickListener { selection ->
                val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val formattedDate = dateFormatter.format(Date(selection))
                CLog.e(TAG, "Selected Date: $formattedDate")
                calenderInterface.dateFrom(true, formattedDate, type) // Passing true by default
            }

            dateRange.addOnNegativeButtonClickListener {
               CLog.e(TAG, "Date selection cancelled")
            }

            dateRange.addOnCancelListener {
                CLog.e(TAG, "Date picker cancelled")
            }
        }

        fun returnMessage(type: String, activity: Activity): String {
            when(type){
                AppStrings.ValidationTypes.invalidMobileNumber->{
                   return activity.resources.getString(R.string.valid_phone_number)
                }
                AppStrings.ValidationTypes.invalidImei->{
                   return activity.resources.getString(R.string.invalid_imei)
                }
                AppStrings.ValidationTypes.imeiEmpty->{
                   return activity.resources.getString(R.string.imei_empty)
                }
                AppStrings.ValidationTypes.emptyMobileModel->{
                   return activity.resources.getString(R.string.mobile_model_empty)
                }
                AppStrings.ValidationTypes.emptyStatus->{
                   return activity.resources.getString(R.string.status_empty)
                }
                AppStrings.ValidationTypes.emptySellerPrice->{
                   return activity.resources.getString(R.string.seller_price_empty)
                }
                AppStrings.ValidationTypes.emptySellerName->{
                   return activity.resources.getString(R.string.seller_name_empty)
                }
                AppStrings.ValidationTypes.emptySellerPhoneNumber->{
                   return activity.resources.getString(R.string.seller_phone_number_empty)
                }
                AppStrings.ValidationTypes.emptySellerDate->{
                   return activity.resources.getString(R.string.seller_date_empty)
                }
                AppStrings.ValidationTypes.emptyImages->{
                   return activity.resources.getString(R.string.images_empty)
                }
                AppStrings.ValidationTypes.invalidEmail->{
                    return activity.resources.getString(R.string.valid_email)
                }
                AppStrings.ValidationTypes.invalidPassword->{
                    return activity.resources.getString(R.string.valid_password)
                }
                AppStrings.ValidationTypes.invalidConfirmPassword->{
                    return activity.resources.getString(R.string.valid_confirm_password)
                }
                AppStrings.ValidationTypes.passwordIncorrect->{
                    return activity.resources.getString(R.string.passwords_not_matched)
                }
                AppStrings.ValidationTypes.validOtp->{
                    return activity.resources.getString(R.string.valid_otp)
                }

                 else->{
                    return ""
                }

            }

        }
        fun isValidPhoneNumber(phoneNumber: String): Boolean {
            // Define a regular expression for a 10-digit phone number
            val phoneRegex = "^[0-9]{10}$"
//            val phoneRegex = "^[0-9]{3,15}$"

            // Use the matches method to check if the input matches the pattern
            return phoneNumber.matches(Regex(phoneRegex))
        }
        fun hideKeyboardForCountry(context: Context, itemView: View) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(itemView.windowToken, 0)
        }

        fun showKeyBoard(activity: Activity, editText: TextInputEditText) {
            val inputMethodManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }
        lateinit var awsRetrofit: Retrofit
        fun getAwsRetrofitInstance(): Retrofit {

            val gson = GsonBuilder().setLenient().create()
            awsRetrofit =
                Retrofit.Builder().baseUrl("https://seconds-mobiles.s3.ap-south-1.amazonaws.com/")
                    .addConverterFactory(GsonConverterFactory.create(gson)).client(httpAuthKey())
                    .build()
            return awsRetrofit
        }
        private fun httpAuthKey(): OkHttpClient? {
            try {
                return OkHttpClient.Builder().addInterceptor { chain: Interceptor.Chain ->
                    val authorisedReq = chain.request().newBuilder().build()
                    chain.proceed(authorisedReq)
                }.readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS).build()

            } catch (e: SocketTimeoutException) {
                Log.e("TAG", "httpAuthKey:SocketTimeoutException ")
                return null
            }
        }
        fun uploadFileApi(
            url: String,//from aws
            filepath: String? = "",
             filename: String? = "", context: Context
        ): Pair<Call<ResponseBody>, MultipleImagesListToBackend> {

            val file = File(filepath)
            Log.e(TAG, "uploadFileApi: ${file.absolutePath}")
//            val requestBody = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
            val mimeType =
                URLConnection.guessContentTypeFromName(file.name) ?: "application/octet-stream"
            val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())

            val retrofit = getAwsRetrofitInstance().create(RestApi::class.java)
            val call = retrofit.uploadFile(url, requestBody)


            val fileUploadList = MultipleImagesListToBackend(
                 filename = filename!!,
             )
            CLog.e(TAG, "uploadFileApi:filename---->${fileUploadList.filename} ", )
            return Pair(call, fileUploadList)
        }

        fun handleAllUploads(
            calls: List<Call<ResponseBody>>,
            fileUploads: List<MultipleImagesListToBackend>,
            onAllComplete: (Boolean, List<MultipleImagesListToBackend>) -> Unit
        ) {
            val completedCalls = mutableListOf<Call<ResponseBody>>()
            var allSuccessful = true
            val successfulFileUploads = mutableListOf<MultipleImagesListToBackend>()

            calls.forEachIndexed { index, call ->
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>, response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            Log.e("putUrl", "onResponse: success")
                            // Add to list on success only if it's a media upload
                            if (index < fileUploads.size) {
                                successfulFileUploads.add(fileUploads[index])
                            }
                        } else {
                            Log.e("putUrl", "onResponse: error ${response.errorBody()?.string()}")
                            allSuccessful = false
                        }
                        completedCalls.add(call)
                        if (completedCalls.size == calls.size) {
                            onAllComplete(allSuccessful, successfulFileUploads)
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("putUrl", "onFailure: ${t.message}")
                        allSuccessful = false
                        completedCalls.add(call)
                        if (completedCalls.size == calls.size) {
                            onAllComplete(allSuccessful, successfulFileUploads)
                        }
                    }
                })
            }
        }

        fun showProgressBar(context: Context) {
            progressBar = Dialog(context)
            progressBar?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            progressBar?.show()
            progressBar?.setContentView(R.layout.app_loading_dialog)
            progressBar?.setCancelable(false)
            progressBar?.setCanceledOnTouchOutside(false)
        }

        fun dismissProgressBar(context: Context) {
            progressBar?.dismiss()
        }
        fun getStringArrayList(list: ArrayList<MultipleImagesListToBackend>): JSONArray {
            val getStringArray = JSONArray() // Create a new JSONArray to hold strings
            list.forEach {
                getStringArray.put(it.filename) // Add only the "name" or the string field you want
            }
            return getStringArray
        }
        fun deleteMediaIds(list: ArrayList<String>?): JSONArray {
            return JSONArray(list ?: emptyList<String>()) // Ensure it's a list of Long values
        }









    }

   /* object DeviceIdManager {
        private const val TAG = "DeviceIdManager"

        fun getDeviceId(context: Context, callback: (String?) -> Unit) {
            CoroutineScope(Dispatchers.IO).launch {
                val sm = SessionManager(context)
                val token = retrieveFcmToken(3)
                token?.let {
                    sm.saveData(AppStrings.SessionValues.deviceId, it)
                    Log.e(TAG, "getDeviceId: token:$it")
                }
                withContext(Dispatchers.Main) {
                    callback(token)
                }
            }
        }


        inline fun <reified T : Parcelable> Intent.parcelableArrayList(key: String): ArrayList<T>? =
            when {
                Build.VERSION.SDK_INT >= 33 -> getParcelableArrayListExtra(key, T::class.java)
                else -> @Suppress("DEPRECATION") getParcelableArrayListExtra(key)
            }


    }*/
}