package com.dicoding.picodiploma.hugme.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.picodiploma.hugme.data.Retrofit.ApiConfig
import com.dicoding.picodiploma.hugme.data.Retrofit.ApiService
import com.dicoding.picodiploma.hugme.data.pref.UserModel
import com.dicoding.picodiploma.hugme.data.pref.UserPreference
import com.dicoding.picodiploma.hugme.data.response.ErrorResponse
import com.dicoding.picodiploma.hugme.data.response.LoginResponse
import com.dicoding.picodiploma.hugme.data.response.RegisterResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException


class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    fun signup(name:String, email:String, password:String): LiveData<Result<RegisterResponse>> = liveData{
        emit(Result.Loading)
        try {
            val response = apiService.register(name, email, password)
            if (response.error == false) {
                emit(Result.Success(response))
            } else {
                emit(Result.Error(response.message ?: "An error occurred"))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody?.message ?: "An error occurred"
            emit(Result.Error("Registration failed: $errorMessage"))
        }catch (e: Exception){
            emit(Result.Error("Internet Issues"))
        }
    }

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.loginUser(email, password)
            if (response.error == false) {
                val aut = UserModel(
                    name = response.loginResult.name ?: "",
                    userId = response.loginResult.userId ?: "",
                    token = response.loginResult.token,
                    isLogin = true
                )
                ApiConfig.token = response.loginResult.token
                userPreference.saveSession(aut)
                emit(Result.Success(response))
            } else {
                emit(Result.Error(response.message ?: "Error"))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody?.message ?: "An error occurred"
            emit(Result.Error("Login failed: $errorMessage"))
        } catch (e: Exception) {
            emit(Result.Error("Internet Issues"))
        }
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService, userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPreference)
            }.also { instance = it }
    }
}