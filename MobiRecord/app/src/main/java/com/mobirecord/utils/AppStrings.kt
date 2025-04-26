package com.mobirecord.utils

class AppStrings {


    class Constants {
        companion object {

            var authorization = "Authorization"
            var defaultToken = "tuneconnectsecretkeychecked"
            var success =200
            var success201 =201
            var photosCount =5
            const val permissions = 11
            val mediaTypeImage = 1
            val min = 0
            val max = 10
            var MAX_IMAGE_SIZE = 50 * 1024 * 1024 // 50 MB


        }
    }
    class Strings {
        companion object {
            var nullableString = "NA"
            var multipleImages = "multipleImages"
            var image = "image/jpeg"
            var video = "video/mp4"
        }
    }

    class SessionValues {
        companion object {

            var accessToken = "accessToken"
            var countriesNationality = "countriesNationality"
            var userId = "userId"
            var refreshToken = "refreshToken"
            var deviceId = "deviceId"
            var spotifyAccessToken = "spotifyAccessToken"
            var userName = "userName"
            var profilePic = "profilePic"
            var profilePicStatus = "profilePicStatus"
            var isUserTappedOnContactView = "isUserTappedOnContactView"
            var APP_ID = "APP_ID"
            var channelName = "channelName"
            var token = "token"
            var callType = "callType"
            var staticUid = "staticUid"
            var otherUserId = "otherUserId"
            var OutgoingCallServiceActive = "OutgoingCallServiceActive"
            var OngoingCallServiceActive = "OngoingCallServiceActive"
            var callId = "callId"
            var userVisitedToolTip = "userVisitedToolTip"
            var isUserVisitSearchToolTip = "isUserVisitSearchToolTip"
            var spotifySongUri = "spotifySongUri"
            var songId = "songId"
            var referralCode = "referralCode"
            var referralSharedTime = "referralSharedTime"
            var inviteCode = "inviteCode"
            var isShowedContactChats = "isShowedContactChats"
        }
    }

    class ResponseData {
        companion object {
            val id = "id"
            val token = "token"
            val email = "email"
            val country_name = "country_name"
            val country_code = "country_code"
            val code = "code"
            val country_flag = "country_flag"
            val dialing_code = "dialing_code"
            val data = "data"
            val emoji = "emoji"
            val currencySymbol = "currencySymbol"
            val currency = "currency"

            var access_token = "access_token"

            var existing_user = "4"
            var referalCode = "5"
            var addSelfie = "3"
            var preferences = "2"
            var userDetails = "1"
            var songId = "songId"
            var referralCode = "referralCode"
            var senderId = "senderId"
            var sharedTime = "sharedTime"


        }
    }

    class ValidationTypes {
        companion object {
            var invalidMobileNumber = "invalidMobileNumber"
            var invalidImei = "invalidImei"
            var imeiEmpty = "imeiEmpty"
            var emptyMobileModel = "emptyMobileModel"
            var emptyStatus = "emptyStatus"
            var emptySellerPrice = "emptySellerPrice"
            var emptySellerName = "emptySellerName"
            var emptySellerPhoneNumber = "emptySellerPhoneNumber"
            var emptySellerDate = "emptySellerDate"
            var emptyImages = "emptyImages"
            val success = "success"
            val customerNameEmpty = "customerNameEmpty"
            val invalidPhoneNumber = "invalidPhoneNumber"
            val invalidEmail = "invalidEmail"
            val invalidPassword = "invalidPassword"
            val invalidConfirmPassword = "invalidConfirmPassword"
            val passwordIncorrect = "passwordIncorrect"
            val validOtp = "validOtp"


        }
    }

    class SnackbarStatus {
        companion object {
            val success = "1"
            val informative = "2"
            val delete = "3"
            val error = "4"
        }
    }

    class InputData {
        companion object {
            var mobileModel = "mobileModel"
            var imei = "imei"
            var buyerPrice = "buyerPrice"
            var sellerPrice = "sellerPrice"
            var profit = "profit"
            var status = "status"
            var sellerName = "sellerName"
            var sellerPhone = "sellerPhone"
            var sellerAadhar = "sellerAadhar"
            var sellerDate = "sellerDate"
            var buyerName = "buyerName"
            var buyerPhone = "buyerPhone"
            var buyingDate = "buyingDate"
             var email = "email"
            var password = "password"
            var page = "page"
            var pageSize = "pageSize"
            var Search = "search"
            var fileNames = "fileNames"
            var imageKeys = "imageKeys"
            var deletedKeys = "deletedKeys"

        }
    }

    class IntentData {
        companion object {
            var mobileNumber = "mobileNumber"
            var countryCode = "countryCode"
            var imagePath = "imagePath"
            var itemModel = "itemModel"
            var songId = "songId"
            var artistId = "artistId"
            var artistImgUrl = "artistImgUrl"
            var from = "from"
            val type = "type"
             val isMultipleImages = "isMultipleImages"
            val imageFile = "imageFile"
            val multiImages = "multiImages"
            val selectedImageUri = "selectedImageUri"
            val maxCount = "maxCount"
            var imageFileList = "imageFileList"
            var type_from = "type_from"
            var isRectangleCrop = "isRectangleCrop"
            var imei = "imei"



        }
    }

