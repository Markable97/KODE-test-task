package com.glushko.kodetestteask.presentation_layer.ui.users_main_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.glushko.kodetestteask.R
import com.glushko.kodetestteask.presentation_layer.vm.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FragmentBottomSheetDialog : BottomSheetDialogFragment() {

    companion object{
        const val TAG = "ModalBottomSheet"
        const val EXTRA_SORT = "type sort"

        fun newInstance(sort:Int): FragmentBottomSheetDialog{
            return FragmentBottomSheetDialog().apply {
                arguments = bundleOf(EXTRA_SORT to sort)
            }
        }
    }

    private lateinit var model: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            if (it.getInt(EXTRA_SORT) == UserViewModel.TYPE_SORT_ALPHABET){
                view.findViewById<RadioButton>(R.id.rb_btn_alphabet).isChecked = true
            }else{
                view.findViewById<RadioButton>(R.id.rb_btn_birthday).isChecked = true
            }
        }
        view.findViewById<RadioGroup>(R.id.rg_sort).setOnCheckedChangeListener { _, checkedId ->
            val typeSort = when(checkedId){
                R.id.rb_btn_alphabet -> UserViewModel.TYPE_SORT_ALPHABET
                R.id.rb_btn_birthday -> UserViewModel.TYPE_SORT_BIRTHDAY
                else -> UserViewModel.TYPE_SORT_ALPHABET
            }
            model.setSort(typeSort)
        }
    }


}