package com.amtech.shariahEquities.profile.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.amtech.shariahEquities.Helper.AppProgressBar
import com.amtech.shariahEquities.forgotPass.model.ModelResetPass
import com.amtech.shariahEquities.login.Login
import com.amtech.shariahEquities.payment.Payment
import com.amtech.shariahEquities.retrofit.ApiClient
import com.amtech.shariahEquities.sharedpreferences.SessionManager
import com.example.tlismimoti.Helper.myToast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rajat.pdfviewer.PdfViewerActivity
import com.sellacha.tlismiherbs.R
 import com.sellacha.tlismiherbs.databinding.FragmentProfileNewBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileNewBinding
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
        return inflater.inflate(R.layout.fragment_profile_new, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileNewBinding.bind(view)
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val parentView: View = layoutInflater.inflate(R.layout.login_dialog, null)
        bottomSheetDialog.setContentView(parentView)
        sessionManager=SessionManager(requireActivity())
        with(binding) {

            tvFullName.text=sessionManager.userName
            tvEmail.text=sessionManager.email
            tvMobile.text=sessionManager.userMobile

            if (sessionManager.subscribed.toString()!="0"){
                binding.upgradeBtn.visibility=View.GONE
            }
            val fullName = sessionManager.userName
            val nameParts = fullName!!.split(" ")
            if (nameParts.size >= 2) {
                 val initials = "${nameParts[0].first()}${nameParts[1].first()}"
                tvNameTag.text = initials
            }else
            {
                tvNameTag.text = fullName
            }
            Log.e("Email",sessionManager.userEmail.toString())
            val imgClose = parentView.findViewById<ImageView>(R.id.imgBackDil)
            val login = parentView.findViewById<Button>(R.id.btnSubscribe)

            imgClose.setOnClickListener {

                bottomSheetDialog.dismiss()
            }
            login.setOnClickListener {
               // startActivity(Intent(activity, Login::class.java))
            }
            val bottomSheetDialog = BottomSheetDialog(requireContext())
            val parentView: View = layoutInflater.inflate(R.layout.login_dialog, null)
            bottomSheetDialog.setContentView(parentView)
            val imgCloseNew = parentView.findViewById<ImageView>(R.id.imgBackDil)
            val btnSubscribe = parentView.findViewById<Button>(R.id.btnSubscribe)

            imgCloseNew.setOnClickListener {
                bottomSheetDialog.dismiss()

            }
            btnSubscribe.setOnClickListener {
                startActivity(Intent(context, Payment::class.java))
            }

            binding.upgradeBtn.setOnClickListener {
                if (sessionManager.subscribed=="0"){
                    try {
                        bottomSheetDialog.show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            btnSignOut.setOnClickListener {
                SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Are you sure want to Logout?")
                    .setConfirmText("Yes")
                    .setCancelText("No")
                     .showCancelButton(true)
                    .setConfirmClickListener { sDialog ->
                        sDialog.cancel()
                        sessionManager.logout()
                        val intent = Intent(requireContext(), Login::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    .setCancelClickListener { sDialog ->
                        sDialog.cancel()
                    }
                    .show()

            }
            cardDelete.setOnClickListener {
                SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Are you sure want to Delete Account?")
                    .setConfirmText("Yes")
                    .setCancelText("No")
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



            cardPrivacyPolicy.setOnClickListener {
                startActivity(
                     PdfViewerActivity.launchPdfFromUrl(
                        context,
                        "http://ehcf.in/public/Privacy_policy.pdf",
                        "Privacy Policy",
                        "",
                        enableDownload = true
                    )
                )
//
//                val intent = Intent(context as Activity, PrivacyPolicy::class.java)
//                    .putExtra("title", "Privacy Policy")
//                (context as Activity).startActivity(intent)
            }
            cardReturnPolicy.setOnClickListener {
                startActivity(
                    PdfViewerActivity.launchPdfFromUrl(
                        context,
                        "http://ehcf.in/public/Return_&_Refund_Policy.pdf",
                        "Return & Refund Policy",
                        "",
                        enableDownload = true
                    )
                )
            }
            cardScreening.setOnClickListener {
                startActivity(
                    PdfViewerActivity.launchPdfFromUrl(
                        context,
                        "http://ehcf.in/public/Shariah_Equities_Screening_Methodology.pdf",
                        "Screening Methodology",
                        "",
                        enableDownload = true
                    )
                )
            }
            cardTrems.setOnClickListener {
                startActivity(
                    PdfViewerActivity.launchPdfFromUrl(
                        context,
                        "http://ehcf.in/public/Shariah_Equities_Terms_and_Conditions.pdf",
                        "Terms and Conditions",
                        "",
                        enableDownload = true
                    )
                )
            }

//            cardOpenDemat.setOnClickListener {
//                val intent = Intent(context as Activity, PrivacyPolicy::class.java)
//                    .putExtra("title", "Open Demat Account")
//                    .putExtra("link", "https://ekyc.motilaloswal.com/Partner/?diyid=429a5fc3-4983-4443-b1fa-4c5a6a6ceacc")
//                (context as Activity).startActivity(intent)
//
////                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://ekyc.motilaloswal.com/Partner/?diyid=429a5fc3-4983-4443-b1fa-4c5a6a6ceacc"))
////                    requireContext().startActivity(Intent.createChooser(intent, "Choose browser"))
//
//            }

            cardContactUs.setOnClickListener {
                val intent = Intent(context as Activity, PrivacyPolicy::class.java)
                    .putExtra("title", "Contact Us")
                    .putExtra("link", "https://shariahequities.in/faq/")
                (context as Activity).startActivity(intent)
            }

            aboutConst.setOnClickListener {
                val intent = Intent(context as Activity, PrivacyPolicy::class.java)
                    .putExtra("title", "About Shariah Equities")
                    .putExtra("link", "https://shariahequities.in/faq/")
                (context as Activity).startActivity(intent)
            }

            cardFAQ.setOnClickListener {
                val intent = Intent(context as Activity, PrivacyPolicy::class.java)
                    .putExtra("title", "FAQs")
                    .putExtra("link", "https://shariahequities.in/faq/")
                (context as Activity).startActivity(intent)
            }

            cardFeedback.setOnClickListener {
                val intent = Intent(context as Activity, PrivacyPolicy::class.java)
                    .putExtra("title", "Feedback")
                    .putExtra("link", "https://forms.gle/atZmhwAk8EL5PZY18")
                (context as Activity).startActivity(intent)
            }

            cardChangePass.setOnClickListener {
                changePassDialog("Change Password")
            }


            if (sessionManager.authTokenUser!!.isNotEmpty()) {
               // apiCallGetSetting()
            }


        }

    }


    private fun changePassDialog(s: String) {
        val view = layoutInflater.inflate(R.layout.dialog_password_change, null)
        dialog = context?.let { Dialog(it) }

        val btnChange = view!!.findViewById<Button>(R.id.btnChangeDialogPass)
        val tvDateFromDilog = view!!.findViewById<TextView>(R.id.tvDateFromDilog)
        val newPass = view!!.findViewById<EditText>(R.id.edtNewPasswordDialogPass)
        val fullName = view!!.findViewById<EditText>(R.id.edtFullNameDil)
        val mobile = view!!.findViewById<EditText>(R.id.edtMobileDil)
        val currentPass = view!!.findViewById<EditText>(R.id.edtCurrentPasswordDialogPass)
        dialog = context?.let { Dialog(it) }
        tvDateFromDilog.text = s

        if (view.parent != null) {
            (view.parent as ViewGroup).removeView(view) // <- fix
        }
        dialog!!.setContentView(view)
        dialog?.setCancelable(true)

        fullName.visibility = View.GONE
        mobile.visibility = View.GONE
        currentPass.setHint("New Password")
        newPass.setHint("Confirm Password")
        dialog?.show()
//        mobile.setText(sessionManager.userMobile)
//        fullName.setText(sessionManager.userName)

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

            // apiCallUpdateSetting(name, mobile, currentPass, password)
            dialog?.dismiss()
            apiCallResetPass(sessionManager.email.toString(), currentPass)
        }
    }
    private fun apiCallResetPass(email: String, pass: String) {
        AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.resetPass(
            email,
            pass,
        )
            .enqueue(object : Callback<ModelResetPass> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ModelResetPass>, response: Response<ModelResetPass>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(requireActivity(), "Server Error")
                            AppProgressBar.hideLoaderDialog()


                        } else if (response.code() == 404) {
                            myToast(requireActivity(), "Something went wrong")
                            AppProgressBar.hideLoaderDialog()

                        } else {
                            myToast(requireActivity(), response.body()!!.message)
                            if (response.body()!!.status==1) {
                                myToast(requireActivity(), "Password Changed successfully")

                            }
                            AppProgressBar.hideLoaderDialog()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(requireActivity(), "Something went wrong")
                        AppProgressBar.hideLoaderDialog()
                    }
                }

                override fun onFailure(call: Call<ModelResetPass>, t: Throwable) {
                    count++
                    if (count <= 2) {
                        apiCallResetPass(email, pass)
                    } else {
                        myToast(requireActivity(), t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }

     private fun apiCallDeleteAccount() {
        AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.deleteUser(sessionManager.id.toString())
            .enqueue(object : Callback<ModelResetPass> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ModelResetPass>, response: Response<ModelResetPass>
                ) {
                    AppProgressBar.hideLoaderDialog()
                    try {
                        when {
                            response.code() == 500 -> myToast(context as Activity, "Server Error")
                            response.code() == 404 -> myToast(
                                context as Activity,
                                "Something went wrong"
                            )
                            response.isSuccessful && response.body() != null -> {
                                myToast(context as Activity, response.body()!!.message)
                                sessionManager.logout()
                                val intent = Intent(requireContext(), Login::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(intent)
                                requireActivity().finish()
                            }
                            else -> myToast(context as Activity, "Unexpected error")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context as Activity, "Something went wrong")
                    }
                }

                override fun onFailure(call: Call<ModelResetPass>, t: Throwable) {
                    count++
                    if (count <= 2) {
                        apiCallDeleteAccount()
                    } else {
                        myToast(context as Activity, "Something went wrong")
                    }
                    AppProgressBar.hideLoaderDialog()
                }
            })
    }

 }