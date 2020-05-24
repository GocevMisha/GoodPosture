package ru.gocev.goodposture.ui.photo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import ru.gocev.goodposture.MainActivity

import ru.gocev.goodposture.R

class ConfirmationFragment : Fragment() {
    lateinit var statusText: TextView
    lateinit var imageView: ImageView
    lateinit var okTextView: TextView
    companion object {
        fun newInstance() = ConfirmationFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root =  inflater.inflate(R.layout.confirmation_fragment, container, false)
        statusText = root.findViewById(R.id.tv_status)
        imageView = root.findViewById(R.id.iv)
        okTextView = root.findViewById(R.id.tv_ok)
        okTextView.setOnClickListener(View.OnClickListener {
            (activity as MainActivity).navigate(R.id.navigation_photo, null)
        })
        val error = arguments?.getBoolean("error") ?: true
        if (error){
            statusText.text = "Ошибка! Не удалось отправить фото"
            imageView.setImageDrawable(activity?.getDrawable(R.drawable.sad_emoji))
        } else{
            statusText.text = "Фото отправлено на обработку"
            imageView.setImageDrawable(activity?.getDrawable(R.drawable.smile_emoji))
        }
        return root
    }


}
