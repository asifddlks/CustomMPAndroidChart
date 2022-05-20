package com.asifddlks.custommpandroidchart

import android.content.Context
import java.io.IOException
import java.io.InputStream

//
// Created by Asif Ahmed on 20/5/22.
//
class FileHelper {

    fun  readFile(context: Context, fileNameWithExtension:String):String?
    {
        var string:String? = null
        try {
            val inputStream: InputStream = context.assets.open(fileNameWithExtension)
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            string = String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return string
    }

    fun  readJSONFile(context: Context, fileNameWithoutExtension:String):String?
    {
        var string:String? = null
        try {
            val inputStream: InputStream = context.resources.openRawResource(
                context.resources.getIdentifier(
                    fileNameWithoutExtension,
                    "raw", context.packageName
                )
            )
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            string = String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return string
    }

}