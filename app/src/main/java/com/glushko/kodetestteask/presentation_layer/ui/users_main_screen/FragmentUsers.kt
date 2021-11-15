package com.glushko.kodetestteask.presentation_layer.ui.users_main_screen

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.glushko.kodetestteask.R
import com.glushko.kodetestteask.business_logic_layer.domain.User
import com.glushko.kodetestteask.presentation_layer.ui.user_detail_screen.FragmentUserInfo
import com.glushko.kodetestteask.presentation_layer.ui.users_main_screen.adapter.UsersAdapter
import com.glushko.kodetestteask.presentation_layer.vm.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import java.util.concurrent.TimeUnit

class FragmentUsers: Fragment(R.layout.fragment_users_list) {

    private val tabs = listOf(
        TabInfo("","Все"),
        TabInfo("android","Android"),
        TabInfo("ios","iOS"),
        TabInfo("design","Дизайн"),
        TabInfo("management","Менеджмент"),
        TabInfo("qa","QA"),
        TabInfo("back_office","Бэк-офис"),
        TabInfo("frontend","Frontend    "),
        TabInfo("hr","HR"),
        TabInfo("pr","PR"),
        TabInfo("backend","Backend"),
        TabInfo("support","Техподдержка"),
        TabInfo("analytics","Аналитика")
    )

    private lateinit var model: UserViewModel

    private lateinit var recycler: RecyclerView
    private lateinit var swiper: SwipeRefreshLayout
    private lateinit var _adapter: UsersAdapter

    private lateinit var imgFind: ImageView
    private lateinit var editTextFindUser: EditText
    private lateinit var btnCancel: TextView

    private lateinit var tablayout: TabLayout

    private lateinit var tittleUpNotFind: TextView
    private lateinit var tittleDwNotFind: TextView
    private lateinit var imgNotFind: ImageView

    private lateinit var btnSort: ImageView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var snackbar: Snackbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        _adapter = UsersAdapter(callback = object : UsersAdapter.Callback {
            override fun onClickItem(user: User) {
                findNavController().navigate(R.id.action_fragmentUsers_to_fragmentUserInfo, bundleOf(FragmentUserInfo.EXTRA_USER to user))
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        snackbar = Snackbar.make(requireView(), resources.getString(R.string.smackbar_loading), Snackbar.LENGTH_INDEFINITE)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.snackbar_loading))
        snackbar.show()
        tablayout = view.findViewById(R.id.tabLayout)
        initTabLayout()
        initRecycler()
        btnSort = view.findViewById(R.id.img_sort)
        btnSort.setOnClickListener {
            FragmentBottomSheetDialog.newInstance(
                model.liveDataSorted.value ?: UserViewModel.TYPE_SORT_ALPHABET
            ).show(parentFragmentManager, FragmentBottomSheetDialog.TAG)
        }
        imgNotFind = view.findViewById(R.id.img_not_find)
        tittleUpNotFind = view.findViewById(R.id.tittle_upper_not_user_find)
        tittleDwNotFind = view.findViewById(R.id.tittle_bottom_not_user_find)


        imgFind = view.findViewById(R.id.img_find)
        editTextFindUser = view.findViewById(R.id.edit_find_user)
        editTextFindUser.clearFocus()
        editTextFindUser.setOnFocusChangeListener { _, isFocus ->
            if (isFocus) {
                imgFind.setImageResource(R.drawable.ic_find_activate)
                btnCancel.visibility = View.VISIBLE
            }else{
                imgFind.setImageResource(R.drawable.ic_find)
            }
        }
        btnCancel = view.findViewById<TextView>(R.id.btn_cancel)
        btnCancel.setOnClickListener {
            editTextFindUser.setText("")
            btnCancel.visibility = View.GONE
            editTextFindUser.clearFocus()
            view.hideKeyboard()
        }

        model.liveDataUsers.observe(viewLifecycleOwner, Observer {
            snackbar.dismiss()
            if (it.success){
                _adapter.clearList()
                _adapter.setListNew(it.items.toMutableList(), 0, it.items.size)
                swiper.isRefreshing = false
            }else{
                val navOption = NavOptions.Builder().setPopUpTo(R.id.fragmentUsers, true).build()
                findNavController().navigate(R.id.action_fragmentUsers_to_fragmentError, null, navOption)
            }
        })

        model.liveDataUserSearch.observe(viewLifecycleOwner, Observer {
            _adapter.clearList()
            if(it.items.isNotEmpty()){
                hideViews()
            }else{
                showView()
            }
            _adapter.setListNew(it.items.toMutableList(), 0, it.items.size)
        })

        model.liveDataSorted.observe(viewLifecycleOwner, Observer {
            if (it == UserViewModel.TYPE_SORT_BIRTHDAY){
                btnSort.setImageResource(R.drawable.ic_sort_activate)
            }else{
                btnSort.setImageResource(R.drawable.ic_sort)
            }
            val department = tablayout.getTabAt(tablayout.selectedTabPosition)?.tag.toString()
            model.searchUser(department, UserViewModel.TYPE_FIND_TAB, department)
        })
        actionSearchUser()


    }


    private fun initTabLayout() {
        tabs.forEach {
            tablayout.addTab(tablayout.newTab().setText(it.tittle).setTag(it.tag))
        }
        tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val department = tab?.tag.toString()
                model.searchUser(department, UserViewModel.TYPE_FIND_TAB, department)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
    }

    private fun initRecycler(){
        swiper = requireView().findViewById(R.id.swiper_users)
        swiper.setColorSchemeResources(R.color.color_swiper_progress_bar)
        swiper.setOnRefreshListener {
            snackbar.show()
            model.getUsers(model.liveDataSorted.value?:UserViewModel.TYPE_SORT_ALPHABET)
        }
        recycler = requireView().findViewById(R.id.recycler_users)
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter = _adapter
    }

    @SuppressLint("CheckResult")
    private fun actionSearchUser(){
        Observable.create(ObservableOnSubscribe<String> {
            editTextFindUser.doAfterTextChanged { str ->
                it.onNext(str.toString())
            }
        })
            .map { text -> text.trim().lowercase()}
            .debounce(250, TimeUnit.MILLISECONDS)
            .subscribe { text ->
                val tab = tablayout.getTabAt(tablayout.selectedTabPosition)
                val department = tab?.tag.toString()
                model.searchUser(text, UserViewModel.TYPE_FIND_USER, department)
            }
    }

    private fun hideViews(){
        swiper.visibility = View.VISIBLE
        imgNotFind.visibility = View.INVISIBLE
        tittleUpNotFind.visibility = View.INVISIBLE
        tittleDwNotFind.visibility = View.INVISIBLE
    }
    private fun showView(){
        swiper.visibility = View.INVISIBLE
        imgNotFind.visibility = View.VISIBLE
        tittleUpNotFind.visibility = View.VISIBLE
        tittleDwNotFind.visibility = View.VISIBLE
    }

}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}