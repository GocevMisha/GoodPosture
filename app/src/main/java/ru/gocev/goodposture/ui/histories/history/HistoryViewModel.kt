package ru.gocev.goodposture.ui.histories.history

import androidx.lifecycle.switchMap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.load.engine.Resource
import com.github.kittinunf.fuel.Fuel
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmList
import io.realm.RealmResults
import ru.gocev.goodposture.responses.GetResponse
import ru.gocev.goodposture.models.HistoryItem
import ru.gocev.goodposture.models.realm_models.DotRealm
import ru.gocev.goodposture.models.realm_models.PersonRealm
import ru.gocev.goodposture.models.realm_models.PhotoRealm

class HistoryViewModel : ViewModel() {
    private var photoList: MutableLiveData<List<HistoryItem>>? = null

    private lateinit var realm: Realm
    private lateinit var realmResults: RealmResults<PhotoRealm>
    internal fun getPhotoList(): MutableLiveData<List<HistoryItem>> {
        realm = Realm.getDefaultInstance()
        if (photoList == null) {
            photoList = MutableLiveData()
            load()
        }
        return photoList as MutableLiveData<List<HistoryItem>>
    }
    fun load(){
        loadPhotosFromDB()
        loadFromServer()
    }
    private fun loadPhotosFromDB() {
        val photos = ArrayList<HistoryItem>()
        realm.beginTransaction()
        realmResults = realm.where(PhotoRealm::class.java).sort("date").findAll()
        realm.commitTransaction()
        for (realmResult in realmResults){
            Log.d("M_HistoryViewModel", realmResult.id.toString())
            val status: HistoryItem.Status = getStatus(realmResult)
            photos.add(HistoryItem(realmResult.id, status, realmResult.date, realmResult.name))
        }
        this.photoList!!.postValue(photos)//for background postValue
        realmResults.addChangeListener(RealmChangeListener {  realmResults->
            photos.clear()
            for (realmResult in realmResults){
                val status: HistoryItem.Status = getStatus(realmResult)
                photos.add(HistoryItem(realmResult.id, status, realmResult.date, realmResult.name))
                }
            Log.d("M_GHJJ", "rtr")
            this.photoList!!.postValue(photos)//for background postValue
        })
    }

    private fun getStatus(realmResult: PhotoRealm): HistoryItem.Status{
        return if(realmResult.status)
                    if(realmResult.error!=null)
                        HistoryItem.Status.ERROR
                    else HistoryItem.Status.OK
                else HistoryItem.Status.WAIT
    }
    private fun loadFromServer(){
        realmResults = realm.where(PhotoRealm::class.java).findAll()
        if (realmResults.size>0) {
            for (realmResult in realmResults) {
                Log.d("M_HistoryViewModel", "${realmResult.name}  ${realmResults.indexOf(realmResult)} ${realmResult.serverId} ${realmResult.personList?.toString()}")
                loadById(realmResult)
            }
        }

    }
    private fun loadById(realmResult: PhotoRealm){
        val baseUrl = "https://bb3cc381.ngrok.io/result"
        Log.d("M_HistoryViewModel", realmResult.serverId)
        Fuel.get(path = baseUrl, parameters =  listOf("id" to realmResult.serverId))
            .timeout(60000)
            .responseObject(GetResponse.Deserializer()) { _, _, result ->
                result.fold(
                    success = {
                        val status = result.component1()?.status
                        val data = result.component1()?.data
                        val dots = data?.dots
                        val check = data?.check
                        val message = result.component1()?.message
                        Log.d("M_InputNameFragment", "Response $status " +
                                "$dots " +
                                "$check " +
                                "$message")
                        realm.beginTransaction()
                        Log.d("M_kek", "${realmResult.name} $dots $check ${dots?.isNotEmpty()} ${check?.isNotEmpty()}")
                        if(status.equals("ok")){
                            if(!message.equals("processing") && data!=null
                                && dots!=null &&check!=null
                                && dots.isNotEmpty() && check.isNotEmpty()){
                                    realmResult.status = true
                                    val personList = RealmList<PersonRealm>()
                                    for(personDots in dots){
                                        val dotsList = RealmList<DotRealm>()
                                        for (dot in personDots){
                                            val dotRealm = DotRealm(dot[0].toFloat(), dot[1].toFloat())
                                            dotsList.add(dotRealm)
                                        }
                                        personList.add(PersonRealm(dotsList, check[ dots.indexOf(personDots)]))
                                    }
                                    realmResult.personList?.clear()
                                    realmResult.personList?.addAll(personList)
                                    Log.d("M_HistoryViewModel", "${realmResult.name} ${realmResult.status} ${realmResult.error}")
                                    realmResult.error = null
                                    Log.d("M_HistoryViewModel", "${realmResult.name} ${realmResult.status} ${realmResult.error} ${getStatus(realmResult)}")


                            }else{
                                if(message.equals("processing")){
                                    realmResult.status = false
                                }
                                else{
                                realmResult.error = "empty"
                                realmResult.status = true}
                            }
                        }
                        else{realmResult.error = message
                             realmResult.status = true}

                        realm.commitTransaction()
                    },
                    failure = { error ->
                        Log.d("M_HistoryViewModel", "An error of type ${error.exception} happened: ${error.message}")
                    }
                )
            }
    }
}