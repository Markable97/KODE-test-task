package com.glushko.kodetestteask.presentation_layer.ui.users_main_screen.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.glushko.kodetestteask.R
import com.glushko.kodetestteask.business_logic_layer.domain.User
import java.text.SimpleDateFormat
import java.util.*

class UsersAdapter(private var list: MutableList<User> = mutableListOf(), val callback: Callback): RecyclerView.Adapter<UsersAdapter.BaseViewHolder>() {

    init {
        list = mutableListOf(User(), User(), User(), User(), User(), User(), User(), User())
    }

    companion object{
        private const val TYPE_SKELET = 0
        private const val TYPE_NORMAL = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val holder = when(viewType){
            TYPE_SKELET -> UsersSkeletViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_user_skelet, parent, false))
            TYPE_NORMAL -> UsersViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_user, parent, false))
            else -> UsersSkeletViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_user_skelet, parent, false))
        }
        return holder
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position].id == "default") {
            TYPE_SKELET
        } else{
            TYPE_NORMAL
        }

    }
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(list[position])

    override fun getItemCount() = list.size

    fun setListNew(users: MutableList<User>, start: Int, count: Int){
        list = users
        notifyItemRangeChanged(start, count)
    }

    fun clearList(){
        val size = list.size
        list.clear()
        notifyItemRangeRemoved(0, size)
    }

    abstract class BaseViewHolder(private val parentView: View) : RecyclerView.ViewHolder(parentView){
        abstract fun onBind(user: User)
    }

    inner class UsersSkeletViewHolder(view: View) : BaseViewHolder(view){
        override fun onBind(user: User) {
        }

    }

    inner class UsersViewHolder(private val itemView: View) : BaseViewHolder(itemView) {
        private val avatar: ImageView = itemView.findViewById(R.id.user_avatar)
        private val userName: TextView = itemView.findViewById(R.id.user_name)
        private val userTag: TextView = itemView.findViewById(R.id.user_tag)
        private val userDepartment: TextView = itemView.findViewById(R.id.user_department)
        private val birthday: TextView = itemView.findViewById(R.id.user_birthday)
        private val lineYearLayout: ConstraintLayout = itemView.findViewById(R.id.user_line_year_lay)
        private val lineYearText: TextView = itemView.findViewById(R.id.user_line_year_text)

        override fun onBind(user: User) {
            Glide.with(itemView.context).load(user.avatarUrl).circleCrop().into(avatar)
            userName.text = "${user.firstName} ${user.lastName}"
            userTag.text = user.userTag.lowercase()
            userDepartment.text = user.department
            itemView.setOnClickListener {
                callback.onClickItem(user)
            }
            if(user.typeView == 0){
                birthday.visibility = View.INVISIBLE
                lineYearLayout.visibility = View.GONE
            }else{
                birthday.visibility = View.VISIBLE
                birthday.text = SimpleDateFormat(
                    "d MMMM",
                    Locale.getDefault()
                ).format(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(user.birthday)).toString()
                if (user.lineYear!="default"){
                    lineYearLayout.visibility = View.VISIBLE
                    lineYearText.text = user.lineYear
                }else{
                    lineYearLayout.visibility = View.GONE
                }
            }
        }

    }

    interface Callback{
        fun onClickItem(user: User)
    }

}