package com.amtech.tlismiherbs.home.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.afdhal_fa.imageslider.`interface`.ItemClickListener
import com.afdhal_fa.imageslider.model.SlideUIModel
import com.amtech.tlismiherbs.Helper.AppProgressBar
import com.sellacha.tlismiherbs.R
import com.amtech.tlismiherbs.category.Listing
import com.amtech.tlismiherbs.category.adapter.AdapterListing
import com.sellacha.tlismiherbs.databinding.FragmentHomeBinding
import com.amtech.tlismiherbs.home.adapter.AdapterCategory
import com.example.tlismimoti.Helper.myToast
import com.amtech.tlismiherbs.home.adapter.AdapterProduct
import com.sellacha.tlismiherbs.home.model.Category
import com.amtech.tlismiherbs.retrofit.ApiClient
import com.amtech.tlismiherbs.sharedpreferences.SessionManager
import com.sellacha.tlismiherbs.home.model.DataX
import com.example.tlismimoti.home.model.ModelProduct
import com.example.tlismimoti.home.model.modelSlider.ModelSlider
import com.facebook.shimmer.ShimmerFrameLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var sessionManager: SessionManager
    private var mainData = ArrayList<DataX>()
    private var allCategoryName = ArrayList<String>()
    private var shimmerFrameLayout: ShimmerFrameLayout? = null
    var count = 0
    var countCat = 0
    var countS = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        sessionManager = SessionManager(requireContext())
        shimmerFrameLayout = view.findViewById(R.id.shimmer1)
        shimmerFrameLayout!!.startShimmer()
        sessionManager.baseURL = "https://tlismiherbs.com/"

        if (sessionManager.userName!!.isEmpty()) {
            binding.layoutUser.visibility = View.GONE
        } else {
            binding.tvUserName.text = sessionManager.userName+ " !"
        }


        binding.edtSearch.addTextChangedListener { str ->
            setRecyclerViewAdapter(mainData.filter {
                it.title != null && it.title.contains(str.toString(), ignoreCase = true)
            } as ArrayList<DataX>)
        }


        apiCallProduct()
        apiCallSlider()
        apiCallGetAllCategory()
        with(binding) {
            tvViewAll1.setOnClickListener {
                val intent = Intent(context as Activity, Listing::class.java)
                    .putExtra("value", binding.tvCat1.text)
                (context as Activity).startActivity(intent)
            }
            tvViewAll2.setOnClickListener {
                val intent = Intent(context as Activity, Listing::class.java)
                    .putExtra("value", binding.tvCat2.text)
                (context as Activity).startActivity(intent)
            }
            tvViewAll3.setOnClickListener {
                val intent = Intent(context as Activity, Listing::class.java)
                    .putExtra("value", binding.tvCat3.text)
                (context as Activity).startActivity(intent)
            }
            tvViewAll4.setOnClickListener {
                val intent = Intent(context as Activity, Listing::class.java)
                    .putExtra("value", binding.tvCat4.text)
                (context as Activity).startActivity(intent)
            }
            tvViewAll5.setOnClickListener {
                val intent = Intent(context as Activity, Listing::class.java)
                    .putExtra("value", binding.tvCat5.text)
                (context as Activity).startActivity(intent)
            }
            tvViewAll6.setOnClickListener {
                val intent = Intent(context as Activity, Listing::class.java)
                    .putExtra("value", binding.tvCat6.text)
                (context as Activity).startActivity(intent)
            }
        }


    }
    private fun setRecyclerViewAdapter(data: ArrayList<DataX>) {
        binding.recycler1.apply {
            adapter = AdapterListing(context, data)
            AppProgressBar.hideLoaderDialog()

        }
    }
    private fun apiCallSlider() {
        // AppProgressBar.showLoaderDialog(requireContext())

        ApiClient.apiService.getSlider(sessionManager.authToken)
            .enqueue(object : Callback<ModelSlider> {
                @SuppressLint("LogNotTimber")
                override fun onResponse(
                    call: Call<ModelSlider>, response: Response<ModelSlider>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(requireActivity(), "Server Error")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.body()!!.data.posts.isEmpty()) {

                            myToast(requireActivity(), "No Slider Found")
                            //   AppProgressBar.hideLoaderDialog()

                        } else {
                            val imageList = ArrayList<SlideUIModel>()

                            for (i in response.body()!!.data.posts) {
                                //uploads/506/23/10/1697450898.png
                                imageList.add(
                                    SlideUIModel(sessionManager.baseURL + i.name, "")
                                )
//                                imageList.add(
//                                    SlideUIModel(baseURL + i.name[1], "")
//                                )
//                                imageList.add(
//                                    SlideUIModel(baseURL + i.name[2], "")
//                                )
//                                imageList.add(
//                                    SlideUIModel(baseURL + i.name[3], "")
//                                )


                            }

                            binding.imageSlide.setImageList(imageList)



                            binding.imageSlide.setItemClickListener(object : ItemClickListener {
                                override fun onItemClick(model: SlideUIModel, position: Int) {
                                    val link = ArrayList<String>()
                                    for (i in response.body()!!.data.posts) {
                                        link.add(i.slug)
                                    }

                                    when (position) {
                                        0 -> {
                                            val intent =
                                                Intent(Intent.ACTION_VIEW, Uri.parse(link[0]))
                                            context?.startActivity(
                                                Intent.createChooser(
                                                    intent,
                                                    "Choose browser"
                                                )
                                            )
                                        }

                                        1 -> {
                                            val intent =
                                                Intent(Intent.ACTION_VIEW, Uri.parse(link[1]))
                                            context?.startActivity(
                                                Intent.createChooser(
                                                    intent,
                                                    "Choose browser"
                                                )
                                            )
                                        }

                                        2 -> {
                                            val intent =
                                                Intent(Intent.ACTION_VIEW, Uri.parse(link[2]))
                                            context?.startActivity(
                                                Intent.createChooser(
                                                    intent,
                                                    "Choose browser"
                                                )
                                            )
                                        }

                                        3 -> {
                                            val intent =
                                                Intent(Intent.ACTION_VIEW, Uri.parse(link[3]))
                                            context?.startActivity(
                                                Intent.createChooser(
                                                    intent,
                                                    "Choose browser"
                                                )
                                            )
                                        }

                                        4 -> {
                                            val intent =
                                                Intent(Intent.ACTION_VIEW, Uri.parse(link[4]))
                                            context?.startActivity(
                                                Intent.createChooser(
                                                    intent,
                                                    "Choose browser"
                                                )
                                            )
                                        }

                                        5 -> {
                                            val intent =
                                                Intent(Intent.ACTION_VIEW, Uri.parse(link[5]))
                                            context?.startActivity(
                                                Intent.createChooser(
                                                    intent,
                                                    "Choose browser"
                                                )
                                            )
                                        }

                                        6 -> {
                                            val intent =
                                                Intent(Intent.ACTION_VIEW, Uri.parse(link[6]))
                                            context?.startActivity(
                                                Intent.createChooser(
                                                    intent,
                                                    "Choose browser"
                                                )
                                            )
                                        }

                                        7 -> {
                                            val intent =
                                                Intent(Intent.ACTION_VIEW, Uri.parse(link[7]))
                                            context?.startActivity(
                                                Intent.createChooser(
                                                    intent,
                                                    "Choose browser"
                                                )
                                            )
                                        }

                                        8 -> {
                                            val intent =
                                                Intent(Intent.ACTION_VIEW, Uri.parse(link[8]))
                                            context?.startActivity(
                                                Intent.createChooser(
                                                    intent,
                                                    "Choose browser"
                                                )
                                            )
                                        }

                                        else -> {
                                            val intent =
                                                Intent(Intent.ACTION_VIEW, Uri.parse(link[0]))
                                            context?.startActivity(
                                                Intent.createChooser(
                                                    intent,
                                                    "Choose browser"
                                                )
                                            )
                                        }
                                    }
                                }
                            })
                            binding.imageSlide.startSliding(2000) // with new period
                            // binding.imageSlide.stopSliding()
                            AppProgressBar.hideLoaderDialog()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ModelSlider>, t: Throwable) {
                    // myToast(requireActivity(),t.message.toString())
                    // myToast(requireActivity(), "Something went wrong")

                    countS++
                    if (countS <= 2) {
                        Log.e("count", countS.toString())
                        apiCallSlider()
                    } else {
                        myToast(requireActivity(), t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }

                    // AppProgressBar.hideLoaderDialog()


                }

            })
    }

    //    private fun openBrowser(context: Context, url: String) {
//        var url = url
//        if (!url.startsWith(HTTP) && !url.startsWith(HTTPS)) {
//            url = HTTP + url
//        }
//        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//        context.startActivity(Intent.createChooser(intent, "Choose browser")) // Choose browser is arbitrary :)
//    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getNewCategories(productResponse: ModelProduct): List<Category> {
        val newCategories = mutableSetOf<Category>()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
        val thresholdDate = LocalDate.parse("2024-01-01")

        productResponse.data.posts.data.forEach { product ->
            product.categories.forEach { category ->
                val createdAtDate = LocalDate.parse(category.created_at, formatter)
                if (createdAtDate.isAfter(thresholdDate)) {
                    newCategories.add(category)
                }
            }
        }
        return newCategories.toList()
    }


    private fun apiCallProduct() {
        // AppProgressBar.showLoaderDialog(requireContext())
        ApiClient.apiService.product(sessionManager.authToken!!)
            .enqueue(object : Callback<ModelProduct> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<ModelProduct>, response: Response<ModelProduct>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(requireActivity(), "Server Error")
                            AppProgressBar.hideLoaderDialog()
                            binding.shimmer1.visibility = View.GONE
                            binding.shimmer2.visibility = View.GONE
                            binding.shimmer3.visibility = View.GONE
                            binding.shimmer4.visibility = View.GONE
                            binding.shimmer5.visibility = View.GONE
                            binding.shimmer6.visibility = View.GONE
                        } else if (response.code() == 404) {
                            myToast(requireActivity(), "Something went wrong")
                            AppProgressBar.hideLoaderDialog()
                            binding.shimmer1.visibility = View.GONE
                            binding.shimmer2.visibility = View.GONE
                            binding.shimmer3.visibility = View.GONE
                            binding.shimmer4.visibility = View.GONE
                            binding.shimmer5.visibility = View.GONE
                            binding.shimmer6.visibility = View.GONE
                        } else if (response.body()!!.data.posts.data.isEmpty()) {
//
                            myToast(requireActivity(), "No Product Found")
                            binding.shimmer1.visibility = View.GONE
                            binding.shimmer2.visibility = View.GONE
                            binding.shimmer3.visibility = View.GONE
                            binding.shimmer4.visibility = View.GONE
                            binding.shimmer5.visibility = View.GONE
                            binding.shimmer6.visibility = View.GONE
                            AppProgressBar.hideLoaderDialog()

                        } else {
                            val men = ArrayList<DataX>()
                            val women = ArrayList<DataX>()
                            val kids = ArrayList<DataX>()
                            mainData = response.body()!!.data.posts.data

                            val newCategories = getNewCategories(response.body()!!)
                            newCategories.forEach {
                                println(it)
                                allCategoryName.add(it.name)
                            }
                            val uniqueItemsSet = allCategoryName.toSet()

                            // Convert the set back to a list if needed
                            val uniqueItemsList = uniqueItemsSet.toList()
                            Log.e("allCategoryName", allCategoryName.toString())
                            Log.e("uniqueItemsList", uniqueItemsList.toString())
                            val list = ArrayList<DataX>()

                            for (i in response.body()!!.data.posts.data) {
                                if (i.categories.isNullOrEmpty()) {
                                    if (!list.contains(i)) {
                                        list.add(i)
                                        //   binding.tvCat1.text = i1.name
                                    }
                                }
                                binding.layoutCat1.visibility = View.VISIBLE
                                binding!!.recycler1.adapter =
                                    activity?.let {
                                        AdapterProduct(it, mainData)
                                    }
                                binding.shimmer1.visibility = View.GONE
                            }
                            count = 0

                            if (uniqueItemsList.size >= 1) {
                                for (i in response.body()!!.data.posts.data) {
                                    for (i1 in i.categories) {
                                        if (i1.name.isNotEmpty()) {
                                            when (i1.name) {
                                                uniqueItemsList[0] -> {
                                                    if (!list.contains(i)) {
                                                        list.add(i)
                                                        binding.tvCat1.text = i1.name
                                                    }

                                                }
                                            }
                                        }

                                    }
                                }
                                binding.layoutCat1.visibility = View.VISIBLE
                                binding.tvCat1.text = uniqueItemsList[0]
                                //list.distinct()

                                binding!!.recycler1.adapter =
                                    activity?.let {
                                        AdapterProduct(it, list)
                                    }
                                binding.shimmer1.visibility = View.GONE
                            }

                            if (uniqueItemsList.size >= 2) {
                                val list = ArrayList<DataX>()
                                for (i in response.body()!!.data.posts.data) {
                                    for (i1 in i.categories) {
                                        if (i1.name.isNotEmpty()) {
                                            when (i1.name) {
                                                uniqueItemsList[1] -> {
                                                    if (!list.contains(i)) {
                                                        list.add(i)
                                                        binding.tvCat2.text = i1.name
                                                    }
                                                }
                                            }
                                        }

                                    }
                                }
                                binding.layoutCat2.visibility = View.VISIBLE
                                binding!!.recyclerVie2.adapter =
                                    activity?.let {
                                        AdapterProduct(
                                            it,
                                            list,
                                        )
                                    }
                                binding.shimmer2.visibility = View.GONE
                            }
                            if (uniqueItemsList.size >= 3) {
                                val list = ArrayList<DataX>()
                                for (i in response.body()!!.data.posts.data) {
                                    for (i1 in i.categories) {
                                        if (i1.name.isNotEmpty()) {
                                            when (i1.name) {
                                                uniqueItemsList[2] -> {
                                                    if (!list.contains(i)) {
                                                        list.add(i)
                                                        binding.tvCat3.text = i1.name
                                                    }
                                                }
                                            }
                                        }

                                    }
                                }
                                list.distinct()
                                binding.layoutCat3.visibility = View.VISIBLE
                                binding!!.recycler3.adapter =
                                    activity?.let {
                                        AdapterProduct(
                                            it,
                                            list,
                                        )
                                    }
                                binding.shimmer3.visibility = View.GONE
                            }

                            if (uniqueItemsList.size >= 4) {
                                val list = ArrayList<DataX>()
                                for (i in response.body()!!.data.posts.data) {
                                    for (i1 in i.categories) {
                                        if (i1.name.isNotEmpty()) {
                                            when (i1.name) {
                                                uniqueItemsList[3] -> {
                                                    if (!list.contains(i)) {
                                                        list.add(i)
                                                        binding.tvCat4.text = i1.name
                                                    }
                                                }
                                            }
                                        }

                                    }

                                }
                                binding.layoutCat4.visibility = View.VISIBLE
                                binding!!.recycler4.adapter =
                                    activity?.let {
                                        AdapterProduct(
                                            it,
                                            list,
                                        )
                                    }
                                binding.shimmer4.visibility = View.GONE
                            }

                            if (uniqueItemsList.size >= 5) {
                                val list = ArrayList<DataX>()
                                for (i in response.body()!!.data.posts.data) {
                                    for (i1 in i.categories) {
                                        if (i1.name.isNotEmpty()) {
                                            when (i1.name) {
                                                uniqueItemsList[4] -> {
                                                    if (!list.contains(i)) {
                                                        list.add(i)
                                                        binding.tvCat5.text = i1.name
                                                    }
                                                }
                                            }
                                        }

                                    }

                                }
                                binding.layoutCat5.visibility = View.VISIBLE
                                binding!!.recycler5.adapter =
                                    activity?.let {
                                        AdapterProduct(
                                            it,
                                            list,
                                        )
                                    }
                                binding.shimmer5.visibility = View.GONE
                            }
                            if (uniqueItemsList.size >= 6) {
                                val list = ArrayList<DataX>()
                                for (i in response.body()!!.data.posts.data) {
                                    for (i1 in i.categories) {
                                        if (i1.name.isNotEmpty()) {
                                            when (i1.name) {
                                                uniqueItemsList[5] -> {
                                                    if (!list.contains(i)) {
                                                        list.add(i)
                                                        binding.tvCat6.text = i1.name
                                                    }
                                                }
                                            }
                                        }

                                    }

                                }
                                binding.layoutCat6.visibility = View.VISIBLE
                                binding!!.recycler6.adapter =
                                    activity?.let {
                                        AdapterProduct(
                                            it,
                                            list,
                                        )
                                    }
                                binding.shimmer6.visibility = View.GONE
                            }


                            //
//                            for (i in response.body()!!.data.posts.data) {
//                                for (i1 in i.categories) {
//                                    if (i1.name.isNotEmpty()) {
//                                        when (i1.name) {
//                                            "National Delivery Available" -> {
//                                                men.add(i)
//                                                binding.tvTitle1.text = i1.name.toString()
//                                                CategorieName = i1.name
//                                            }
//
//                                            "WOMEN" -> women.add(i)
//                                            "KIDS" -> kids.add(i)
//                                        }
//                                    }
//
//                                }
//                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ModelProduct>, t: Throwable) {
                    //myToast(requireActivity(), "Something went wrong")
                    count++
                    if (count <= 3) {
                        Log.e("count", count.toString())
                        apiCallProduct()
                    } else {
                        myToast(requireActivity(), t.message.toString())
                        AppProgressBar.hideLoaderDialog()
                        count = 0

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }

    private fun apiCallGetAllCategory() {
        // AppProgressBar.showLoaderDialog(requireContext())
        ApiClient.apiService.product(sessionManager.authToken!!)
            .enqueue(object : Callback<ModelProduct> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<ModelProduct>, response: Response<ModelProduct>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(requireActivity(), "Server Error")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.code() == 404) {
                            myToast(requireActivity(), "Something went wrong")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.body()!!.data.posts.data.isEmpty()) {
//
                            myToast(requireActivity(), "No Category Found")

                            AppProgressBar.hideLoaderDialog()

                        } else {
                            val men = java.util.ArrayList<DataX>()
                            val women = java.util.ArrayList<DataX>()
                            val kids = java.util.ArrayList<DataX>()
                            mainData = response.body()!!.data.posts.data

                            val newCategories = getNewCategories(response.body()!!)
                            newCategories.forEach {
                                println(it)
                                allCategoryName.add(it.name)
                            }
                            val uniqueItemsSet = allCategoryName.toSet()
                            countCat = 0
                            // Convert the set back to a list if needed
                            val uniqueItemsList = uniqueItemsSet.toList()
                            Log.e("allCategoryName", allCategoryName.toString())
                            Log.e("uniqueItemsList", uniqueItemsList.toString())

                            binding.recyclerCat.adapter =
                                activity?.let {
                                    AdapterCategory(
                                        it,
                                        uniqueItemsList,
                                    )
                                }
                            if (uniqueItemsList.isNotEmpty()) {
                                binding.layoutCatMainNew.visibility = View.VISIBLE
                            }


                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ModelProduct>, t: Throwable) {
                    //myToast(requireActivity(), "Something went wrong")
                    countCat++
                    if (countCat <= 3) {
                        Log.e("count", countCat.toString())
                        apiCallGetAllCategory()
                    } else {
                        myToast(requireActivity(), t.message.toString())
                        AppProgressBar.hideLoaderDialog()
                        countCat = 0

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }


}