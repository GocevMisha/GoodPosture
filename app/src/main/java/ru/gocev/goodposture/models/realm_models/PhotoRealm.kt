package ru.gocev.goodposture.models.realm_models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

public open class PhotoRealm(
    @PrimaryKey public var id:  Long = 0,
    public open var name: String = "",
    public open var uri: String? = null,
    public open var status: Boolean = false,
    public open var serverId: String = "",
    public open var date: Date = Date(),
    public open var personList: RealmList<PersonRealm>? = null,
    public open var error: String? = null
) : RealmObject(){

}