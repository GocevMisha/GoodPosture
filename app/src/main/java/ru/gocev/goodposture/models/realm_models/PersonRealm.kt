package ru.gocev.goodposture.models.realm_models

import io.realm.RealmList
import io.realm.RealmObject

public open class PersonRealm(
    public open var dots: RealmList<DotRealm>? = null,
    public open var check: Boolean = false
) : RealmObject(){}
