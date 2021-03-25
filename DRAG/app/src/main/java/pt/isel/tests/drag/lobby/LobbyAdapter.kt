package pt.isel.tests.drag.lobby

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import pt.isel.tests.drag.R
import pt.isel.tests.drag.repository.entities.*


class LobbyAdapter internal  constructor(private val context: Context)  :
    RecyclerView.Adapter<LobbyAdapter.LobbyViewHolder>() {

    inner class LobbyViewHolder(lobbyView: View) : RecyclerView.ViewHolder(lobbyView) {
        val playerNameView: TextView = lobbyView.findViewById(R.id.player_name_view)
        val playerStateView: TextView = lobbyView.findViewById(R.id.player_state_view)
        val playerActionButton: Button = lobbyView.findViewById(R.id.player_action_button)
    }

    private val inflater = LayoutInflater.from(context)

    var playersList: List<IPlayer> = emptyList()
    set(players){
        field = players
        notifyDataSetChanged()
    }

    private lateinit var currentPlayer: IPlayer

    var newNamePlayer: MutableLiveData<PlayerRename> = MutableLiveData()
    var removePlayer: MutableLiveData<IPlayer> = MutableLiveData()

    fun setCurrentPlayer(player: IPlayer){
        currentPlayer = player
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LobbyAdapter.LobbyViewHolder {
        val lobbyView = inflater.inflate(R.layout.player_item, parent, false)
        return LobbyViewHolder(lobbyView)
    }

    override fun onBindViewHolder(holder: LobbyAdapter.LobbyViewHolder, position: Int) {
        val player = playersList[position]

        holder.playerNameView.text = player.name
        holder.playerStateView.text = if(player.state == PlayerState.READY) context.getString(R.string.ready_string) else context.getString(R.string.not_ready_string)

        if(!this::currentPlayer.isInitialized){
            holder.playerActionButton.setOnClickListener {
                playerChangeNameDialog(player)
            }
        }
        else{
            with(currentPlayer as RemotePlayer){
                if(this.type == PlayerType.OWNER)
                    handlePlayerOwner(holder, this, player as RemotePlayer)
                else
                    handlePlayerNormal(holder, this, player as RemotePlayer)
            }
        }

    }



    private fun handlePlayerOwner(holder: LobbyAdapter.LobbyViewHolder, currentPlayer: RemotePlayer,
                                  player: RemotePlayer) {
        if(currentPlayer.docRef == player.docRef){
            holder.playerActionButton.visibility = View.VISIBLE
            holder.playerActionButton.setText(R.string.edit_name_value)
            holder.playerActionButton.setOnClickListener{
                playerChangeNameDialog(player)
            }
        }
        else{
            holder.playerActionButton.visibility = View.VISIBLE
            holder.playerActionButton.setText(R.string.remove_string)
            holder.playerActionButton.setOnClickListener {
                removePlayer.postValue(player)
            }
        }
    }

    private fun handlePlayerNormal(holder: LobbyAdapter.LobbyViewHolder, currentPlayer: RemotePlayer,
                                   player: RemotePlayer) {
        if(player.docRef != currentPlayer.docRef){
            holder.playerActionButton.visibility = View.INVISIBLE
        }
        else{
            //necessary cause the position can change because of the sorting
            holder.playerActionButton.visibility = View.VISIBLE
            holder.playerActionButton.setOnClickListener{
                playerChangeNameDialog(player)
            }
        }
    }

    private fun playerChangeNameDialog(player: IPlayer){
        val edit = EditText(context)
        edit.hint = context.getString(R.string.player_name_hint)
                .format(context.resources.getInteger(R.integer.min_chars))
        AlertDialog.Builder(context)
            .setView(edit)
            .setPositiveButton(R.string.confirm_string) {dialog: DialogInterface, _: Int ->
                val name = edit.text.toString()
                if(name.length >= context.resources.getInteger(R.integer.min_chars)){
                    newNamePlayer.postValue(PlayerRename(player.name, name))
                    dialog.dismiss()
                }
                else {
                    edit.error = context.getString(R.string.lobby_name_error).format(
                        context.resources.getInteger(R.integer.min_chars))
                }
            }
            .setNegativeButton(R.string.cancel_string){ dialog: DialogInterface, _: Int ->
                dialog.cancel()
            }
            .show()
    }

    override fun getItemCount(): Int = playersList.size
}

data class PlayerRename(val oldName: String, val newName: String)