package com.example.ticketapps.login

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ticketapps.util.sharedpref.Constant
import com.example.ticketapps.util.sharedpref.SharedPrefProvider
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class LoginViewModel : ViewModel(), CoroutineScope {

    val isLoadingLiveData = MutableLiveData<Boolean>()
    val isLoginLiveData = MutableLiveData<Boolean>()
    private lateinit var idAccount: String
    private lateinit var nameAccount: String
    private lateinit var token: String
    val isMessageLiveData = MutableLiveData<String>()

    private lateinit var service: LoginApiService
    private lateinit var sharedPref: SharedPrefProvider

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Main

    fun setLoginService(service: LoginApiService) {
        this.service = service
    }

    fun putSharedPreferences(mContext: Context) {
        sharedPref = SharedPrefProvider(mContext)
        sharedPref.putString(Constant.KEY_NAME, nameAccount)
        sharedPref.putString(Constant.KEY_ID_ACCOUNT, idAccount)
        sharedPref.putString(Constant.KEY_TOKEN, token)
        sharedPref.putBoolean(Constant.KEY_REMEMBERLOGIN, true)
    }

    fun callLoginApi(email: String?, password: String?) {
        launch {
            isLoadingLiveData.value = true
            val response = withContext(Dispatchers.Main) {
                try {
                    service.loginRequest(email, password)
                } catch (e: Throwable) {
                    e.printStackTrace()
                    isLoginLiveData.value = false
                }
            }
            if (response is LoginResponse) {
                if (response.success) {
                    nameAccount = response.data.name.toString()
                    idAccount = response.data.idAccount.toString()
                    token = response.data.token.toString()
                    isLoginLiveData.value = true
                }
                isMessageLiveData.value = response.message
            }
            isLoadingLiveData.value = false
        }
    }
}