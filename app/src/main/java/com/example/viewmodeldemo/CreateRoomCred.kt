package com.example.viewmodeldemo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.viewmodeldemo.databinding.FragmentCreateRoomCredBinding
import com.google.gson.Gson

class CreateRoomCred : Fragment() {

    private lateinit var binding : FragmentCreateRoomCredBinding
    private var roomid : String = "init"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCreateRoomCredBinding.inflate(inflater, container, false)

        SocketHandler.setSocket()
        val mSocket = SocketHandler.getSocket()
        mSocket.connect()

        mSocket.on("room_id"){
            args -> if(args[0]!=null && args[1] != null){
                activity?.runOnUiThread {
                    roomid = args[0] as String
                    val playerId  = args[1] as String
                    val bundle = bundleOf("room_id" to roomid, "player_id" to playerId)
                    findNavController().navigate(R.id.action_createRoomCred_to_waitScreen, bundle)
                }
            }
        }

        binding.btnCreateRoom.setOnClickListener {
            var gson = Gson()
            var data = object {
                val playerName = binding.etAdminName.text.toString()
                val numberOfPlayers = binding.etNumber.text.toString()
            }
            var jsonString = gson.toJson(data)
            mSocket.emit("create", jsonString)
        }

        return binding.root
    }
}