package ru.gocev.goodposture.ui.histories.result

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm
import io.realm.RealmResults
import ru.gocev.goodposture.models.ResultItem
import ru.gocev.goodposture.models.realm_models.PhotoRealm

class ResultViewModel : ViewModel() {
    private var result: MutableLiveData<ResultItem>? = null
    private lateinit var realm: Realm
    private lateinit var realmResults: RealmResults<PhotoRealm>
    internal fun getResult(id: Long?): MutableLiveData<ResultItem> {
        if (result == null) {
            result = MutableLiveData()
            loadResult(id)
        }
        return result as MutableLiveData<ResultItem>
    }
    private fun loadResult(id: Long?) {
        realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realmResults = realm.where(PhotoRealm::class.java).equalTo("id", id).findAll()
        realm.commitTransaction()
        for (realmResult in realmResults){
            Log.d("M_ResultViewModel", realmResult.name)
            result?.postValue(ResultItem(realmResult.name, realmResult.personList, realmResult.uri ))
        }


    }
}
