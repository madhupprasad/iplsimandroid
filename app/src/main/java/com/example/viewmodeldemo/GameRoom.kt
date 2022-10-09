package com.example.viewmodeldemo

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.example.viewmodeldemo.databinding.FragmentGameRoomBinding
import com.google.gson.Gson
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response


class GameRoom : Fragment() {
    private lateinit var binding : FragmentGameRoomBinding
    private var bidAmount : Int = 5

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

        val rid = requireArguments().getString("room_id")
        val pid = requireArguments().getString("player_id")

        val mSocket = SocketHandler.getSocket()
        var gson = Gson()

        var data = object {
            val roomId = rid
            val playerId = pid
        }

        mSocket.emit("start_game",gson.toJson(data))

        mSocket.on("balance_update"){
            args -> if(args[0] != null){
                activity?.runOnUiThread {
                    binding.tvBalance.text = "$${args[0].toString()}"
                }
            }
        }

        mSocket.on("on_bid"){
            args -> if(args[0]!=null){
                val data = args[0] as JSONObject
            activity?.runOnUiThread {
                binding.tvHighestBid.text = "$${data.get("highestBid").toString()}"
                binding.tvHighestBidder.text = "${ data.get("playerName").toString() }"
            }
        }
        }

        mSocket.on("cricketer_data"){
            args -> if(args[0] != null){
                val cricketer = args[0] as JSONObject
                activity?.runOnUiThread {
                    binding.tvHighestBid.text = "Waiting ..."
                    binding.tvHighestBidder.text = "Waiting ..."
                    binding.tvCricketerName.text = cricketer.get("player") as CharSequence?
                    binding.tvBaseBid.text = "Base price : $${cricketer.get("base").toString()}"
                    val imgurl = cricketer.get("image").toString()
                    Picasso.get().load(imgurl)
                        .placeholder(R.drawable.avatar)
                        .into(binding.imageview)
                }
            }
        }

        mSocket.on("timer"){
            args -> if(args[0] != null){
                activity?.runOnUiThread {
                    val time = args[0].toString()
                    if(time.toInt() < 5){
                        binding.tvTimer.setTextColor(Color.RED)
                    }else{
                        binding.tvTimer.setTextColor(Color.GRAY)
                    }
                    binding.tvTimer.text = time
                }
            }
        }

        mSocket.on("sold"){
            activity?.runOnUiThread {
                val pName = it[0].toString()
                Toast.makeText(context, "Sold to $pName", Toast.LENGTH_SHORT).show()
            }
        }

        mSocket.on("timeout"){
            mSocket.emit("transaction_end", gson.toJson(data))
        }

        mSocket.on("unsold"){
            activity?.runOnUiThread {
                Toast.makeText(context, "Unsold", Toast.LENGTH_SHORT).show()
            }
        }

        mSocket.on("crystal"){
            activity?.runOnUiThread {
                binding.tvCrystal.text = "$${it[0].toString()}"
            }
        }

        mSocket.on("out_of_balance"){
            activity?.runOnUiThread {
                Toast.makeText(context, "Out of balance!", Toast.LENGTH_SHORT).show()
            }
        }


        val retService = RetrofitInstance.getRetrofitInstance().create(CreateRoomService::class.java)


        binding.btnInfo.setOnClickListener {
            val dialogBinding = layoutInflater.inflate(R.layout.my_custom_dialog, null)
            val myDialog = Dialog(requireContext())
            myDialog.setContentView(dialogBinding)
            myDialog.setCancelable(true)
            myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.GRAY))

            val createRoomResponse : LiveData<Response<CreateRoomResponse>> = liveData {
                if(rid != null){
                    val res = retService.createRoom(CreateRoomData(roomId = rid))
                    emit(res)
                }
            }
            createRoomResponse.observe(viewLifecycleOwner, Observer {
                val infoText = it.body()?.info
                val info = dialogBinding.findViewById<TextView>(R.id.tvInfo)
                info.text = infoText.toString()
                myDialog.show()
            })
        }


        binding.btnBid.setOnClickListener {
            var data = object {
                val roomId = rid
                val playerId = pid
                val amount = bidAmount
            }
            mSocket.emit("on_bid", gson.toJson(data) )
        }

        return binding.root
    }
}