package com.example.viewmodeldemo

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.example.viewmodeldemo.databinding.FragmentRoomInfoBinding

class RoomInfo : Fragment() {

    private lateinit var binding : FragmentRoomInfoBinding
    private lateinit var viewModel: RoomInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoomInfoBinding.inflate(inflater, container, false)

        binding.btnCreate.setOnClickListener {
            findNavController().navigate(R.id.action_roomInfo_to_createRoomCred)
        }

        binding.btnJoin.setOnClickListener {
            findNavController().navigate(R.id.action_roomInfo_to_joinRoomCred)
        }

        return binding.root
    }
}