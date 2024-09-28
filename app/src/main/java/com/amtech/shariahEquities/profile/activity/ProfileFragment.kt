package com.amtech.shariahEquities.profile.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.amtech.shariahEquities.Helper.AppProgressBar
import com.amtech.shariahEquities.forgotPass.model.ModelResetPass
import com.amtech.shariahEquities.login.Login
import com.amtech.shariahEquities.payment.Payment
import com.amtech.shariahEquities.profile.activity.adapter.AdapterTransList
import com.amtech.shariahEquities.profile.activity.model.ModelTransList
import com.amtech.shariahEquities.retrofit.ApiClient
import com.amtech.shariahEquities.sharedpreferences.SessionManager
import com.example.tlismimoti.Helper.myToast
import com.google.android.gms.common.api.Api
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rajat.pdfviewer.PdfViewerActivity
import com.sellacha.tlismiherbs.R
 import com.sellacha.tlismiherbs.databinding.FragmentProfileNewBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ProfileFragment : Fragment(),AdapterTransList.Download {
    private lateinit var binding: FragmentProfileNewBinding
      lateinit var sessionManager: SessionManager
    private lateinit var adapterTransList: AdapterTransList
    var dialog: Dialog? = null
    var count = 0
    var countT = 0
    var count1 = 0
    var countU = 0
    var countDes = 0
    private var transList = ArrayList<com.amtech.shariahEquities.profile.activity.model.Result>()

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
        apiTransList()
        with(binding) {

            tvFullName.text=sessionManager.userName
            tvEmail.text=sessionManager.email
            tvMobile.text=sessionManager.userMobile

            if (sessionManager.subscribed.toString()!="0"){
                binding.upgradeBtn.visibility=View.GONE
            }
            try {
                val fullName = sessionManager.userName
                val nameParts = fullName!!.split(" ")
                if (nameParts.size >= 2) {
                     val initials = "${nameParts[0].first()}${nameParts[1].first()}"
                    tvNameTag.text = initials
                }else
                {
                    tvNameTag.text = fullName[0].toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
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
                    .putExtra("link", "https://shariahequities.in")
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

            cardTransaction.setOnClickListener {
                showTransDialog()
            }


            if (sessionManager.authTokenUser!!.isNotEmpty()) {
               // apiCallGetSetting()
            }


        }

    }
    @SuppressLint("MissingInflatedId")
    private fun showTransDialog() {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.popup_transaction_list, null)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.rvDialogCompanyList)
        val btnSave = dialogView.findViewById<Button>(R.id.btnClose)
        val tvNoTansFound = dialogView.findViewById<TextView>(R.id.tvNoTansFound)
        val edtSearch = dialogView.findViewById<EditText>(R.id.edtSearch)
         val close = dialogView.findViewById<ImageView>(R.id.imgClose)

        if (transList.isEmpty()){
            tvNoTansFound.visibility=View.VISIBLE
        }else{
            adapterTransList = AdapterTransList(requireContext(),transList,this@ProfileFragment)
            recyclerView.adapter = adapterTransList

        }


//         edtSearch.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                if (s!!.isNotEmpty()){
//                    recyclerView.scrollToPosition(0)
//                }
//                val filteredList = transList.filter {
//                    it.name_of_company.contains(s.toString(), ignoreCase = true) ||
//                            it.nse_symbol_bse_script_id.contains(s.toString(), ignoreCase = true)
//                }
//                adapterTransList.submitList(filteredList)
//            }

//            override fun afterTextChanged(s: Editable?) {}
//        })
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        close.setOnClickListener {
            dialog!!.dismiss()
        }
        btnSave.setOnClickListener {
                dialog!!.dismiss()
        }

        dialog!!.show()
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
    private fun apiTransList() {
       // AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.transList(
            sessionManager.id.toString()
        )
            .enqueue(object : Callback<ModelTransList> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ModelTransList>, response: Response<ModelTransList>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(requireActivity(), "Server Error")
                            AppProgressBar.hideLoaderDialog()


                        } else if (response.code() == 404) {
                            myToast(requireActivity(), "Something went wrong")
                            AppProgressBar.hideLoaderDialog()

                        } else {
                            if (response.body()!!.result.isNotEmpty()){
                                if (response.body()!!.status==1) {
                                    transList=response.body()!!.result
                                }
                            }

                            AppProgressBar.hideLoaderDialog()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(requireActivity(), "Something went wrong")
                        AppProgressBar.hideLoaderDialog()
                    }
                }

                override fun onFailure(call: Call<ModelTransList>, t: Throwable) {
                    countT++
                    if (countT <= 3) {
                        apiTransList()
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
    fun apiCallDownloadPdfAndOpen(userId: String, paymentId: String) {
        AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.getInvoice(userId, paymentId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        count1 =0
                        val fileName = "invoice_$userId$paymentId.pdf"
                        val savedFile = saveFileToStorage(body, fileName)
                        if (savedFile != null) {
                            AppProgressBar.hideLoaderDialog()
                            openPdf(savedFile)  // Open the PDF once it is saved
                        } else {
                            AppProgressBar.hideLoaderDialog()
                            myToast(context as Activity, "Failed to save file")
                        }
                    } ?: run {
                        AppProgressBar.hideLoaderDialog()
                        Log.e("API Error", "Empty response body")
                        myToast(context as Activity, "Received empty response")
                    }
                } else {
                    AppProgressBar.hideLoaderDialog()
                    Log.e("API Error", "Response Code: ${response.code()}")
                    val errorMessage = when (response.code()) {
                        404 -> "Invoice not found"
                        500 -> "Server error, please try again later"
                        else -> "Unexpected error: ${response.code()}"
                    }
                    myToast(context as Activity, errorMessage)

                    response.errorBody()?.let {
                        Log.e("API Error Body", "Error Body: ${it.string()}")
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                AppProgressBar.hideLoaderDialog()
                Log.e("Download Error", "Error message: ${t.message}")
                count1++
                if (count1 <= 2) {
                    apiCallDownloadPdfAndOpen(userId,paymentId)
                } else {
                    myToast(context as Activity, "Something went wrong")
                }
                when (t) {
                    is IOException -> {
                        myToast(context as Activity, "Network error, please check your connection")
                    }
                    else -> {
                        myToast(context as Activity, "Failed to download file: ${t.localizedMessage}")
                    }
                }
            }
        })
    }


    private fun openPdf(file: File) {
        val uri = FileProvider.getUriForFile(context as Activity, "${context?.packageName}.provider", file)

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            myToast(context as Activity, "No application to view PDF")
        }
    }

    private fun saveFileToStorage(body: ResponseBody, fileName: String): File? {
        return try {
            val downloadDir = context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadDir, fileName)
            val inputStream = body.byteStream()
            val outputStream = FileOutputStream(file)
            val buffer = ByteArray(4096)
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            outputStream.flush()
            outputStream.close()
            inputStream.close()

            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    override fun downloadInvoice(id: String) {
        apiCallDownloadPdfAndOpen(sessionManager.id.toString(),id)

    }

}