package com.glushko.kodetestteask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.glushko.kodetestteask.presentation_layer.ui.error_screen.FragmentError
import com.glushko.kodetestteask.presentation_layer.vm.UserViewModel

class MainActivity : AppCompatActivity(), FragmentError.CallbackFragmentError {

    lateinit var model: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        model = ViewModelProvider(this).get(UserViewModel::class.java)
        model.getUsers()

    }

    override fun onClickBtnTryAgain() {
        model.getUsers()
    }
}