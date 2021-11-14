package com.glushko.kodetestteask.presentation_layer.ui.error_screen

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.glushko.kodetestteask.R
import com.google.android.material.snackbar.Snackbar

class FragmentError: Fragment(R.layout.fragment_error) {

    lateinit var callback: CallbackFragmentError

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            callback = context as CallbackFragmentError
        }catch (ex: ClassCastException){
            throw ClassCastException("must implement CallbackFragmentError")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Snackbar.make(requireView(), "Не могу обновить данные. Что-то пошло не так", Snackbar.LENGTH_SHORT)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.snackbar_err)).show()
        view.findViewById<TextView>(R.id.bnt_try_again).setOnClickListener {
            val navOption = NavOptions.Builder().setPopUpTo(R.id.fragmentUsers, true).build()
            findNavController().navigate(R.id.action_fragmentError_to_fragmentUsers,null, navOption)
            callback.onClickBtnTryAgain()
        }
    }

    interface CallbackFragmentError{
        fun onClickBtnTryAgain()
    }

}