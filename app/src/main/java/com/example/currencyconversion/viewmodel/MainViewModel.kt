package com.example.currencyconversion.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.currencyconversion.api.FixerObject
import com.example.currencyconversion.api.FixerService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*


class MainViewModel : ViewModel() {
    private val url = "http://data.fixer.io/api/"
    private val apiAccessKey = "06c58a61ea6773db4cba5709a1e4c6ec"
    private val fixerService: FixerService

    val fixers = MutableLiveData<ArrayList<FixerObject>>()
    val adapterItems = MutableLiveData<ArrayList<Any>>()
    val apiError = MutableLiveData<String>()

    init {
        val client = OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }).build()

        fixerService = Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FixerService::class.java)
    }

    fun loadFirstDay() {
        fixerService.getLatest(apiAccessKey).enqueue(object : Callback<FixerObject> {
            override fun onResponse(call: Call<FixerObject>, response: Response<FixerObject>) {
                if (response.isSuccessful) {
                    val fixerObject = response.body()
                    if (fixerObject != null) {
                        if (!fixerObject.success)
                            apiError.value = "API Field success is false"
                        fixers.value = arrayListOf(fixerObject)
                    }
                }
            }

            override fun onFailure(call: Call<FixerObject>, t: Throwable) {
                apiError.value = t.message
            }
        })
    }

    fun loadNextDay() {
        val day: String = fixers.value!!.last().date.asDayAndDecrease()
        fixerService.getByDate(day, apiAccessKey).enqueue(object : Callback<FixerObject> {
            override fun onResponse(call: Call<FixerObject>, response: Response<FixerObject>) {
                if (response.isSuccessful) {
                    val fixerObject = response.body()
                    if (fixerObject != null) {
                        if (!fixerObject.success)
                            apiError.value = "API Field success is false"
                        fixers.apply {
                            value!!.add(fixerObject)
                            notifyObserver()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<FixerObject>, t: Throwable) {
                apiError.value = t.message
            }
        })
    }

    fun shouldFirstLoad() = fixers.value == null || fixers.value!!.size == 0

    fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }

    private fun String.asDayAndDecrease(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN)
        calendar.let {
            it.time = dateFormat.parse(this)
            it.add(Calendar.DATE, -1)
        }
        return dateFormat.format(calendar.time)
    }
}