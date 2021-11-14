package com.glushko.kodetestteask.presentation_layer.ui.user_detail_screen

import android.annotation.SuppressLint
import android.icu.text.DateFormat.Field.YEAR
import android.icu.text.StringPrepParseException
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.glushko.kodetestteask.R
import com.glushko.kodetestteask.business_logic_layer.domain.User
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.Calendar.YEAR
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import android.content.Intent
import android.net.Uri
import com.bumptech.glide.Glide


class FragmentUserInfo : Fragment(R.layout.fragment_user_info) {

    companion object {
        const val EXTRA_USER = "user_info"
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = arguments?.getParcelable<User>(EXTRA_USER) ?: User()
        Glide.with(requireContext()).load(user.avatarUrl).circleCrop().into(view.findViewById(R.id.avatar_user_info))
        view.findViewById<TextView>(R.id.name_user_info).text = "${user.firstName} ${user.lastName}"
        view.findViewById<TextView>(R.id.tag_user_info).text = user.userTag.lowercase()
        view.findViewById<TextView>(R.id.department_user_info).text = user.department
        view.findViewById<TextView>(R.id.birthday_user_info).text =
            getDate(user.birthday)
        view.findViewById<TextView>(R.id.age_user_info).text = calculateAge(user.birthday)
        view.findViewById<TextView>(R.id.number_user_info).text = user.phone

        view.findViewById<LinearLayout>(R.id.btn_phone_user_info).setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", user.phone, null))
            startActivity(intent)
        }
        view.findViewById<ImageButton>(R.id.btn_back_user_info).setOnClickListener {
            val navOption = NavOptions.Builder().setPopUpTo(R.id.fragmentUsers, true).build()
            findNavController().navigate(R.id.action_fragmentUserInfo_to_fragmentUsers, null, navOption)
        }

    }

    private fun getDate(birthday: String): String {
        return SimpleDateFormat(
            "d MMMM yyyy",
            Locale.getDefault()
        ).format(SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).parse(birthday)).toString()
    }

    private fun calculateAge(birthDate: String): String {
        val age =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val current = LocalDate.now()
                val other = LocalDate.parse(birthDate)
                Period.between(current, other).years
            } else {
                val dateFormat = SimpleDateFormat("yyyy-mm-dd", Locale.getDefault())
                val other = dateFormat.parse(birthDate)
                val current = Date()
                val diffInMillies = current.time - other.time
                ((TimeUnit.DAYS.convert(
                    diffInMillies,
                    TimeUnit.MILLISECONDS
                )) / 365).toInt()
            }
        return "${abs(age)} ${ageSign(abs(age))}"
    }

    private fun ageSign(age: Int): String {
        return when (age % 10) {
            in 1..4 -> {
                if (age in 10..19) {
                    resources.getString(R.string.age_first)
                } else {
                    if(age % 10 == 1){
                        resources.getString(R.string.age_second)
                    }else{
                        resources.getString(R.string.age_third)
                    }
                }
            }
            else -> resources.getString(R.string.age_first)
        }
    }
}