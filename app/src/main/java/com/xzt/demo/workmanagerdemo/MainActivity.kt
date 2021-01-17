package com.xzt.demo.workmanagerdemo

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.preference.Preference
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.work.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    companion object{
        const  val baseUrl = "http://qqlykm.cn/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        doWork.setOnClickListener {
            doWork()
            show(this,showContent)
        }
        cancelWork.setOnClickListener {
            cancelWork()
        }
        //再次进入直接根据id获取之前成功的内容
        show(this,showContent)
    }

    /**
     * 执行任务
     */
    fun doWork(){
        //创建单工作请求对象:OneTimeWorkRequest
        val worker =  OneTimeWorkRequest.Builder(RequestWorker :: class.java)
                //设置允许进行网络请求的标识
            .setInputData(Data.Builder().putBoolean("isRequest",true).build())
            .setConstraints(myConstraints)
                .build()
        //WorkManager执行任务
        WorkManager.getInstance().enqueue(worker)
        //用sharePreference保存任务ID
        val adRequestId = worker.id
        var arid by Preference("requestId", "")
        arid = adRequestId.toString()
    }

    /**
     * 任务取消
     */
    fun cancelWork(){
        //获取用sharePreference保存的任务ID
        var arid by Preference("requestId", "")
        if (!arid.equals("")) {
            val uuid = UUID.fromString(arid)
            WorkManager.getInstance().cancelWorkById(uuid)
        }
    }

    /**
     * 根据id,展示网络请求到的内容
     */
    fun show(owner: LifecycleOwner, view: TextView) {
        var arid by Preference("requestId", "")
        if (!arid.equals("")) {
            val uuid = UUID.fromString(arid)
            //livedata实时监听任务状态
            WorkManager.getInstance().getWorkInfoByIdLiveData(uuid)
                .observe(owner, Observer {
                    if (it.state == WorkInfo.State.SUCCEEDED) {
                        val adResult = it.outputData.getString("data")
                        view.text = adResult
                    }
                })
        }
    }
    //这是约束条件
    @RequiresApi(Build.VERSION_CODES.M)
    val myConstraints = Constraints.Builder()
        //网络连接时执行
        .setRequiredNetworkType(NetworkType.CONNECTED)
        // 在待机状态下执行，需要 API 23
        //.setRequiresDeviceIdle(true)
        // 在充电时执行
       .setRequiresCharging(true)
        .build()
}