    class preferencesTypes {
        companion object {
            var gender = 1
            var musicInterest = 2
            var languages = 3
            var document = 1
            var category = 2
            var socialMediaTypes = 3
        }
    }

    class InputParams {
        companion object {
            var grant_type = "grant_type"
            var redirect_uri = "redirect_uri"
            var Authorization = "Authorization"
            var Basic = "Basic"
            var client_credentials = "client_credentials"
        }
    }

    class ResponseTypes {
        companion object {
            var recentlyAdded = "recentlyAdded"
            var artists = "artists"
            var liked = "liked"
            var celebrities = "celebrities"


        }
    }

    class commentStatus {
        companion object {
            var addComment = 1
            var editComment = 2
            var deleteComment = 3
        }
    }

    class clickTpyes {
        companion object {
            var profile = 10
            var editComment = 1
            var deleteComment = 2
            var reportComment = 3
            var like = 4
            var chat = 5
            var default = 0
            var songCv = 6
            var playSong = 7
            var deleteSelfChat = 8
            var deleteOtherChat = 9
            var deleteForMe = 10
            var deleteForEveryone = 11
            var onClickReceivingGift = 12
            var onClickSendingGift = 13

            const val block = 1
            const val report = 2
            const val zero = "0"
        }
    }

    class from {
        companion object {
            var profileFragment = "profileFragment"
            var homeFragment = "homeFragment"
            var chatActivity = "chatActivity"
            var incomingCallActivity = "incomingCallActivity"
            var splashActivity = "splashActivity"
            var notifications = "notifications"
            var callActionReceiver = "callActionReceiver"
            var onGoingCallServices = "onGoingCallServices"
            var outGoingCallServices = "outGoingCallServices"
            var newGroupActivity = "newGroupActivity"
            var createGroupActivity = "createGroupActivity"
            var groupInfoActivity = "groupInfoActivity"
        }
    }

    class notificationSettingsTypes {
        companion object {
            var messages = 0
            var likes = 1
            var comments = 2
            var video = 3
            var audio = 4
        }
    }

    class songListTypes {
        companion object {
            var home = 0
            var recently = 5
            var liked = 2
            var recentlyAdded = 1
            var artists = 3
            var tracks = 4
        }
    }

    class spotifyAppState {
        companion object {
            var notInstalled = 1
            var installedButNotLogin = 2
            var connected = 3
            var sdkLimitations = 4
        }
    }

    class bottomSheetTypes {
        companion object {
            var logout = 1
            var deleteAccount = 2
            var blockUser = 3
            var clearChat = 4
            var deleteChat = 5
            var deleteNotification = 6
            var deleteSelectedNotification = 7
            var no = 0
            var deleteSelfChat = 8
            var deleteOtherChat = 9
            var deleteGroup = 10
            var exitGroup = 11
            var removeMemberFromGroup = 12
            var privateuser = 13
        }
    }
    interface ActivityFrom {
        companion object {
            var addDetailsActivity= "AddDetailsActivity"
        }
    }

    interface Types {
        companion object {
            val imageCamera = "imageCamera"
            val imageGallery = "imageGallery"
            val videoGallery = "videoGallery"
            val document = "document"
            val videoCamera = "videoCamera"
            val audio = "audio"
            var imageVideoGallery = "imageVideoGallery"
            val type_from = "type_from"
        }
    }

    interface contentTypes {
        companion object {
            val song = "song"
            val text = "text"
            val gift = "gift"
        }
    }

    interface chatConnectedTypes {
        companion object {
            var song: String = "song"
            var contact = "contact"
        }
    }

    interface apiInputParamTypes {
        companion object {
            //  1 - block and 2 - report

            var block = 1
            var report = 2
        }
    }

    interface FCMData {
        companion object {
            val data = "data"
            val orderStatus = "order_status"
            val id = "id"
            val title = "title"
            val message = "message"
            val type = "type"
            val body = "body"
            val senderUserId = "senderUserId"
            val notificationId = "notificationId"
            val badgeCount = "batchCount"
            val channelName = "channelName"
            val token = "token"
            val appId = "appId"
            val callType = "callType"
            val songId = "songId"
            val callId = "callId"
            val roomID = "roomID"
            val chatType = "chatType"

        }
    }

