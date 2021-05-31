package appjpm4everyone.ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import appjpm4everyone.base.BaseActivity
import appjpm4everyone.ui.mainactivity.MainActivity
import appjpm4everyone.ui.mapskotlin.BuildConfig
import appjpm4everyone.ui.mapskotlin.databinding.ActivitySplashBinding
import appjpm4everyone.ui.viewplace.ViewPlaceActivity

private const val TIME_SLEEP: Long = 2500

@Suppress("DEPRECATION")
class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUi()
    }

    private fun initUi() {
        binding.tvVersion.text = BuildConfig.VERSION_NAME
        // Use
        val handler = Handler()
        handler.postDelayed({
            // do something after 1000ms
            onNavigationMain()
        }, TIME_SLEEP)

    }

    private fun onNavigationMain() {
        // Start the MainActivity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}