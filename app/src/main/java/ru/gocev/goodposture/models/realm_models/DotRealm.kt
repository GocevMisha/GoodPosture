package ru.gocev.goodposture.models.realm_models

import io.realm.RealmObject

public open class DotRealm(
    public open var x: Float? = 0f,
    public open var y: Float? = 0f
) : RealmObject(){}
