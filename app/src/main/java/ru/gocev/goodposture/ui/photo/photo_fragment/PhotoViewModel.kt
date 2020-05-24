package ru.gocev.goodposture.ui.photo.photo_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PhotoViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Чтобы проверить осанку, добавьте фото"
    }
    val text: LiveData<String> = _text
}