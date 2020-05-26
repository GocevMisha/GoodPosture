package ru.gocev.goodposture.ui.photo

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FileDataPart
import com.github.kittinunf.fuel.core.Method
import io.realm.Realm
import ru.gocev.goodposture.MainActivity
import ru.gocev.goodposture.R
import ru.gocev.goodposture.models.realm_models.PhotoRealm
import ru.gocev.goodposture.responses.PostResponse
import java.io.File


class InputNameFragment : Fragment() {
    lateinit var nameEditText: TextView
    lateinit var imageView: ImageView
    lateinit var sendTextView: TextView
    lateinit var realm: Realm
    private lateinit var progressBar: LinearLayout
    companion object {
        fun newInstance() = InputNameFragment()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.input_name_fragment, container, false)
        realm = Realm.getDefaultInstance()
        progressBar = root.findViewById(R.id.llProgressBar)
        nameEditText = root.findViewById(R.id.et_name)
        imageView = root.findViewById(R.id.iv)
        val uri = arguments?.getParcelable<Uri>("image")
        Glide.with(requireContext()).load(uri).into(imageView)
        sendTextView = root.findViewById(R.id.tv_send)
        sendTextView.setOnClickListener(View.OnClickListener {
            progressBar.visibility = View.VISIBLE
            var error = true
            if (uri != null) {
                val file = File(getRealPathFromURI(requireContext(), uri))
                upload( file = file)
            }
        })
        return root
    }


    private fun upload( file: File) {
        val pref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val baseUrl = pref.getString("api_url", "") as String + "upload"
        Fuel.upload(path = baseUrl, method = Method.POST)
            .add{ FileDataPart(file, "file",  file.name)}
            .timeout(60000)
            .responseObject(PostResponse.Deserializer()) { _, _, result ->
                result.fold(
                    success = {
                         val serverId = result.component1()?.data?.id
                        val status = result.component1()?.status
                        if(status.equals("ok") && serverId!=null)
                            okLeave(uri = file.path, serverId = serverId)
                        else errorLeave()
                    },
                    failure = { error ->
                        Log.d("M_InputNameFragment","An error of type ${error.exception} happened: ${error.message}")
                        errorLeave()
                    }
                )

            }
    }
    private fun okLeave(uri: String, serverId: String){
        addPhotoToDb(uri, serverId)
        val bundle = Bundle()
        bundle.putBoolean("error", false)
        progressBar.visibility = View.INVISIBLE
        (activity as MainActivity).navigate(R.id.navigation_confirmation, bundle)
    }
    private fun errorLeave(){
        val bundle = Bundle()
        bundle.putBoolean("error", true)
        progressBar.visibility = View.INVISIBLE
        (activity as MainActivity).navigate(R.id.navigation_confirmation, bundle)
    }
    private fun getRealPathFromURI(
        context: Context,
        contentUri: Uri?
    ): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
            val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        } finally {
            cursor?.close()
        }
    }


    private fun addPhotoToDb(uri: String, serverId: String){
        realm.beginTransaction()
        val num = realm.where<PhotoRealm>(PhotoRealm::class.java).max("id")
        val nextID = if (num == null) {
            1
        } else {
            num.toInt() + 1
        }
        val photoRealm = realm.createObject(PhotoRealm::class.java, nextID)
        if(nameEditText.text.isEmpty())
           photoRealm.name = "Фото №${photoRealm.id}"
        else photoRealm.name = nameEditText.text.toString()
        photoRealm.serverId = serverId
        photoRealm.uri = uri
        realm.commitTransaction()
    }
}
