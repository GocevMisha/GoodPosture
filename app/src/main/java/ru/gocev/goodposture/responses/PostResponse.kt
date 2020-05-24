package ru.gocev.goodposture.responses

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class PostResponse (
     val status : String,
     val data : Data
){
    data class Data (
        val id: String
    )
    class Deserializer: ResponseDeserializable<PostResponse> {
        override fun deserialize(content: String): PostResponse? = Gson().fromJson(content, PostResponse::class.java)
    }
}
