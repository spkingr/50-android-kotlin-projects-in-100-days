package me.liuqingwen.android.projectbasicmvp.model

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Qingwen on 2018-2-16, project: ProjectBasicMVP.
 *
 * @Author: Qingwen
 * @DateTime: 2018-2-16
 * @Package: me.liuqingwen.android.projectbasicmvp.model in project: ProjectBasicMVP
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

private const val LOGIN_BASE_URL = "http://www.liuqingwen.me/"

object APILoginService
{
    private val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(LOGIN_BASE_URL)
            .build()
    private val loginService = retrofit.create(ILoginService::class.java)
    
    fun login(username: String, password: String, onSuccess: ((LoginResponse)->Unit)? = null, onFailure: ((Throwable)->Unit)? = null) = this.loginService
                    .login(username, password)
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ if (it.result) onSuccess?.invoke(it) else onFailure?.invoke(Throwable(it.info)) }, { onFailure?.invoke(it) })!!
}

private interface ILoginService
{
    @FormUrlEncoded
    @POST("/test/login_db.php")
    fun login(@Field("username") username:String, @Field("password") password:String): Observable<LoginResponse>
}

data class LoginResponse(val result:Boolean, val info:String)