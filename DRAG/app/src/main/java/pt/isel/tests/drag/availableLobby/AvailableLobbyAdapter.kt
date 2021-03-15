package pt.isel.tests.drag.availableLobby

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pt.isel.tests.drag.R

const val TAG ="AvailableLobbyAdapter"
class AvailableLobbyAdapter internal  constructor(private val context: Context) :
    RecyclerView.Adapter<AvailableLobbyAdapter.LobbyViewHolder>() {

    inner class LobbyViewHolder(lobbyView: View) : RecyclerView.ViewHolder(lobbyView) {
        val lobbyNameView: TextView = lobbyView.findViewById<TextView>(R.id.lobby_name_header)
        val lobbyAvailabilityView: TextView = lobbyView.findViewById<TextView>(R.id.lobby_availability_header)
        val lobbyJoinButton: Button = lobbyView.findViewById<Button>(R.id.join_button)
    }

    private val inflater = LayoutInflater.from(context)

    var lobbyList = listOf<String>("hello")
    set(lobby){
        field = lobby
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LobbyViewHolder {
        val lobbyView = inflater.inflate(R.layout.available_lobby_item, parent, false)
        return LobbyViewHolder(lobbyView)
    }

    override fun onBindViewHolder(holder: LobbyViewHolder, position: Int) {
        val lobby = lobbyList[position]
        with(holder){
            if(position == 0){
                lobbyJoinButton.visibility = View.INVISIBLE
                lobbyNameView.textSize = 18F
                lobbyAvailabilityView.textSize = 18F
            }
            Log.d(TAG, String.format("%d", position))
        }
    }

    override fun getItemCount(): Int = lobbyList.size
}