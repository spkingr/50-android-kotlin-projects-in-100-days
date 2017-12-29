package me.liuqingwen.android.projectimageuploader

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import java.io.File
import java.util.*

/**
 * Created by Qingwen on 2017-2017-12-28, project: ProjectImageUploader.
 *
 * @Author: Qingwen
 * @DateTime: 2017-12-28
 * @Package: me.liuqingwen.android.projectimageuploader in project: ProjectImageUploader
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

const val FILE_PROVIDER_AUTHORITY = "com.android.camera.action.CROP"

data class UploadResult(val result:Boolean, val info:String, val author:String?, val url:String?, val date:String?, val description:String?)
data class LoginResult(val result:Boolean, val info:String)

fun getCropIntent(uri:Uri?) = Intent(FILE_PROVIDER_AUTHORITY).apply {
    this.setDataAndType(uri, "image/*")
    this.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(File(Environment.getExternalStorageDirectory(), "${Date().time}.jpeg")))
    this.putExtra("crop", "true")
    this.putExtra("aspectX", 1)
    this.putExtra("aspectY", 1)
    this.putExtra("outputX", 800)
    this.putExtra("outputY", 800)
    this.putExtra("scale", true)
    this.putExtra("noFaceDetection", true)
    this.putExtra("return-data", true)
    this.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.name)
    this.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    this.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
}

fun getRealUri(context: Context, uri : Uri?):Uri?
{
    if (uri == null || Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT)
    {
        return uri
    }
    
    var path:String? = null
    when
    {
        DocumentsContract.isDocumentUri(context, uri) -> when
        {
            isExternalStorageDocument(uri) -> {
                val docId = DocumentsContract.getDocumentId(uri)
                val values = docId.split(":")
                val type = values.first()
                if ("primary" == type.toLowerCase())
                {
                    //path ? or absolute path?
                    path = Environment.getExternalStorageDirectory().path + "/" + values[1]
                }
            }
            isDownloadsDocument(uri) -> {
                val docId = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), docId.toLong())
                path = getDataColumnFromUri(context, contentUri)
            }
            isMediaDocument(uri) -> {
                val docId = DocumentsContract.getDocumentId(uri)
                val values = docId.split(":")
                val type = values.first()
                val contentUri = when(type.toLowerCase()) {
                    "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    else -> null
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(values[1])
                path = contentUri?.let { getDataColumnFromUri(context, it, selection, selectionArgs) }
            }
            else -> {}
        }
        "content" == uri.scheme.toLowerCase()         -> path = if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumnFromUri(context, uri)
        "file" == uri.scheme.toLowerCase()            -> path = uri.path
        else -> {}
    }
    
    return path?.let {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) FileProvider.getUriForFile(context, "me.liuqingwen.fileProvider", File(it))
        else Uri.fromFile(File(it))
    }
}

fun getDataColumnFromUri(context: Context, uri: Uri, selection : String? = null, selectionArgs : Array<String>? = null):String?
{
    val column = "_data"
    val projection = arrayOf(column)
    var cursor:Cursor? = null
    return try
    {
        cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.moveToFirst()
        val index = cursor?.getColumnIndexOrThrow(column)
        index?.let { cursor?.getString(it) }
    }
    catch (e:Exception) { null }
    finally { cursor?.close() }
}

fun isExternalStorageDocument(uri: Uri) = "com.android.externalstorage.documents" == uri.authority.toLowerCase()
fun isDownloadsDocument(uri: Uri) = "com.android.providers.downloads.documents" == uri.authority.toLowerCase()
fun isMediaDocument(uri: Uri) = "com.android.providers.media.documents" == uri.authority.toLowerCase()
fun isGooglePhotosUri(uri: Uri) = "com.google.android.apps.photos.content" == uri.authority.toLowerCase()