package appjpm4everyone.base

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import appjpm4everyone.ui.mapskotlin.R
import appjpm4everyone.utils.CustomProgressBar
import de.mateware.snacky.Snacky

abstract class BaseActivity : AppCompatActivity() {

    lateinit var progressBar: CustomProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressBar = CustomProgressBar()
    }

    override fun onDestroy() {
        progressBar.hideProgress()
        super.onDestroy()
    }

    fun showShortSnackError(activity: Activity, message: String) {
        showSnack(activity, message, Snacky.LENGTH_SHORT)
    }

    fun showShortSnackError(activity: Activity, msgResource: Int) {
        showSnack(activity, msgResource, Snacky.LENGTH_SHORT)
    }

    fun showLongSnackError(activity: Activity, message: String) {
        showSnack(activity, message, Snacky.LENGTH_LONG)
    }

    fun showLongSnackError(activity: Activity, msgResource: Int) {
        showSnack(activity, msgResource, Snacky.LENGTH_LONG)
    }

    fun showSnack(activity: Activity, message: String, lenght: Int) {
        //val typeface = ResourcesCompat.getFont(this, R.font.brown_regular)
        Snacky.builder()
            .setActivity(activity)
            .setTextSize(16f)
            //.setTextTypeface(typeface)
            .setIcon(R.drawable.ic_error)
            .setText(message)
            .setBackgroundColor(ContextCompat.getColor(activity, R.color.pink))
            .setDuration(lenght)
            .error()
            .show()
    }

    fun showSnack(activity: Activity, msgResource: Int, lenght: Int) {
        //val typeface = ResourcesCompat.getFont(this, R.font.brown_regular)
        Snacky.builder()
            .setActivity(activity)
            .setTextSize(16f)
            //.setTextTypeface(typeface)
            .setText(msgResource)
            .setIcon(R.drawable.ic_error)
            .setDuration(lenght)
            .setBackgroundColor(ContextCompat.getColor(activity, R.color.pink))
            .error()
            .show()
    }

    fun delayMs(millS: Long) {
        // Use
        val handler = Handler()
        handler.postDelayed({
        }, millS)
    }
}
