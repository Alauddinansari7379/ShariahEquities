package com.amtech.shariahEquities.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.amtech.shariahEquities.Helper.AppProgressBar
import com.amtech.shariahEquities.fragments.model.ModelCompanyDetails
import com.amtech.shariahEquities.retrofit.ApiClient
import com.example.tlismimoti.Helper.myToast
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.sellacha.tlismiherbs.databinding.ActivityComplianceReportBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ComplianceReportActivity : AppCompatActivity() {
    private var context: Context = this@ComplianceReportActivity
    var id = ""
    private var count = 0
    private lateinit var binding: ActivityComplianceReportBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComplianceReportBinding.inflate(layoutInflater)
        id = intent.getIntExtra("id", 0).toString()
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

        setContentView(binding.root)


        apiCallGetCompanyDetails(id)
    }

    private fun setupPieChart(interestIncome: String, fl: Float) {
        val entries = listOf(
            PieEntry(interestIncome.toFloat(), "interestIncome"),
            PieEntry(fl, "Interest Bearing Securities Market Cap")
//            PieEntry(0.97f, "Not Halal")
        )

        val dataSet = PieDataSet(entries, "Compliance Report").apply {
            colors = listOf(Color.GREEN, Color.YELLOW, Color.RED)
            valueTextColor = Color.BLACK
            valueTextSize = 12f
        }

        val pieData = PieData(dataSet)
        binding.pieChartView.data = pieData
        binding.pieChartView.invalidate()
    }

    private fun setupGaugeChart(chart1: String, chart: GaugeChartWithPointer) {
        val entries = ArrayList<PieEntry>()
        val percentage = chart1.toFloat()
        val remaining = 100f - percentage

        entries.add(PieEntry(percentage, "Shariah Compliant"))
        entries.add(PieEntry(remaining, ""))

        // Setup colors
        val colors = ArrayList<Int>()
        colors.add(Color.rgb(255, 77, 77))  // Red for the gauge
        colors.add(Color.rgb(245, 245, 245)) // Light gray for the remaining part

        // Create dataset and configure its appearance
        val dataSet = PieDataSet(entries, "")
        dataSet.colors = colors
        dataSet.setDrawValues(false)  // Don't show value labels

        val data = PieData(dataSet)

        // Customize the pie chart for a gauge appearance
        chart.data = data
        chart.description.isEnabled = false
        chart.isDrawHoleEnabled = true
        chart.setHoleColor(Color.TRANSPARENT)
        chart.setTransparentCircleAlpha(0)
        chart.holeRadius = 80f
        chart.setDrawEntryLabels(false)
        chart.legend.isEnabled = false
        chart.rotationAngle = 180f  // Start at the top
        chart.setUsePercentValues(true)
        chart.setPercentage(percentage)
        chart.isRotationEnabled=false
        chart.setTouchEnabled(false)

        // Clip to show only half of the pie chart (make it a gauge)
        chart.post {
            chart.setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null)
            chart.clipBounds = Rect(0, 0, chart.width, chart.height / 2)
            chart.invalidate() // Refresh the chart
        }
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
                                updateDate.text ="Updated on "+ response.body()!!.result.created_at.substringBefore("T")
                                companyName.text = response.body()!!.result.name_of_company
                                tvDebtCapture.text = response.body()!!.result.debts_market_cap+"%"
                                tvSecuritiesCapture.text = response.body()!!.result.interest_income+"%"
                                tvMarketCap.text =
                                    "₹" + (response.body()?.result?.interest_bearing_securities_market_cap?.takeIf { it.isNotEmpty() }
                                        ?: "0.00")
                                tvTotalDebt.text =
                                    "₹" + (response.body()?.result?.debts_market_cap?.takeIf { it.isNotEmpty() }
                                        ?: "0.00")
                                if (response.body()!!.result.final.contentEquals("PASS")) {
                                    binding.llBusiness.visibility = View.VISIBLE
                                } else {
                                    binding.llBusinessFail.visibility = View.VISIBLE
                                    binding.layoutFinancial.visibility = View.GONE

                                }
                                 if (response.body()!!.result.financial_screening.contentEquals("PASS")){
                                    binding.financialActivityStatus.visibility=View.VISIBLE

                                }else{
                                    binding.financialActivityStatusFail.visibility=View.VISIBLE

                                }
                                if (response.body()!!.result.financial_screening.isNullOrEmpty()){
                                    binding.layoutFinancialSub.visibility=View.GONE
                                }
                                val marketCap = response.body()?.result?.debts_market_cap?.toDoubleOrNull() ?: 0.0
                                val maxValue = 100.0
                                val progress = (marketCap / maxValue * 100).toInt().coerceIn(0, 100)
                                progressBarDebt.progress = progress

                                val marketCap1 = response.body()?.result?.interest_income?.toDoubleOrNull() ?: 0.0
                                val maxValue1 = 100.0
                                val progress1 = (marketCap1 / maxValue1 * 100).toInt().coerceIn(0, 100)
                                securitiesProgressBar.progress = progress1

                                setupPieChart(
                                    response.body()?.result?.interest_income?.takeIf { !it.isNullOrEmpty() }
                                        ?: "0",
                                    response.body()?.result?.interest_bearing_securities_market_cap?.toFloatOrNull()
                                        ?: 0f
                                )
                                setupGaugeChart(
                                    (response.body()!!.result.interest_bearing_securities_market_cap?.toFloatOrNull()
                                        ?: 0f).toString(), piechart
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
