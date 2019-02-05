package com.penda.listkeeper

import android.content.Context
import android.content.Intent
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
    }
}