package com.sid.nike.utils

import java.text.ParseException
import java.text.SimpleDateFormat

class AppUtils{
    companion object {
        fun convertToGeneralDateFormat(dateString:String?): String{
            var output = ""
            if(dateString != null) {
                try {
                    var inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    var date = inputFormat.parse(dateString)

                    var outputFormat = SimpleDateFormat("dd MMM yyyy")
                    output = outputFormat.format(date)
                } catch (exception:ParseException){}
            }
            return output
        }
    }
}