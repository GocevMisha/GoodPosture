package ru.gocev.goodposture.models

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.history_item.view.*
import ru.gocev.goodposture.MainActivity
import ru.gocev.goodposture.R
import java.text.SimpleDateFormat
import java.util.*


class HistoryAdapter(val ctx: Context?, val data: LiveData<List<HistoryItem>>): RecyclerView.Adapter<ViewH>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewH {
        return ViewH(LayoutInflater.from(ctx).inflate(R.layout.history_item,p0,false))
    }

    override fun getItemCount(): Int {
        Log.d("Adapter Size ",data.value.toString())
        return data.value!!.size

    }

    override fun onBindViewHolder(p0: ViewH, p1: Int) {
        p0.bindItems(data.value!![p1])

    }
}

class ViewH(itemView: View): RecyclerView.ViewHolder(itemView) {

    fun bindItems(historyItem: HistoryItem){

        val name = itemView.tv_name
        val date = itemView.tv_date
        val status = itemView.tv_status
        name.text = historyItem.name
        val dateFormat = SimpleDateFormat("HH:mm:ss dd.MM.yyyy", Locale("ru"))
        date.text = dateFormat.format(historyItem.date)
        when(historyItem.status){
            HistoryItem.Status.OK -> status.text = "Готово"
            HistoryItem.Status.WAIT -> status.text = "Обрабатывается"
            HistoryItem.Status.ERROR -> status.text = "Ошибка"
        }
        itemView.setOnClickListener(View.OnClickListener {
            val bundle = Bundle()
            bundle.putLong("id", historyItem.id)
            bundle.putSerializable("status", historyItem.status)
            (itemView.context as MainActivity).navigate(R.id.navigation_result, bundle)
        })
    }
}