package com.amtech.tlismiherbs.sharedpreferences

import android.content.Context
import android.preference.PreferenceManager

class SessionManager(context: Context?) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    companion object {
        private const val IS_LOGIN = "islogin"
        private const val CUSTOMER_NAME = "customer_name"
        private const val BASE_URL = "base_url"
        private const val LATITUDE = "latitude"
        private const val LONGITUDE = "longitude"
        private const val EMAIL = "email"
        private const val FCM_TOKEN = "fcm_token"
        private const val AUTH_TOKEN = "auth_token"
        private const val AUTH_TOKEN_USER = "auth_token_user"
        private const val USER_EMAIL = "user_email"
        private const val USER_NAME = "user_name"
        private const val USER_MOBILE = "user_mobile"
        private const val GENDER = "gender"
        private const val ID = "id"
        private const val PASSWORD = "password"
        private const val PHONE_NUMBER = "phone_number"
        private const val PHONE_WITH_CODE = "phone_with_code"
        private const val PROFILE_PICTURE = "profile_picture"
        private const val BOOKING_TYPE = "bookingType"
        private const val PRICE = "pricing"
        private const val DEVICEID = "deviceId"
        private const val SELECTED_DATE = "selected_date"
        private const val RANDOM_KEY = "random_key"

    }

    var isLogin: Boolean
        get() = prefs.getBoolean(IS_LOGIN, false)
        set(isLogin) {
            prefs.edit().putBoolean(IS_LOGIN, isLogin).apply()
        }

    fun logout() {
        prefs.edit().clear().apply()
    }

    var fcmToken: String?
        get() = prefs.getString(FCM_TOKEN, "")
        set(fcmToken) {
            prefs.edit().putString(FCM_TOKEN, fcmToken).apply()
        }


    var authToken: String?
        get() = prefs.getString(AUTH_TOKEN, "")
        set(authToken) {
            prefs.edit().putString(AUTH_TOKEN, authToken).apply()
        }

    var authTokenUser: String?
        get() = prefs.getString(AUTH_TOKEN_USER, "")
        set(authTokenUser) {
            prefs.edit().putString(AUTH_TOKEN_USER, authTokenUser).apply()
        }

    var userEmail: String?
        get() = prefs.getString(USER_EMAIL, "")
        set(userEmail) {
            prefs.edit().putString(USER_EMAIL, userEmail).apply()
        }

    var userName: String?
        get() = prefs.getString(USER_NAME, "")
        set(userName) {
            prefs.edit().putString(USER_NAME, userName).apply()
        }

    var userMobile: String?
        get() = prefs.getString(USER_MOBILE, "")
        set(userMobile) {
            prefs.edit().putString(USER_MOBILE, userMobile).apply()
        }

    var deviceId: String?
        get() = prefs.getString(DEVICEID, "")
        set(deviceId) {
            prefs.edit().putString(DEVICEID, deviceId).apply()
        }
    var randomKey: String?
        get() = prefs.getString(RANDOM_KEY, "")
        set(randomKey) {
            prefs.edit().putString(RANDOM_KEY, randomKey).apply()
        }
    var latitude: String?
        get() = prefs.getString(LATITUDE, "")
        set(latitude) {
            prefs.edit().putString(LATITUDE, latitude).apply()
        }
    var longitude: String?
        get() = prefs.getString(LONGITUDE, "")
        set(longitude) {
            prefs.edit().putString(LONGITUDE, longitude).apply()
        }
    var bookingType: String?
        get() = prefs.getString(BOOKING_TYPE, "")
        set(bookingType) {
            prefs.edit().putString(BOOKING_TYPE, bookingType).apply()
        }
    var pricing: String?
        get() = prefs.getString(PRICE, "")
        set(pricing) {
            prefs.edit().putString(PRICE, pricing).apply()
        }
    var password: String?
        get() = prefs.getString(PASSWORD, "")
        set(password) {
            prefs.edit().putString(PASSWORD, password).apply()
        }
    var customerName: String?
        get() = prefs.getString(CUSTOMER_NAME, "")
        set(customerName) {
            prefs.edit().putString(CUSTOMER_NAME, customerName).apply()
        }
    var baseURL: String?
        get() = prefs.getString(BASE_URL, "")
        set(baseURL) {
            prefs.edit().putString(BASE_URL, baseURL).apply()
        }
    var email: String?
        get() = prefs.getString(EMAIL, "")
        set(email) {
            prefs.edit().putString(EMAIL, email).apply()
        }

    var phoneNumber: String?
        get() = prefs.getString(PHONE_NUMBER, "")
        set(phoneNumber) {
            prefs.edit().putString(PHONE_NUMBER, phoneNumber).apply()
        }
    var phoneWithCode: String?
        get() = prefs.getString(PHONE_WITH_CODE, "")
        set(phoneWithCode) {
            prefs.edit().putString(PHONE_WITH_CODE, phoneWithCode).apply()
        }

    var id: Int
        get() = prefs.getInt(ID, -1)
        set(id) {
            prefs.edit().putInt(ID, id).apply()
        }

    var gender: String?
        get() = prefs.getString(GENDER, "")
        set(gender) {
            prefs.edit().putString(GENDER, gender).apply()
        }
    var selectedDate: String?
        get() = prefs.getString(SELECTED_DATE, "")
        set(selectedDate) {
            prefs.edit().putString(SELECTED_DATE, selectedDate).apply()
        }
    var profilePic: String?
        get() = prefs.getString(PROFILE_PICTURE, "")
        set(profilePic) {
            prefs.edit().putString(PROFILE_PICTURE, profilePic).apply()
        }

}