package ru.gocev.goodposture.ui.histories.result

import android.app.AlertDialog
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.realm.Realm
import io.realm.RealmList
import ru.gocev.goodposture.MainActivity
import ru.gocev.goodposture.R
import ru.gocev.goodposture.models.HistoryItem
import ru.gocev.goodposture.models.ResultItem
import ru.gocev.goodposture.models.realm_models.DotRealm
import ru.gocev.goodposture.models.realm_models.PersonRealm
import ru.gocev.goodposture.models.realm_models.PhotoRealm


class ResultFragment : Fragment() {

    companion object {
        fun newInstance() =
            ResultFragment()
    }

    private lateinit var viewModel: ResultViewModel
    lateinit var resultText: TextView
    lateinit var imageView: ImageView
    lateinit var deleteButton: FloatingActionButton
    val colors = listOf<Int>( Color.YELLOW, Color.BLUE, Color.GREEN, Color.RED, Color.rgb(179, 1, 255), Color.BLACK, Color.CYAN, Color.rgb(255, 99, 0), Color.rgb(244, 120, 196), Color.rgb(153, 77, 0))
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.result_fragment, container, false)
        resultText = root.findViewById(R.id.tv_result)
        imageView = root.findViewById(R.id.iv)
        deleteButton = root.findViewById(R.id.fab_delete)

        viewModel = ViewModelProviders.of(this).get(ResultViewModel::class.java)

        val id = arguments?.getLong("id")

        deleteButton.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Хотите удалить фото?")
            builder.setPositiveButton("Да") { dialog, which ->
                deletePhoto(id)
                (activity as MainActivity).navigate(R.id.navigation_histories, null)
            }
            builder.setNegativeButton("Нет") { dialog, which ->
            }
            builder.show()

        })

        when (arguments?.getSerializable("status") as HistoryItem.Status) {
            HistoryItem.Status.OK -> {
                viewModel.getResult(id).observe(viewLifecycleOwner, Observer<ResultItem> { result ->
                    (activity as MainActivity).supportActionBar?.title = result.name
                    setText(result.persons)

                    Glide.with(requireContext())
                        .asBitmap()
                        .load(result.url)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                drawPointsByPerson(bitmap = resource, persons = result.persons)
                            }
                            override fun onLoadCleared(placeholder: Drawable?) {
                            }
                        })



                })
            }
            HistoryItem.Status.WAIT -> {
                resultText.text = "Фото еще обрабатывается"
                imageView.layoutParams.height = pxFromDp(context, 200f)
                imageView.layoutParams.width = pxFromDp(context, 200f)
                imageView.setImageDrawable(context?.getDrawable(R.drawable.result_photo_defult))
            }
            HistoryItem.Status.ERROR -> {
                resultText.text = "Не удалось обработать фото"
                imageView.layoutParams.height = pxFromDp(context, 200f)
                imageView.layoutParams.width = pxFromDp(context, 200f)
                imageView.setImageDrawable(context?.getDrawable(R.drawable.sad_emoji))
            }
        }
        return root
    }


    private fun setText(persons: RealmList<PersonRealm>?){
        resultText.text = "Результат:\n"
        var i = 0
        if (persons != null) {
            for(person in persons){
                Log.d("M_ResultFragment", "${person.check}")
                i++
                val  line = SpannableString("$i человек - ${if(person.check) "хорошая осанка" else "плохая осанка"}\n")
                Log.d("M_ResultFragment", line.toString())
                line.setSpan(ForegroundColorSpan(colors[i%10]), 0, line.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                resultText.append(line)
            }
        }
    }
    fun drawPointsByPerson(bitmap: Bitmap, persons: RealmList<PersonRealm>?){
        if (persons != null) {
            var i = 0
            for(person in persons){
                i++
                drawPoints(bitmap, person.dots, colors[i%10])
            }
        }
    }

    fun drawPoints(bitmap: Bitmap, points: RealmList<DotRealm>?, color: Int){
        val myOptions: BitmapFactory.Options = BitmapFactory.Options();
        myOptions.inScaled = false
        myOptions.inPreferredConfig = Bitmap.Config.ARGB_8888 // important
        val canvas = Canvas(bitmap)
        val radius = bitmap.height/100f
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = color
        if (points != null) {
            for (dot in points){

                dot.y?.let { dot.x?.let { it1 ->
                    if (it!=0f || it!=0f)
                    canvas.drawCircle(it, it1, radius, paint)
                }
                }
            }
        }
        imageView.setImageBitmap(bitmap)
    }

    private fun deletePhoto(id: Long?){
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val realmResults = realm.where(PhotoRealm::class.java).equalTo("id", id).findAll()
        for (realmResult in realmResults){
            realmResult.deleteFromRealm()
        }
        realm.commitTransaction()
    }

    fun pxFromDp(context: Context?, dp: Float): Int {
        return (dp * (context?.resources?.displayMetrics?.density ?: 1f)).toInt()
    }




}

