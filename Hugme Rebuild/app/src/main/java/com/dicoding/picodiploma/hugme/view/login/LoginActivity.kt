package com.dicoding.picodiploma.hugme.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.hugme.data.pref.UserModel
import com.dicoding.picodiploma.hugme.databinding.ActivityLoginBinding
import com.dicoding.picodiploma.hugme.view.ViewModelFactory
import com.dicoding.picodiploma.hugme.data.Result
import com.dicoding.picodiploma.hugme.view.main.MainActivity
import com.dicoding.picodiploma.hugme.view.signup.SignupActivity

class   LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.TextLinkSignUp.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupAction() {

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isEmpty()){
                binding.emailEditText.error = "Email tidak boleh kosong"
                return@setOnClickListener
            }
            if (password.isEmpty()){
                binding.passwordEditText.error = "password tidak boleh kosong"
                return@setOnClickListener

            } else if (email.isNotEmpty() && password.isNotEmpty()) {

                viewModel.login(email, password).observe(this){result ->
                    if (result!=null){
                        when (result){
                            is Result.Loading -> {
                                showLoading(isLoading = true)
                            }
                            is Result.Success -> {
                                AlertDialog.Builder(this).apply {
                                    setTitle("Login Success")
                                    setMessage("Welcome $email")

                                    create()
                                    show()
                                }
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                                val dataUser = UserModel(
                                    result.data.loginResult.token,
                                    result.data.loginResult.userId.toString(),
                                    result.data.loginResult.name.toString(),
                                    true
                                )
                                showLoading(isLoading = false)
                                viewModel.saveSession(dataUser)
                                finish()
                            }

                            is Result.Error ->{
                                showLoading(isLoading = false)
                                showToast(result.error)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)
        val text = ObjectAnimator.ofFloat(binding.TextLinkSignUp, View.ALPHA, 1f).setDuration(100)
        val tvsignup = ObjectAnimator.ofFloat(binding.TextSignUp,View.ALPHA, 1f).setDuration(100)
        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login,
                text,
                tvsignup
            )
            startDelay = 100
        }.start()
    }
}