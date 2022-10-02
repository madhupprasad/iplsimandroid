package com.example.viewmodeldemo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.viewmodeldemo.databinding.FragmentJoinRoomCredBinding
import com.google.gson.Gson
import org.json.JSONObject

class JoinRoomCred : Fragment() {
    private lateinit var binding: FragmentJoinRoomCredBinding
    private var mutex = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentJoinRoomCredBinding.inflate(inflater, container, false)

        SocketHandler.setSocket()
        val mSocket = SocketHandler.getSocket()
        mSocket.connect()

        mSocket.on("user_joined"){
            args -> if(args[0]!=null){
                activity?.runOnUiThread {
                    val id = bundleOf("room_id" to binding.etRoomId.text.toString(), "player_id" to args[0] as String)
                    if(mutex){
                        mutex = false
                        findNavController().navigate(R.id.action_joinRoomCred_to_waitScreen, id)
                    }
                }
            }
        }

        binding.btnJoinRoom.setOnClickListener {
            var gson = Gson()
            var data = object {
                val roomId : String = binding.etRoomId.text.toString()
                val playerName : String = binding.etJoinName.text.toString()
            }
            var jsonString = gson.toJson(data)
            mSocket.emit("join", jsonString)
        }

        return binding.root
    }
}