package com.glushko.kodetestteask.business_logic_layer.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class User(
    val id: String = "default",
    val avatarUrl: String = "default",
    val firstName: String = "default",
    val lastName: String = "default",
    val userTag: String = "default",
    val department: String = "default",
    val position: String = "default",
    val birthday: String = "default",
    val phone: String = "default",
    //Переменные не участвуюшие в парсинге
    var typeView: Int = 0, //Для отображения в recycler 0 - обычный вид, 1 - с датой
    var dopBirthday: String = "default",
    var lineYear: String = "default",
):Parcelable