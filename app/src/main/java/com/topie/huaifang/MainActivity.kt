package com.topie.huaifang

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.topie.huaifang.extensions.log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val service = HFRetrofit.retrofit.create(HFService::class.java)
        btn_submit.setOnClickListener({
            et_main.text.toString().trim()
            service.login(LoginRequestBody("bigniu", "12345"))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        tv_show.text = it.body()?.json
                    }, {
                        log("", it)
                    })
        })
    }
}
