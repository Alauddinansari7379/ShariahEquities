package com.amtech.shariahEquities.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.amtech.shariahEquities.Helper.AppProgressBar
import com.amtech.shariahEquities.fragments.model.ModelCompanyDetails
import com.amtech.shariahEquities.payment.Payment
import com.amtech.shariahEquities.retrofit.ApiClient
import com.amtech.shariahEquities.sharedpreferences.SessionManager
import com.ekn.gruzer.gaugelibrary.Range
import com.example.tlismimoti.Helper.myToast
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rajat.pdfviewer.PdfViewerActivity
import com.sellacha.tlismiherbs.R
import com.sellacha.tlismiherbs.databinding.ActivityComplianceReportBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ComplianceReportActivity : AppCompatActivity() {
    private var context: Context = this@ComplianceReportActivity
    var id = ""
    private var count = 0
    private lateinit var binding: ActivityComplianceReportBinding
    lateinit var sessionManager: SessionManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComplianceReportBinding.inflate(layoutInflater)
        id = intent.getIntExtra("id", 0).toString()
        sessionManager = SessionManager(context)
        binding.showDetailsButton.setOnClickListener {
            if (binding.showDetailsTxt.visibility == View.VISIBLE) {
                binding.showDetailsTxt.visibility = View.GONE
                binding.showDetailsButton.text = "Show Details"
            } else {
                binding.showDetailsTxt.visibility = View.VISIBLE
                binding.showDetailsButton.text = "Hide Details"
            }
        }
        binding.showdetailsbutton1.setOnClickListener {
            if (binding.securitiesShowDetailsTxt.visibility == View.VISIBLE) {
                binding.securitiesShowDetailsTxt.visibility = View.GONE
                binding.showdetailsbutton1.text = "Show Details"
            } else {
                binding.securitiesShowDetailsTxt.visibility = View.VISIBLE
                binding.showdetailsbutton1.text = "Hide Details"
            }
        }
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
        binding.standardsLabel.setOnClickListener {
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
        setContentView(binding.root)


        val bottomSheetDialog = BottomSheetDialog(context)
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

        if (sessionManager.subscribed != "0") {
            apiCallGetCompanyDetails(id)
        } else {
            binding.SampleReport.visibility = View.VISIBLE
            binding.layoutMain.visibility = View.GONE
         }

         binding.layoutViewSample.setOnClickListener {
            startActivity(Intent(context, ComplianceSampleReport::class.java))
        }

        binding.upgradeBtn.setOnClickListener {
            bottomSheetDialog.show()
        }

    }

    private fun setupPieChart(interestIncome: String, fl: Float) {
        val entries = listOf(
            PieEntry(interestIncome.toFloat(), "Complaint"),
            PieEntry(fl, "Non-Complaint")
//            PieEntry(0.97f, "Not Halal")
        )
        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                ContextCompat.getColor(
                    context,
                    R.color.green
                ),   // Convert resource ID to color value
                ContextCompat.getColor(context, R.color.yellow),
                Color.RED // This is already a color value, so no need to convert
            )
            valueTextColor = Color.BLACK
            valueTextSize = 12f
        }

        val pieData = PieData(dataSet)
        binding.pieChartView.data = pieData
        binding.pieChartView.invalidate()
    }

    private fun setupPieChartFin(interestIncome: String, fl: Float) {
        val entries = listOf(
            PieEntry(interestIncome.toFloat(), "Interest Income"),
            PieEntry(fl, "Interest Bearing Securities Market Cap")
//            PieEntry(0.97f, "Not Halal")
        )
        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                ContextCompat.getColor(
                    context,
                    R.color.green
                ),   // Convert resource ID to color value
                ContextCompat.getColor(context, R.color.yellow),
                Color.RED // This is already a color value, so no need to convert
            )
            valueTextColor = Color.BLACK
            valueTextSize = 12f
        }

        val pieData = PieData(dataSet)
        binding.pieChartView1.data = pieData
        binding.pieChartView1.invalidate()
    }

    private fun setupGaugeChart(chart1: String) {
        val range = Range()
        range.color = Color.parseColor("#00b20b")
        range.from = 0.0
        range.to = 30.0

//        val range2 = Range()
//        range2.color = Color.parseColor("#E3E500")
//        range2.from = 50.0
//        range2.to = 100.0

        val range3 = Range()
        range3.color = Color.parseColor("#ce0000")
        range3.from = 30.0
        range3.to = 100.0



        //add color ranges to gauge
        binding.halfGauge.addRange(range)
        // binding.halfGauge.addRange(range2)
        binding.halfGauge.addRange(range3)
//        binding.halfGauge.startAngle = 180f  // Set the start angle at 180 degrees
//        binding.halfGauge.sweepAngle = 180f
        //set min max and current value
        binding.halfGauge.minValue = 0.0
        binding.halfGauge.maxValue = 100.0
        binding.halfGauge.value = chart1.toDouble()
        binding.halfGauge.setFormatter { value ->
            "${value}%" }


    }
    private fun setupGaugeChartDebt(chart1: String) {
        val range = Range()
        range.color = Color.parseColor("#00b20b")
        range.from = 0.0
        range.to = 30.0

//        val range2 = Range()
//        range2.color = Color.parseColor("#E3E500")
//        range2.from = 50.0
//        range2.to = 100.0

        val range3 = Range()
        range3.color = Color.parseColor("#ce0000")
        range3.from = 30.0
        range3.to = 100.0


        //add color ranges to gauge
        binding.halfGaugeDebt.addRange(range)
        // binding.halfGauge.addRange(range2)
        binding.halfGaugeDebt.addRange(range3)
//        binding.halfGauge.startAngle = 180f  // Set the start angle at 180 degrees
//        binding.halfGauge.sweepAngle = 180f
        //set min max and current value
        binding.halfGaugeDebt.minValue = 0.0
        binding.halfGaugeDebt.maxValue = 100.0
        binding.halfGaugeDebt.value = chart1.toDouble()
        binding.halfGaugeDebt.setFormatter { value ->
            "${value}%" }
    }
    private fun setupGaugeChartSecurities(chart1: String) {
        val range = Range()
        range.color = Color.parseColor("#00b20b")
        range.from = 0.0
        range.to = 30.0

//        val range2 = Range()
//        range2.color = Color.parseColor("#E3E500")
//        range2.from = 50.0
//        range2.to = 100.0

        val range3 = Range()
        range3.color = Color.parseColor("#ce0000")
        range3.from = 30.0
        range3.to = 100.0


        //add color ranges to gauge
        binding.halfGaugeSecurities.addRange(range)
        // binding.halfGauge.addRange(range2)
        binding.halfGaugeSecurities.addRange(range3)
//        binding.halfGauge.startAngle = 180f  // Set the start angle at 180 degrees
//        binding.halfGauge.sweepAngle = 180f
        //set min max and current value
        binding.halfGaugeSecurities.minValue = 0.0
        binding.halfGaugeSecurities.maxValue = 100.0
        binding.halfGaugeSecurities.value = chart1.toDouble()
        binding.halfGaugeSecurities.setFormatter { value ->
            "${value}%" }
    }
    private fun setupGaugeChartNon(chart1: String) {
        val range = Range()
        range.color = Color.parseColor("#00b20b")
        range.from = 0.0
        range.to = 5.0

//        val range2 = Range()
//        range2.color = Color.parseColor("#E3E500")
//        range2.from = 50.0
//        range2.to = 100.0

        val range3 = Range()
        range3.color = Color.parseColor("#ce0000")
        range3.from = 5.0
        range3.to = 100.0


        //add color ranges to gauge
        binding.halfGaugeNon.addRange(range)
        // binding.halfGauge.addRange(range2)
        binding.halfGaugeNon.addRange(range3)
//        binding.halfGauge.startAngle = 180f  // Set the start angle at 180 degrees
//        binding.halfGauge.sweepAngle = 180f
        //set min max and current value
        binding.halfGaugeNon.minValue = 0.0
        binding.halfGaugeNon.maxValue = 100.0
        binding.halfGaugeNon.value = chart1.toDouble()
        binding.halfGaugeNon.setFormatter { value ->
            "${value}%" }

    }

    private fun apiCallGetCompanyDetails(id: String) {
        AppProgressBar.showLoaderDialog(context)
        binding.progressBarDebt.progress = 0
        binding.securitiesProgressBar.progress = 0

        ApiClient.apiService.getCompanyDetails(id).enqueue(object : Callback<ModelCompanyDetails> {
            @SuppressLint("LogNotTimber", "SetTextI18n")
            override fun onResponse(
                call: Call<ModelCompanyDetails>,
                response: Response<ModelCompanyDetails>
            ) {
                AppProgressBar.hideLoaderDialog()

                try {
                    if (response.code() == 500) {
                        myToast(this@ComplianceReportActivity, "Server Error")
                        AppProgressBar.hideLoaderDialog()
                    } else if (response.code() == 404) {
                        myToast(this@ComplianceReportActivity, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()
                    } else {

                        if (response.body()!!.result.status == 1) {
                            with(binding) {
                                updateDate.text =
                                    "Updated on " + response.body()!!.result.created_at.substringBefore(
                                        "T"
                                    )
                                companyName.text = response.body()!!.result.name_of_company
                                tvExchange.text = response.body()!!.result.exchange
                                companySymbol.text =
                                    response.body()!!.result.nse_symbol_bse_script_id
                                tvIndustryGroup.text = response.body()!!.result.industry_group
                                tvDebtCapture.text = response.body()!!.result.debts_market_cap + "%"
                                tvSecuritiesCapture.text =
                                    response.body()!!.result.interest_bearing_securities_market_cap + "%"
                                tvNonPermissible.text =
                                    response.body()!!.result.interest_income + "%"
                                tvMarketCap.text =
                                    (response.body()?.result?.interest_bearing_securities_market_cap?.takeIf { it.isNotEmpty() }
                                        ?: "0.00") + "%"
                                tvTotalDebt.text =
                                    (response.body()?.result?.debts_market_cap?.takeIf { it.isNotEmpty() }
                                        ?: "0.00") + "%"
                                if (response.body()!!.result.final.contentEquals("PASS")) {
                                    binding.llBusiness.visibility = View.VISIBLE
                                } else {
                                    binding.llBusinessFail.visibility = View.VISIBLE
                                    binding.layoutFinancial.visibility = View.GONE

                                }
                                if (response.body()!!.result.financial_screening != null && response.body()!!.result.financial_screening.contentEquals(
                                        "PASS"
                                    )
                                ) {
                                    binding.financialActivityStatus.visibility = View.VISIBLE

                                } else {
                                    binding.financialActivityStatusFail.visibility = View.VISIBLE

                                }
                                if (response.body()!!.result.financial_screening.isNullOrEmpty()) {
                                    binding.layoutFinancialSub.visibility = View.GONE
                                }
                                val marketCap =
                                    response.body()?.result?.debts_market_cap?.toDoubleOrNull()
                                        ?: 0.0
                                val maxValue = 100.0
                                val progress = (marketCap / maxValue * 100).toInt().coerceIn(0, 100)
                                progressBarDebt.progress = progress

                                val marketCap1 =
                                    response.body()?.result?.interest_bearing_securities_market_cap?.toDoubleOrNull()
                                        ?: 0.0
                                val maxValue1 = 100.0
                                val progress1 =
                                    (marketCap1 / maxValue1 * 100).toInt().coerceIn(0, 100)
                                securitiesProgressBar.progress = progress1

                                val marketCap2 =
                                    response.body()?.result?.interest_income?.toDoubleOrNull()
                                        ?: 0.0
                                val maxValue2 = 100.0
                                val progress2 =
                                    (marketCap2 / maxValue2 * 100).toInt().coerceIn(0, 100)
                                ProgressBarNon.progress = progress2

                                setupPieChart(
                                    response.body()?.result?.debts_market_cap?.takeIf { !it.isNullOrEmpty() }
                                        ?: "0",
                                    response.body()?.result?.interest_bearing_securities_market_cap?.toFloatOrNull()
                                        ?: 0f
                                )

                                setupPieChartFin(
                                    response.body()?.result?.interest_income?.takeIf { !it.isNullOrEmpty() }
                                        ?: "0",
                                    response.body()?.result?.interest_bearing_securities_market_cap?.toFloatOrNull()
                                        ?: 0f
                                )

                                if (response.body()?.result?.final == "PASS") {
                                    binding.complianceTag.visibility = View.VISIBLE
                                    binding.nonComplianceTag.visibility = View.GONE
                                } else {
                                    binding.nonComplianceTag.visibility = View.VISIBLE
                                    binding.complianceTag.visibility = View.GONE

                                }
                                setupGaugeChart(
                                    (response.body()!!.result.interest_income?.toFloatOrNull()
                                        ?: 0f).toString()
                                )
                                setupGaugeChartNon(
                                    (response.body()!!.result.interest_income?.toFloatOrNull()
                                        ?: 0f).toString()
                                )
                                setupGaugeChartSecurities(
                                    (response.body()!!.result.interest_bearing_securities_market_cap?.toFloatOrNull()
                                        ?: 0f).toString()
                                )
                                setupGaugeChartDebt(
                                    (response.body()!!.result.debts_market_cap?.toFloatOrNull()
                                        ?: 0f).toString()
                                )
                            }

                        }
                        AppProgressBar.hideLoaderDialog()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    myToast(context as Activity, "Something went wrong")
                    AppProgressBar.hideLoaderDialog()

                }
            }

            override fun onFailure(call: Call<ModelCompanyDetails>, t: Throwable) {
                AppProgressBar.hideLoaderDialog()

                count++
                if (count <= 3) {
                    apiCallGetCompanyDetails(id)
                } else {
                    myToast(
                        this@ComplianceReportActivity,
                        t.message.toString()
                    )
                }
            }
        })
    }

}
