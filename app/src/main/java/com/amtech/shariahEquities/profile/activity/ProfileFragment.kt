package com.amtech.shariahEquities.profile.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.amtech.shariahEquities.sharedpreferences.SessionManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sellacha.tlismiherbs.R
import com.sellacha.tlismiherbs.databinding.FragmentProfileBinding

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
        sessionManager=SessionManager(requireActivity())
        with(binding) {

            val imgClose = parentView.findViewById<ImageView>(R.id.imgBackDil)
            val login = parentView.findViewById<Button>(R.id.btnLoginDil)

            imgClose.setOnClickListener {

                bottomSheetDialog.dismiss()
            }
            login.setOnClickListener {
               // startActivity(Intent(activity, Login::class.java))
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

            cardOpenDemat.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://ekyc.motilaloswal.com/Partner/?diyid=429a5fc3-4983-4443-b1fa-4c5a6a6ceacc"))
                    requireContext().startActivity(Intent.createChooser(intent, "Choose browser"))

            }


            if (sessionManager.authTokenUser!!.isNotEmpty()) {
               // apiCallGetSetting()
            }


        }

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

             dialog?.dismiss()
        }
    }

 }