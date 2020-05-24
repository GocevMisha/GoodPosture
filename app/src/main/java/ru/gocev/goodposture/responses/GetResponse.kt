package ru.gocev.goodposture.responses

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class GetResponse (
    val status : String,
    val data : Data,
    val message: String
){
    data class Data (
        val dots : List<List<List<Double>>>,
        val check : List<Boolean>
    )
    class Deserializer: ResponseDeserializable<GetResponse> {
        override fun deserialize(content: String): GetResponse? = Gson().fromJson(content, GetResponse::class.java)
    }
}