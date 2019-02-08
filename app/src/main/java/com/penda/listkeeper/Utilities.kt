package com.penda.listkeeper

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import com.penda.listkeeper.datamodel.ListElement
import com.penda.listkeeper.datamodel.MList
import java.text.SimpleDateFormat
import java.util.*

class Utilities {

    lateinit var context: Context

    companion object {

        fun getDateString(): String {
            val format = "EEEE, MMMM dd, yyyy"
            val sdf = SimpleDateFormat(format, Locale.US)
            val cal = Calendar.getInstance()
            return sdf.format(cal.time)
        }

        fun buildShareIntent(mList: MList, elements: List<ListElement>, context: Context) {
            var shareText = mList.listName + "\n" + mList.listDate + "\n\n"
            var count = 1
            for (element in elements) {
                if (element.elementState.equals("active")) {
                    shareText += Integer.toString(count) + ") " + element.elementValue + "\n"
                    count++
                }
            }
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.type = "text/plain"
            emailIntent.putExtra(Intent.EXTRA_TEXT, shareText)
            context.startActivity(Intent.createChooser(emailIntent, "Choose app..."))
        }

        fun <T> putPrefs(t: T, name: String, context: Context) {
            val prefs = context.getSharedPreferences("listkeeperprefs", Context.MODE_PRIVATE)
            val editor = prefs.edit()
            when(t){
                is Boolean -> editor.putBoolean(name, t)
                is String -> editor.putString(name, t)
                is Int -> editor.putInt(name, t)
                else -> {}
            }
            editor.apply()
        }

        fun <T> getPrefs(name: String, t: T, context: Context): Bundle {
            val prefs = context.getSharedPreferences("listkeeperprefs", Context.MODE_PRIVATE)
            val bundle =  Bundle()
            when(t){
                is Boolean -> bundle.putBoolean(name, prefs.getBoolean(name, t))
                is Int -> bundle.putInt(name, prefs.getInt(name, t))
                is String -> bundle.putString(name, prefs.getString(name, t))
                else -> {}
            }
            return bundle
        }

        fun animateMic(): Animation{
            val animation = AlphaAnimation(1f,0f)
            animation.apply{
                duration = 500
                interpolator = LinearInterpolator()
                repeatCount = Animation.INFINITE
                repeatMode = Animation.REVERSE
            }
            return animation
        }
    }
}