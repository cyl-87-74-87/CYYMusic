package com.xiangxue.architecture.utils

import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File

/**
 * <pre>
 * author:
 * blog  :
 * time  :
 * desc  : utils derry
</pre> *
 */
class CompatUtils {
    private var uriClipUri: Uri? = null
    private var takePhotoSaveAdr: Uri? = null
    private fun taskPhoto(activity: AppCompatActivity) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val mImageCaptureUri: Uri
        // 判断7.0android系统
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //临时添加一个拍照权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            //通过FileProvider获取uri
            takePhotoSaveAdr = FileProvider.getUriForFile(
                activity, activity.packageName,
                File(activity.externalCacheDir, "savephoto.jpg")
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, takePhotoSaveAdr)
        } else {
            mImageCaptureUri = Uri.fromFile(File(activity.externalCacheDir, "savephoto.jpg"))
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri)
        }
        activity.startActivityForResult(intent, TAKE_PHOTO)
    }

    private fun selecetPhoto(activity: AppCompatActivity) {
        val intent = Intent(Intent.ACTION_PICK, null)
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*")
        activity.startActivityForResult(intent, PHOTO_ALBUM)
    }

    private fun startPhotoZoom(activity: AppCompatActivity, uri: Uri?, requestCode: Int) {
        val intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(uri, "image/*")
        intent.putExtra("crop", "true")
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        intent.putExtra("outputX", 60)
        intent.putExtra("outputY", 60)
        //uriClipUri为Uri类变量，实例化uriClipUri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (requestCode == TAKE_PHOTO) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.clipData = ClipData.newRawUri(MediaStore.EXTRA_OUTPUT, uri)
                uriClipUri = uri
            } else if (requestCode == PHOTO_ALBUM) {
                uriClipUri = Uri.parse(
                    "file://" + "/" + activity.externalCacheDir!!
                        .absolutePath + "/" + "clip.jpg"
                )
            }
        } else {
            uriClipUri =
                Uri.parse("file://" + "/" + activity.externalCacheDir!!.absolutePath + "/" + "clip.jpg")
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriClipUri)
        intent.putExtra("return-data", false)
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        intent.putExtra("noFaceDetection", true)
        activity.startActivityForResult(intent, PHOTO_CLIP)
    }

    fun onActivityResult(
        activity: AppCompatActivity,
        requestCode: Int,
        resultCode: Int,
        data: Intent
    ) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
            when (requestCode) {
                TAKE_PHOTO -> {
                    var clipUri: Uri? = null
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (takePhotoSaveAdr != null) {
                            clipUri = takePhotoSaveAdr
                        }
                    } else {
                        clipUri =
                            Uri.fromFile(File(activity.externalCacheDir.toString() + "/savephoto.jpg"))
                    }
                    startPhotoZoom(activity, clipUri, TAKE_PHOTO)
                }
                PHOTO_ALBUM -> startPhotoZoom(activity, data.data, PHOTO_ALBUM)
                PHOTO_CLIP -> {
                }
                else -> {
                }
            }
        }
    }

    companion object {
        private const val TAKE_PHOTO = 100
        private const val PHOTO_ALBUM = 200
        private const val PHOTO_CLIP = 300
    }
}