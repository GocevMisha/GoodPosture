package ru.gocev.goodposture.models

import io.realm.RealmList
import ru.gocev.goodposture.models.realm_models.PersonRealm

data class ResultItem ( val name: String, val persons: RealmList<PersonRealm>?, val url: String?)