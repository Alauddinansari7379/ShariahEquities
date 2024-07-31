package com.amtech.tlismiherbs.profile.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.amtech.tlismiherbs.Helper.AppProgressBar
 import com.amtech.tlismiherbs.MainActivity
import com.sellacha.tlismiherbs.R
import com.sellacha.tlismiherbs.databinding.FragmentProfileBinding
import com.amtech.tlismiherbs.login.Login
import com.amtech.tlismiherbs.login.SignUp
import com.amtech.tlismiherbs.myOrder.activity.MyOrder
import com.amtech.tlismiherbs.retrofit.ApiClient
import com.amtech.tlismiherbs.sharedpreferences.SessionManager
import com.example.tlismimoti.Helper.myToast
import com.example.tlismimoti.mainActivity.ModelDestoryCart
import com.example.tlismimoti.setting.model.ModelSetting
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
      lateinit var sessionManager: SessionManager
    var dialog: Dialog? = null
    var count = 0
    var countU = 0
    var countDes = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val parentView: View = layoutInflater.inflate(R.layout.login_dialog, null)
        bottomSheetDialog.setContentView(parentView)
        with(binding) {
            cardPromo.setOnClickListener {
                startActivity(Intent(requireContext(), Promo::class.java))
            }
            cardPrivacyPolicy.setOnClickListener {
                val intent = Intent(context as Activity, PrivacyPolicy::class.java)
                    .putExtra("title", "Privacy Policy")
                (context as Activity).startActivity(intent)
            }
            cardContactUs.setOnClickListener {
                val intent = Intent(context as Activity, PrivacyPolicy::class.java)
                    .putExtra("title", "Contact Us")
                (context as Activity).startActivity(intent)
            }
            cardMyOrder.setOnClickListener {
                if (sessionManager.authTokenUser!!.isEmpty()) {
                         try {
                            bottomSheetDialog.show()
                        }catch (e:Exception){
                            e.printStackTrace()
                        }

                 }else
                startActivity(Intent(requireContext(), MyOrder::class.java))
            }
            val imgClose = parentView.findViewById<ImageView>(R.id.imgBackDil)
            val login = parentView.findViewById<Button>(R.id.btnLoginDil)
            val signUp = parentView.findViewById<Button>(R.id.btnSignUpDil)

            imgClose.setOnClickListener {

                bottomSheetDialog.dismiss()
            }
            login.setOnClickListener {
                startActivity(Intent(activity, Login::class.java))
            }
            signUp.setOnClickListener {
                startActivity(Intent(activity, SignUp::class.java))
            }
            cardRefer.setOnClickListener {
                startActivity(Intent(requireContext(), ReferAndEarn::class.java))
            }
            cardChangePass.setOnClickListener {
                if (sessionManager.authTokenUser!!.isEmpty()) {
                    try {
                        bottomSheetDialog.show()
                    }catch (e:Exception){
                        e.printStackTrace()
                    }

                }else
                     changePassDialog("Change Password")

            }

            imgEdit.setOnClickListener {
                if (sessionManager.authTokenUser!!.isEmpty()) {
                    try {
                        bottomSheetDialog.show()
                    }catch (e:Exception){
                        e.printStackTrace()
                    }

                }else
                     changePassDialog("Update Profile")

            }


            sessionManager = SessionManager(activity)

            if (sessionManager.authTokenUser!!.isNotEmpty()) {
                apiCallGetSetting()
            }


            btnSignOut.setOnClickListener {
                if (sessionManager.authTokenUser!!.isNotEmpty()) {
                    SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure want to Logout?")
                        .setCancelText("No")
                        .setConfirmText("Yes")
                        .showCancelButton(true)
                        .setConfirmClickListener { sDialog ->
                            sDialog.cancel()
                            sessionManager.userMobile = ""
                            sessionManager.authTokenUser = ""
                            sessionManager.userName = ""
                            sessionManager.userEmail = ""
                            sessionManager.deviceId = ""
                            activity?.let { myToast(it, "Successfully Logout") }
                            startActivity(Intent(activity, MainActivity::class.java))
                        }
                        .setCancelClickListener { sDialog ->
                            sDialog.cancel()
                        }
                        .show()


                }else
                    bottomSheetDialog.show()

            }
            cardDelete.setOnClickListener {
                if (sessionManager.authTokenUser!!.isNotEmpty()) {
                    SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure want to Delete?")
                        .setCancelText("No")
                        .setConfirmText("Yes")
                        .showCancelButton(true)
                        .setConfirmClickListener { sDialog ->
                            sDialog.cancel()
                            apiCallDeleteAccount()
                        }
                        .setCancelClickListener { sDialog ->
                            sDialog.cancel()
                        }
                        .show()


                }
                else
                    bottomSheetDialog.show()
            }

        }

    }

    private fun apiCallGetSetting() {
        // AppProgressBar.showLoaderDialog(activity)
        ApiClient.apiService.userSettings(
            sessionManager.authToken,
            sessionManager.userEmail.toString()
        )
            .enqueue(object : Callback<ModelSetting> {
                override fun onResponse(
                    call: Call<ModelSetting>, response: Response<ModelSetting>
                ) {
                    try {

                        if (response.code() == 500) {
                            activity?.let { myToast(it, "Server Error") }
                            AppProgressBar.hideLoaderDialog()
                        } else if (response.code() == 404) {
                            activity?.let { myToast(it, "Something went wrong") }
                            AppProgressBar.hideLoaderDialog()

                        } else {
                            sessionManager.userMobile = response.body()!!.data.check.mobile
                            sessionManager.userName = response.body()!!.data.check.name
                            sessionManager.userEmail = response.body()!!.data.check.email

                            binding.tvFullName.text = response.body()!!.data.check.name
                            binding.tvEmail.text = response.body()!!.data.check.email
                            binding.tvMobile.text = response.body()!!.data.check.mobile
                            AppProgressBar.hideLoaderDialog()

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        activity?.let { myToast(it, "Something went wrong") }
                        AppProgressBar.hideLoaderDialog()


                    }
                }

                override fun onFailure(call: Call<ModelSetting>, t: Throwable) {
                    count++
                    if (count <= 3) {
                        Log.e("count", count.toString())
                        apiCallGetSetting()
                    } else {
                        activity?.let { myToast(it, t.message.toString()) }
                        AppProgressBar.hideLoaderDialog()
                        activity?.let { myToast(it, "Something went wrong") }

                    }
                    // AppProgressBar.hideLoaderDialog()


                }

            })
    }

    private fun apiCallUpdateSetting(
        name: String,
        mobile: String,
        currentPass: String,
        password: String
    ) {
        AppProgressBar.showLoaderDialog(activity)
        ApiClient.apiService.userSettingsUpdate(
            sessionManager.authToken,
            sessionManager.userEmail.toString(),
            name,
            mobile,
            currentPass,
            password,
        )
            .enqueue(object : Callback<ModelDestoryCart> {
                override fun onResponse(
                    call: Call<ModelDestoryCart>, response: Response<ModelDestoryCart>
                ) {
                    try {

                        if (response.code() == 500) {
                            activity?.let { myToast(it, "Server Error") }
                            AppProgressBar.hideLoaderDialog()
                        } else if (response.code() == 404) {
                            activity?.let { myToast(it, "Something went wrong") }
                            AppProgressBar.hideLoaderDialog()

                        } else {
                            activity?.let { myToast(it, response.body()!!.data) }
                            apiCallGetSetting()
                            AppProgressBar.hideLoaderDialog()

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        activity?.let { myToast(it, "Something went wrong") }
                        AppProgressBar.hideLoaderDialog()


                    }
                }

                override fun onFailure(call: Call<ModelDestoryCart>, t: Throwable) {
                    countU++
                    if (countU <= 2) {
                        Log.e("count", countU.toString())
                        apiCallGetSetting()
                    } else {
                        activity?.let { myToast(it, t.message.toString()) }
                        AppProgressBar.hideLoaderDialog()
                        activity?.let { myToast(it, "Something went wrong") }

                    }
                    // AppProgressBar.hideLoaderDialog()


                }

            })
    }

    private fun changePassDialog(s: String) {
        val view = layoutInflater.inflate(R.layout.dialog_password_change, null)
        dialog = activity?.let { Dialog(it) }

        val btnChange = view!!.findViewById<Button>(R.id.btnChangeDialogPass)
        val tvDateFromDilog = view!!.findViewById<TextView>(R.id.tvDateFromDilog)
        val newPass = view!!.findViewById<EditText>(R.id.edtNewPasswordDialogPass)
        val fullName = view!!.findViewById<EditText>(R.id.edtFullNameDil)
        val mobile = view!!.findViewById<EditText>(R.id.edtMobileDil)
        val currentPass = view!!.findViewById<EditText>(R.id.edtCurrentPasswordDialogPass)
        dialog = activity?.let { Dialog(it) }
        tvDateFromDilog.text=s

        if (view.parent != null) {
            (view.parent as ViewGroup).removeView(view) // <- fix
        }
        dialog!!.setContentView(view)
        dialog?.setCancelable(true)

        dialog?.show()
        mobile.setText(sessionManager.userMobile)
        fullName.setText(sessionManager.userName)

        btnChange.setOnClickListener {
            if (currentPass.text!!.isEmpty()) {
                currentPass.error = "Enter Current Password"
                currentPass.requestFocus()
                return@setOnClickListener
            }
            if (newPass.text!!.isEmpty()) {
                newPass.error = "Enter New Password"
                newPass.requestFocus()
                return@setOnClickListener
            }
            val password = newPass.text.toString().trim()
            val currentPass = currentPass.text.toString().trim()
            val name = fullName.text.toString().trim()
            val mobile = mobile.text.toString().trim()

            apiCallUpdateSetting(name, mobile, currentPass, password)
            dialog?.dismiss()
        }
    }

    private fun apiCallDeleteAccount() {
        AppProgressBar.showLoaderDialog(activity)
        ApiClient.apiService.deleteAccount(
            sessionManager.authToken,
            sessionManager.userEmail
        )
            .enqueue(object : Callback<ModelDestoryCart> {
                override fun onResponse(
                    call: Call<ModelDestoryCart>, response: Response<ModelDestoryCart>
                ) {
                    try {
                        if (response.code() == 500) {
                            activity?.let { myToast(it, "Server Error") }
                            AppProgressBar.hideLoaderDialog()
                        } else if (response.code() == 404) {
                            activity?.let { myToast(it, "Something went wrong") }
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.code() == 200) {
                            activity?.let { myToast(it, "Successfully Account Deleted") }
                            sessionManager.userMobile = ""
                            sessionManager.authTokenUser = ""
                            sessionManager.userName = ""
                            sessionManager.userEmail = ""
                            sessionManager.deviceId = ""
                            AppProgressBar.hideLoaderDialog()
                            val intent = Intent(activity, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

                            startActivity(intent)
                        } else {
                            AppProgressBar.hideLoaderDialog()

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        activity?.let { myToast(it, "Something went wrong") }
                        AppProgressBar.hideLoaderDialog()


                    }
                }

                override fun onFailure(call: Call<ModelDestoryCart>, t: Throwable) {
                    //myToast(requireActivity(), "Something went wrong")
                    countDes++
                    if (countDes <= 2) {
                        Log.e("count", countDes.toString())
                        apiCallDeleteAccount()
                    } else {
                        activity?.let { myToast(it, t.message.toString()) }
                        AppProgressBar.hideLoaderDialog()

                    }
                    // AppProgressBar.hideLoaderDialog()


                }

            })
    }
}