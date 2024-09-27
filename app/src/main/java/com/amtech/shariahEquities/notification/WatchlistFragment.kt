package com.amtech.shariahEquities.notification

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.amtech.shariahEquities.Helper.AppProgressBar
import com.amtech.shariahEquities.notification.adapter.WatchListAdapter
import com.amtech.shariahEquities.notification.adapter.moduledeletewatchlist.ModuleDeleteWatchList
import com.amtech.shariahEquities.notification.modelwatchlist.ModelWatchList
import com.amtech.shariahEquities.notification.modelwatchlist.Result
import com.amtech.shariahEquities.retrofit.ApiClient
import com.amtech.shariahEquities.sharedpreferences.SessionManager
import com.example.tlismimoti.Helper.myToast
import com.github.dhaval2404.imagepicker.ImagePicker.Companion.REQUEST_CODE
import com.google.android.material.snackbar.Snackbar
import com.sellacha.tlismiherbs.R
import com.sellacha.tlismiherbs.databinding.FragmentStocksBinding
import com.sellacha.tlismiherbs.databinding.FragmentWatchlistBinding
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class WatchlistFragment : Fragment(), WatchListAdapter.Delete {
    private var _binding: FragmentWatchlistBinding? = null
    private val binding get() = _binding!!
    private var watchList = ArrayList<Result>()
    private var count = 0
    private var countRe = 0
    private var countDe = 0
    lateinit var sessionManager: SessionManager
    private lateinit var watchListAdapter: WatchListAdapter
    private var companyList = mutableListOf<Result>()
    private lateinit var workbook: XSSFWorkbook  // Declare workbook as a class variable
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWatchlistBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())
        apiCallGetWatchList()
        initRecyclerView()
        // setupSwipeToDelete()
        setupSpinner()


        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                performSearch(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.btnExport.setOnClickListener {
            checkAndRequestStoragePermission()
            exportToExcel()

        }
        return binding.root
    }

    private fun setupSpinner() {
        val filterOptions = ArrayList<String>()
        filterOptions.add("All")
        filterOptions.add("Compliant")
        filterOptions.add("Non-Compliant")

        val adapter = ArrayAdapter(requireContext(), R.layout.simple_list_item_1, filterOptions)
        adapter.setDropDownViewResource(R.layout.simple_list_item_1)
        binding.spinnerFilter.adapter = adapter
        binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedFilter = filterOptions[position]
                filterListByCompliance(selectedFilter)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        binding.spinnerFilter.setSelection(0)
    }

    private fun filterListByCompliance(selectedFilter: String) {
        val filteredList = when (selectedFilter) {
            "All" -> watchList
            "Compliant" -> watchList.filter { it.complaint_type == 1 }
            "Non-Compliant" -> watchList.filter { it.complaint_type == 0 }
            else -> watchList
        }
        setRecyclerViewAdapter(ArrayList(filteredList))
    }

    private fun initRecyclerView() {
        watchListAdapter = WatchListAdapter(watchList, this)
        binding.rvWatchlist.apply {
            adapter = watchListAdapter
        }
    }

    private fun apiCallGetWatchList() {
        AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.getWatchList(sessionManager.id.toString())
            .enqueue(object : Callback<ModelWatchList> {
                override fun onResponse(
                    call: Call<ModelWatchList>, response: Response<ModelWatchList>
                ) {
                    AppProgressBar.hideLoaderDialog()
                    try {
                        if (response.isSuccessful && response.body() != null) {
                            val responseBody = response.body()!!
                            watchList.clear()
                            watchList.addAll(responseBody.result)
                            watchListAdapter.notifyDataSetChanged()
                            filterListByCompliance("All")
                            //  myToast(context as Activity, responseBody.message)
                        } else {
                            myToast(context as Activity, "Unexpected error")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context as Activity, "Something went wrong")
                    }
                }

                override fun onFailure(call: Call<ModelWatchList>, t: Throwable) {
                    count++
                    if (count <= 2) {
                        apiCallGetWatchList()
                    } else {
                        myToast(context as Activity, "Something went wrong")
                    }
                    AppProgressBar.hideLoaderDialog()
                }
            })
    }

    private fun apiCallGetWatchListRe() {
        //  AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.getWatchList(sessionManager.id.toString())
            .enqueue(object : Callback<ModelWatchList> {
                override fun onResponse(
                    call: Call<ModelWatchList>, response: Response<ModelWatchList>
                ) {
                    AppProgressBar.hideLoaderDialog()
                    try {
                        if (response.isSuccessful && response.body() != null) {
                            val responseBody = response.body()!!
                            watchList.clear()
                            watchList.addAll(responseBody.result)
                            watchListAdapter.notifyDataSetChanged()
                            filterListByCompliance("All")
                            //  myToast(context as Activity, responseBody.message)
                        } else {
                            myToast(context as Activity, "Unexpected error")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context as Activity, "Something went wrong")
                    }
                }

                override fun onFailure(call: Call<ModelWatchList>, t: Throwable) {
                    countRe++
                    if (countRe <= 3) {
                        apiCallGetWatchListRe()
                    } else {
                        myToast(context as Activity, "Something went wrong")
                    }
                    AppProgressBar.hideLoaderDialog()
                }
            })
    }

    private fun performSearch(query: String) {
        val trimmedQuery = query.trim()

        // Show all data when query is empty
        val filteredList = if (trimmedQuery.isEmpty()) {
            watchList // Show the full list if no search query is entered
        } else {
            watchList.filter { result ->
                result.name_of_company?.contains(trimmedQuery, ignoreCase = true) == true ||
                        result.symbol?.contains(trimmedQuery, ignoreCase = true) == true
            }
        }

        // Update the adapter with the filtered list
        setRecyclerViewAdapter(ArrayList(filteredList))
    }

    private fun setRecyclerViewAdapter(filteredList: ArrayList<Result>) {
        watchListAdapter =
            WatchListAdapter(filteredList, this) // Update the adapter's data
        binding.rvWatchlist.adapter = watchListAdapter // Set the new adapter
        watchListAdapter.notifyDataSetChanged() // Notify the adapter about the data change
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    /*    private fun handleDeletionError(position: Int) {
            watchListAdapter.notifyItemChanged(position)
            Snackbar.make(
                binding.root,
                "Failed to delete item. Please try again.",
                Snackbar.LENGTH_LONG
            )
                .setAction("Retry") {
                    val itemToRetry = watchList[position]
                    deleteWatchListItem(itemToRetry, position)
                }
                .show()
        }*/


    /*
        private fun setupSwipeToDelete() {
            val itemTouchHelperCallback = object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    if (position >= 0 && position < watchList.size) {
                        val itemToDelete = watchList[position]
                        showDeleteConfirmationDialog(itemToDelete, position)
                    }
                    watchListAdapter.notifyDataSetChanged()
                }

            }

            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper.attachToRecyclerView(binding.rvWatchlist)
        }
    */


    private fun showDeleteConfirmationDialog(id: String) {
        val sweetAlertDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Are you sure you want to delete this item?")
            .setConfirmText("Yes")
            .setCancelText("No")
            .showCancelButton(true)
            .setConfirmClickListener { sDialog ->
                sDialog.dismissWithAnimation()
                apiDeleteWatchList(id)

            }
            .setCancelClickListener { sDialog ->
                sDialog.dismissWithAnimation()
            }

        sweetAlertDialog.setCanceledOnTouchOutside(false)
        sweetAlertDialog.setCancelable(false)
        sweetAlertDialog.show()
    }

    override fun delete(id: String) {
        showDeleteConfirmationDialog(id)
    }

    private fun apiDeleteWatchList(id: String) {
        ApiClient.apiService.deleteWatchList(id, sessionManager.id.toString())
            .enqueue(object : Callback<ModuleDeleteWatchList> {
                override fun onResponse(
                    call: Call<ModuleDeleteWatchList>,
                    response: Response<ModuleDeleteWatchList>
                ) {
                    if (response.body()!!.status == 1) {
                        //  watchList.removeAt(position)
                        apiCallGetWatchListRe()
                        countDe = 0
//                        watchListAdapter.notifyItemRemoved(position)
//                        watchListAdapter.notifyDataSetChanged()
                        myToast(context as Activity, "Item deleted successfully")
                    } else {
                        when (response.code()) {
                            400 -> myToast(context as Activity, "Bad Request: Invalid data")
                            401 -> myToast(context as Activity, "Unauthorized: Please login again")
                            404 -> myToast(context as Activity, "Not Found: Item not found")
                            500 -> myToast(context as Activity, "Server Error: Try again later")
                            else -> myToast(context as Activity, "Unexpected error occurred")
                        }
                        //  handleDeletionError(position)
                    }
                }

                override fun onFailure(call: Call<ModuleDeleteWatchList>, t: Throwable) {
                    // handleDeletionError(position)
                    countDe++
                    if (countDe <= 3) {
                        apiDeleteWatchList(id)
                    } else {
                        myToast(context as Activity, "Network Error: ${t.message}")
                    }
                }
            })
    }

    private fun exportToExcel() {
        try {
            // Initialize the workbook and create a new sheet
            workbook = XSSFWorkbook()  // Initialize workbook
            val sheet = workbook.createSheet("Watchlist")

            // Set the width of the columns
            sheet.setColumnWidth(0, 35 * 256) // Name of Company (30 characters wide)
            sheet.setColumnWidth(1, 25 * 256) // Symbol (25 characters wide)
            sheet.setColumnWidth(2, 20 * 256) // Complaint Type (20 characters wide)

            // Create a font for the header
            val headerFont = workbook.createFont().apply {
                bold = true  // Set font to bold
                fontHeightInPoints = 12.toShort() // Set font size
            }

            // Create a style for the header
            val headerStyle = workbook.createCellStyle().apply {
                setFont(headerFont)  // Apply the bold font to the style
            }

            // Create header row and set row height
            val headerRow = sheet.createRow(0)

            // Create cells and set values with the header style
            val cell0 = headerRow.createCell(0)
            cell0.setCellValue("Name of Company")
            cell0.cellStyle = headerStyle  // Apply the header style

            val cell1 = headerRow.createCell(1)
            cell1.setCellValue("Symbol")
            cell1.cellStyle = headerStyle  // Apply the header style

            val cell2 = headerRow.createCell(2)
            cell2.setCellValue("Complaint Type")
            cell2.cellStyle = headerStyle  // Apply the header style

            headerRow.heightInPoints = 20f  // Set header row height (in points)

            // Create styles for compliant and non-compliant cells
            val compliantStyle = workbook.createCellStyle().apply {
                fillForegroundColor = IndexedColors.LIGHT_GREEN.index
                fillPattern = FillPatternType.SOLID_FOREGROUND
            }

            val nonCompliantStyle = workbook.createCellStyle().apply {
                fillForegroundColor = IndexedColors.RED.index
                fillPattern = FillPatternType.SOLID_FOREGROUND
            }

            // Fill in the data
            for (i in watchList.indices) {
                val row = sheet.createRow(i + 1)
                row.heightInPoints = 18f  // Set row height (in points)
                row.createCell(0).setCellValue(watchList[i].name_of_company)
                row.createCell(1).setCellValue(watchList[i].nse_symbol_bse_script_id)
                val complaintCell = row.createCell(2)
                if (watchList[i].final == "PASS") {
                    complaintCell.setCellValue("Compliant")
                    complaintCell.cellStyle = compliantStyle
                } else {
                    complaintCell.setCellValue("Non-Compliant")
                    complaintCell.cellStyle = nonCompliantStyle
                }
            }

            // Create an Intent to prompt the user to select a location to save the file
            promptSaveFile()  // Call method to prompt save

        } catch (e: Exception) {
            Log.e("Excel", "Error exporting watchlist", e)
            Toast.makeText(requireContext(), "Error exporting watchlist", Toast.LENGTH_SHORT).show()
        }
    }

    // Method to prompt for saving the file
    private fun promptSaveFile() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            putExtra(Intent.EXTRA_TITLE, "Watchlist.xlsx")  // Set default file name
        }

        startActivityForResult(intent, 101)  // Define CREATE_FILE_REQUEST_CODE
    }

    // Handle the result of the file save
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            data?.data?.also { uri ->
                try {
                    requireContext().contentResolver.openOutputStream(uri)?.use { outputStream ->
                        workbook.write(outputStream)
                        Log.d("Excel", "File written to: $uri")
                    } ?: run {
                        Log.e("Excel", "Output stream is null")
                    }
                } catch (e: IOException) {
                    Log.e("Excel", "Error writing to file", e)
                }
            }
        }

     }


    private fun checkAndRequestStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE
            )
        } else {
            // Permission is already granted, proceed with export
            exportToExcel()
        }
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted, proceed with export
                exportToExcel()
            } else {
                // Permission denied, notify the user
                myToast(requireActivity(), "Permission denied, unable to export Watchlist.")
            }
        }
    }

}
