package com.yudiz.dynamodbdemo.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.yudiz.dynamodbdemo.BR
import com.yudiz.dynamodbdemo.R
import com.yudiz.dynamodbdemo.data.PlayerDataModel

class PlayerListAdapter(
    private val playerList: MutableList<PlayerDataModel>,
    private val playerClickListener: PlayerClickListener
) :
    RecyclerView.Adapter<PlayerListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_player,
            parent,
            false
        )
    )

    override fun getItemCount() = playerList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.setVariable(BR.player, playerList[position])
        holder.binding.setVariable(BR.click, View.OnClickListener { v ->
            if (holder.adapterPosition >= 0) {
                when (v.id) {
                    R.id.iv_delete ->
                        playerClickListener.onDeleteClicked(position, playerList[position].id!!)
                    else ->
                        playerClickListener.onPlayerClicked(playerList[position])
                }
            }
        })
    }

    fun deleteItemAt(pos: Int) {
        playerList.removeAt(pos)
        notifyItemRemoved(pos)
        notifyItemRangeChanged(0, playerList.size)
    }

    fun replaceData(playerList: List<PlayerDataModel>) {
        this.playerList.let {
            it.clear()
            it.addAll(playerList)
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {}

    interface PlayerClickListener {
        fun onDeleteClicked(pos: Int, id: String)
        fun onPlayerClicked(player: PlayerDataModel)
    }

}