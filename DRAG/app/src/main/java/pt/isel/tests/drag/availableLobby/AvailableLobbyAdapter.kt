package pt.isel.tests.drag.availableLobby

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pt.isel.tests.drag.R
import pt.isel.tests.drag.repository.Lobby

const val TAG ="AvailableLobbyAdapter"
const val AVAILABILITY = "%d/%d"

class AvailableLobbyAdapter internal  constructor(private val context: Context) :
    RecyclerView.Adapter<AvailableLobbyAdapter.LobbyViewHolder>() {

    inner class LobbyViewHolder(lobbyView: View) : RecyclerView.ViewHolder(lobbyView) {
        val lobbyNameView: TextView = lobbyView.findViewById(R.id.lobby_name_header)
        val lobbyAvailabilityView: TextView = lobbyView.findViewById(R.id.lobby_availability_header)
        val lobbyJoinButton: Button = lobbyView.findViewById(R.id.join_button)
    }

    private val inflater = LayoutInflater.from(context)

    var lobbyList = emptyList<Lobby>()
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
        holder.lobbyNameView.text = lobby.name
        holder.lobbyAvailabilityView.text =  String.format(AVAILABILITY, lobby.players.size, lobby.nPlayers)
    }

    override fun getItemCount(): Int = lobbyList.size
}