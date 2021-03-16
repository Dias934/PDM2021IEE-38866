package pt.isel.tests.drag.lobby

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.recyclerview.widget.RecyclerView
import pt.isel.tests.drag.R
import pt.isel.tests.drag.repository.Player
import pt.isel.tests.drag.repository.PlayerState

class LobbyAdapter internal  constructor(private val context: Context) :
    RecyclerView.Adapter<LobbyAdapter.LobbyViewHolder>() {

    inner class LobbyViewHolder(lobbyView: View) : RecyclerView.ViewHolder(lobbyView) {
        val playerNameView: TextView = lobbyView.findViewById(R.id.player_name_view)
        val playerStateView: TextView = lobbyView.findViewById(R.id.player_state_view)
        val editNameButton: Button = lobbyView.findViewById(R.id.edit_player_name_button)
    }

    private val inflater = LayoutInflater.from(context)

    var playersList: List<Player> = emptyList()
    set(players){
        field = players
        notifyDataSetChanged()
    }

    var currentPlayer: Player? = null
        set(player) {
            field = player
            notifyDataSetChanged()
        }

    var newNamePlayer: MutableLiveData<Player> = MutableLiveData()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LobbyAdapter.LobbyViewHolder {
        val lobbyView = inflater.inflate(R.layout.player_item, parent, false)
        return LobbyViewHolder(lobbyView)
    }

    override fun onBindViewHolder(holder: LobbyAdapter.LobbyViewHolder, position: Int) {
        val player = playersList[position]

        holder.playerNameView.text = player.name
        holder.playerStateView.text = if(player.state == PlayerState.READY) context.getString(R.string.ready_string) else context.getString(R.string.not_ready_string)
        holder.editNameButton.setOnClickListener {
            val edit = EditText(context)
            val x = context.getString(R.string.player_name_hint)
                .format(context.resources.getInteger(R.integer.min_chars))
            edit.hint = x
            val dialog = AlertDialog.Builder(context)
                .setView(edit)
                .setPositiveButton(R.string.confirm_string) {dialog: DialogInterface, _: Int ->
                    val name = edit.text.toString()
                    if(name.length >= context.resources.getInteger(R.integer.min_chars)){
                        playersList[position].name = name
                        newNamePlayer.postValue(playersList[position])
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

    }

    override fun getItemCount(): Int = playersList.size
}