package com.example.viewmodeldemo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.viewmodeldemo.databinding.FragmentGameRoomBinding


class GameRoom : Fragment() {
    private lateinit var binding : FragmentGameRoomBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGameRoomBinding.inflate(inflater, container, false)



        return binding.root
    }
}