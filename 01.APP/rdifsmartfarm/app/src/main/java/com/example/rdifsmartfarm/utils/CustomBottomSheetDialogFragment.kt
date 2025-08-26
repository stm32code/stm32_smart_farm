package com.example.rdifsmartfarm.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rdifsmartfarm.databinding.BottomSheetDialogFrgmentLayoutBinding

import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class CustomBottomSheetDialogFragment : BottomSheetDialogFragment(), HandlerAction {
    private lateinit var binding: BottomSheetDialogFrgmentLayoutBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 填充底部弹窗的布局文件
        binding = BottomSheetDialogFrgmentLayoutBinding.inflate(
            inflater,
            container,
            false
        )
//        val dao = UserDao(requireContext())
//        val list: MutableList<Any>? = dao.query()
//        if (list != null) {
//            for (i in list.indices) {
//                val u: User = list[i] as User
//                if (u.uname == "admin") {
//                    list.removeAt(i)
//                    break
//                }
//            }
//            if (list.size > 0) {
//                binding.settingList.adapter = AdminListViewAdapter(requireContext(), list)
//            } else {
//                MToast.mToast(requireContext(), "还没有数据")
//            }
//        }
        return binding.root
    }
}
