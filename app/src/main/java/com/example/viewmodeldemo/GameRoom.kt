package com.example.viewmodeldemo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.viewmodeldemo.databinding.FragmentGameRoomBinding
import com.google.gson.Gson


class GameRoom : Fragment() {
    private lateinit var binding : FragmentGameRoomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val mSocket = SocketHandler.getSocket()
                val rid = requireArguments().getString("room_id")
                val pid = requireArguments().getString("player_id")
                var gson = Gson()
                var data = object {
                    val roomId : String? = rid
                    val playerId : String? = pid
                }
                var jsonString = gson.toJson(data)
                mSocket.emit("end", jsonString)

                findNavController().navigate(R.id.action_gameRoom_to_roomInfo2)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGameRoomBinding.inflate(inflater, container, false)
        return binding.root
    }
}