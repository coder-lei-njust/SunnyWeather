package com.sunnyweather.android.logic.network

import retrofit2.Call
import retrofit2.Response
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object SunnyWeatherNetwork {

    private  val  placeService=ServiceCreator.create(PlaceService::class.java)
    //协程
    suspend fun  searchPlaces(query:String)= placeService.searchPlace(query).await()
    //在call类里面定义await()函数
    private suspend fun <T> Call<T>.await():T{
        return suspendCoroutine {continuation ->//lambda 中的程序在普通线程中执行
            enqueue(object :retrofit2.Callback<T>{
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body=response.body()
                    if(body!=null) {
                        continuation.resume(body)//协程恢复执行
                    }else{
                        RuntimeException("Response body is null")
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}