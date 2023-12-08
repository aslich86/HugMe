package com.dicoding.picodiploma.hugme.di

import android.content.Context
import com.dicoding.picodiploma.hugme.data.Retrofit.ApiConfig
import com.dicoding.picodiploma.hugme.data.UserRepository
import com.dicoding.picodiploma.hugme.data.pref.UserPreference
import com.dicoding.picodiploma.hugme.data.pref.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(apiService, pref)
    }
}