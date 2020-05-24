package ru.gocev.goodposture.ui.photo.photo_fragment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ru.gocev.goodposture.MainActivity
import ru.gocev.goodposture.R

class PhotoFragment : Fragment() {
    companion object {
        private const val IMAGE_PICK_CODE = 999
    }
    private lateinit var photoViewModel: PhotoViewModel
    private val CAMERA_REQUEST_CODE=0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        photoViewModel =
            ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_photo, container, false)
        val photoTextView: TextView = root.findViewById(R.id.tv_photo)
        photoViewModel.text.observe(this, Observer {
            photoTextView.text = it
        })
        val addPhotoImageButton: ImageButton = root.findViewById(R.id.ib_add_photo)
        addPhotoImageButton.setOnClickListener(View.OnClickListener {
            if(isStoragePermissionGranted())
                dispatchTakePictureIntent()
        })
        return root
    }

    fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(context!! ,Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                Log.v("log", "Permission is granted")
                true
            } else {
                Log.v("log", "Permission is revoked")
                ActivityCompat.requestPermissions(
                    activity as MainActivity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    2
                )
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("log", "Permission is granted")
            true
        }
    }
    val REQUEST_IMAGE_CAPTURE = 1

    private fun dispatchTakePictureIntent() {
        Intent(Intent.ACTION_PICK).also { takePictureIntent ->
            takePictureIntent.type = "image/*"
            takePictureIntent.resolveActivity((activity as MainActivity).packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.d("M_PhotoFragment", "rtr")
            val image = data?.data as Uri
            val bundle = Bundle()
            bundle.putParcelable("image", image)
            (activity as MainActivity).navigate(R.id.navigation_input_name, bundle)
        }
    }
}