    interface NotificationTypes {
        companion object {
            var CHAT_MESSAGES = "CHAT_MESSAGES"
            var CHAT_SONG = "CHAT_SONG"
            var AUDIO_CALL_INITATED = "AUDIO_CALL_INITATED"
            var VIDEO_CALL_INITATED = "VIDEO_CALL_INITATED"
            var LIKES = "LIKES"
            var COMMENTS = "COMMENTS"
            var SELFIE_VERIFICATION_ACCEPTED = "SELFIE_VERIFICATION_ACCEPTED"
            var SELFIE_VERIFICATION_REJECTED = "SELFIE_VERIFICATION_REJECTED"
            var AUDIO_MISSED_CALL = "AUDIO_MISSED_CALL"
            var VIDEO_MISSED_CALL = "VIDEO_MISSED_CALL"
            var AUDIO_DECLINED_CALL = "AUDIO_DECLINED_CALL"
            var VIDEO_DECLINED_CALL = "VIDEO_DECLINED_CALL"
            var AUDIO_END_CALL = "AUDIO_END_CALL"
            var VIDEO_END_CALL = "VIDEO_END_CALL"
            var CELEBRITY_REJECTED = "CELEBRITY_REJECTED"
            var CELEBRITY_ACCEPTED = "CELEBRITY_ACCEPTED"

        }
    }

    interface Fragments {
        companion object {
            var HOME_FRAGMENT = "1"
            var NOTIFICATION_FRAGMENT = "2"
            var CHAT_FRAGMENT = "3"
            var PROFILE_FRAGMENT = "4"
        }
    }

    interface CallTypes {
        companion object {
            var AUDIO_CALL = 1
            var VIDEO_CALL = 2
            var AUDIO_CALL_INITATED = "AUDIO_CALL_INITATED"
            var Audio = "Audio"
            var Video = "Video"

        }
    }

    interface CallStatus {
        companion object {
            var CallDrop = "CallDrop"
            var IncomingCall = "IncomingCall"
            var OutgoingCall = "OutgoingCall"
            var InCall = "InCall"
        }
    }

    interface callStatusTypes {
        companion object {
            var missed = 1
            var end = 2
            var declined = 3
        }
    }

    interface NotificationsType {
        companion object {
            var missedCall = 1
            var declinedCall = 2
            var others = 3
            var endCall = 4
        }
    }

    interface ToolTipTypes {
        companion object {
            var likesCount = "likesCount"
            var commentsCount = "commentsCount"

        }
    }

    interface AchievementsTypes {
        companion object {
            var songshare = "1"
            var referral = "2"
            var referralEarn = "3"
            var sendGift = "4"

        }
    }

    interface TabNames {
        companion object {
            var allChats = "All Chats"
            var groups = "Groups"
            var virtualGifts = "Virtual Gifts"
            var history = "History"
            var songsListed = "Songs Listed"
            var songsEngaged = "Songs Engaged"
        }
    }

    interface chatTypes {
        companion object {
            var directChat = "1"
            var groupChat = "2"
        }
    }

    interface interactionTypes {
        companion object {
            var commented = "Commented"
            var Liked = "Liked"
        }
    }

    interface socialMediaTypes {
        companion object {
            var twitter = 89
            var facebook = 90
            var instagram = 91
            var otherLinks = 92
        }
    }

    interface socialMediaNames {
        companion object {
            var twitter = "Twitter"
            var facebook = "Facebook"
            var instagram = "Instagram"
            var youtube = "Youtube"
            var otherLinks = ""
        }
    }
    class DateFormat {
        companion object {
            val dd_mm_yyyy = "dd/MM/yyyy"
            val dd_mm_yyyy_hh_mm_ss = "dd/MM/yyyy HH:mm:ss"
            val eee_mmm_dd = "EEE, MMM dd"
            val yyyy_mm_dd = "YYYY-MM-dd"
            var eee_mmm_dd_hh_mm_ss_z_yyyy = "EEE MMM dd HH:mm:ss z yyyy"
            var eee_mmm_dd_hh_mm_ss_zzz_yyyy = "EEE MMM dd HH:mm:ss zzz yyyy"
            var yyyy_mm_dd_t_hh_mm_ss = "yyyy-MM-dd'T'HH:mm:ss"
            // var yyyy_mm_dd_t_hh_mm_ss_sss_z = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
            var hh_mm = "hh:mm a"
            var eeee_hh_mm = "EEEE hh:mm a"
            var dd_mmm_hh_mm = "dd MMM, hh:mm a"
            var dd_mmm_hh_mm_24 = "dd MMM, hh:mm"
            var eee_dd_mmm_yy = "EEE, ddMMM yy"
            var dd_mmm = "dd MMM"
            var time24Format = "HH:mm"
            val eee_dd_mmm = "EEE, d MMM"
            val yyyy_mm_dd_t_hh_mm_ss_sss_z = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
            // val yyyy_MM_dd = "yyyy-MM-dd"
            val ddMMyyyy = "dd/MM/yyyy"
            val dd_MM_yyyy = "dd-MM-yyyy"
            val dd_MMM_yyyy = "dd MMM yyyy"
            val dd_MMM_yyyy1 = "dd,MMM yyyy"

            val dd_mm_yyyy_hh_mm = "dd/MM/yyyy HH:mm"
            var dd_mmm_eeee_hh_mm = "dd MMM yyyy, HH:mm"

        }
    }


}