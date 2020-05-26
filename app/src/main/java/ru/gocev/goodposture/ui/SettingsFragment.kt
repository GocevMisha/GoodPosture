package ru.gocev.goodposture.ui

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import ru.gocev.goodposture.MainActivity
import ru.gocev.goodposture.R


class SettingsFragment : Fragment() {
    lateinit var ulrEditText: EditText
    lateinit var saveButton: Button
    lateinit var pref: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        pref = requireActivity().getPreferences(Context.MODE_PRIVATE)

        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        ulrEditText = root.findViewById(R.id.url)
        ulrEditText.setText(pref.getString("api_url", ""))
        saveButton = root.findViewById(R.id.save_button)

        saveButton.setOnClickListener(View.OnClickListener {
            val edt = pref.edit()
            edt.putString("api_url", ulrEditText.text.toString())
            edt.apply()
            val toast: Toast = Toast.makeText(requireActivity(), "Адрес сервера сохранен", Toast.LENGTH_LONG)
            toast.show()
        })
        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            Log.d("M_PhotoFragment", "rtr")
            val image = data?.data as Uri
            val bundle = Bundle()
            bundle.putParcelable("image", image)
            (activity as MainActivity).navigate(R.id.navigation_input_name, bundle)
        }
    }
}