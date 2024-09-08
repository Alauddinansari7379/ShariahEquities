package com.amtech.shariahEquities

//import com.google.android.play.core.appupdate.AppUpdateInfo
//import com.google.android.play.core.appupdate.AppUpdateManagerFactory
//import com.google.android.play.core.install.model.UpdateAvailability
//import com.google.android.play.core.tasks.Task
import android.Manifest
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.amtech.shariahEquities.sharedpreferences.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.sellacha.tlismiherbs.R
import com.sellacha.tlismiherbs.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var context = this@MainActivity
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val NOTIFICATION_PERMISSION_CODE = 123

    lateinit var sessionManager: SessionManager
    private lateinit var bottomNav: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        sessionManager = SessionManager(context)
        bottomNav = binding.bottomNavigationView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.hostFragment)
        val navController = navHostFragment!!.findNavController()
        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.bootom_nav_menu)
        binding.bottomNavigationView.setupWithNavController(navController)
        if (sessionManager.deviceId!!.isEmpty()) {
            val deviceID = getDeviceId(context)
            sessionManager.deviceId = deviceID
        }
        val bottomSheetDialog = BottomSheetDialog(context)
        val parentView: View = layoutInflater.inflate(R.layout.login_dialog, null)
        bottomSheetDialog.setContentView(parentView)
        val imgCloseNew = parentView.findViewById<ImageView>(R.id.imgBackDil)
        val login = parentView.findViewById<Button>(R.id.btnLoginDil)

        imgCloseNew.setOnClickListener {
            bottomSheetDialog.dismiss()

        }
        login.setOnClickListener {
           // startActivity(Intent(context, Login::class.java))
        }

        Log.e("DeviceId", sessionManager.deviceId.toString())
        requestNotificationPermission()
        Log.e("authTokenUser", sessionManager.authTokenUser.toString())
        Log.e("userEmail", sessionManager.userEmail.toString())
        Log.e("userName", sessionManager.userName.toString())
        Log.e("sessionManager.fcmToken", sessionManager.fcmToken.toString())

        if (sessionManager.randomKey!!.isEmpty()){
            sessionManager.randomKey=generateRandomString(10)
            val randomString = generateRandomString(10)
            println("Random String: $randomString")
        }

//        checkForUpdate(this)
//        checkForUpdate(this)
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                Log.e("Notification", "onCreate: PERMISSION GRANTED")
                // sendNotification(this)
            }

            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                Snackbar.make(
                    findViewById<View>(android.R.id.content).rootView,
                    "Notification blocked",
                    Snackbar.LENGTH_LONG
                ).setAction("Settings") {
                    // Responds to click on the action
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    val uri: Uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }.show()
            }


            else -> {
                // The registered ActivityResultCallback gets the result of this request
                requestPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
        with(binding) {
            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.fragment_explore -> {
                        appCompatTextView2.text = "Explore"
                    }
                    R.id.fragment_search -> {
                        appCompatTextView2.text = "Search"
                    }
                    R.id.fragment_watchlist -> {
                        appCompatTextView2.text = "WatchList"
                        if (sessionManager.authTokenUser!!.isEmpty()){
                            try {
                                bottomSheetDialog.show()
                            }catch (e:Exception){
                                e.printStackTrace()
                            }
                        }
                    }
                    R.id.fragment_portfolio -> {
                        appCompatTextView2.text = "Profile"
                        if (sessionManager.authTokenUser!!.isEmpty()){
                            try {
                                bottomSheetDialog.show()
                            }catch (e:Exception){
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }

            tvUserName.text=sessionManager.userName
            btnUpgrade.setOnClickListener {
                if (sessionManager.authTokenUser!!.isEmpty()){
                    try {
                        bottomSheetDialog.show()
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }            }
        }

    }
    private fun getDeviceId(context: Context): String? {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

    }
//    private fun checkForUpdate(context: Context) {
//        val appUpdateManager = AppUpdateManagerFactory.create(context)
//        val appUpdateInfoTask: Task<AppUpdateInfo> = appUpdateManager.appUpdateInfo
//
//        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
//            val currentVersionCode = context.packageManager
//                .getPackageInfo(context.packageName, 0).versionCode
//
//            Log.d("UpdateCheck", "Installed version: $currentVersionCode")
//            Log.d("UpdateCheck", "Play Store version: ${appUpdateInfo.availableVersionCode()}")
//
//            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
//                appUpdateInfo.clientVersionStalenessDays() ?: -1 >= 0) {
//                Log.d("UpdateCheck", "Update available")
//                showUpdateDialog(context)
//            } else {
//                Log.d("UpdateCheck", "No update available")
//            }
//        }
//    }
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                //  sendNotification(this)
                // myToast(this@MainActivity,"Permission granted")
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }

    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY
            ) == PackageManager.PERMISSION_GRANTED
        ) return
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY
            )
        ) {
        }
        ActivityCompat.requestPermissions(
            this,
            arrayOf<String>(Manifest.permission.ACCESS_NOTIFICATION_POLICY),
            NOTIFICATION_PERMISSION_CODE
        )
    }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            hasNotificationPermissionGranted = isGranted
            if (!isGranted) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Build.VERSION.SDK_INT >= 33) {
                        if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                            showNotificationPermissionRationale()
                        } else {
                            showSettingDialog()
                        }
                    }
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "notification permission granted",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    var hasNotificationPermissionGranted = false
    private val isNotificationListenerEnabled: Boolean
        private get() {
            val pkgName = packageName
            val cn = ComponentName(pkgName, "$pkgName.NotificationListener")
            val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
            return flat != null && flat.contains(cn.flattenToString())
        }
    private fun showSettingDialog() {
        MaterialAlertDialogBuilder(
            this,
            com.google.android.material.R.style.MaterialAlertDialog_Material3
        )
            .setTitle("Notification Permission")
            .setMessage("Notification permission is required, Please allow notification permission from setting")
            .setPositiveButton("Ok") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    private fun generateRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
    private fun showNotificationPermissionRationale() {

        MaterialAlertDialogBuilder(
            this,
            com.google.android.material.R.style.MaterialAlertDialog_Material3
        )
            .setTitle("Alert")
            .setMessage("Notification permission is required, to show notification")
            .setPositiveButton("Ok") { _, _ ->
                if (Build.VERSION.SDK_INT >= 33) {
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Checking the request code of our request
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            // If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Displaying a toast
                Toast.makeText(
                    this,
                    "Permission granted now you can read the storage",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                // Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
    private fun showUpdateDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Update Available")
        builder.setMessage("A new version of the app is available. Please update to the latest version.")
        builder.setPositiveButton("Update") { dialog, _ ->
            // Redirect to Play Store
            val appPackageName = context.packageName
            try {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
            } catch (e: android.content.ActivityNotFoundException) {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.setCancelable(false)
        builder.show()
    }
}