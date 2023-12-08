package com.dicoding.picodiploma.hugme.view.signup

import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.hugme.data.UserRepository

class SignupViewModel(private val repository: UserRepository) : ViewModel() {

    fun signup(name : String, email: String, password: String) =
        repository.signup(name, email, password)

}