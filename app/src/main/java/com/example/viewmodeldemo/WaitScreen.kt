package com.example.viewmodeldemo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
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

                findNavController().navigate(R.id.action_waitScreen_to_roomInfo)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWaitScreenBinding.inflate(inflater, container, false)

        roomId = requireArguments().getString("room_id")

        val mSocket = SocketHandler.getSocket()

        Toast.makeText(activity, "lalalalalal", Toast.LENGTH_SHORT).show()

        mSocket.emit("get_joined_players", roomId)
        mSocket.emit("all_joined", roomId)

        mSocket.on("all_joined"){
            args -> if(args[0] != null){
                Log.i("TEST", "lol")
            activity?.runOnUiThread {
                if(args[0] == true){
                    val pid = requireArguments().getString("player_id")
                    val id = bundleOf("room_id" to roomId, "player_id" to pid)
                    mSocket.emit("stop_timer")
                    findNavController().navigate(R.id.action_waitScreen_to_gameRoom, id)
                }
            }
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