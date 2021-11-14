package com.glushko.kodetestteask.business_logic_layer.interactor

import android.annotation.SuppressLint
import android.os.Build
import com.glushko.kodetestteask.business_logic_layer.domain.User
import com.glushko.kodetestteask.data_layer.datasource.NetworkService
import com.glushko.kodetestteask.data_layer.datasource.response.ResponseUsers
import com.glushko.kodetestteask.presentation_layer.vm.UserViewModel
import io.reactivex.Observable
import io.reactivex.Single
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class UseCase() {

    private val current = Date()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val dateFormatYear = SimpleDateFormat("yyyy", Locale.getDefault())
    private val dateFormatMonth = SimpleDateFormat("MM", Locale.getDefault())
    private val dateFormatDay = SimpleDateFormat("dd", Locale.getDefault())

    fun getUsers(): Observable<ResponseUsers> {
        return NetworkService.makeNetworkServiceRxJava().getUsers()
    }

    fun searchUser(text: String, users: MutableList<User>, typeFind: Int, department: String, typeSort: Int): Single<List<User>> {
        val usersReturn = when (typeFind) {
            UserViewModel.TYPE_FIND_TAB -> {
                if(department.isNotEmpty()){
                    sortUserDop(users.filter { it.department == text }.toMutableList(), typeSort)
                }else{
                    sortUserDop(users.toMutableList(), typeSort)
                }
            }
            UserViewModel.TYPE_FIND_USER -> {
                if (department.isEmpty()){
                    sortUserDop(users.filter {
                        it.firstName.contains(text, true) || it.lastName.contains(text, true)
                                || it.userTag.contains(text, true)
                    }.toMutableList(), typeSort)
                }else{
                    sortUserDop(users.filter {
                        (it.firstName.contains(text, true) || it.lastName.contains(text, true)
                                || it.userTag.contains(text, true)) && it.department == department
                    }.toMutableList(), typeSort)
                }
            }
            else -> users
        }
        return Single.just(usersReturn)
    }

    @SuppressLint("CheckResult")
    fun sortUser(users: MutableList<User>, typeSort: Int): Observable<List<User>> {
        val usersReturn = when (typeSort) {
            UserViewModel.TYPE_SORT_ALPHABET -> prepareList(users, typeSort).sortedWith(compareBy { it.firstName })
            UserViewModel.TYPE_SORT_BIRTHDAY -> setLineYear(prepareList(users, typeSort).sortedWith(compareBy {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LocalDate.parse(
                        it.dopBirthday,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    )
                } else {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.dopBirthday)
                }
            }).toMutableList(), typeSort)
            else -> users
        }
        return Observable.fromArray(usersReturn)
    }
    private fun sortUserDop(users: MutableList<User>, typeSort: Int): List<User> {
        if (users.isNotEmpty()){
            val usersReturn = when (typeSort) {
                UserViewModel.TYPE_SORT_ALPHABET -> setLineYear(prepareList(users, typeSort).sortedWith(compareBy { it.firstName }).toMutableList(), typeSort)
                UserViewModel.TYPE_SORT_BIRTHDAY -> setLineYear(prepareList(users, typeSort).sortedWith(compareBy {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        LocalDate.parse(
                            it.dopBirthday,
                            DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        )
                    } else {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.dopBirthday)
                    }
                }).toMutableList(), typeSort)
                else -> users
            }
            return usersReturn
        }else{
            return users
        }
    }

    private fun setLineYear(users: MutableList<User>, typeSort: Int): List<User>{
        val endYear = dateFormat.parse("${dateFormatYear.format(current)}-12-31")
        val minDateAll =  endYear.time - dateFormat.parse(users.first().dopBirthday).time
        var indexAt = 0
        users.forEachIndexed { index, user ->
            users[index].lineYear = "default"
            if(typeSort == UserViewModel.TYPE_SORT_BIRTHDAY){
                if (index > 0){
                    val curDat = dateFormat.parse(user.dopBirthday)
                    val diff = endYear.time - curDat.time
                    if(diff>=0){
                        if(diff < minDateAll){
                            indexAt = index
                        }
                    }
                }
            }
        }
        if(typeSort == UserViewModel.TYPE_SORT_BIRTHDAY && users.isNotEmpty()){
            if(indexAt == 0){
                if(minDateAll < 0){
                    users[indexAt].lineYear = "${dateFormatYear.format(current).toInt() + 1}"
                }else{
                    users[indexAt+1].lineYear = "${dateFormatYear.format(current).toInt() + 1}"
                }
            }else{
                users[indexAt+1].lineYear = "${dateFormatYear.format(current).toInt() + 1}"
            }
        }
        return users.toList()
    }

    private fun prepareList(users: MutableList<User>, typeSort: Int): MutableList<User> {

        users.forEach {
            if(typeSort == UserViewModel.TYPE_SORT_ALPHABET){
                it.typeView = 0// протавляем тип для recycler
                it.dopBirthday = newDate(it.birthday)
            } else{
                it.typeView = 1
                it.dopBirthday = newDate(it.birthday)
            }
        }
        return users
    }



    private fun newDate(birthday: String): String{
        //Вытащили дату

        //Вытащить текущий год
        val nowYear = dateFormatYear.format(current).toInt()
        //Текущий месяц и день
        val nowMonth = dateFormatMonth.format(current).toInt()
        val nowDay = dateFormatDay.format(current).toInt()
        //Выташим из дата дня рождения месяц и день
        val userMonth = dateFormatMonth.format(dateFormat.parse(birthday)).toInt()
        val userDay = dateFormatDay.format(dateFormat.parse(birthday)).toInt()
        val newDate = if(userMonth < nowMonth){
            "${nowYear+1}-${getForDateFormat(userMonth)}-${getForDateFormat(userDay)}"
        }else{
            if(userMonth == nowMonth && userDay < nowDay){
                "${nowYear+1}-${getForDateFormat(userMonth)}-${getForDateFormat(userDay)}"
            }else{
                "${nowYear}-${getForDateFormat(userMonth)}-${getForDateFormat(userDay)}"
            }
        }
        return newDate
    }

    private fun getForDateFormat(digit: Int): String{
        return if(digit>=10) digit.toString() else "0${digit}"
    }
}