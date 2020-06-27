package kr.eunice.custume

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import gun0912.tedimagepicker.builder.TedImagePicker
import kr.eunice.custume.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), View.OnClickListener {

    val imageUri = ObservableField<Uri?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(
            this@MainActivity,
            R.layout.activity_main
        )

        binding.activity = this@MainActivity

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            askOverlayPermission()
        }
    }

    private fun askOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION)
    }

    override fun onClick(v: View?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            startService(Intent(this@MainActivity, FloatingViewService::class.java))
            finish()
        } else if (Settings.canDrawOverlays(this)) {
            startService(Intent(this@MainActivity, FloatingViewService::class.java))
            finish()
        } else {
            askOverlayPermission()
            Toast.makeText(
                this,
                "You need System Alert Window Permission to do this",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun loadImage() {
        if (listOf<Int>(
                ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ).any { it != PackageManager.PERMISSION_GRANTED }
        ) {

            // Permission is not granted
            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(
//                    this@MainActivity,
//                    Manifest.permission.READ_EXTERNAL_STORAGE
//                )
//            ) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
//            } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE
            )

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
//            }
        } else {
            startImagePicker()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    loadImage()
//                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }


    private fun startImagePicker() {
        TedImagePicker.with(this)
            .start { uri -> showSingleImage(uri) }
    }

    private fun showSingleImage(uri: Uri) {
        imageUri.set(uri)
    }

    companion object {
        const val SYSTEM_ALERT_WINDOW_PERMISSION = 2084

        const val MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 1
        const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2
    }
}