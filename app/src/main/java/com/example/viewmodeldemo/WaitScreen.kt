package com.example.viewmodeldemo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.example.viewmodeldemo.databinding.FragmentWaitScreenBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import org.json.JSONArray

class WaitScreen : Fragment() {

    private lateinit var binding: FragmentWaitScreenBinding
    private var roomId : String? = null

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
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWaitScreenBinding.inflate(inflater, container, false)

        val mSocket = SocketHandler.getSocket()

        roomId = requireArguments().getString("room_id")

        mSocket.emit("get_joined_players", roomId)

        mSocket.on("all_joined"){
            args -> if(args[0] != null){
            activity?.runOnUiThread {
                if(args[0] == true){
                    findNavController().navigate(R.id.action_waitScreen_to_gameRoom)
                }
            }
            }
        }

        mSocket.on("exit") { args ->
            if (args[0] != null) {
                val pid = requireArguments().getString("player_id")
                if(args[0] == pid){
                    activity?.runOnUiThread {
                        findNavController().navigate(R.id.action_waitScreen_to_roomInfo)
                    }
                }
            }else{
                activity?.runOnUiThread {
                    findNavController().navigate(R.id.action_waitScreen_to_roomInfo)
                }
            }
        }

        mSocket.on("refresh_players"){
            args -> if(args[0]!=null && args[0]==true){
                mSocket.emit("get_joined_players", roomId)
            }
        }

        mSocket.on("get_joined_players"){
            args -> if(args[0] != null){
            activity?.runOnUiThread {
                var resultString = ""
                val listVal = args[0] as JSONArray
                for (i in 0 until listVal.length()) {
                    val player = listVal.getJSONObject(i)
                    resultString = "$resultString ${player["playerName"]} \n"
                }
                binding.tvPlayerNames.text = resultString
            }
            }
        }

        if(roomId != null){
            binding.tvRoomId.text = roomId
        }

        return binding.root
    }
}